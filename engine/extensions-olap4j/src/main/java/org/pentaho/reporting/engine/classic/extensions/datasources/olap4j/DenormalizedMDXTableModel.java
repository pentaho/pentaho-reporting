/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Member;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.util.IntList;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.util.ResultSetProcessingLib;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Axis indexes are predefined: 0 = Columns, 1 = rows, 2 = pages, 3 = sections, 4 = Chapters, 5 and greater: unnamed) Up
 * to 128 axises can be specified. There can be queries with no axis at all, which returns a single cell.
 *
 * @author : Thomas Morgner
 */
@SuppressWarnings( { "UnnecessaryUnboxing" } )
public class DenormalizedMDXTableModel extends AbstractTableModel implements CloseableTableModel, MetaTableModel {
  private static final Log logger = LogFactory.getLog( DenormalizedMDXTableModel.class );
  private final List<Member> columnToMemberMapping;

  private final CellSet resultSet;
  private int rowCount;
  private final int columnCount;
  private final List<String> columnNames;
  private final int[] axesSize;
  private final IntList columnToAxisPosition;
  private final QueryResultWrapper resultWrapper;

  @Deprecated
  public DenormalizedMDXTableModel( final QueryResultWrapper resultSet ) {
    this( resultSet, 0, false );
  }

  public DenormalizedMDXTableModel( final QueryResultWrapper resultWrapper,
                                    final int rowLimit,
                                    final boolean membersOnAxisSorted ) {
    ArgumentNullException.validate( "resultWrapper", resultWrapper );

    this.resultWrapper = resultWrapper;
    this.resultSet = resultWrapper.getCellSet();
    final List<CellSetAxis> axes = this.resultSet.getAxes();

    this.axesSize = ResultSetProcessingLib.computeItemsPerAxis( axes );
    this.rowCount = computeRowCount( axesSize );
    final int[] axesMembers = ResultSetProcessingLib.computeTotalColumnsPerAxis( axes, 0, membersOnAxisSorted );

    this.columnCount = computeColumnCount( axesMembers );
    this.columnToAxisPosition = ResultSetProcessingLib.computeColumnToAxisMapping( axes, axesMembers, columnCount, 0 );
    this.columnToMemberMapping = Collections.unmodifiableList
      ( ResultSetProcessingLib.computeColumnToMemberMapping( axes, axesMembers, 0, membersOnAxisSorted ) );
    this.columnNames = computeColumnNames( columnToMemberMapping );

    if ( rowLimit > 0 ) {
      rowCount = Math.min( rowLimit, rowCount );
    }
  }

  private List<String> computeColumnNames( final List<Member> columnToMemberMapper ) {
    ArrayList<String> columnNames = new ArrayList<String>();
    for ( final Member member : columnToMemberMapper ) {
      columnNames.add( member.getLevel().getUniqueName() );
    }

    final Member measureName = computeMeasureName( resultSet );
    if ( measureName != null ) {
      columnNames.add( measureName.getUniqueName() );
    } else {
      columnNames.add( "Measure" );
    }
    return columnNames;
  }

  private int computeColumnCount( final int[] axesMembers ) {
    if ( axesMembers.length == 0 ) {
      return 1;
    }

    boolean emptyAxisFound = false;
    int columnCount = 0;
    for ( int i = 0; i < axesMembers.length; i++ ) {
      columnCount += axesMembers[ i ];
      if ( axesMembers[ i ] == 0 ) {
        emptyAxisFound = true;
      }
    }

    if ( !emptyAxisFound ) {
      columnCount += 1;
    }

    return columnCount;
  }

  private int computeRowCount( final int[] axesSize ) {
    int rowCount = 1;
    for ( int i = 0; i < axesSize.length; i++ ) {
      final int size = axesSize[ i ];
      rowCount *= size;
    }

    return Math.max( 1, rowCount );
  }

  private static Member computeMeasureName( final CellSet resultSet ) {
    final List<Position> positionList = resultSet.getFilterAxis().getPositions();
    for ( int i = 0; i < positionList.size(); i++ ) {
      final Position position = positionList.get( i );
      final List<Member> members = position.getMembers();
      for ( int positionIndex = 0; positionIndex < members.size(); positionIndex++ ) {

        Member m = members.get( positionIndex );
        while ( m != null ) {
          if ( m.getMemberType() == Member.Type.MEASURE ) {
            return m;
          }
          m = m.getParentMember();
        }
      }
    }

    return null;
  }

  public int getRowCount() {
    return rowCount;
  }

  public int getColumnCount() {
    return columnCount;
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
   * <code>column</code> cannot be found, returns an empty string.
   *
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName( final int column ) {
    return columnNames.get( column );
  }

  public Class getColumnClass( final int columnIndex ) {
    if ( getRowCount() == 0 ) {
      return Object.class;
    }
    try {
      final Object targetClassObj = getValueAt( 0, columnIndex );
      if ( targetClassObj == null ) {
        return Object.class;
      } else {
        return targetClassObj.getClass();
      }
    } catch ( final Exception e ) {
      return Object.class;
    }
  }

  public Object getValueAt( final int rowIndex,
                            final int columnIndex ) {
    if ( columnIndex >= columnNames.size() ) {
      throw new IndexOutOfBoundsException();
    }

    if ( isMeasureColumn( columnIndex ) ) {
      final List<Integer> cellKey = computeCellKey( rowIndex );
      try {
        final Cell cell = resultSet.getCell( cellKey );
        if ( cell.isNull() ) {
          return null;
        }
        return cell.getValue();
      } catch ( final NullPointerException pe ) {
        pe.printStackTrace();
      }
    }

    final List<Integer> cellKey = computeCellKey( rowIndex );
    Member candidateMember = getCandidateMembers( columnIndex, cellKey );
    if ( candidateMember != null ) {
      return candidateMember.getName();
    }
    return null;
  }

  private boolean isMeasureColumn( final int columnIndex ) {
    if ( columnIndex >= columnToMemberMapping.size() ) {
      return true;
    }
    return false;
  }

  private List<Integer> computeCellKey( final int rowIndex ) {
    final List<Integer> cellKey = new ArrayList<Integer>( axesSize.length );
    int tmpRowIdx = rowIndex;
    for ( int i = 0; i < axesSize.length; i++ ) {
      final int axisSize = axesSize[ i ];
      if ( axisSize == 0 ) {
        cellKey.add( 0 );
      } else {
        final int pos = tmpRowIdx % axisSize;
        cellKey.add( pos );
        tmpRowIdx = tmpRowIdx / axisSize;
      }
    }

    return cellKey;
  }


  private Member getCandidateMembers( final int columnIndex,
                                      final List<Integer> cellKey ) {

    final int axisIndex = columnToAxisPosition.get( columnIndex );
    final List<CellSetAxis> axes = resultSet.getAxes();
    final CellSetAxis axis = axes.get( axisIndex );

    final List<Position> positionList = axis.getPositions();
    if ( positionList.isEmpty() ) {
      return null;
    }

    final int posIndex = cellKey.get( axisIndex );
    final Position position = positionList.get( posIndex );

    final Member memberByName = findMemberByName( position.getMembers(), columnIndex );
    if ( memberByName != null ) {
      return memberByName;
    }
    return findRootMember( position.getMembers(), columnIndex );
  }

  private Member findRootMember( final List<Member> position, final int columnIndex ) {
    final Dimension dimension = columnToMemberMapping.get( columnIndex ).getDimension();
    for ( int i = 0; i < position.size(); i++ ) {
      final Member member = position.get( i );
      if ( dimension.equals( member.getDimension() ) ) {
        if ( member.getParentMember() == null ) {
          return member;
        }
      }
    }
    return null;
  }

  private Member findMemberByName( final List<Member> position, final int columnIndex ) {
    final Dimension dimension = columnToMemberMapping.get( columnIndex ).getDimension();
    for ( int i = 0; i < position.size(); i++ ) {
      final Member member = position.get( i );
      if ( dimension.equals( member.getDimension() ) ) {
        Member match = searchContextMemberOfParents( member, columnIndex );
        if ( match != null ) {
          return match;
        }
      }
    }
    return null;
  }

  private Member searchContextMemberOfParents( final Member member, final int columnIndex ) {
    String columnName = getColumnName( columnIndex );

    Member candidate = member;
    while ( candidate != null ) {
      if ( candidate.getLevel().getUniqueName().equals( columnName ) ) {
        return candidate;
      }
      candidate = candidate.getParentMember();
    }
    return null;
  }

  public void close() {
    try {
      resultSet.close();
    } catch ( final SQLException e ) {
      // ignore, but log.
    }
    try {
      final Statement statement = resultWrapper.getStatement();
      statement.close();
    } catch ( final SQLException e ) {
      // ignore ..
    }
  }

  /**
   * Returns the meta-attribute as Java-Object. The object type that is expected by the report engine is defined in the
   * TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model into a
   * model suitable for reporting.
   * <p/>
   * Meta-data models that only describe meta-data for columns can ignore the row-parameter.
   *
   * @param rowIndex    the row of the cell for which the meta-data is queried.
   * @param columnIndex the index of the column for which the meta-data is queried.
   * @return the meta-data object.
   */
  public DataAttributes getCellDataAttributes( final int rowIndex, final int columnIndex ) {
    if ( columnIndex >= columnNames.size() ) {
      throw new IndexOutOfBoundsException();
    }

    if ( isMeasureColumn( columnIndex ) ) {
      final List<Integer> cellKey = computeCellKey( rowIndex );
      final Cell cell = resultSet.getCell( cellKey );
      return new MDXMetaDataCellAttributes( EmptyDataAttributes.INSTANCE, cell );
    }

    final List<Integer> cellKey = computeCellKey( rowIndex );
    Member contextMember = getCandidateMembers( columnIndex, cellKey );
    if ( contextMember != null ) {
      return new MDXMetaDataMemberAttributes( EmptyDataAttributes.INSTANCE, contextMember );
    }
    return EmptyDataAttributes.INSTANCE;
  }

  public boolean isCellDataAttributesSupported() {
    return true;
  }

  public DataAttributes getColumnAttributes( final int column ) {
    return EmptyDataAttributes.INSTANCE;
  }

  /**
   * Returns table-wide attributes. This usually contain hints about the data-source used to query the data as well as
   * hints on the sort-order of the data.
   *
   * @return the table attributes.
   */
  public DataAttributes getTableAttributes() {
    final DefaultDataAttributes dataAttributes = new DefaultDataAttributes();
    dataAttributes.setMetaAttribute( MetaAttributeNames.Core.NAMESPACE,
      MetaAttributeNames.Core.CROSSTAB_MODE, DefaultConceptQueryMapper.INSTANCE,
      MetaAttributeNames.Core.CROSSTAB_VALUE_NORMALIZED );
    return dataAttributes;
  }
}

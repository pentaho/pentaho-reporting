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
import org.olap4j.PreparedOlapStatement;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This tablemodel performs some preprocessing to get multi-dimensional resultset (with row and column headers) into a
 * classical table-structure. The banded tablemodel wraps the column axis into columns of the tablemodel and maps all
 * other axes into rows.
 * <p/>
 * Technically this is a hybrid between the legacy-banded-mdx model and the denormalized model. The column axis is
 * computed similar to the legacy-banded model, while all other axes are simply denormalized.
 *
 * @author : Thomas Morgner
 */
public class BandedMDXTableModel extends AbstractTableModel
  implements CloseableTableModel, MetaTableModel {
  private static final Log logger = LogFactory.getLog( BandedMDXTableModel.class );
  private final List<Member> columnToMemberMapping;
  private final CellSet resultSet;
  private final int columnCount;
  private final List<String> columnNames;
  private final int[] axesSize;
  private final IntList columnToAxisPosition;
  private final QueryResultWrapper resultWrapper;
  private int rowCount;

  @Deprecated
  public BandedMDXTableModel( final QueryResultWrapper resultSet, final int rowLimit ) {
    this( resultSet, rowLimit, false );
  }

  public BandedMDXTableModel( final QueryResultWrapper resultWrapper, final int rowLimit,
                              final boolean membersOnAxisSorted ) {
    ArgumentNullException.validate( "resultWrapper", resultWrapper );

    this.resultWrapper = resultWrapper;
    this.resultSet = resultWrapper.getCellSet();

    final List<CellSetAxis> axes = this.resultSet.getAxes();

    this.axesSize = ResultSetProcessingLib.computeItemsPerAxis( axes );
    this.rowCount = computeRowCount( axesSize );

    final int[] axesMembers = ResultSetProcessingLib.computeTotalColumnsPerAxis( axes, 1, membersOnAxisSorted );

    this.columnCount = computeColumnCount( axesMembers, this.axesSize );
    this.columnToAxisPosition = ResultSetProcessingLib.computeColumnToAxisMapping( axes, axesMembers, columnCount, 1 );
    this.columnToMemberMapping = Collections.unmodifiableList
      ( ResultSetProcessingLib.computeColumnToMemberMapping( axes, axesMembers, 1, membersOnAxisSorted ) );
    this.columnNames = computeColumnNames( axes, columnToMemberMapping );

    if ( rowLimit > 0 ) {
      rowCount = Math.min( rowLimit, rowCount );
    }
  }

  protected int computeColumnCount( final int[] axesMembers, final int[] axesSize ) {
    if ( axesSize.length == 0 ) {
      return 1;
    }

    int columnCount = axesSize[ 0 ];
    for ( int i = 1; i < axesMembers.length; i++ ) {
      columnCount += axesMembers[ i ];
    }
    return columnCount;
  }

  protected int computeRowCount( final int[] axesSize ) {
    if ( axesSize.length > 1 ) {
      int rowCount = axesSize[ 1 ];
      for ( int i = 2; i < axesSize.length; i++ ) {
        final int size = axesSize[ i ];
        rowCount *= size;
      }
      return rowCount;
    } else {
      // special case of having only members on the column axis (but not on row or higher)
      // or having no member on any axis at all
      return 1;
    }
  }

  protected List<String> computeColumnNames( final List<CellSetAxis> axes,
                                             final List<Member> columnToMemberMapper ) {
    ArrayList<String> columnNames = new ArrayList<String>();
    for ( final Member member : columnToMemberMapper ) {
      columnNames.add( member.getLevel().getUniqueName() );
    }
    if ( axes.size() > 0 ) {
      // now create the column names for the column-axis
      final CellSetAxis axis = axes.get( 0 );
      final List<Position> positions = axis.getPositions();
      for ( int i = 0; i < positions.size(); i++ ) {
        final Position position = positions.get( i );
        columnNames.add( ResultSetProcessingLib.computeUniqueColumnName( position ) );
      }
    } else {
      columnNames.add( "Measure" );
    }
    return Collections.unmodifiableList( columnNames );
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

  public Object getValueAt( final int rowIndex,
                            final int columnIndex ) {
    if ( columnIndex >= columnNames.size() ) {
      throw new IndexOutOfBoundsException();
    }

    if ( isMeasureColumn( columnIndex ) ) {
      final List<Integer> cellKey = computeCellKey( rowIndex, columnIndex );
      final Cell cell = resultSet.getCell( cellKey );
      if ( cell.isNull() ) {
        return null;
      }
      return cell.getValue();
    }

    final List<Integer> cellKey = computeCellKey( rowIndex, columnIndex );

    Member candidateMember = getCandidateMembers( columnIndex, cellKey );
    if ( candidateMember != null ) {
      return candidateMember.getName();
    }
    return null;
  }

  public Class<?> getColumnClass( final int columnIndex ) {
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
    } catch ( Exception e ) {
      return Object.class;
    }
  }

  private List<Integer> computeCellKey( final int rowIndex, final int columnIndex ) {
    if ( axesSize.length == 0 ) {
      return Collections.emptyList();
    }

    final int correctedColIndex;
    if ( axesSize.length > 0 ) {
      final int startOfColumnIndex = columnCount - axesSize[ 0 ];
      if ( columnIndex < startOfColumnIndex ) {
        // this is a query for a axis-header
        correctedColIndex = -1;
      } else {
        correctedColIndex = columnIndex - startOfColumnIndex;
      }
    } else {
      correctedColIndex = 0;
    }

    final List<Integer> cellKey = new ArrayList<Integer>();
    cellKey.add( correctedColIndex );

    int tmpRowIdx = rowIndex;
    for ( int i = 1; i < axesSize.length; i++ ) {
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

  private boolean isMeasureColumn( final int columnIndex ) {
    if ( columnIndex >= columnToMemberMapping.size() ) {
      return true;
    }
    return false;
  }

  public void close() {
    try {
      resultSet.close();
    } catch ( SQLException e ) {
      // ignore, but log.
    }
    try {
      final PreparedOlapStatement statement = resultWrapper.getStatement();
      statement.close();
    } catch ( SQLException e ) {
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
      final List<Integer> cellKey = computeCellKey( rowIndex, columnIndex );
      final Cell cell = resultSet.getCell( cellKey );
      return new MDXMetaDataCellAttributes( EmptyDataAttributes.INSTANCE, cell );
    }

    final List<Integer> cellKey = computeCellKey( rowIndex, columnIndex );
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

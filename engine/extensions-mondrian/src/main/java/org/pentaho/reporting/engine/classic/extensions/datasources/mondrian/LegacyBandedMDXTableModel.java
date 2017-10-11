/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import mondrian.olap.Axis;
import mondrian.olap.Cell;
import mondrian.olap.Member;
import mondrian.olap.Position;
import mondrian.olap.Result;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.libraries.base.util.GenericObjectTable;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * This tablemodel performs some preprocessing to get multi-dimensional resultset (with row and column headers) into a
 * classical table-structure. The query must be a two-dimensional query or the whole process will break.
 * <p/>
 * This class exists for legacy reasons to provide existing reports the same view on MDX data as implemented in the
 * Pentaho-Platform and the Report-Designer. It can also be somewhat useful if you have a requirement to produce banded
 * reporting over a MDX data source.
 *
 * @author : Thomas Morgner
 */
public class LegacyBandedMDXTableModel extends AbstractTableModel
  implements CloseableTableModel, MetaTableModel {
  private static final int AXIS_COLUMN = 0;
  private static final int AXIS_ROW = 1;

  private Result resultSet;
  private GenericObjectTable rowHeaders;
  private GenericObjectTable columnHeaders;
  private String[] columnNames;
  private int columnCount;
  private int rowCount;
  private boolean emptyAxisCase;

  public LegacyBandedMDXTableModel( final Result resultSet, final int queryLimitValue )
    throws ReportDataFactoryException {
    this.resultSet = resultSet;
    final Axis[] axes = resultSet.getAxes();
    final int axisCount = axes.length;
    if ( axisCount > 2 ) {
      throw new ReportDataFactoryException( "Cannot handle results with more than two axes." );
    }
    this.rowHeaders = createRowHeaders();
    this.columnHeaders = createColumnHeaders();

    if ( rowHeaders.getRowCount() > 0 ) {
      columnCount = rowHeaders.getColumnCount() + columnHeaders.getColumnCount();
    } else {
      columnCount = columnHeaders.getColumnCount();
    }
    if ( columnCount == 0 ) {
      columnCount = 1;
      emptyAxisCase = true;
      columnNames = new String[] { "Measure" };
      rowCount = 1;
    } else {
      columnNames = new String[ columnCount ];
      for ( int i = 0; i < columnNames.length; i++ ) {
        columnNames[ i ] = calcColumnName( i );
      }

      if ( axisCount == 2 ) {
        rowCount = rowHeaders.getRowCount();
      } else {
        rowCount = Math.max( 1, rowHeaders.getRowCount() );
      }
    }

    if ( queryLimitValue > 0 ) {
      rowCount = Math.min( queryLimitValue, rowCount );
    }
  }

  public String getColumnName( final int column ) {
    return columnNames[ column ];
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

  public int getRowCount() {
    return rowCount;
  }

  public int getColumnCount() {
    return columnCount;
  }

  public void close() {
    resultSet.close();
  }

  private String calcColumnName( int columnNumber ) {
    if ( columnNumber < rowHeaders.getColumnCount() ) {
      return calcColumnNameFromRowHeader( columnNumber );
    } else {
      columnNumber -= rowHeaders.getColumnCount();
    }

    final StringBuffer buf = new StringBuffer( 32 );
    for ( int i = 0; i < columnHeaders.getRowCount(); i++ ) {
      if ( i > 0 ) {
        buf.append( '/' );
      }
      buf.append( String.valueOf( columnHeaders.getObject( i, columnNumber ) ) );
    }
    return buf.toString();
  }


  private String calcColumnNameFromRowHeader( final int columnNumber ) {
    // Flatten out the column headers into one column-name
    final Axis[] axes = this.resultSet.getAxes();
    final Axis axis = axes[ AXIS_ROW ];
    final List<Position> positions = axis.getPositions();
    final Position firstPosition = positions.get( 0 );
    if ( columnNumber < firstPosition.size() ) {
      final Member member = firstPosition.get( columnNumber );
      return member.getHierarchy().getName();
    } else {
      final Member member = firstPosition.get( firstPosition.size() - 1 );
      return member.getHierarchy().getName() + '{' + columnNumber + '}';
    }
  }

  private GenericObjectTable createColumnHeaders() {
    final Axis[] axes = this.resultSet.getAxes();
    if ( axes.length < 1 ) {
      return new GenericObjectTable();
    }
    final Axis axis = axes[ AXIS_COLUMN ];
    final List<Position> positions = axis.getPositions();
    final int colCount = positions.size();
    final GenericObjectTable result = new GenericObjectTable( 20, Math.max( 1, colCount ) );
    for ( int c = 0; c < colCount; c++ ) {
      final Position position = positions.get( c );
      Member member = null;
      final int rowCount = position.size();
      for ( int r = 0; r < rowCount; r++ ) {
        member = position.get( r );
        if ( member != null ) {
          result.setObject( r, c, member.getName() );
        }
      }

      if ( member != null ) {
        result.setObject( rowCount, c, member.getHierarchy().getName() );
      }
    }
    return result;
  }

  /**
   * @return
   */
  private GenericObjectTable createRowHeaders() {
    final Axis[] axes = this.resultSet.getAxes();
    if ( axes.length < 2 ) {
      return new GenericObjectTable();
    }
    final Axis axis = axes[ AXIS_ROW ];
    final List<Position> positions = axis.getPositions();
    final int rowCount = positions.size();
    final GenericObjectTable result = new GenericObjectTable( Math.max( 1, rowCount ), 5 );

    for ( int r = 0; r < rowCount; r++ ) {
      final Position position = positions.get( r );
      Member member = null;
      final int colCount = position.size();
      for ( int c = 0; c < colCount; c++ ) {
        member = position.get( c );
        result.setObject( r, c, member.getName() );
      }
      if ( member != null ) {
        result.setObject( r, colCount, member.getHierarchy().getName() );
      }
    }
    return result;
  }


  public Object getValueAt( final int rowIndex,
                            final int columnIndex ) {
    if ( resultSet == null ) {
      return null;
    }

    if ( columnIndex < rowHeaders.getColumnCount() ) {
      return rowHeaders.getObject( rowIndex, columnIndex );
    }

    if ( emptyAxisCase ) {
      final Cell cell = resultSet.getCell( new int[ 0 ] );
      if ( cell.isNull() ) {
        return null;
      }
      return cell.getValue();
    }

    if ( rowHeaders.getRowCount() == 0 ) {

      final int[] key = new int[ 1 ];
      key[ 0 ] = ( columnIndex );
      final Cell cell = resultSet.getCell( key );
      if ( cell.isNull() ) {
        return null;
      }
      return cell.getValue();
    }

    final int[] key = new int[ 2 ];
    key[ 0 ] = ( columnIndex - rowHeaders.getColumnCount() );
    key[ 1 ] = rowIndex;
    final Cell cell = resultSet.getCell( key );
    if ( cell.isNull() ) {
      return null;
    }
    return cell.getValue();
  }

  public DataAttributes getCellDataAttributes( final int rowIndex,
                                               final int columnIndex ) {
    // how does mondrian return formatting information?
    // I dont know yet ...
    if ( resultSet == null ) {
      return EmptyDataAttributes.INSTANCE;
    }

    if ( columnIndex < rowHeaders.getColumnCount() ) {
      return EmptyDataAttributes.INSTANCE;
    }

    if ( emptyAxisCase ) {
      final Cell cell = resultSet.getCell( new int[ 0 ] );
      return new MDXMetaDataCellAttributes( EmptyDataAttributes.INSTANCE, cell );
    }

    if ( rowHeaders.getRowCount() == 0 ) {

      final int[] key = new int[ 1 ];
      key[ 0 ] = ( columnIndex );
      final Cell cell = resultSet.getCell( key );
      return new MDXMetaDataCellAttributes( EmptyDataAttributes.INSTANCE, cell );
    }

    final int[] key = new int[ 2 ];
    key[ 0 ] = ( columnIndex - rowHeaders.getColumnCount() );
    key[ 1 ] = rowIndex;
    final Cell c = resultSet.getCell( key );
    return new MDXMetaDataCellAttributes( EmptyDataAttributes.INSTANCE, c );
  }

  public boolean isCellDataAttributesSupported() {
    return true;
  }

  public DataAttributes getColumnAttributes( final int column ) {
    return EmptyDataAttributes.INSTANCE;
  }

  public DataAttributes getTableAttributes() {
    return EmptyDataAttributes.INSTANCE;
  }
}

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

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;

import java.util.Arrays;
import java.util.HashSet;

/**
 * A datarow that acts as padding source. It overrides the columns from either report-data or expressions datarow with
 * values collected by the crosstab-specification. It also provides padding for the advance calls.
 * <p/>
 * There are three padding scenarios that must be covered by this data-row:
 * <ul>
 * <li>Leading columns are missing
 * <p>
 * The crosstab-specification's current cursor position is not at the computed position for the current column key (the
 * column values read from the actual data-row). Therefore the system has to insert fake columns until the cursor has
 * advanced to the current position. As we are effectively duplicating rows, this may lead to corrupted data if we
 * duplicate non-group-columns.
 * </p>
 * </li>
 * <li>Inner columns are missing
 * <p>
 * After an advance, the computed column-key position is greater than the current cursor position. Therefore the system
 * has to insert fake data until the positions match again.
 * </p>
 * </li>
 * <li>Trailing columns are missing
 * <p>
 * If the advance would trigger a group-break, check whether the current cursor position is already at the end of the
 * columns list. If not, stay on the current row and insert as many fake rows as needed.
 * </p>
 * </li>
 * </ul>
 * The last two cases may be consolidated into one case.
 *
 * @author Thomas Morgner
 */
public class PaddingController implements Cloneable {
  private static final Log logger = LogFactory.getLog( PaddingController.class );

  private int currentCursorPosition;
  private CrosstabSpecification crosstabSpecification;
  private Object[] key;
  private String[] columnNames;
  private Object[] rowKey;
  private String[] rowNames;
  private HashSet<String> knownNames;
  private HashSet<String> knownRowNames;

  public PaddingController( final PaddingController dataRow ) {
    this.currentCursorPosition = dataRow.currentCursorPosition;
    this.crosstabSpecification = dataRow.crosstabSpecification;
    this.key = dataRow.key.clone();
    this.rowKey = dataRow.rowKey.clone();
    this.columnNames = dataRow.columnNames;
    this.rowNames = dataRow.rowNames;
    this.knownNames = dataRow.knownNames;
    this.knownRowNames = dataRow.knownRowNames;
  }

  public PaddingController( final CrosstabSpecification crosstabSpecification ) {
    if ( crosstabSpecification == null ) {
      throw new NullPointerException();
    }
    this.crosstabSpecification = crosstabSpecification;
    this.columnNames = this.crosstabSpecification.getColumnDimensionNames();
    this.rowNames = this.crosstabSpecification.getRowDimensionNames();
    this.key = new Object[columnNames.length];
    this.rowKey = new Object[rowNames.length];
    this.currentCursorPosition = 0;

    this.knownNames = new HashSet<String>();
    this.knownNames.addAll( Arrays.asList( columnNames ) );
    this.knownNames.addAll( Arrays.asList( rowNames ) );

    this.knownRowNames = new HashSet<String>();
    this.knownRowNames.addAll( Arrays.asList( rowNames ) );
  }

  /**
   * Do we need a case1 padding?
   *
   * @param globalView
   * @return the number of rows needed for the pre-padding or zero if no pre-padding is required.
   */
  public int getPrePaddingRows( final DataRow globalView ) {
    if ( key.length == 0 ) {
      return 0;
    }

    for ( int i = 0; i < key.length; i++ ) {
      key[i] = globalView.get( columnNames[i] );
    }

    final int computedPosition = crosstabSpecification.indexOf( currentCursorPosition, key );
    if ( computedPosition < 0 ) {
      // not found, so all remaining columns must be padded. This will be handled by the post padding.
      // logger.debug("Pre: NF " + computedPosition + " CurrentPos: " + currentCursorPosition + " Key: " + printKey
      // (key));
      return 0;
    }
    // logger.debug("Pre:  F " + computedPosition + " CurrentPos: " + currentCursorPosition + " Key: " + printKey(key));
    // logger.debug("Pre: 2F " + (computedPosition - currentCursorPosition));
    return computedPosition - currentCursorPosition;
  }

  public int getCurrentCursorPosition() {
    return currentCursorPosition;
  }

  public int getCrosstabColumnCount() {
    return crosstabSpecification.size();
  }

  private String printKey( final Object[] data ) {
    final StringBuffer s = new StringBuffer( "{" );
    for ( int i = 0; i < data.length; i++ ) {
      if ( i > 0 ) {
        s.append( ',' );
      }
      s.append( data[i] );
    }
    return s + "}";
  }

  public PaddingController advance() {
    final PaddingController dataRow = new PaddingController( this );
    dataRow.currentCursorPosition += 1;
    return dataRow;
  }

  public void activate( final MasterDataRowChangeHandler dataRow ) {
    if ( key.length == 0 ) {
      return;
    }

    if ( currentCursorPosition >= crosstabSpecification.size() ) {
      throw new IllegalStateException();
    }

    final Object[] currentColumn = crosstabSpecification.getKeyAt( currentCursorPosition );
    final MasterDataRowChangeEvent event = dataRow.getReusableEvent();
    event.reuse( MasterDataRowChangeEvent.COLUMN_UPDATED, "", "" );
    for ( int i = 0; i < columnNames.length; i++ ) {
      event.setColumnName( columnNames[i] );
      event.setColumnValue( currentColumn[i] );
      event.setOptional( true );
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Replacing Column-dimension value: " + columnNames[i] + " = " + currentColumn[i] );
      }
      dataRow.dataRowChanged( event );
    }

  }

  public void refreshPaddedRow( final MasterDataRowChangeHandler dataRow, final ReportDataRow reportDataRow ) {
    if ( reportDataRow == null ) {
      return;
    }

    final MasterDataRowChangeEvent chEvent = dataRow.getReusableEvent();
    chEvent.reuse( MasterDataRowChangeEvent.COLUMN_UPDATED, "", "" );
    final int columnCount = reportDataRow.getColumnCount();
    for ( int i = 0; i < columnCount; i++ ) {
      final String name = reportDataRow.getColumnName( i );
      if ( knownRowNames.contains( name ) ) {
        chEvent.setColumnName( name );
        chEvent.setColumnValue( reportDataRow.get( i ) );
      } else if ( knownNames.contains( name ) ) {
        // should have been handled in the activate run ..
        continue;
      } else {
        chEvent.setColumnName( name );
        chEvent.setColumnValue( null );
      }
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Replacing padded value: " + name + " = " + chEvent.getColumnValue() );
      }
      dataRow.dataRowChanged( chEvent );
    }
  }

  public void refreshRow( final MasterDataRowChangeHandler dataRow, final ReportDataRow reportDataRow ) {
    if ( reportDataRow == null ) {
      return;
    }

    final MasterDataRowChangeEvent chEvent = dataRow.getReusableEvent();
    chEvent.reuse( MasterDataRowChangeEvent.COLUMN_UPDATED, "", "" );
    final int columnCount = reportDataRow.getColumnCount();
    for ( int i = 0; i < columnCount; i++ ) {
      final String name = reportDataRow.getColumnName( i );
      if ( knownRowNames.contains( name ) == false && knownNames.contains( name ) ) {
        // should have been handled in the activate run ..
        continue;
      }

      chEvent.setColumnName( name );
      chEvent.setColumnValue( reportDataRow.get( i ) );
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Refreshing value: " + name + " = " + chEvent.getColumnValue() );
      }
      dataRow.dataRowChanged( chEvent );
    }
  }

  public PaddingController resetRowCursor() {
    // logger.debug("################### Reset rowcounter");

    final PaddingController dataRow = new PaddingController( this );
    dataRow.currentCursorPosition = 0;
    return dataRow;
  }

  public Object[] createRowKey( final FastGlobalView globalView ) {
    final Object[] rowKey = new Object[rowNames.length];
    for ( int i = 0; i < rowKey.length; i++ ) {
      rowKey[i] = globalView.get( rowNames[i] );
    }
    return rowKey;
  }

  public Object[] createColumnKey( final FastGlobalView globalView ) {
    final Object[] colKey = new Object[columnNames.length];
    for ( int i = 0; i < colKey.length; i++ ) {
      colKey[i] = globalView.get( columnNames[i] );
    }
    return colKey;
  }

  public CrosstabSpecification getCrosstabSpecification() {
    return crosstabSpecification;
  }

  public PaddingController clone() {
    try {
      return (PaddingController) super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }
}

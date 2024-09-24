/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.parameters;

import javax.swing.table.TableModel;

public class DefaultParameterValues implements ParameterValues {
  private TableModel parent;
  private int keyColumnIdx;
  private int valueColumnIdx;
  private String keyColumn;
  private String valueColumn;

  public DefaultParameterValues( final TableModel parent, final String keyColumn, final String valueColumn ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }
    if ( keyColumn == null ) {
      throw new NullPointerException();
    }
    if ( valueColumn == null ) {
      throw new NullPointerException();
    }

    this.keyColumn = keyColumn;
    this.valueColumn = valueColumn;
    this.parent = parent;
    final int colCount = parent.getColumnCount();
    keyColumnIdx = -1;
    valueColumnIdx = -1;
    for ( int i = 0; i < colCount; i++ ) {
      final String colName = parent.getColumnName( i );
      if ( colName.equals( keyColumn ) ) {
        keyColumnIdx = i;
      }
      if ( colName.equals( valueColumn ) ) {
        valueColumnIdx = i;
      }
    }

    if ( keyColumnIdx == -1 ) {
      throw new IllegalArgumentException( "Unable to locate the key column in the dataset." );
    }
    if ( valueColumnIdx == -1 ) {
      throw new IllegalArgumentException( "Unable to locate the value column in the dataset." );
    }
  }

  public String getValueColumn() {
    return valueColumn;
  }

  public String getKeyColumn() {
    return keyColumn;
  }

  public int getRowCount() {
    return parent.getRowCount();
  }

  public Object getKeyValue( final int row ) {
    return parent.getValueAt( row, keyColumnIdx );
  }

  public Object getTextValue( final int row ) {
    return parent.getValueAt( row, valueColumnIdx );
  }
}

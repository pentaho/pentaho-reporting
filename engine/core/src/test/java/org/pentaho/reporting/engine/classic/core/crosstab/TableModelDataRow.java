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


package org.pentaho.reporting.engine.classic.core.crosstab;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

import javax.swing.table.TableModel;
import java.util.HashMap;

public class TableModelDataRow implements DataRow {
  private TableModel data;
  private int currentRow;
  private String[] columnNames;
  private HashMap<String, Integer> nameindex;

  public TableModelDataRow( final TableModel data ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    this.data = data;
    this.columnNames = new String[data.getColumnCount()];
    this.nameindex = new HashMap<String, Integer>();
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String name = data.getColumnName( i );
      columnNames[i] = name;
      nameindex.put( name, IntegerCache.getInteger( i ) );
    }

    this.currentRow = -1;
  }

  public int getCurrentRow() {
    return currentRow;
  }

  public void setCurrentRow( final int currentRow ) {
    if ( currentRow < -1 || currentRow >= data.getRowCount() ) {
      throw new IndexOutOfBoundsException();
    }

    this.currentRow = currentRow;
  }

  /**
   * Returns the value of the function, expression or column using its specific name. The given name is translated into
   * a valid column number and the the column is queried. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method <code>getValueAt(row, column)</code>
   * gets called.
   *
   * @param col
   *          the item index.
   * @return the value.
   */
  public Object get( final String col ) {
    if ( col == null ) {
      throw new NullPointerException();
    }

    if ( currentRow >= 0 ) {
      final Integer o = nameindex.get( col );
      if ( o == null ) {
        return null;
      }
      return data.getValueAt( currentRow, o.intValue() );
    }
    return null;
  }

  public String[] getColumnNames() {
    return columnNames.clone();
  }

  /**
   * Checks whether the value contained in the column has changed since the last advance-operation.
   *
   * @param name
   *          the name of the column.
   * @return true, if the value has changed, false otherwise.
   */
  public boolean isChanged( final String name ) {
    return false;
  }
}

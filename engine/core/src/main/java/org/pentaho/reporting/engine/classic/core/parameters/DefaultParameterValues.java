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

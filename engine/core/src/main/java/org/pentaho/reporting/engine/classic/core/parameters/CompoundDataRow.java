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

package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.libraries.base.util.LinkedMap;

import java.util.LinkedHashMap;

public class CompoundDataRow implements DataRow {
  private LinkedHashMap<String, Boolean> columnSources;
  private DataRow envDataRow;
  private DataRow dataRow;

  public CompoundDataRow( final DataRow envDataRow, final DataRow dataRow ) {
    this.envDataRow = envDataRow;
    this.dataRow = dataRow;

    columnSources = new LinkedHashMap<String, Boolean>();
    final String[] dataRowNames = dataRow.getColumnNames();
    for ( int i = 0; i < dataRowNames.length; i++ ) {
      final String dataRowName = dataRowNames[i];
      columnSources.put( dataRowName, Boolean.TRUE );
    }

    final String[] envRowNames = envDataRow.getColumnNames();
    for ( int i = 0; i < envRowNames.length; i++ ) {
      final String dataRowName = envRowNames[i];
      columnSources.put( dataRowName, Boolean.FALSE );
    }
  }

  public DataRow getEnvDataRow() {
    return envDataRow;
  }

  public DataRow getDataRow() {
    return dataRow;
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
    final Boolean b = columnSources.get( col );
    if ( Boolean.FALSE.equals( b ) ) {
      return envDataRow.get( col );
    }
    return dataRow.get( col );
  }

  /**
   * Returns the known column names, this data-row understands. The column names may change over time but do not change
   * while a event is processed by a function. The array returned is a copy of the internal data-storage and can be
   * safely modified.
   *
   * @return the column names as array.
   */
  public String[] getColumnNames() {
    final LinkedMap columnSources = new LinkedMap();
    final String[] dataRowNames = dataRow.getColumnNames();
    for ( int i = 0; i < dataRowNames.length; i++ ) {
      final String dataRowName = dataRowNames[i];
      columnSources.put( dataRowName, Boolean.TRUE );
    }

    final String[] envRowNames = envDataRow.getColumnNames();
    for ( int i = 0; i < envRowNames.length; i++ ) {
      final String dataRowName = envRowNames[i];
      columnSources.put( dataRowName, Boolean.FALSE );
    }
    return (String[]) columnSources.keys( new String[columnSources.size()] );
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

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "CompoundDataRow" );
    sb.append( "{envDataRow=" ).append( envDataRow );
    sb.append( ", dataRow=" ).append( dataRow );
    sb.append( '}' );
    return sb.toString();
  }
}

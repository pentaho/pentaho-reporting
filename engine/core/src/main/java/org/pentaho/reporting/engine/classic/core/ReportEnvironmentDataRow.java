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

package org.pentaho.reporting.engine.classic.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class ReportEnvironmentDataRow implements DataRow {
  private ReportEnvironment environment;
  private LinkedHashMap<String, String> columnMap;
  private String[] columnNames;

  public ReportEnvironmentDataRow( final ReportEnvironment environment ) {
    this( environment, DefaultReportEnvironmentMapping.INSTANCE );
  }

  public ReportEnvironmentDataRow( final ReportEnvironment environment,
      final ReportEnvironmentMapping reportEnvironmentMapping ) {
    final Map<String, String> envMapping = reportEnvironmentMapping.createEnvironmentMapping();
    this.columnMap = new LinkedHashMap<String, String>();
    for ( final Map.Entry<String, String> entry : envMapping.entrySet() ) {
      final String key = entry.getKey();
      final String value = entry.getValue();
      if ( StringUtils.isEmpty( key ) == false && StringUtils.isEmpty( value ) == false ) {
        this.columnMap.put( value, key );
      }
    }
    this.columnNames = columnMap.keySet().toArray( new String[columnMap.size()] );
    this.environment = environment;
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
    final String envName = columnMap.get( col );
    if ( envName == null ) {
      return null;
    }
    if ( envName.endsWith( "-array" ) ) { // NON-NLS
      final String name = envName.substring( 0, envName.length() - 6 );
      final Object s = environment.getEnvironmentProperty( name );
      if ( s == null ) {
        return new String[0];
      }

      final CSVTokenizer csvTokenizer = new CSVTokenizer( String.valueOf( s ), ",", "\"", false );
      final int length = csvTokenizer.countTokens();
      final String[] rolesArray = new String[length];
      for ( int i = 0; i < length; i += 1 ) {
        rolesArray[i] = csvTokenizer.nextToken();
      }
      return rolesArray;
    }
    return environment.getEnvironmentProperty( envName );
  }

  /**
   * Returns the known column names, this data-row understands. The column names may change over time but do not change
   * while a event is processed by a function. The array returned is a copy of the internal data-storage and can be
   * safely modified.
   *
   * @return the column names as array.
   */
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

  public boolean isArray( final String columnName ) {
    final String envName = columnMap.get( columnName );
    if ( envName == null ) {
      return false;
    }
    return ( envName.endsWith( "-array" ) ); // NON-NLS
  }

  public ReportEnvironment getEnvironment() {
    return environment;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "ReportEnvironmentDataRow" );
    sb.append( "{columnMap=" ).append( columnMap );
    sb.append( '}' );
    return sb.toString();
  }
}

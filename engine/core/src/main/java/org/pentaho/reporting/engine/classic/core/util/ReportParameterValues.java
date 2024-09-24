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

package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.engine.classic.core.DataRow;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class ReportParameterValues implements Cloneable, Serializable, DataRow {
  private LinkedHashMap<String, Object> linkedMap;

  public ReportParameterValues() {
    this.linkedMap = new LinkedHashMap<String, Object>();
  }

  public ReportParameterValues( final ReportParameterValues values ) {
    if ( values == null ) {
      throw new NullPointerException();
    }
    this.linkedMap = (LinkedHashMap<String, Object>) values.linkedMap.clone();
  }

  public Object put( final String col, final Object value ) {
    if ( col == null ) {
      throw new NullPointerException();
    }

    return linkedMap.put( col, value );
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
    return linkedMap.get( col );
  }

  public String[] getColumnNames() {
    return linkedMap.keySet().toArray( new String[linkedMap.size()] );
  }

  /**
   * Checks whether the value contained in the column has changed since the last advance-operation.
   *
   * @param name
   *          the name of the column.
   * @return always false, as parameters are considered static during the report processing.
   */
  public boolean isChanged( final String name ) {
    return false;
  }

  public Object clone() {
    try {
      final ReportParameterValues o = (ReportParameterValues) super.clone();
      o.linkedMap = (LinkedHashMap<String, Object>) linkedMap.clone();
      return o;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }

  public void putAll( final DataRow dataRow ) {
    if ( dataRow == null ) {
      throw new NullPointerException();
    }

    final String[] names = dataRow.getColumnNames();
    for ( int i = 0; i < names.length; i++ ) {
      final String name = names[i];
      put( name, dataRow.get( name ) );
    }
  }

  public void clear() {
    linkedMap.clear();
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "ReportParameterValues" );
    sb.append( "{linkedMap=" ).append( linkedMap );
    sb.append( '}' );
    return sb.toString();
  }

}

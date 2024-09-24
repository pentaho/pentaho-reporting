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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.util.PropertyLookupParser;

import java.util.ArrayList;

/**
 * Creation-Date: 16.04.2006, 20:30:42
 *
 * @author Thomas Morgner
 */
public class SQLParameterLookupParser extends PropertyLookupParser {
  private ArrayList<String> fields;
  private boolean expandArray;

  public SQLParameterLookupParser( final boolean expandArray ) {
    this.expandArray = expandArray;
    this.fields = new ArrayList<String>();
    setMarkerChar( '$' );
    setOpeningBraceChar( '{' );
    setClosingBraceChar( '}' );
  }

  protected String lookupVariable( final String name ) {
    fields.add( name );
    return "?"; //$NON-NLS-1$
  }

  public String[] getFields() {
    return fields.toArray( new String[fields.size()] );
  }

  protected void handleVariableLookup( final StringBuilder result, final DataRow parameters, final String columnName ) {
    if ( parameters == null ) {
      throw new NullPointerException( "Parameters must never be null" );
    }

    final String s = lookupVariable( columnName );

    // if we are building a parameter entry for an array of values
    // we need to build a list of ? followed by commas (,)
    // if the input array is {"One","Two","Three"} the replacement will be ?,?,?
    // so we have the possibility now of taking WHERE REGION IN ${REGION} and producing WHERE REGION IN ?,?,?
    // SimpleSQLReportDataFactory "knows" how to place each value in the array in each "? slot"
    final Object propertyValue = parameters.get( columnName );
    if ( expandArray && propertyValue instanceof Object[] ) {
      final Object[] parameterValues = (Object[]) propertyValue;
      for ( int j = 0; j < parameterValues.length - 1; j++ ) {
        result.append( s );
        result.append( ',' );
      }
      result.append( s );
    } else {
      result.append( s );
    }
  }

  public boolean isExpandArray() {
    return expandArray;
  }
}

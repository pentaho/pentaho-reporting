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

package org.pentaho.reporting.libraries.css.parser.stylehandler;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 26.11.2005, 19:16:43
 *
 * @author Thomas Morgner
 */
public abstract class ListOfValuesReadHandler implements CSSValueReadHandler {
  private int maxCount;
  private boolean distinctValues;

  protected ListOfValuesReadHandler() {
    maxCount = Integer.MAX_VALUE;
    distinctValues = false;
  }

  protected ListOfValuesReadHandler( int maxCount, final boolean distinct ) {
    this.maxCount = maxCount;
    this.distinctValues = distinct;
  }

  public boolean isDistinctValues() {
    return distinctValues;
  }

  public int getMaxCount() {
    return maxCount;
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    final ArrayList list = new ArrayList();
    int count = 0;
    while ( value != null && count < maxCount ) {
      final CSSValue pvalue = parseValue( value );
      if ( pvalue == null ) {
        return null;
      }
      if ( distinctValues == false ||
        list.contains( pvalue ) == false ) {
        list.add( pvalue );
      }
      value = CSSValueFactory.parseComma( value );
      count += 1;
    }

    return new CSSValueList( list );
  }

  protected abstract CSSValue parseValue( final LexicalUnit value );
}

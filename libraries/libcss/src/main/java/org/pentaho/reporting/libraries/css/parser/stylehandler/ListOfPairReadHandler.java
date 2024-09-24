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
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 26.11.2005, 19:23:09
 *
 * @author Thomas Morgner
 */
public abstract class ListOfPairReadHandler implements CSSValueReadHandler {
  protected ListOfPairReadHandler() {
  }


  public synchronized CSSValue createValue( StyleKey name, LexicalUnit value ) {
    ArrayList values = new ArrayList();

    while ( value != null ) {
      final CSSValue firstPosition = parseFirstPosition( value );
      if ( firstPosition == null ) {
        return null;
      }

      value = value.getNextLexicalUnit();
      final CSSValue secondPosition = parseSecondPosition( value, firstPosition );
      if ( secondPosition == null ) {
        return null;
      }

      addToResultList( values, firstPosition, secondPosition );
      value = CSSValueFactory.parseComma( value );
    }

    return new CSSValueList( values );
  }

  protected void addToResultList( ArrayList values,
                                  CSSValue firstPosition,
                                  CSSValue secondPosition ) {
    values.add( new CSSValuePair( firstPosition, secondPosition ) );
  }

  protected abstract CSSValue parseFirstPosition( final LexicalUnit value );

  protected abstract CSSValue parseSecondPosition( final LexicalUnit value,
                                                   final CSSValue first );

}

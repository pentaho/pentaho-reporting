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

package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.TextAutoSpace;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.stylehandler.ListOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 02.12.2005, 19:47:48
 *
 * @author Thomas Morgner
 */
public class TextAutoSpaceReadHandler extends ListOfConstantsReadHandler {
  public TextAutoSpaceReadHandler() {
    super( 4, false, true );
    addValue( TextAutoSpace.IDEOGRAPH_ALPHA );
    addValue( TextAutoSpace.IDEOGRAPH_NUMERIC );
    addValue( TextAutoSpace.IDEOGRAPH_PARENTHESIS );
    addValue( TextAutoSpace.IDEOGRAPH_SPACE );
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( value.getStringValue().equalsIgnoreCase( "none" ) ) {
        return new CSSValueList( new CSSValue[] { TextAutoSpace.NONE } );
      }
    }
    return super.createValue( name, value );
  }
}

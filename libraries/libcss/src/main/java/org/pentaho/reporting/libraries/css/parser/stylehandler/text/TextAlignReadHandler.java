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

import org.pentaho.reporting.libraries.css.keys.text.TextAlign;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 19:47:58
 *
 * @author Thomas Morgner
 */
public class TextAlignReadHandler extends OneOfConstantsReadHandler {
  public TextAlignReadHandler() {
    super( true );
    addValue( TextAlign.CENTER );
    addValue( TextAlign.END );
    addValue( TextAlign.JUSTIFY );
    addValue( TextAlign.LEFT );
    addValue( TextAlign.RIGHT );
    addValue( TextAlign.START );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE ) {
      return new CSSStringValue( CSSStringType.STRING, value.getStringValue() );
    }

    return super.lookupValue( value );
  }
}

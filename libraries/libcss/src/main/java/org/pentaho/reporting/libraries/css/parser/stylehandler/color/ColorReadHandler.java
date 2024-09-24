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

package org.pentaho.reporting.libraries.css.parser.stylehandler.color;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.util.ColorUtil;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 26.11.2005, 20:22:46
 *
 * @author Thomas Morgner
 */
public class ColorReadHandler implements CSSValueReadHandler {

  public ColorReadHandler() {
  }

  public CSSValue createValue( final StyleKey name, final LexicalUnit value ) {
    return createColorValue( value );
  }

  public static CSSValue createColorValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_FUNCTION ||
      value.getLexicalUnitType() == LexicalUnit.SAC_RGBCOLOR ) {
      return CSSValueFactory.parseFunction( value );
    } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      // a constant color name
      return ColorUtil.parseIdentColor( value.getStringValue() );
    }
    return null;
  }

}

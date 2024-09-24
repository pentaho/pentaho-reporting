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

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 08.12.2005, 17:10:11
 *
 * @author Thomas Morgner
 */
public class TextScriptReadHandler implements CSSValueReadHandler {
  public TextScriptReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }
    if ( value.getStringValue().equalsIgnoreCase( "auto" ) ) {
      return CSSAutoValue.getInstance();
    }
    return new CSSConstant( value.getStringValue() );
  }
}

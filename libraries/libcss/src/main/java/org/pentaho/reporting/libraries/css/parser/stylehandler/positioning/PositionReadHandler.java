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

package org.pentaho.reporting.libraries.css.parser.stylehandler.positioning;

import org.pentaho.reporting.libraries.css.keys.positioning.Position;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 21.12.2005, 18:32:53
 *
 * @author Thomas Morgner
 */
public class PositionReadHandler extends OneOfConstantsReadHandler {
  public PositionReadHandler() {
    super( false );
    addValue( Position.ABSOLUTE );
    addValue( Position.FIXED );
    addValue( Position.RELATIVE );
    addValue( Position.STATIC );
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    final CSSValue result = super.createValue( name, value );
    if ( result != null ) {
      return result;
    }

    // maybe the position is a 'running(..)' function.
    if ( CSSValueFactory.isFunctionValue( value ) ) {
      final CSSFunctionValue cssFunctionValue = CSSValueFactory.parseFunction( value );
      if ( cssFunctionValue != null ) {
        // we are a bit restrictive for now ..
        if ( "running".equals( cssFunctionValue.getFunctionName() ) ) {
          return cssFunctionValue;
        }
      }
    }
    return null;
  }
}

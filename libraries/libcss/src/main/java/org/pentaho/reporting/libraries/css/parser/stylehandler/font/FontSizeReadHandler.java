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

package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontSizeConstant;
import org.pentaho.reporting.libraries.css.keys.font.RelativeFontSize;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 16:51:33
 *
 * @author Thomas Morgner
 */
public class FontSizeReadHandler extends OneOfConstantsReadHandler {
  public FontSizeReadHandler() {
    this( false );
  }

  protected FontSizeReadHandler( final boolean autoAllowed ) {
    super( autoAllowed );
    addValue( FontSizeConstant.LARGE );
    addValue( FontSizeConstant.MEDIUM );
    addValue( FontSizeConstant.SMALL );
    addValue( FontSizeConstant.X_LARGE );
    addValue( FontSizeConstant.XX_LARGE );
    addValue( FontSizeConstant.X_SMALL );
    addValue( FontSizeConstant.XX_SMALL );
    addValue( RelativeFontSize.LARGER );
    addValue( RelativeFontSize.SMALLER );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
      return CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, value.getFloatValue() );
    }
    CSSValue constant = super.lookupValue( value );
    if ( constant != null ) {
      return constant;
    }
    return CSSValueFactory.createLengthValue( value );
  }
}

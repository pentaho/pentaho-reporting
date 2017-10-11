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

package org.pentaho.reporting.libraries.css.parser.stylehandler.page;

import org.pentaho.reporting.libraries.css.keys.page.PageSize;
import org.pentaho.reporting.libraries.css.keys.page.PageSizeFactory;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.stylehandler.AbstractWidthReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.w3c.css.sac.LexicalUnit;

import java.awt.print.PageFormat;

/**
 * Creation-Date: 30.11.2005, 18:04:27
 *
 * @author Thomas Morgner
 */
public class SizeReadHandler extends AbstractWidthReadHandler
  implements CSSValueReadHandler {
  public SizeReadHandler() {
    super( true, false );
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      String ident = value.getStringValue();
      if ( ident.equalsIgnoreCase( "auto" ) ) {
        return CSSAutoValue.getInstance();
      }
      final PageSize ps = PageSizeFactory.getInstance().getPageSizeByName( ident );
      if ( ps == null ) {
        return null;
      }

      value = value.getNextLexicalUnit();
      int pageOrientation = PageFormat.PORTRAIT;
      if ( value != null ) {
        if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
          return null;
        }

        if ( value.getStringValue().equalsIgnoreCase( "landscape" ) ) {
          pageOrientation = PageFormat.LANDSCAPE;
        } else if ( value.getStringValue().equalsIgnoreCase( "reverse-landscape" ) ) {
          pageOrientation = PageFormat.REVERSE_LANDSCAPE;
        } else if ( value.getStringValue().equalsIgnoreCase( "portrait" ) ) {
          pageOrientation = PageFormat.PORTRAIT;
        } else {
          return null;
        }
      }

      if ( pageOrientation == PageFormat.LANDSCAPE ||
        pageOrientation == PageFormat.REVERSE_LANDSCAPE ) {
        return new CSSValuePair( CSSNumericValue.createPtValue( ps.getHeight() ),
          CSSNumericValue.createPtValue( ps.getWidth() ) );
      } else {
        return new CSSValuePair( CSSNumericValue.createPtValue( ps.getWidth() ),
          CSSNumericValue.createPtValue( ps.getHeight() ) );
      }
    } else {
      final CSSNumericValue horizontalWidth = (CSSNumericValue) parseWidth( value );
      if ( horizontalWidth == null ) {
        return null;
      }

      value = value.getNextLexicalUnit();

      final CSSNumericValue verticalWidth;
      if ( value == null ) {
        verticalWidth = horizontalWidth;
      } else {
        verticalWidth = (CSSNumericValue) parseWidth( value );
        if ( verticalWidth == null ) {
          return null;
        }
      }

      return new CSSValuePair( horizontalWidth, verticalWidth );
    }
  }

}

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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

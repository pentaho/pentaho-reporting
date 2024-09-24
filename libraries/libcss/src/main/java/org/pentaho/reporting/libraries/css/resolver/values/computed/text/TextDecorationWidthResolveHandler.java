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

package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.keys.border.BorderWidth;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.keys.text.TextDecorationWidth;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class TextDecorationWidthResolveHandler extends ConstantsResolveHandler {
  public TextDecorationWidthResolveHandler() {
    addValue( BorderWidth.THIN, CSSNumericValue.createValue( CSSNumericType.PT, 0.5 ) );
    addValue( BorderWidth.MEDIUM, CSSNumericValue.createValue( CSSNumericType.PT, 1 ) );
    addValue( BorderWidth.THICK, CSSNumericValue.createValue( CSSNumericType.PT, 1.5 ) );
    addValue( TextDecorationWidth.DASH, CSSNumericValue.createValue( CSSNumericType.PT, 0.75 ) );
    addValue( TextDecorationWidth.BOLD, CSSNumericValue.createValue( CSSNumericType.PT, 1.25 ) );
    setFallback( CSSNumericValue.ZERO_LENGTH );
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] {
      FontStyleKeys.FONT_SIZE
    };
  }

  protected CSSValue resolveValue( final DocumentContext process,
                                   final LayoutElement currentNode,
                                   final StyleKey key ) {
    final CSSValue value = currentNode.getLayoutStyle().getValue( key );
    if ( value instanceof CSSConstant ) {
      return super.resolveValue( process, currentNode, key );
    }
    return value;
  }
}

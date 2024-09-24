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

package org.pentaho.reporting.libraries.css.resolver.values.computed.border;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.border.BorderStyle;
import org.pentaho.reporting.libraries.css.keys.border.BorderStyleKeys;
import org.pentaho.reporting.libraries.css.keys.border.BorderWidth;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

import java.util.HashMap;

/**
 * Creation-Date: 11.12.2005, 22:20:16
 *
 * @author Thomas Morgner
 */
public class BorderWidthResolveHandler extends ConstantsResolveHandler {
  private HashMap keyMapping;

  public BorderWidthResolveHandler() {
    keyMapping = new HashMap();
    keyMapping.put( BorderStyleKeys.BORDER_TOP_WIDTH, BorderStyleKeys.BORDER_TOP_STYLE );
    keyMapping.put( BorderStyleKeys.BORDER_LEFT_WIDTH, BorderStyleKeys.BORDER_LEFT_STYLE );
    keyMapping.put( BorderStyleKeys.BORDER_BOTTOM_WIDTH, BorderStyleKeys.BORDER_BOTTOM_STYLE );
    keyMapping.put( BorderStyleKeys.BORDER_RIGHT_WIDTH, BorderStyleKeys.BORDER_RIGHT_STYLE );
    keyMapping.put( BorderStyleKeys.BORDER_BREAK_WIDTH, BorderStyleKeys.BORDER_BREAK_STYLE );

    addValue( BorderWidth.THIN, CSSNumericValue.createValue( CSSNumericType.PT, 1 ) );
    addValue( BorderWidth.MEDIUM, CSSNumericValue.createValue( CSSNumericType.PT, 3 ) );
    addValue( BorderWidth.THICK, CSSNumericValue.createValue( CSSNumericType.PT, 5 ) );
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
      BorderStyleKeys.BORDER_TOP_STYLE,
      BorderStyleKeys.BORDER_LEFT_STYLE,
      BorderStyleKeys.BORDER_BOTTOM_STYLE,
      BorderStyleKeys.BORDER_RIGHT_STYLE,
      BorderStyleKeys.BORDER_BREAK_STYLE
    };
  }

  protected CSSValue resolveValue( final DocumentContext process,
                                   final LayoutElement currentNode,
                                   final StyleKey key ) {
    final StyleKey borderStyleKey = (StyleKey) keyMapping.get( key );
    if ( borderStyleKey == null ) {
      // invalid
      throw new IllegalArgumentException( "This is not a valid key: " + key );
    }

    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue borderStyle = layoutContext.getValue( borderStyleKey );
    if ( BorderStyle.NONE.equals( borderStyle ) ) {
      return CSSNumericValue.ZERO_LENGTH;
    }

    final CSSValue value = layoutContext.getValue( key );
    if ( value instanceof CSSConstant ) {
      return super.resolveValue( process, currentNode, key );
    }
    return value;
  }
}

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.css.resolver.values.percentages.fonts;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

/**
 * Creation-Date: 18.12.2005, 19:46:43
 *
 * @author Thomas Morgner
 */
public class FontSizeAdjustResolveHandler implements ResolveHandler {
  public FontSizeAdjustResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] {
      FontStyleKeys.FONT_SIZE,
      FontStyleKeys.FONT_FAMILY,
      FontStyleKeys.FONT_EFFECT,
      FontStyleKeys.FONT_SMOOTH,
      FontStyleKeys.FONT_STRETCH,
      FontStyleKeys.FONT_VARIANT,
      FontStyleKeys.FONT_WEIGHT,
    };
  }

  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( key );
    if ( value instanceof CSSNumericValue == false ) {
      return; // do nothing
    }
    final CSSNumericValue nval = (CSSNumericValue) value;
    if ( CSSNumericType.NUMBER.equals( nval.getType() ) == false ) {
      return; // syntax error, do nothing
    }
    final LayoutElement parent = currentNode.getParentLayoutElement();
    if ( parent == null ) {
      return; // no parent to resolve against ...
    }

    final double adjustFactor = nval.getValue();
    final FontMetrics fontMetrics = process.getOutputMetaData().getFontMetrics( layoutContext );
    if ( fontMetrics == null ) {
      return; // no font metrics means no valid font...
    }

    final double actualFontXHeight = FontStrictGeomUtility.toExternalValue( fontMetrics.getXHeight() );
    final double fontSize = fontMetrics.getAscent();
    final double aspectRatio = actualFontXHeight / fontSize;
    final double result = ( fontSize * ( adjustFactor / aspectRatio ) );

    layoutContext.setValue( FontStyleKeys.FONT_SIZE, CSSNumericValue.createPtValue( result ) );
  }
}

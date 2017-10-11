/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.values.percentages.text;

import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.css.StyleSheetUtility;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

/**
 * Creation-Date: 21.12.2005, 15:12:04
 *
 * @author Thomas Morgner
 */
public class WordSpacingResolveHandler implements ResolveHandler {
  public WordSpacingResolveHandler() {
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

  /**
   * Resolves a single property.
   *
   * @param currentNode
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    // Percentages get resolved against the width of a standard space (0x20)
    // character.
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final FontMetrics fm = process.getOutputMetaData().getFontMetrics( layoutContext );
    if ( fm == null ) {
      final CSSValue value = layoutContext.getValue( FontStyleKeys.FONT_FAMILY );
      DebugLog.log( "FontFamily is " + value + " but has not been set?" + currentNode );
      return;
    }
    final double width = FontStrictGeomUtility.toExternalValue( fm.getCharWidth( 0x20 ) );
    final CSSNumericValue percentageBase =
      CSSNumericValue.createValue( CSSNumericType.PT, width );
    final CSSNumericValue min = StyleSheetUtility.convertLength
      ( resolveValue( layoutContext, TextStyleKeys.X_MIN_WORD_SPACING ), percentageBase, currentNode );
    final CSSNumericValue max = StyleSheetUtility.convertLength
      ( resolveValue( layoutContext, TextStyleKeys.X_MAX_WORD_SPACING ), percentageBase, currentNode );
    final CSSValue opt = StyleSheetUtility.convertLength
      ( resolveValue( layoutContext, TextStyleKeys.X_OPTIMUM_WORD_SPACING ), percentageBase, currentNode );

    layoutContext.setValue( TextStyleKeys.X_MIN_WORD_SPACING, min );
    layoutContext.setValue( TextStyleKeys.X_MAX_WORD_SPACING, max );
    layoutContext.setValue( TextStyleKeys.X_OPTIMUM_WORD_SPACING, opt );
  }

  private CSSNumericValue resolveValue( final LayoutStyle style, final StyleKey key ) {
    final CSSValue value = style.getValue( key );
    if ( value instanceof CSSNumericValue == false ) {
      // this also covers the valid 'normal' property.
      // it simply means, dont add extra space to the already existing spaces
      return CSSNumericValue.ZERO_LENGTH;
    }

    return (CSSNumericValue) value;
  }
}

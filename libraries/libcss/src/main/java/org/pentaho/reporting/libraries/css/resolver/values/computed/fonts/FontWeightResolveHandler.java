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


package org.pentaho.reporting.libraries.css.resolver.values.computed.fonts;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.keys.font.FontWeight;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 18.12.2005, 20:33:42
 *
 * @author Thomas Morgner
 */
public class FontWeightResolveHandler implements ResolveHandler {
  private static final StyleKey[] EMPTY_KEYS = new StyleKey[ 0 ];

  public FontWeightResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return
   */
  public StyleKey[] getRequiredStyles() {
    return EMPTY_KEYS;
  }

  /**
   * Resolves a single property.
   *
   * @param currentNode
   * @param style
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( key );
    final int fontWeight;
    if ( FontWeight.BOLD.equals( value ) ) {
      // ask the parent ...
      fontWeight = 700;
    } else if ( FontWeight.NORMAL.equals( value ) ) {
      // ask the parent ...
      fontWeight = 400;
    } else if ( FontWeight.BOLDER.equals( value ) ) {
      final int parentFontWeight = queryParent( currentNode.getParentLayoutElement() );
      fontWeight = Math.max( 900, parentFontWeight + 100 );
    } else if ( FontWeight.LIGHTER.equals( value ) ) {
      final int parentFontWeight = queryParent( currentNode.getParentLayoutElement() );
      fontWeight = Math.min( 100, parentFontWeight - 100 );
    } else if ( value instanceof CSSNumericValue ) {
      final CSSNumericValue nval = (CSSNumericValue) value;
      if ( CSSNumericType.NUMBER.equals( nval.getType() ) == false ) {
        // preserve the parent's weight...
        fontWeight = queryParent( currentNode.getParentLayoutElement() );
      } else {
        fontWeight = (int) nval.getValue();
      }
    } else {
      fontWeight = queryParent( currentNode.getParentLayoutElement() );
    }

    layoutContext
      .setValue( FontStyleKeys.FONT_WEIGHT, CSSNumericValue.createValue( CSSNumericType.NUMBER, fontWeight ) );
  }

  private int queryParent( final LayoutElement parent ) {
    if ( parent == null ) {
      return 400; // Normal
    }

    final CSSValue value = parent.getLayoutStyle().getValue( FontStyleKeys.FONT_WEIGHT );
    if ( value instanceof CSSNumericValue == false ) {
      throw new IllegalStateException( "Parent was not resolved correctly" );
    }
    CSSNumericValue nval = (CSSNumericValue) value;
    if ( CSSNumericType.NUMBER.equals( nval.getType() ) == false ) {
      throw new IllegalStateException( "Parent was not resolved correctly" );
    }
    return (int) nval.getValue();
  }
}

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


package org.pentaho.reporting.libraries.css.resolver.values.computed.fonts;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.font.FontStretch;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 18.12.2005, 20:33:42
 *
 * @author Thomas Morgner
 */
public class FontStretchResolveHandler extends ConstantsResolveHandler {
  public FontStretchResolveHandler() {
    addNormalizeValue( FontStretch.CONDENSED );
    addNormalizeValue( FontStretch.EXPANDED );
    addNormalizeValue( FontStretch.EXTRA_CONDENSED );
    addNormalizeValue( FontStretch.EXTRA_EXPANDED );
    addNormalizeValue( FontStretch.NORMAL );
    addNormalizeValue( FontStretch.SEMI_CONDENSED );
    addNormalizeValue( FontStretch.SEMI_EXPANDED );
    addNormalizeValue( FontStretch.ULTRA_CONDENSED );
    addNormalizeValue( FontStretch.ULTRA_EXPANDED );
  }

  /**
   * Resolves a single property.
   *
   * @param currentNode
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( key );
    final CSSConstant result;
    if ( FontStretch.WIDER.equals( value ) ) {
      // ask the parent ...
      final CSSConstant parentStretch = queryParent( currentNode.getParentLayoutElement() );
      result = FontStretch.getByOrder( FontStretch.getOrder( parentStretch ) + 1 );
    } else if ( FontStretch.NARROWER.equals( value ) ) {
      // ask the parent ...
      final CSSConstant parentStretch = queryParent( currentNode.getParentLayoutElement() );
      result = FontStretch.getByOrder( FontStretch.getOrder( parentStretch ) - 1 );
    } else if ( value instanceof CSSConstant ) {
      final CSSConstant stretch = (CSSConstant) lookupValue( (CSSConstant) value );
      if ( stretch != null ) {
        result = stretch;
      } else {
        result = FontStretch.NORMAL;
      }
    } else {
      result = FontStretch.NORMAL;
    }
    layoutContext.setValue( key, result );
  }

  private CSSConstant queryParent( final LayoutElement parent ) {
    if ( parent == null ) {
      return FontStretch.NORMAL;
    }
    final CSSValue parentValue =
      parent.getLayoutStyle().getValue( FontStyleKeys.FONT_STRETCH );
    if ( parentValue == null ) {
      //      Log.error("Assertation failed: Parent stretch is null");
      return FontStretch.NORMAL;
    }
    // normalize ..
    return FontStretch.getByOrder( FontStretch.getOrder( parentValue ) );
  }
}

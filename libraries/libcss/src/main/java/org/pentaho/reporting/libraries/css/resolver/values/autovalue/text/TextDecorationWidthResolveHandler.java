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


package org.pentaho.reporting.libraries.css.resolver.values.autovalue.text;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;

public class TextDecorationWidthResolveHandler implements ResolveHandler {
  private static final StyleKey[] EMPTY_KEYS = new StyleKey[ 0 ];

  public TextDecorationWidthResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return EMPTY_KEYS;
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
    layoutContext.setValue( key, CSSNumericValue.createValue( CSSNumericType.PT, 1 ) );
  }
}

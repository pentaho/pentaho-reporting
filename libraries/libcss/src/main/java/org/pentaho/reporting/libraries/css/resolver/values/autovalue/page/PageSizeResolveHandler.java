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


package org.pentaho.reporting.libraries.css.resolver.values.autovalue.page;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.keys.page.PageSize;
import org.pentaho.reporting.libraries.css.keys.page.PageStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;

/**
 * Creation-Date: 16.06.2006, 13:52:17
 *
 * @author Thomas Morgner
 */
public class PageSizeResolveHandler implements ResolveHandler {
  private static final StyleKey[] EMPTY_KEYS = new StyleKey[ 0 ];

  public PageSizeResolveHandler() {
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
   * @param style
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final PageSize ps = process.getOutputMetaData().getDefaultPageSize();
    final CSSValue page =
      new CSSValuePair( CSSNumericValue.createPtValue( ps.getWidth() ),
        CSSNumericValue.createPtValue( ps.getHeight() ) );
    currentNode.getLayoutStyle().setValue( PageStyleKeys.SIZE, page );
  }
}

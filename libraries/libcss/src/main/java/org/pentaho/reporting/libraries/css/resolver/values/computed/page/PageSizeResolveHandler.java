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


package org.pentaho.reporting.libraries.css.resolver.values.computed.page;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.page.PageSize;
import org.pentaho.reporting.libraries.css.keys.page.PageSizeFactory;
import org.pentaho.reporting.libraries.css.keys.page.PageStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;

/**
 * Creation-Date: 16.06.2006, 13:56:31
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
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( PageStyleKeys.SIZE );

    String name = null;
    if ( value instanceof CSSStringValue ) {
      final CSSStringValue sval = (CSSStringValue) value;
      name = sval.getValue();
    } else if ( value instanceof CSSConstant ) {
      name = value.toString();
    }

    PageSize ps = null;
    if ( name != null ) {
      ps = PageSizeFactory.getInstance().getPageSizeByName( name );
    }

    if ( ps == null ) {
      ps = process.getOutputMetaData().getDefaultPageSize();
    }
    // if it is stll null, then the output target is not valid.
    // We will crash in that case ..
    final CSSValue page =
      new CSSValuePair( CSSNumericValue.createPtValue( ps.getWidth() ),
        CSSNumericValue.createPtValue( ps.getHeight() ) );
    layoutContext.setValue( PageStyleKeys.SIZE, page );
  }
}

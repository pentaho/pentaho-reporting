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


package org.pentaho.reporting.libraries.css.resolver.values.computed.content;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.internal.InternalStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ContentSpecification;
import org.pentaho.reporting.libraries.css.resolver.values.QuotesPair;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;

import java.util.ArrayList;

public class QuotesResolveHandler extends ConstantsResolveHandler {
  public QuotesResolveHandler() {

  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[ 0 ];
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
    final CSSValue rawValue = layoutContext.getValue( key );
    if ( rawValue instanceof CSSValueList == false ) {
      return;
    }
    final ArrayList quotes = new ArrayList();
    final CSSValueList list = (CSSValueList) rawValue;
    final int length = ( list.getLength() % 2 );
    for ( int i = 0; i < length; i++ ) {
      final CSSValue openValue = list.getItem( i * 2 );
      final CSSValue closeValue = list.getItem( i * 2 + 1 );

      if ( openValue instanceof CSSStringValue == false ) {
        continue;
      }
      if ( closeValue instanceof CSSStringValue == false ) {
        continue;
      }

      final CSSStringValue openQuote = (CSSStringValue) openValue;
      final CSSStringValue closeQuote = (CSSStringValue) closeValue;
      quotes.add( new QuotesPair( openQuote.getValue(), closeQuote.getValue() ) );
    }

    if ( quotes.isEmpty() ) {
      return;
    }

    final QuotesPair[] quotesArray =
      (QuotesPair[]) quotes.toArray( new QuotesPair[ quotes.size() ] );
    final ContentSpecification contentSpecification =
      (ContentSpecification) layoutContext.getValue( InternalStyleKeys.INTERNAL_CONTENT );
    contentSpecification.setQuotes( quotesArray );
  }
}

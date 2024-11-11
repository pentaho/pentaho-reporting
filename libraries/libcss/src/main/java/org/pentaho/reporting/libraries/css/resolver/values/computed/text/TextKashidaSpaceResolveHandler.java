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


package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 21.12.2005, 15:00:43
 *
 * @author Thomas Morgner
 */
public class TextKashidaSpaceResolveHandler implements ResolveHandler {
  public TextKashidaSpaceResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return
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
    final CSSValue value = layoutContext.getValue( key );
    if ( ( value instanceof CSSNumericValue ) == false ) {
      return;
    }
    final CSSNumericValue nval = (CSSNumericValue) value;
    if ( CSSNumericType.PERCENTAGE.equals( nval.getType() ) == false ) {
      return;
    }
    double percentage = nval.getValue();
    if ( percentage < 0 ) {
      percentage = 0;
    }
    if ( percentage > 100 ) {
      percentage = 100;
    }
    layoutContext.setValue( TextStyleKeys.TEXT_KASHIDA_SPACE,
      CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, percentage ) );
  }
}

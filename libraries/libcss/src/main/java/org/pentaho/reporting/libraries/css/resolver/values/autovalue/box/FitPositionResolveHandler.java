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


package org.pentaho.reporting.libraries.css.resolver.values.autovalue.box;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.text.BlockProgression;
import org.pentaho.reporting.libraries.css.keys.text.Direction;
import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;


public class FitPositionResolveHandler implements ResolveHandler {
  private static final CSSNumericValue LEFT_TOP = CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, 0 );
  private static final CSSNumericValue RIGHT_BOTTOM = CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, 100 );

  public FitPositionResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] {
      TextStyleKeys.BLOCK_PROGRESSION,
      TextStyleKeys.DIRECTION
    };
  }

  /**
   * Resolves a single property.
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final boolean rightToLeft = Direction.RTL.equals
      ( layoutContext.getValue( TextStyleKeys.DIRECTION ) );
    final CSSValue blockProgression = layoutContext.getValue( TextStyleKeys.BLOCK_PROGRESSION );
    // this might be invalid ...
    if ( BlockProgression.TB.equals( blockProgression ) ) {
      if ( rightToLeft ) {
        layoutContext.setValue( key, new CSSValuePair( RIGHT_BOTTOM, LEFT_TOP ) );
      } else {
        layoutContext.setValue( key, new CSSValuePair( LEFT_TOP, LEFT_TOP ) );
      }
    } else if ( BlockProgression.RL.equals( blockProgression ) ) {
      if ( rightToLeft ) {
        layoutContext.setValue( key, new CSSValuePair( LEFT_TOP, LEFT_TOP ) );
      } else {
        layoutContext.setValue( key, new CSSValuePair( RIGHT_BOTTOM, LEFT_TOP ) );
      }
    } else if ( BlockProgression.LR.equals( blockProgression ) ) {
      if ( rightToLeft ) {
        layoutContext.setValue( key, new CSSValuePair( RIGHT_BOTTOM, RIGHT_BOTTOM ) );
      } else {
        layoutContext.setValue( key, new CSSValuePair( LEFT_TOP, LEFT_TOP ) );
      }
    }
  }
}

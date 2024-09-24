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

package org.pentaho.reporting.libraries.css.resolver.values.computed.position;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.box.BoxStyleKeys;
import org.pentaho.reporting.libraries.css.keys.box.DisplayModel;
import org.pentaho.reporting.libraries.css.keys.box.DisplayRole;
import org.pentaho.reporting.libraries.css.keys.box.Floating;
import org.pentaho.reporting.libraries.css.keys.positioning.Position;
import org.pentaho.reporting.libraries.css.keys.positioning.PositioningStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;


public class PositionResolveHandler extends ConstantsResolveHandler {
  public PositionResolveHandler() {
    addNormalizeValue( Position.ABSOLUTE );
    addNormalizeValue( Position.FIXED );
    addNormalizeValue( Position.RELATIVE );
    addNormalizeValue( Position.STATIC );
    setFallback( Position.STATIC );
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] {
      BoxStyleKeys.DISPLAY_MODEL,
    };
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
    final CSSValue displayModel = layoutContext.getValue( BoxStyleKeys.DISPLAY_MODEL );
    if ( DisplayRole.NONE.equals( displayModel ) ) {
      // skip ... the element will not be displayed ...
      layoutContext.setValue( PositioningStyleKeys.POSITION, Position.STATIC );
      return;
    }

    final CSSValue rawValue = layoutContext.getValue( key );
    if ( rawValue instanceof CSSFunctionValue ) {
      // OK; check for pending ..
      final CSSFunctionValue function = (CSSFunctionValue) rawValue;
      if ( "running".equals( function.getFunctionName() ) ) {
        // The element will be inside a block-context (same behaviour as
        // for floats)
        layoutContext.setValue( BoxStyleKeys.DISPLAY_MODEL, DisplayModel.BLOCK_INSIDE );
        layoutContext.setValue( BoxStyleKeys.DISPLAY_ROLE, DisplayRole.BLOCK );
        return;
      }
      layoutContext.setValue( PositioningStyleKeys.POSITION, Position.STATIC );
      return;
    }

    final CSSConstant value = (CSSConstant) resolveValue( process, currentNode, key );
    layoutContext.setValue( PositioningStyleKeys.POSITION, value );
    if ( Position.ABSOLUTE.equals( value ) ||
      Position.FIXED.equals( value ) ) {
      // http://www.w3.org/TR/REC-CSS2/visuren.html#propdef-float
      // this is specified in 9.7: Relationships between 'display',
      // 'position', and 'float':

      // Quote: Otherwise, 'position' has the value 'absolute' or 'fixed',
      // 'display' is set to 'block' and 'float' is set to 'none'. The position
      // of the box will be determined by the 'top', 'right', 'bottom' and
      // 'left' properties and the box's containing block.
      layoutContext.setValue( BoxStyleKeys.DISPLAY_MODEL, DisplayModel.BLOCK_INSIDE );
      layoutContext.setValue( BoxStyleKeys.DISPLAY_ROLE, DisplayRole.BLOCK );
      layoutContext.setValue( BoxStyleKeys.FLOAT, Floating.NONE );
    }
  }
}

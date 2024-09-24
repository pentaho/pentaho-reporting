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

package org.pentaho.reporting.libraries.css.resolver.values.computed.box;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.box.BoxStyleKeys;
import org.pentaho.reporting.libraries.css.keys.box.DisplayModel;
import org.pentaho.reporting.libraries.css.keys.box.DisplayRole;
import org.pentaho.reporting.libraries.css.keys.box.Floating;
import org.pentaho.reporting.libraries.css.keys.positioning.PositioningStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class FloatResolveHandler extends ConstantsResolveHandler {
  public FloatResolveHandler() {
    addNormalizeValue( Floating.BOTTOM );
    addNormalizeValue( Floating.LEFT );
    addNormalizeValue( Floating.END );
    addNormalizeValue( Floating.INSIDE );
    addNormalizeValue( Floating.IN_COLUMN );
    addNormalizeValue( Floating.MID_COLUMN );
    addNormalizeValue( Floating.NONE );
    addNormalizeValue( Floating.OUTSIDE );
    addNormalizeValue( Floating.RIGHT );
    addNormalizeValue( Floating.START );
    addNormalizeValue( Floating.TOP );
    setFallback( Floating.NONE );
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] {
      BoxStyleKeys.DISPLAY_ROLE,
      PositioningStyleKeys.POSITION
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
    final CSSValue displayRole = layoutContext.getValue( BoxStyleKeys.DISPLAY_ROLE );
    final CSSValue floating;
    if ( DisplayRole.NONE.equals( displayRole ) ) {
      floating = Floating.NONE;
    } else {
      floating = resolveValue( process, currentNode, key );
    }

    if ( Floating.NONE.equals( floating ) == false ) {
      //  Otherwise, if 'float' has a value other than 'none', 'display'
      // is set to 'block' and the box is floated.
      layoutContext.setValue( BoxStyleKeys.DISPLAY_MODEL, DisplayModel.BLOCK_INSIDE );
      layoutContext.setValue( BoxStyleKeys.DISPLAY_ROLE, DisplayRole.BLOCK );
    }

    layoutContext.setValue( key, Floating.NONE );
  }
}

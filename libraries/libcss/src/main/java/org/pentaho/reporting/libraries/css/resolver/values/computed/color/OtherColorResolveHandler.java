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

package org.pentaho.reporting.libraries.css.resolver.values.computed.color;

import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.color.ColorStyleKeys;
import org.pentaho.reporting.libraries.css.keys.color.HtmlColors;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.values.CSSColorValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 11.12.2005, 23:28:29
 *
 * @author Thomas Morgner
 */
public class OtherColorResolveHandler extends ColorResolveHandler {
  public OtherColorResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] { ColorStyleKeys.COLOR };
  }

  protected CSSColorValue getCurrentColor( final LayoutElement currentNode ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( ColorStyleKeys.COLOR );
    if ( value instanceof CSSColorValue ) {
      return (CSSColorValue) value;
    } else {
      return HtmlColors.BLACK;
    }
  }
}

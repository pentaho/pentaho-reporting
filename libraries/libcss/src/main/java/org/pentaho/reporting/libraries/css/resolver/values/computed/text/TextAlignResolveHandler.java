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

package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.text.TextAlign;
import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 21.12.2005, 14:17:42
 *
 * @author Thomas Morgner
 */
public class TextAlignResolveHandler extends ConstantsResolveHandler {
  public TextAlignResolveHandler() {
    addNormalizeValue( TextAlign.CENTER );
    addNormalizeValue( TextAlign.END );
    addNormalizeValue( TextAlign.JUSTIFY );
    addNormalizeValue( TextAlign.LEFT );
    addNormalizeValue( TextAlign.RIGHT );
    addNormalizeValue( TextAlign.START );
    setFallback( TextAlign.START );
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
    if ( value instanceof CSSStringValue ) {
      // this is a sub-string alignment.
      return;
    }

    final CSSConstant alignValue =
      (CSSConstant) resolveValue( process, currentNode, key );
    layoutContext.setValue( TextStyleKeys.TEXT_ALIGN, alignValue );
  }
}

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


package org.pentaho.reporting.libraries.css.resolver.values.computed.hyperlinks;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.hyperlinks.TargetName;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 21.12.2005, 11:32:33
 *
 * @author Thomas Morgner
 */
public class TargetNameResolveHandler extends ConstantsResolveHandler {
  public TargetNameResolveHandler() {
    addNormalizeValue( TargetName.CURRENT );
    addNormalizeValue( TargetName.MODAL );
    addNormalizeValue( TargetName.NEW );
    addNormalizeValue( TargetName.PARENT );
    addNormalizeValue( TargetName.ROOT );
    setFallback( TargetName.CURRENT );
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
    if ( value instanceof CSSConstant ) {
      super.resolve( process, currentNode, key );
    } else if ( value instanceof CSSStringValue ) {
      // do nothing, accept it as is...
    } else {
      layoutContext.setValue( key, getFallback() );
    }
  }

}

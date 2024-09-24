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

package org.pentaho.reporting.libraries.css.resolver.values.computed;

import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;


/**
 * Creation-Date: 14.12.2005, 23:08:14
 *
 * @author Thomas Morgner
 */
public abstract class ListOfConstantsResolveHandler extends ConstantsResolveHandler {
  public ListOfConstantsResolveHandler() {
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
    final CSSValue value = currentNode.getLayoutStyle().getValue( key );
    if ( value == null ) {
      return;
    }
    if ( value instanceof CSSValueList == false ) {
      return;
    }

    final CSSValueList list = (CSSValueList) value;
    final int length = list.getLength();
    if ( length == 0 ) {
      return;
    }

    for ( int i = 0; i < length; i++ ) {
      final CSSValue item = list.getItem( i );
      if ( item instanceof CSSConstant == false ) {
        resolveInvalidItem( process, currentNode, key, i );
      } else {
        resolveItem( process, currentNode, key, i, (CSSConstant) item );
      }
    }
  }

  protected void resolveInvalidItem( final DocumentContext process,
                                     final LayoutElement currentNode,
                                     final StyleKey key,
                                     final int index ) {
    DebugLog.log( "Encountered invalid item in Style " + key + " at index " + index );
  }

  protected abstract boolean resolveItem( final DocumentContext process,
                                          LayoutElement currentNode,
                                          StyleKey key,
                                          int index,
                                          CSSConstant item );
}

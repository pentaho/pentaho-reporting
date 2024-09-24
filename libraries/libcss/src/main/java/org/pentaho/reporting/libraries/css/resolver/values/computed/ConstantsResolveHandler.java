/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.values.computed;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;

import java.util.HashMap;


/**
 * Creation-Date: 11.12.2005, 23:15:57
 *
 * @author Thomas Morgner
 */
public abstract class ConstantsResolveHandler implements ResolveHandler {
  private static final StyleKey[] EMPTY_STYLE_KEYS = new StyleKey[ 0 ];

  private HashMap constants;
  private CSSValue fallback;

  protected ConstantsResolveHandler() {
    constants = new HashMap();
  }

  public CSSValue getFallback() {
    return fallback;
  }

  protected void setFallback( final CSSValue fallback ) {
    this.fallback = fallback;
  }

  protected CSSValue lookupValue( final CSSConstant value ) {
    return (CSSValue) constants.get( value );
  }

  protected void addValue( final CSSConstant constant, final CSSValue value ) {
    constants.put( constant, value );
  }

  protected void addNormalizeValue( final CSSConstant constant ) {
    constants.put( constant, constant );
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return EMPTY_STYLE_KEYS;
  }

  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {

    final CSSValue value = resolveValue( process, currentNode, key );
    if ( value != null ) {
      currentNode.getLayoutStyle().setValue( key, value );
    }
  }

  protected CSSValue resolveValue( final DocumentContext process,
                                   final LayoutElement currentNode,
                                   final StyleKey key ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( key );
    if ( value instanceof CSSConstant == false ) {
      final CSSValue fallback = getFallback();
      if ( fallback != null ) {
        return fallback;
      }
      return null;
    }

    final CSSConstant constant = (CSSConstant) value;
    final CSSValue resolvedValue = lookupValue( constant );
    if ( resolvedValue != null ) {
      //      layoutContext.setValue(key, resolvedValue);
      return resolvedValue;
    }

    final CSSValue fallback = getFallback();
    if ( fallback != null ) {
      //      layoutContext.setValue(key, fallback);
      return fallback;
    }

    return null;
  }
}

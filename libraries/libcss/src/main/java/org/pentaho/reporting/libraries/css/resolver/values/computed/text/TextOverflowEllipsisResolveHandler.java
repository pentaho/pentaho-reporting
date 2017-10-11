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

package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;

/**
 * Creation-Date: 21.12.2005, 16:48:23
 *
 * @author Thomas Morgner
 */
public class TextOverflowEllipsisResolveHandler implements ResolveHandler {
  public TextOverflowEllipsisResolveHandler() {
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
    CSSStringValue lineEllipsis = null;
    CSSStringValue blockEllipsis = null;
    if ( value instanceof CSSValueList ) {
      final CSSValueList vlist = (CSSValueList) value;
      if ( vlist.getLength() == 2 ) {
        lineEllipsis = filterString( vlist.getItem( 0 ) );
        blockEllipsis = filterString( vlist.getItem( 1 ) );
      } else if ( vlist.getLength() == 1 ) {
        lineEllipsis = filterString( vlist.getItem( 0 ) );
        blockEllipsis = filterString( vlist.getItem( 0 ) );
      }
    }
    if ( lineEllipsis == null ) {
      lineEllipsis = new CSSStringValue( CSSStringType.STRING, ".." );
    }
    if ( blockEllipsis == null ) {
      blockEllipsis = new CSSStringValue( CSSStringType.STRING, ".." );
    }

    layoutContext.setValue( TextStyleKeys.X_BLOCK_TEXT_OVERFLOW_ELLIPSIS, blockEllipsis );
    layoutContext.setValue( TextStyleKeys.X_LINE_TEXT_OVERFLOW_ELLIPSIS, lineEllipsis );
  }

  private CSSStringValue filterString( final CSSValue value ) {
    if ( value instanceof CSSStringValue == false ) {
      return null;
    }
    return (CSSStringValue) value;
  }
}

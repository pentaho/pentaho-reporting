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

package org.pentaho.reporting.libraries.css.resolver.values.computed.fonts;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.font.FontStretch;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 18.12.2005, 20:33:42
 *
 * @author Thomas Morgner
 */
public class FontStretchResolveHandler extends ConstantsResolveHandler {
  public FontStretchResolveHandler() {
    addNormalizeValue( FontStretch.CONDENSED );
    addNormalizeValue( FontStretch.EXPANDED );
    addNormalizeValue( FontStretch.EXTRA_CONDENSED );
    addNormalizeValue( FontStretch.EXTRA_EXPANDED );
    addNormalizeValue( FontStretch.NORMAL );
    addNormalizeValue( FontStretch.SEMI_CONDENSED );
    addNormalizeValue( FontStretch.SEMI_EXPANDED );
    addNormalizeValue( FontStretch.ULTRA_CONDENSED );
    addNormalizeValue( FontStretch.ULTRA_EXPANDED );
  }

  /**
   * Resolves a single property.
   *
   * @param currentNode
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( key );
    final CSSConstant result;
    if ( FontStretch.WIDER.equals( value ) ) {
      // ask the parent ...
      final CSSConstant parentStretch = queryParent( currentNode.getParentLayoutElement() );
      result = FontStretch.getByOrder( FontStretch.getOrder( parentStretch ) + 1 );
    } else if ( FontStretch.NARROWER.equals( value ) ) {
      // ask the parent ...
      final CSSConstant parentStretch = queryParent( currentNode.getParentLayoutElement() );
      result = FontStretch.getByOrder( FontStretch.getOrder( parentStretch ) - 1 );
    } else if ( value instanceof CSSConstant ) {
      final CSSConstant stretch = (CSSConstant) lookupValue( (CSSConstant) value );
      if ( stretch != null ) {
        result = stretch;
      } else {
        result = FontStretch.NORMAL;
      }
    } else {
      result = FontStretch.NORMAL;
    }
    layoutContext.setValue( key, result );
  }

  private CSSConstant queryParent( final LayoutElement parent ) {
    if ( parent == null ) {
      return FontStretch.NORMAL;
    }
    final CSSValue parentValue =
      parent.getLayoutStyle().getValue( FontStyleKeys.FONT_STRETCH );
    if ( parentValue == null ) {
      //      Log.error("Assertation failed: Parent stretch is null");
      return FontStretch.NORMAL;
    }
    // normalize ..
    return FontStretch.getByOrder( FontStretch.getOrder( parentValue ) );
  }
}

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
import org.pentaho.reporting.libraries.css.keys.border.BorderWidth;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.keys.text.TextDecorationWidth;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class TextDecorationWidthResolveHandler extends ConstantsResolveHandler {
  public TextDecorationWidthResolveHandler() {
    addValue( BorderWidth.THIN, CSSNumericValue.createValue( CSSNumericType.PT, 0.5 ) );
    addValue( BorderWidth.MEDIUM, CSSNumericValue.createValue( CSSNumericType.PT, 1 ) );
    addValue( BorderWidth.THICK, CSSNumericValue.createValue( CSSNumericType.PT, 1.5 ) );
    addValue( TextDecorationWidth.DASH, CSSNumericValue.createValue( CSSNumericType.PT, 0.75 ) );
    addValue( TextDecorationWidth.BOLD, CSSNumericValue.createValue( CSSNumericType.PT, 1.25 ) );
    setFallback( CSSNumericValue.ZERO_LENGTH );
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] {
      FontStyleKeys.FONT_SIZE
    };
  }

  protected CSSValue resolveValue( final DocumentContext process,
                                   final LayoutElement currentNode,
                                   final StyleKey key ) {
    final CSSValue value = currentNode.getLayoutStyle().getValue( key );
    if ( value instanceof CSSConstant ) {
      return super.resolveValue( process, currentNode, key );
    }
    return value;
  }
}

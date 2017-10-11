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

package org.pentaho.reporting.libraries.css.resolver.values.computed.color;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.color.CSSSystemColors;
import org.pentaho.reporting.libraries.css.keys.color.ColorStyleKeys;
import org.pentaho.reporting.libraries.css.keys.color.HtmlColors;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.function.FunctionFactory;
import org.pentaho.reporting.libraries.css.resolver.function.StyleValueFunction;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.util.ColorUtil;
import org.pentaho.reporting.libraries.css.values.CSSColorValue;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;


/**
 * Creation-Date: 11.12.2005, 23:28:29
 *
 * @author Thomas Morgner
 */
public class ColorResolveHandler implements ResolveHandler {
  private static final StyleKey[] EMPTY_KEYS = new StyleKey[ 0 ];

  public ColorResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return
   */
  public StyleKey[] getRequiredStyles() {
    return EMPTY_KEYS;
  }

  /**
   * Resolves a single property.
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final LayoutStyle style = currentNode.getLayoutStyle();
    CSSValue value = style.getValue( key );

    if ( value instanceof CSSColorValue ) {
      return;
    }


    // it might as well be a RGB- or HSL- function.

    if ( value instanceof CSSFunctionValue ) {
      final CSSFunctionValue functionValue = (CSSFunctionValue) value;
      final StyleValueFunction function = FunctionFactory.getInstance().getStyleFunction
        ( functionValue.getFunctionName() );
      if ( function == null ) {
        value = HtmlColors.BLACK;
      } else {
        try {
          value = function.evaluate( process, currentNode, functionValue );
        } catch ( FunctionEvaluationException e ) {
          value = HtmlColors.BLACK;
        }
      }

      if ( value instanceof CSSColorValue ) {
        style.setValue( key, value );
        return;
      }
    }


    if ( value instanceof CSSConstant == false ) {
      style.setValue( key, HtmlColors.BLACK );
      return;
    }
    if ( CSSSystemColors.CURRENT_COLOR.equals( value ) ) {
      style.setValue( key, getCurrentColor( currentNode ) );
      return;
    }

    final CSSValue c = ColorUtil.parseIdentColor( value.getCSSText() );
    if ( c != null ) {
      style.setValue( key, c );
    } else {
      style.setValue( key, HtmlColors.BLACK );
    }
  }

  protected CSSColorValue getCurrentColor( final LayoutElement currentNode ) {
    final LayoutElement parent = currentNode.getParentLayoutElement();
    if ( parent != null ) {
      final LayoutStyle layoutContext = parent.getLayoutStyle();
      final CSSValue value = layoutContext.getValue( ColorStyleKeys.COLOR );
      if ( value instanceof CSSColorValue ) {
        return (CSSColorValue) value;
      }
    }
    return ( HtmlColors.BLACK );
  }

}

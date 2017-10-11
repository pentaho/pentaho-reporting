/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.function.values;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.function.FunctionUtilities;
import org.pentaho.reporting.libraries.css.resolver.function.StyleValueFunction;
import org.pentaho.reporting.libraries.css.util.ColorUtil;
import org.pentaho.reporting.libraries.css.values.CSSColorValue;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSRawValue;
import org.pentaho.reporting.libraries.css.values.CSSResourceValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import java.awt.*;
import java.net.URL;

public class AttrValueFunction implements StyleValueFunction {
  public AttrValueFunction() {
  }

  public boolean isAutoResolveable() {
    return true;
  }

  public CSSValue evaluate( final DocumentContext layoutProcess,
                            final LayoutElement element,
                            final CSSFunctionValue function )
    throws FunctionEvaluationException {
    final CSSValue[] params = function.getParameters();
    if ( params.length < 2 ) {
      throw new FunctionEvaluationException
        ( "The parsed attr() function needs at least two parameters." );
    }
    final String namespace = FunctionUtilities.resolveString
      ( layoutProcess, element, params[ 0 ] );
    final String name = FunctionUtilities.resolveString
      ( layoutProcess, element, params[ 1 ] );

    String type = null;
    if ( params.length >= 3 ) {
      type = FunctionUtilities.resolveString( layoutProcess, element, params[ 2 ] );
    }

    if ( namespace == null || "".equals( namespace ) || "*".equals( namespace ) ) {
      final Object value = element.getAttribute( element.getNamespace(), name );
      return convertValue( layoutProcess, value, type );

    } else {
      // thats easy.
      final Object value = element.getAttribute( namespace, name );
      return convertValue( layoutProcess, value, type );
    }
  }


  private CSSValue convertValue( final DocumentContext layoutProcess,
                                 final Object value,
                                 final String type )
    throws FunctionEvaluationException {
    if ( value instanceof CSSValue ) {
      throw new FunctionEvaluationException();
    }

    if ( value instanceof String ) {
      final String strVal = (String) value;
      if ( "length".equals( type ) ) {
        return FunctionUtilities.parseNumberValue( strVal );
      } else if ( "url".equals( type ) ) {
        return FunctionUtilities.loadResource( layoutProcess, strVal );
      } else if ( "color".equals( type ) ) {
        final CSSValue colorValue = ColorUtil.parseColor( strVal );
        if ( colorValue == null ) {
          throw new FunctionEvaluationException();
        }
        return colorValue;
      } else {
        // auto-mode. We check for URLs, as this is required for images
        return FunctionUtilities.parseValue( layoutProcess, strVal );
      }
    } else if ( value instanceof URL ) {
      return FunctionUtilities.loadResource( layoutProcess, value );
    } else if ( value instanceof Resource ) {
      return new CSSResourceValue( (Resource) value );
    } else if ( value instanceof ResourceKey ) {
      return FunctionUtilities.loadResource( layoutProcess, value );
    } else if ( value instanceof Number ) {
      return FunctionUtilities.parseNumberValue( value.toString(), type );
    } else if ( value instanceof Color ) {
      final Color color = (Color) value;
      return new CSSColorValue
        ( color.getRed(), color.getGreen(),
          color.getBlue(), color.getAlpha() );
    } else {
      return new CSSRawValue( value );
    }
  }

}

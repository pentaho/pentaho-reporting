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


package org.pentaho.reporting.libraries.css.resolver.function.content;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.function.ContentFunction;
import org.pentaho.reporting.libraries.css.resolver.function.FunctionUtilities;
import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.statics.ExternalContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.statics.ResourceContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.statics.StaticTextToken;
import org.pentaho.reporting.libraries.css.util.ColorUtil;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSResourceValue;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import java.net.URL;
import java.util.Date;

/**
 * Creation-Date: 15.04.2006, 18:33:56
 *
 * @author Thomas Morgner
 */
public class AttrValueFunction implements ContentFunction {
  public AttrValueFunction() {
  }

  public ContentToken evaluate( final DocumentContext layoutProcess,
                                final LayoutElement element,
                                final CSSFunctionValue function )
    throws FunctionEvaluationException {
    final CSSValue[] params = function.getParameters();
    if ( params.length < 2 ) {
      throw new FunctionEvaluationException
        ( "The parsed attr() function needs at least two parameters." );
    }
    final String namespace = FunctionUtilities.resolveString( layoutProcess, element, params[ 0 ] );
    final String name = FunctionUtilities.resolveString( layoutProcess, element, params[ 1 ] );

    String type = null;
    if ( params.length >= 3 ) {
      type = FunctionUtilities.resolveString( layoutProcess, element, params[ 2 ] );
    }

    if ( namespace == null || "".equals( namespace ) ) {
      final Object value = element.getAttribute( element.getNamespace(), name );
      return convertValue( layoutProcess, value, type );

    } else if ( "*".equals( namespace ) ) {
      // this is a lot of work. Query all attributes in all namespaces...
      final Object value = element.getAttribute( "*", name );
      return convertValue( layoutProcess, value, type );
    } else {
      // thats easy.
      final Object value = element.getAttribute( namespace, name );
      return convertValue( layoutProcess, value, type );
    }
  }


  private ContentToken convertValue( final DocumentContext layoutProcess,
                                     final Object value,
                                     final String type )
    throws FunctionEvaluationException {
    if ( value instanceof CSSValue ) {
      throw new FunctionEvaluationException();
    }

    if ( value instanceof String ) {
      final String strVal = (String) value;
      if ( "length".equals( type ) ) {
        final CSSNumericValue cssNumericValue = FunctionUtilities.parseNumberValue( strVal );
        return new StaticTextToken( cssNumericValue.getCSSText() );
      } else if ( "url".equals( type ) ) {
        final CSSResourceValue cssResourceValue = FunctionUtilities.loadResource( layoutProcess, strVal );
        final Resource resource = cssResourceValue.getValue();
        return new ResourceContentToken( resource );
      } else if ( "color".equals( type ) ) {
        final CSSValue colorValue = ColorUtil.parseColor( strVal );
        if ( colorValue == null ) {
          throw new FunctionEvaluationException();
        }
        return new StaticTextToken( colorValue.getCSSText() );
      } else {
        // auto-mode. We check for URLs, as this is required for images
        final CSSValue cssValue = FunctionUtilities.parseValue( layoutProcess, strVal );
        if ( cssValue instanceof CSSResourceValue ) {
          final CSSResourceValue cssResourceValue = (CSSResourceValue) cssValue;
          final Resource resource = cssResourceValue.getValue();
          return new ResourceContentToken( resource );
        } else if ( cssValue instanceof CSSStringValue ) {
          final CSSStringValue sval = (CSSStringValue) cssValue;
          return new StaticTextToken( sval.getValue() );
        } else {
          return new StaticTextToken( cssValue.getCSSText() );
        }
      }
    } else if ( value instanceof URL ) {
      final CSSResourceValue cssResourceValue = FunctionUtilities.loadResource( layoutProcess, value );
      final Resource resource = cssResourceValue.getValue();
      return new ResourceContentToken( resource );
    } else if ( value instanceof Resource ) {
      return new ResourceContentToken( (Resource) value );
    } else if ( value instanceof ResourceKey ) {
      final CSSResourceValue cssResourceValue = FunctionUtilities.loadResource( layoutProcess, value );
      final Resource resource = cssResourceValue.getValue();
      return new ResourceContentToken( resource );
    } else if ( value instanceof Date ) {
      return new StaticTextToken( String.valueOf( value ) );
    } else if ( value instanceof Number ) {
      return new StaticTextToken( String.valueOf( value ) );
    } else {
      return new ExternalContentToken( value );
    }
  }

}

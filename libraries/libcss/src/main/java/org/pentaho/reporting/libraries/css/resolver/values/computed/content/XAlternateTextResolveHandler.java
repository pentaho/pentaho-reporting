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

package org.pentaho.reporting.libraries.css.resolver.values.computed.content;

import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.content.ContentStyleKeys;
import org.pentaho.reporting.libraries.css.keys.content.ContentValues;
import org.pentaho.reporting.libraries.css.keys.internal.InternalStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.function.FunctionFactory;
import org.pentaho.reporting.libraries.css.resolver.function.StyleValueFunction;
import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.statics.ExternalContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.statics.ResourceContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.statics.StaticTextToken;
import org.pentaho.reporting.libraries.css.resolver.values.ContentSpecification;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSAttrFunction;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSRawValue;
import org.pentaho.reporting.libraries.css.values.CSSResourceValue;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;
import java.util.ArrayList;

public class XAlternateTextResolveHandler implements ResolveHandler {
  private static final ContentToken[] DEFAULT_CONTENT = new ContentToken[ 0 ];

  public XAlternateTextResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] {
      ContentStyleKeys.COUNTER_RESET,
      ContentStyleKeys.COUNTER_INCREMENT,
      ContentStyleKeys.QUOTES,
      ContentStyleKeys.STRING_DEFINE
    };
  }

  /**
   * Resolves a String. As the string may contain the 'contents' property, it is not resolvable here. The
   * ContentNormalizer needs to handle this property instead. (But this code prepares everything ..)
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement element,
                       final StyleKey key ) {
    final LayoutStyle layoutContext = element.getLayoutStyle();
    final ContentSpecification contentSpecification =
      (ContentSpecification) layoutContext.getValue( InternalStyleKeys.INTERNAL_CONTENT );
    final CSSValue value = layoutContext.getValue( key );
    if ( value instanceof CSSConstant ) {
      if ( ContentValues.NONE.equals( value ) ) {
        contentSpecification.setStrings( XAlternateTextResolveHandler.DEFAULT_CONTENT );
        return;
      }
    }

    contentSpecification.setStrings( XAlternateTextResolveHandler.DEFAULT_CONTENT );
    if ( value instanceof CSSAttrFunction ) {
      final ContentToken token =
        evaluateFunction( (CSSFunctionValue) value, process, element );
      if ( token == null ) {
        return;
      }
      contentSpecification.setStrings( new ContentToken[] { token } );
    }

    if ( value instanceof CSSValueList == false ) {
      return; // cant handle that one
    }

    final ArrayList tokens = new ArrayList();
    final CSSValueList list = (CSSValueList) value;
    final int size = list.getLength();
    for ( int i = 0; i < size; i++ ) {
      final CSSValueList sequence = (CSSValueList) list.getItem( i );
      for ( int j = 0; j < sequence.getLength(); j++ ) {
        final CSSValue content = sequence.getItem( j );
        final ContentToken token = createToken( process, element, content );
        if ( token == null ) {
          // ok, a failure. Skip to the next content spec ...
          tokens.clear();
          break;
        }
        tokens.add( token );
      }
    }

    final ContentToken[] contents = (ContentToken[]) tokens.toArray
      ( new ContentToken[ tokens.size() ] );
    contentSpecification.setStrings( contents );
  }


  private ContentToken createToken( final DocumentContext process,
                                    final LayoutElement element,
                                    final CSSValue content ) {
    if ( content instanceof CSSStringValue ) {
      final CSSStringValue sval = (CSSStringValue) content;
      if ( CSSStringType.STRING.equals( sval.getType() ) ) {
        return new StaticTextToken( sval.getValue() );
      } else {
        // this is an external URL, so try to load it.
        final CSSFunctionValue function = new CSSFunctionValue
          ( "url", new CSSValue[] { sval } );
        return evaluateFunction( function, process, element );
      }
    }

    if ( content instanceof CSSFunctionValue ) {
      return evaluateFunction( (CSSFunctionValue) content, process, element );
    }

    if ( content instanceof CSSConstant ) {
      if ( ContentValues.DOCUMENT_URL.equals( content ) ) {
        final ResourceKey baseKey = process.getContextKey();
        final ResourceManager resourceManager = process.getResourceManager();
        final URL url = resourceManager.toURL( baseKey );
        if ( url != null ) {
          return new StaticTextToken( url.toExternalForm() );
        }
        return null;
      }
    }
    return null;
  }

  private ContentToken evaluateFunction( final CSSFunctionValue function,
                                         final DocumentContext process,
                                         final LayoutElement element ) {
    final StyleValueFunction styleFunction =
      FunctionFactory.getInstance().getStyleFunction( function.getFunctionName() );
    try {
      final CSSValue value = styleFunction.evaluate( process, element, function );
      if ( value instanceof CSSResourceValue ) {
        final CSSResourceValue refValue = (CSSResourceValue) value;
        return new ResourceContentToken( refValue.getValue() );
      } else if ( value instanceof CSSStringValue ) {
        final CSSStringValue strval = (CSSStringValue) value;
        return new StaticTextToken( strval.getValue() );
      } else if ( value instanceof CSSRawValue ) {
        final CSSRawValue rawValue = (CSSRawValue) value;
        return new ExternalContentToken( rawValue.getValue() );
      }
      return new StaticTextToken( value.getCSSText() );
    } catch ( FunctionEvaluationException e ) {
      DebugLog.log( "Evaluation failed " + e );
      return null;
    }
  }
}

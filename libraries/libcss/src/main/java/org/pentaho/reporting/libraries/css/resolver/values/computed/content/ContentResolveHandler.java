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
import org.pentaho.reporting.libraries.css.counter.CounterStyle;
import org.pentaho.reporting.libraries.css.counter.CounterStyleFactory;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.content.ContentStyleKeys;
import org.pentaho.reporting.libraries.css.keys.content.ContentValues;
import org.pentaho.reporting.libraries.css.keys.internal.InternalStyleKeys;
import org.pentaho.reporting.libraries.css.keys.list.ListStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.function.ContentFunction;
import org.pentaho.reporting.libraries.css.resolver.function.FunctionFactory;
import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.CloseQuoteToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.ContentsToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.CounterToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.OpenQuoteToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.statics.StaticTextToken;
import org.pentaho.reporting.libraries.css.resolver.values.ContentSpecification;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSAttrFunction;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ContentResolveHandler implements ResolveHandler {
  private static final ContentToken[] DEFAULT_CONTENT = new ContentToken[] { ContentsToken.CONTENTS };
  private static final ContentToken[] PSEUDO_CONTENT = new ContentToken[] {};
  private CSSValue listCounter;
  private HashMap tokenMapping;

  public ContentResolveHandler() {
    tokenMapping = new HashMap();
    tokenMapping.put( ContentValues.CONTENTS, ContentsToken.CONTENTS );
    tokenMapping.put( ContentValues.OPEN_QUOTE, new OpenQuoteToken( false ) );
    tokenMapping.put( ContentValues.NO_OPEN_QUOTE, new OpenQuoteToken( true ) );
    tokenMapping.put( ContentValues.CLOSE_QUOTE, new CloseQuoteToken( false ) );
    tokenMapping.put( ContentValues.NO_CLOSE_QUOTE, new CloseQuoteToken( true ) );

    final CSSStringValue param =
      new CSSStringValue( CSSStringType.STRING, "list-item" );
    listCounter = new CSSFunctionValue( "counter", new CSSValue[] { param } );

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
      ContentStyleKeys.STRING_SET
    };
  }

  /**
   * Resolves a single property.
   *
   * @param process the current layout process controlling everyting
   * @param element the current layout element that is processed
   * @param key     the style key that is computed.
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
        contentSpecification.setAllowContentProcessing( false );
        contentSpecification.setInhibitContent( false );
        contentSpecification.setContents( PSEUDO_CONTENT );
        return;
      } else if ( ContentValues.INHIBIT.equals( value ) ) {
        contentSpecification.setAllowContentProcessing( false );
        contentSpecification.setInhibitContent( true );
        contentSpecification.setContents( PSEUDO_CONTENT );
        return;
      } else if ( ContentValues.NORMAL.equals( value ) ) {
        //        if (layoutContext.isPseudoElement())
        //        {
        //          if (isListMarker(element))
        //          {
        //            processListItem(process, element, contentSpecification);
        //            return;
        //          }
        //          else
        //          {
        //            // a pseudo-element does not have content by default.
        //            contentSpecification.setAllowContentProcessing(false);
        //            contentSpecification.setInhibitContent(true);
        //            contentSpecification.setContents(PSEUDO_CONTENT);
        //            return;
        //          }
        //        }
      }
    }

    contentSpecification.setInhibitContent( false );
    contentSpecification.setAllowContentProcessing( true );
    contentSpecification.setContents( DEFAULT_CONTENT );

    if ( value instanceof CSSAttrFunction ) {
      final ContentToken token = evaluateFunction( (CSSFunctionValue) value, process, element );
      if ( token == null ) {
        return;
      }
      contentSpecification.setContents( new ContentToken[] { token } );
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
      if ( tokens.isEmpty() == false ) {
        final ContentToken[] contents = (ContentToken[]) tokens.toArray
          ( new ContentToken[ tokens.size() ] );
        contentSpecification.setContents( contents );
        return;
      }
    }

  }

  private void processListItem( final DocumentContext process,
                                final LayoutElement element,
                                final ContentSpecification contentSpecification ) {
    contentSpecification.setAllowContentProcessing( false );
    contentSpecification.setInhibitContent( false );

    final LayoutStyle layoutContext = element.getLayoutStyle();
    final CSSValue value =
      layoutContext.getValue( ListStyleKeys.LIST_STYLE_IMAGE );
    if ( value != null ) {
      final ContentToken token = createToken( process, element, value );
      if ( token != null ) {
        contentSpecification.setContents( new ContentToken[] { token } );
        return;
      }
    }

    final ContentToken token = createToken( process, element, listCounter );
    if ( token instanceof CounterToken ) {
      final CounterToken counterToken = (CounterToken) token;
      final CounterStyle style = counterToken.getStyle();
      final String suffix = style.getSuffix();
      if ( suffix == null || suffix.length() == 0 ) {
        contentSpecification.setContents( new ContentToken[] { token } );
      } else {
        contentSpecification.setContents
          ( new ContentToken[] { counterToken, new StaticTextToken( suffix ) } );
      }
    } else {
      contentSpecification.setContents( new ContentToken[] { token } );
    }
  }

  //  private boolean isListMarker (final LayoutElement element)
  //  {
  //    final LayoutStyle layoutContext = element.getLayoutStyle();
  //    if ("marker".equals(layoutContext.getPseudoElement()) == false)
  //    {
  //       return false;
  //    }
  //    final LayoutElement parent = element.getParentLayoutElement();
  //    if (parent == null)
  //    {
  //      return false;
  //    }
  //    final CSSValue parentDisplayRole =
  //        parent.getLayoutStyle().getValue(BoxStyleKeys.DISPLAY_ROLE);
  //    if (DisplayRole.LIST_ITEM.equals(parentDisplayRole))
  //    {
  //      return true;
  //    }
  //
  //    return false;
  //  }

  private ContentToken createToken( final DocumentContext process,
                                    final LayoutElement element,
                                    final CSSValue content ) {
    try {
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

        final ContentToken token = (ContentToken) tokenMapping.get( content );
        if ( token != null ) {
          return token;
        }

        return resolveContentAlias( content );
      }

      if ( content instanceof CSSFunctionValue ) {
        return evaluateFunction( (CSSFunctionValue) content, process, element );
      }
    } catch ( Exception e ) {
      DebugLog.log( "Content-Resolver: Failed to evaluate " + content );
    }

    return null;
  }

  private ContentToken resolveContentAlias( final CSSValue content ) {

    if ( ContentValues.FOOTNOTE.equals( content ) ) {
      final CounterStyle style =
        CounterStyleFactory.getInstance().getCounterStyle( "normal" );
      return new CounterToken( "footnote", style );
    }
    if ( ContentValues.ENDNOTE.equals( content ) ) {
      final CounterStyle style =
        CounterStyleFactory.getInstance().getCounterStyle( "normal" );
      return new CounterToken( "endnote", style );
    }
    if ( ContentValues.SECTIONNOTE.equals( content ) ) {
      final CounterStyle style =
        CounterStyleFactory.getInstance().getCounterStyle( "normal" );
      return new CounterToken( "section-note", style );
    }
    if ( ContentValues.LISTITEM.equals( content ) ) {
      final CounterStyle style =
        CounterStyleFactory.getInstance().getCounterStyle( "normal" );
      return new CounterToken( "list-item", style );
    }
    return null;
  }

  private ContentToken evaluateFunction( final CSSFunctionValue function,
                                         final DocumentContext process,
                                         final LayoutElement element ) {
    final ContentFunction styleFunction =
      FunctionFactory.getInstance().getContentFunction( function.getFunctionName() );
    if ( styleFunction == null ) {
      return null;
    }
    try {
      return styleFunction.evaluate( process, element, function );
    } catch ( FunctionEvaluationException e ) {
      DebugLog.log( "Evaluation failed " + e );
      return null;
    }
  }
}

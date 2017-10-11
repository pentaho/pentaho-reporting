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

package org.pentaho.reporting.libraries.css.resolver.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.css.LibCssBoot;
import org.pentaho.reporting.libraries.css.dom.DefaultLayoutStyle;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.content.ContentStyleKeys;
import org.pentaho.reporting.libraries.css.model.CSSDeclarationRule;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
import org.pentaho.reporting.libraries.css.model.StyleRule;
import org.pentaho.reporting.libraries.css.model.StyleSheet;
import org.pentaho.reporting.libraries.css.namespace.NamespaceCollection;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.StyleResolver;
import org.pentaho.reporting.libraries.css.resolver.function.StyleValueFunction;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.HashMap;
import java.util.Iterator;

public abstract class AbstractStyleResolver implements StyleResolver {
  private static final String INITIAL_CSS_PREFIX = "org.pentaho.reporting.libraries.css.styles.initial";
  private static final String CSS_VALUE_FUNCTIONS_PREFIX =
    "org.pentaho.reporting.libraries.css.styles.value-functions.";
  private static final Log logger = LogFactory.getLog( AbstractStyleResolver.class );
  private LayoutStyle initialStyle;
  private DocumentContext documentContext;
  private NamespaceCollection namespaces;
  private StyleKey[] keys;
  private HashMap functions;

  protected AbstractStyleResolver() {
    functions = new HashMap();
  }

  public void initialize( final DocumentContext documentContext ) {
    this.documentContext = documentContext;
    this.namespaces = documentContext.getNamespaces();
    initializeFunctions();
  }

  private void initializeFunctions() {
    final Configuration config = LibCssBoot.getInstance().getGlobalConfig();
    Iterator it = config.findPropertyKeys( CSS_VALUE_FUNCTIONS_PREFIX );
    while ( it.hasNext() ) {
      final String key = (String) it.next();
      final String value = config.getConfigProperty( key );
      final String name = key.substring( CSS_VALUE_FUNCTIONS_PREFIX.length() ).toLowerCase();
      final Object o =
        ObjectUtilities.loadAndInstantiate( value, AbstractStyleResolver.class, StyleValueFunction.class );
      if ( o != null ) {
        functions.put( name, o );
      }
    }
  }

  protected void loadInitialStyle( DocumentContext context ) {
    this.initialStyle = new DefaultLayoutStyle();
    try {
      final ResourceManager manager = documentContext.getResourceManager();

      // Get the configuration file properties for the initial css load
      final Configuration config = LibCssBoot.getInstance().getGlobalConfig();
      Iterator it = config.findPropertyKeys( INITIAL_CSS_PREFIX );
      while ( it.hasNext() ) {
        // Get the next initial set of styles to add to the global list of initial styles
        final String key = (String) it.next();
        final String value = config.getConfigProperty( key );

        // Load the style information
        final Resource resource = manager.createDirectly( value, StyleSheet.class );
        final StyleSheet initialStyleSheet = (StyleSheet) resource.getResource();

        // Add to the master list
        final int rc = initialStyleSheet.getRuleCount();
        for ( int i = 0; i < rc; i++ ) {
          final StyleRule rule = initialStyleSheet.getRule( i );
          if ( rule instanceof CSSDeclarationRule ) {
            final CSSDeclarationRule drule = (CSSDeclarationRule) rule;
            copyStyleInformation( initialStyle, drule, null );
          }
        }
      }
    } catch ( Exception e ) {
      logger.error( "Initial-StyleSheet could not be parsed. This is a FATAL error.", e );
      throw new IllegalStateException( "Initial-StyleSheet could not be parsed. This is a FATAL error." );
    }
  }

  protected void copyStyleInformation
    ( final LayoutStyle target, final CSSDeclarationRule rule, final LayoutElement element ) {
    try {
      final StyleRule parentRule = rule.getParentRule();
      if ( parentRule instanceof CSSDeclarationRule ) {
        copyStyleInformation( target, (CSSDeclarationRule) parentRule, element );
      }

      if ( element == null ) {
        final StyleKey[] propertyKeys = rule.getPropertyKeysAsArray();
        for ( int i = 0; i < propertyKeys.length; i++ ) {
          final StyleKey key = propertyKeys[ i ];
          final CSSValue propertyCSSValue = rule.getPropertyCSSValue( key );
          if ( propertyCSSValue != null ) {
            target.setValue( key, propertyCSSValue );
          }
        }
        return;
      }

      final StyleKey[] propertyKeys = rule.getPropertyKeysAsArray();
      final CSSValue[] values = rule.getStyleValues();
      for ( int i = 0; i < values.length; i++ ) {
        final StyleKey key = propertyKeys[ i ];
        final CSSValue value = rule.getPropertyCSSValue( key );
        if ( ContentStyleKeys.CONTENT.equals( key ) ||
          ContentStyleKeys.STRING_DEFINE.equals( key ) ||
          ContentStyleKeys.STRING_SET.equals( key ) ) {
          // dont resolve that one ..
          values[ i ] = value;
        } else {
          values[ i ] = resolveValue( value, element );
        }
      }

      for ( int i = 0; i < values.length; i++ ) {
        final StyleKey key = propertyKeys[ i ];

        final CSSValue value = values[ i ];
        if ( value != null ) {
          target.setValue( key, value );
        }
      }
    } catch ( FunctionEvaluationException e ) {
      // something went terribly wrong
      //      Log.debug("Skipping rule, as resolving failed.");
    }

  }

  protected CSSValue resolveValue( final CSSValue value, final LayoutElement element )
    throws FunctionEvaluationException

  {
    if ( element == null ) {
      return value;
    }
    if ( value == null ) {
      return null;
    }
    if ( containsResolveableFunction( value ) == false ) {
      return value;
    }

    if ( value instanceof CSSFunctionValue ) {
      // thats plain and simple - resolve it directly.
      final CSSFunctionValue functionValue = (CSSFunctionValue) value;
      final String name = functionValue.getFunctionName().toLowerCase();
      final StyleValueFunction o = (StyleValueFunction) functions.get( name );
      if ( o == null ) {
        throw new FunctionEvaluationException( "No such function registered: " + name );
      }
      return o.evaluate( documentContext, element, (CSSFunctionValue) value );
    } else if ( value instanceof CSSValueList ) {
      final CSSValueList list = (CSSValueList) value;
      final int length = list.getLength();
      final CSSValue[] retValus = new CSSValue[ length ];
      for ( int i = 0; i < length; i++ ) {
        final CSSValue item = list.getItem( i );
        retValus[ i ] = resolveValue( item, element );
      }
      return new CSSValueList( retValus );
    } else if ( value instanceof CSSValuePair ) {
      final CSSValuePair pair = (CSSValuePair) value;
      return new CSSValuePair
        ( resolveValue( pair.getFirstValue(), element ),
          resolveValue( pair.getSecondValue(), element ) );
    } else {
      return value;
    }
  }

  protected boolean containsResolveableFunction( final CSSValue value ) {
    if ( value == null ) {
      return false;
    }
    if ( value instanceof CSSFunctionValue ) {
      return true;
    }

    if ( value instanceof CSSValueList ) {
      final CSSValueList list = (CSSValueList) value;
      final int length = list.getLength();
      for ( int i = 0; i < length; i++ ) {
        final CSSValue item = list.getItem( i );
        if ( containsResolveableFunction( item ) ) {
          return true;
        }
      }
      return false;
    }

    if ( value instanceof CSSValuePair ) {
      final CSSValuePair pair = (CSSValuePair) value;
      if ( containsResolveableFunction( pair.getFirstValue() ) ) {
        return true;
      }
      if ( containsResolveableFunction( pair.getSecondValue() ) ) {
        return true;
      }
      return false;
    }
    return false;
  }

  public LayoutStyle getInitialStyle() {
    return initialStyle;
  }

  protected DocumentContext getDocumentContext() {
    return documentContext;
  }

  protected StyleKey[] getKeys() {
    if ( keys == null ) {
      keys = StyleKeyRegistry.getRegistry().getKeys();
    }
    return keys;
  }

  protected NamespaceCollection getNamespaces() {
    return namespaces;
  }

  protected abstract void resolveOutOfContext( LayoutElement element );

}

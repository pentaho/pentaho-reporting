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

package org.pentaho.reporting.libraries.css.resolver.function;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.css.LibCssBoot;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Creation-Date: 16.04.2006, 14:15:37
 *
 * @author Thomas Morgner
 */
public final class FunctionFactory {
  public static final String VALUE_FUNCTIONS_KEY_RANGE =
    "org.pentaho.reporting.libraries.css.functions.values.";
  public static final String CONTENT_FUNCTIONS_KEY_RANGE =
    "org.pentaho.reporting.libraries.css.functions.content.";

  private HashMap styleFunctions;
  private HashMap contentFunctions;
  private static FunctionFactory instance;

  public static FunctionFactory getInstance() {
    if ( instance == null ) {
      instance = new FunctionFactory();
      instance.registerDefault();
    }
    return instance;
  }

  private FunctionFactory() {
    styleFunctions = new HashMap();
    contentFunctions = new HashMap();
  }

  public void registerDefault() {
    final Configuration config = LibCssBoot.getInstance().getGlobalConfig();
    final Iterator valueKeys = config.findPropertyKeys( VALUE_FUNCTIONS_KEY_RANGE );
    while ( valueKeys.hasNext() ) {
      final String key = (String) valueKeys.next();
      final String value = config.getConfigProperty( key );
      final String name = key.substring( VALUE_FUNCTIONS_KEY_RANGE.length() );
      final Object maybeFunction = ObjectUtilities.loadAndInstantiate
        ( value, FunctionFactory.class, StyleValueFunction.class );
      if ( maybeFunction instanceof StyleValueFunction ) {
        styleFunctions.put( name.toLowerCase(), maybeFunction );
      }
    }

    final Iterator contentKeys = config.findPropertyKeys( CONTENT_FUNCTIONS_KEY_RANGE );
    while ( contentKeys.hasNext() ) {
      final String key = (String) contentKeys.next();
      final String value = config.getConfigProperty( key );
      final String name = key.substring( CONTENT_FUNCTIONS_KEY_RANGE.length() );
      final Object maybeFunction = ObjectUtilities.loadAndInstantiate
        ( value, FunctionFactory.class, ContentFunction.class );
      if ( maybeFunction instanceof ContentFunction ) {
        contentFunctions.put( name.toLowerCase(), maybeFunction );
      }
    }

  }

  public StyleValueFunction getStyleFunction( final String name ) {
    final StyleValueFunction function = (StyleValueFunction) styleFunctions.get( name.toLowerCase() );
    if ( function == null ) {
      //Log.warn ("Unrecognized style function encountered: " + name);
    }
    // todo: Check for null values in all callers ..
    return function;
  }

  public ContentFunction getContentFunction( final String name ) {
    final ContentFunction function = (ContentFunction) contentFunctions.get( name.toLowerCase() );
    if ( function == null ) {
      //Log.warn ("Unrecognized content function encountered: " + name);
    }
    // todo: Check for null values in all callers ..
    return function;
  }
}

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

package org.pentaho.reporting.libraries.formula.function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.HashNMap;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Creation-Date: 02.11.2006, 12:48:32
 *
 * @author Thomas Morgner
 */
public class DefaultFunctionRegistry implements FunctionRegistry {
  private static final Log logger = LogFactory.getLog( DefaultFunctionRegistry.class );

  private static final String FUNCTIONS_PREFIX = "org.pentaho.reporting.libraries.formula.functions.";
  private static final String[] EMPTY_ARRAY = new String[ 0 ];
  private static final FunctionCategory[] EMPTY_CATEGORIES = new FunctionCategory[ 0 ];

  private FunctionCategory[] categories;
  private HashNMap<FunctionCategory, String> categoryFunctions;
  private HashMap<String, String> functions;
  private HashMap<String, FunctionDescription> functionMetaData;
  private HashMap<String, Class> cachedFunctions;

  public DefaultFunctionRegistry() {
    cachedFunctions = new HashMap<String, Class>();
    categoryFunctions = new HashNMap<FunctionCategory, String>();
    functionMetaData = new HashMap<String, FunctionDescription>();
    functions = new HashMap<String, String>();
    categories = EMPTY_CATEGORIES;
  }

  public FunctionCategory[] getCategories() {
    return categories.clone();
  }

  public Function[] getFunctions() {
    final String[] fnNames = getFunctionNames();
    final ArrayList<Function> functions = new ArrayList<Function>( fnNames.length );
    for ( int i = 0; i < fnNames.length; i++ ) {
      final String aName = fnNames[ i ];
      final Function function = createFunction( aName );
      if ( function == null ) {
        logger.debug( "There is no such function: " + aName );
      } else {
        functions.add( function );
      }
    }
    return functions.toArray( new Function[ functions.size() ] );
  }

  public String[] getFunctionNames() {
    return functions.keySet().toArray( new String[ functions.size() ] );
  }

  public String[] getFunctionNamesByCategory( final FunctionCategory category ) {
    return categoryFunctions.toArray( category, EMPTY_ARRAY );
  }

  public Function[] getFunctionsByCategory( final FunctionCategory category ) {
    final String[] fnNames = categoryFunctions.toArray( category, EMPTY_ARRAY );
    final ArrayList<Function> functions = new ArrayList<Function>( fnNames.length );
    for ( int i = 0; i < fnNames.length; i++ ) {
      final String aName = fnNames[ i ];
      final Function function = createFunction( aName );
      if ( function != null ) {
        functions.add( function );
      }
    }
    return functions.toArray( new Function[ functions.size() ] );
  }

  public Function createFunction( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    final String functionClass = functions.get( name.toUpperCase() );
    final Class cachedClass = cachedFunctions.get( functionClass );
    if ( cachedClass != null ) {
      try {
        return (Function) cachedClass.newInstance();
      } catch ( Exception e ) {
        return null;
      }
    }

    final Function function = ObjectUtilities.loadAndInstantiate
      ( functionClass, DefaultFunctionRegistry.class, Function.class );
    if ( function == null ) {
      logger.debug( "There is no such function: " + name );
    } else {
      cachedFunctions.put( functionClass, function.getClass() );
    }
    return function;
  }

  public FunctionDescription getMetaData( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    return functionMetaData.get( name.toUpperCase() );
  }

  public void initialize( final Configuration configuration ) {
    final Iterator functionKeys =
      configuration.findPropertyKeys( FUNCTIONS_PREFIX );
    final HashSet<FunctionCategory> categories = new HashSet<FunctionCategory>();


    while ( functionKeys.hasNext() ) {
      final String classKey = (String) functionKeys.next();
      if ( classKey.endsWith( ".class" ) == false ) {
        continue;
      }

      final String className = configuration.getConfigProperty( classKey );
      if ( className.length() == 0 ) {
        continue;
      }
      final Object fn = ObjectUtilities.loadAndInstantiate
        ( className, DefaultFunctionRegistry.class, Function.class );
      if ( fn instanceof Function == false ) {
        continue;
      }

      final Function function = (Function) fn;

      final int endIndex = classKey.length() - 6; // 6 = ".class".length();
      final String descrKey = classKey.substring( 0, endIndex ) + ".description";
      final String descrClassName = configuration.getConfigProperty( descrKey );
      final Object descr = ObjectUtilities.loadAndInstantiate
        ( descrClassName, DefaultFunctionRegistry.class, FunctionDescription.class );

      final FunctionDescription description;
      if ( descr instanceof FunctionDescription == false ) {
        description = new DefaultFunctionDescription( function.getCanonicalName() );
      } else {
        description = (FunctionDescription) descr;
      }

      final FunctionCategory cat = description.getCategory();
      categoryFunctions.add( cat, function.getCanonicalName() );
      functionMetaData.put( function.getCanonicalName(), description );
      functions.put( function.getCanonicalName(), className );
      categories.add( cat );
    }

    this.categories = categories.toArray( new FunctionCategory[ categories.size() ] );
  }

}

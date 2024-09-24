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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * This report data factory uses introspection to search for a report data source. The query can have the following
 * formats:
 * <p/>
 * &lt;full-qualified-classname&gt;#methodName(Parameters) &lt;full-qualified-classname&gt;(constructorparams)
 * #methodName(Parameters) &lt;full-qualified-classname&gt;(constructorparams)
 *
 * @author Thomas Morgner
 */
public class StaticDataFactory extends AbstractDataFactory {
  private static final String[] EMPTY_NAMES = new String[0];
  private static final String[] EMPTY_PARAMS = EMPTY_NAMES;

  /**
   * DefaultConstructor.
   */
  public StaticDataFactory() {
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return true;
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   *          the method call.
   * @param parameters
   *          the set of parameters.
   * @return the tablemodel from the executed method call, never null.
   */
  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    final int methodSeparatorIdx = query.indexOf( '#' );

    if ( ( methodSeparatorIdx + 1 ) >= query.length() ) {
      // If we have a method separator, then it cant be at the end of the text.
      throw new ReportDataFactoryException( "Malformed query: " + query ); //$NON-NLS-1$
    }

    if ( methodSeparatorIdx == -1 ) {
      // we have no method. So this query must be a reference to a tablemodel
      // instance.
      final int parameterStartIdx = query.indexOf( '(' );
      final String[] parameterNames;
      final String constructorName;
      if ( parameterStartIdx == -1 ) {
        parameterNames = StaticDataFactory.EMPTY_PARAMS;
        constructorName = query;
      } else {
        parameterNames = createParameterList( query, parameterStartIdx );
        constructorName = query.substring( 0, parameterStartIdx );
      }

      try {
        final Constructor c = findDirectConstructor( constructorName, parameterNames.length );

        final Object[] params = new Object[parameterNames.length];
        for ( int i = 0; i < parameterNames.length; i++ ) {
          final String name = parameterNames[i];
          params[i] = parameters.get( name );
        }
        return (TableModel) c.newInstance( params );
      } catch ( Exception e ) {
        throw new ReportDataFactoryException( "Unable to instantiate class for non static call.", e ); //$NON-NLS-1$
      }
    }

    return createComplexTableModel( query, methodSeparatorIdx, parameters );
  }

  public String[] getParameterFields( final String query ) throws ReportDataFactoryException {
    final int methodSeparatorIdx = query.indexOf( '#' );
    if ( ( methodSeparatorIdx + 1 ) >= query.length() ) {
      // malformed query ..
      return null;
    }

    if ( methodSeparatorIdx == -1 ) {
      // we have no method. So this query must be a reference to a tablemodel
      // instance.
      final int parameterStartIdx = query.indexOf( '(' );
      if ( parameterStartIdx == -1 ) {
        return StaticDataFactory.EMPTY_PARAMS;
      } else {
        final String[] list = createParameterList( query, parameterStartIdx );
        final LinkedHashSet<String> hashSet = new LinkedHashSet<String>( Arrays.asList( list ) );
        return hashSet.toArray( new String[hashSet.size()] );
      }
    }

    final String constructorSpec = query.substring( 0, methodSeparatorIdx );
    final int constParamIdx = constructorSpec.indexOf( '(' );
    if ( constParamIdx == -1 ) {
      final String methodSpec = query.substring( methodSeparatorIdx + 1 );
      final int parameterStartIdx = methodSpec.indexOf( '(' );
      if ( parameterStartIdx == -1 ) {
        // no parameters. Nice.
        return StaticDataFactory.EMPTY_PARAMS;
      } else {
        final String[] list = createParameterList( methodSpec, parameterStartIdx );
        final LinkedHashSet<String> hashSet = new LinkedHashSet<String>( Arrays.asList( list ) );
        return hashSet.toArray( new String[hashSet.size()] );
      }
    }

    // We have to find a suitable constructor ..
    final String[] constructorParameterNames = createParameterList( constructorSpec, constParamIdx );
    final LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
    hashSet.addAll( Arrays.asList( constructorParameterNames ) );

    final String methodQuery = query.substring( methodSeparatorIdx + 1 );
    final int parameterStartIdx = methodQuery.indexOf( '(' );
    if ( parameterStartIdx != -1 ) {
      final String[] list = createParameterList( methodQuery, parameterStartIdx );
      hashSet.addAll( Arrays.asList( list ) );
    }
    return hashSet.toArray( new String[hashSet.size()] );
  }

  /**
   * Performs a complex query, where the tablemodel is retrieved from an method that was instantiated using parameters.
   *
   * @param query
   *          the query-string that contains the method to call.
   * @param methodSeparatorIdx
   *          the position where the method specification starts.
   * @param parameters
   *          the set of parameters.
   * @return the resulting tablemodel, never null.
   * @throws ReportDataFactoryException
   *           if something goes wrong.
   */
  private TableModel
    createComplexTableModel( final String query, final int methodSeparatorIdx, final DataRow parameters )
      throws ReportDataFactoryException {
    final String constructorSpec = query.substring( 0, methodSeparatorIdx );
    final int constParamIdx = constructorSpec.indexOf( '(' );
    if ( constParamIdx == -1 ) {
      // Either a static call or a default constructor call..
      return loadFromDefaultConstructor( query, methodSeparatorIdx, parameters );
    }

    // We have to find a suitable constructor ..
    final String className = query.substring( 0, constParamIdx );
    final String[] parameterNames = createParameterList( constructorSpec, constParamIdx );
    final Constructor c = findIndirectConstructor( className, parameterNames.length );

    final String methodQuery = query.substring( methodSeparatorIdx + 1 );
    final String[] methodParameterNames;
    final String methodName;
    final int parameterStartIdx = methodQuery.indexOf( '(' );
    if ( parameterStartIdx == -1 ) {
      // no parameters. Nice.
      methodParameterNames = StaticDataFactory.EMPTY_PARAMS;
      methodName = methodQuery;
    } else {
      methodName = methodQuery.substring( 0, parameterStartIdx );
      methodParameterNames = createParameterList( methodQuery, parameterStartIdx );
    }
    final Method m = findCallableMethod( className.trim(), methodName.trim(), methodParameterNames.length );

    try {
      final Object[] constrParams = new Object[parameterNames.length];
      for ( int i = 0; i < parameterNames.length; i++ ) {
        final String name = parameterNames[i];
        constrParams[i] = parameters.get( name );
      }
      final Object o = c.newInstance( constrParams );

      final Object[] methodParams = new Object[methodParameterNames.length];
      for ( int i = 0; i < methodParameterNames.length; i++ ) {
        final String name = methodParameterNames[i];
        methodParams[i] = parameters.get( name );
      }
      final Object data = m.invoke( o, methodParams );
      if ( data == null ) {
        throw new ReportDataFactoryException( "The call did not return a valid tablemodel." );
      }
      return (TableModel) data;
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( "Unable to instantiate class for non static call." ); //$NON-NLS-1$
    }
  }

  /**
   * Loads a tablemodel from a parameterless class or method. Call does not use any parameters.
   *
   * @param query
   *          the query-string that contains the method to call.
   * @param methodSeparatorIdx
   *          the position where the method specification starts.
   * @param parameters
   *          the set of parameters.
   * @return the resulting tablemodel, never null.
   * @throws ReportDataFactoryException
   *           if something goes wrong.
   */
  private TableModel loadFromDefaultConstructor( final String query, final int methodSeparatorIdx,
      final DataRow parameters ) throws ReportDataFactoryException {
    final String className = query.substring( 0, methodSeparatorIdx );

    final String methodSpec = query.substring( methodSeparatorIdx + 1 );
    final String methodName;
    final String[] parameterNames;
    final int parameterStartIdx = methodSpec.indexOf( '(' );
    if ( parameterStartIdx == -1 ) {
      // no parameters. Nice.
      parameterNames = StaticDataFactory.EMPTY_PARAMS;
      methodName = methodSpec;
    } else {
      parameterNames = createParameterList( methodSpec, parameterStartIdx );
      methodName = methodSpec.substring( 0, parameterStartIdx );
    }

    try {
      final Method m = findCallableMethod( className.trim(), methodName.trim(), parameterNames.length );
      final Object[] params = new Object[parameterNames.length];
      for ( int i = 0; i < parameterNames.length; i++ ) {
        final String name = parameterNames[i];
        params[i] = parameters.get( name );
      }

      if ( Modifier.isStatic( m.getModifiers() ) ) {
        final Object data = m.invoke( null, params );
        if ( data == null ) {
          throw new ReportDataFactoryException( "The call did not return a valid tablemodel." );
        }
        return (TableModel) data;
      }

      final ClassLoader classLoader = getClassLoader();
      final Class c = Class.forName( className, false, classLoader );
      final Object o = c.newInstance();
      if ( o == null ) {
        throw new ReportDataFactoryException( "Unable to instantiate class for non static call." ); //$NON-NLS-1$
      }
      final Object data = m.invoke( o, params );
      if ( data == null ) {
        throw new ReportDataFactoryException( "The call did not return a valid tablemodel." );
      }
      return (TableModel) data;
    } catch ( ReportDataFactoryException rdfe ) {
      throw rdfe;
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( "Something went terribly wrong: ", e ); //$NON-NLS-1$
    }
  }

  /**
   * Creates the list of column names that should be mapped into the method or constructor parameters.
   *
   * @param query
   *          the query-string.
   * @param parameterStartIdx
   *          the index from where to read the parameter list.
   * @return an array with column names.
   * @throws ReportDataFactoryException
   *           if something goes wrong.
   */
  private String[] createParameterList( final String query, final int parameterStartIdx )
    throws ReportDataFactoryException {
    final int parameterEndIdx = query.lastIndexOf( ')' );
    if ( parameterEndIdx < parameterStartIdx ) {
      throw new ReportDataFactoryException( "Malformed query: " + query ); //$NON-NLS-1$
    }
    final String parameterText = query.substring( parameterStartIdx + 1, parameterEndIdx );
    final CSVTokenizer tokenizer = new CSVTokenizer( parameterText, ",", "\"", false );
    final int size = tokenizer.countTokens();
    final String[] parameterNames = new String[size];
    int i = 0;
    while ( tokenizer.hasMoreTokens() ) {
      parameterNames[i] = tokenizer.nextToken();
      i += 1;
    }
    return parameterNames;
  }

  /**
   * Returns the current classloader.
   *
   * @return the current classloader.
   */
  protected ClassLoader getClassLoader() {
    return ObjectUtilities.getClassLoader( StaticDataFactory.class );
  }

  /**
   * Tries to locate a method-object for the call. This method will throw an Exception if the method was not found or
   * not public.
   *
   * @param className
   *          the name of the class where to seek the method.
   * @param methodName
   *          the name of the method.
   * @param paramCount
   *          the parameter count of the method we seek.
   * @return the method object.
   * @throws ReportDataFactoryException
   *           if something goes wrong.
   */
  private Method findCallableMethod( final String className, final String methodName, final int paramCount )
    throws ReportDataFactoryException {
    final ClassLoader classLoader = getClassLoader();

    if ( classLoader == null ) {
      throw new ReportDataFactoryException( "No classloader!" ); //$NON-NLS-1$
    }
    try {
      final Class c = Class.forName( className, false, classLoader );
      if ( Modifier.isAbstract( c.getModifiers() ) ) {
        throw new ReportDataFactoryException( "Abstract class cannot be handled!" ); //$NON-NLS-1$
      }

      final Method[] methods = c.getMethods();
      for ( int i = 0; i < methods.length; i++ ) {
        final Method method = methods[i];
        if ( Modifier.isPublic( method.getModifiers() ) == false ) {
          continue;
        }
        if ( method.getName().equals( methodName ) == false ) {
          continue;
        }
        final Class returnType = method.getReturnType();
        if ( TableModel.class.isAssignableFrom( returnType ) == false ) {
          continue;
        }
        if ( method.getParameterTypes().length != paramCount ) {
          continue;
        }
        return method;
      }
    } catch ( ClassNotFoundException e ) {
      throw new ReportDataFactoryException( "No such Class: " + className, e ); //$NON-NLS-1$
    }
    throw new ReportDataFactoryException( "No such Method: " + className + '#' + methodName ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Tries to locate a suitable public constructor for the number of parameters. This will return the first constructor
   * that matches, no matter whether the parameter types will match too.
   * <p/>
   * The Class that is referenced must be a Tablemodel implementation.
   *
   * @param className
   *          the classname on where to find the constructor.
   * @param paramCount
   *          the number of parameters expected in the constructor.
   * @return the Constructor object, never null.
   * @throws ReportDataFactoryException
   *           if the constructor could not be found or something went wrong.
   */
  private Constructor findDirectConstructor( final String className, final int paramCount )
    throws ReportDataFactoryException {
    final ClassLoader classLoader = getClassLoader();
    if ( classLoader == null ) {
      throw new ReportDataFactoryException( "No classloader!" ); //$NON-NLS-1$
    }

    try {
      final Class c = Class.forName( className, false, classLoader );
      if ( TableModel.class.isAssignableFrom( c ) == false ) {
        throw new ReportDataFactoryException(
            "The specified class must be either a TableModel or a ReportData implementation: " + className ); //$NON-NLS-1$
      }
      if ( Modifier.isAbstract( c.getModifiers() ) ) {
        throw new ReportDataFactoryException( "The specified class cannot be instantiated: it is abstract:" + className ); //$NON-NLS-1$
      }

      final Constructor[] methods = c.getConstructors();
      for ( int i = 0; i < methods.length; i++ ) {
        final Constructor method = methods[i];
        if ( Modifier.isPublic( method.getModifiers() ) == false ) {
          continue;
        }
        if ( method.getParameterTypes().length != paramCount ) {
          continue;
        }
        return method;
      }
    } catch ( ClassNotFoundException e ) {
      throw new ReportDataFactoryException( "No such Class", e ); //$NON-NLS-1$
    }
    throw new ReportDataFactoryException( "There is no constructor in class " + className + //$NON-NLS-1$
        " that accepts " + paramCount + " parameters." ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Tries to locate a constructor that accepts the specified number of parameters. The referenced class can be of any
   * type, as we will call a method on that class that will return the tablemodel for us.
   *
   * @param className
   *          the classname of the class where to search the constructor.
   * @param paramCount
   *          the numbers of parameters expected.
   * @return the constructor object, never null.
   * @throws ReportDataFactoryException
   *           if the constructor could not be found or something went wrong.
   */
  private Constructor findIndirectConstructor( final String className, final int paramCount )
    throws ReportDataFactoryException {
    final ClassLoader classLoader = getClassLoader();
    if ( classLoader == null ) {
      throw new ReportDataFactoryException( "No classloader!" ); //$NON-NLS-1$
    }

    try {
      final Class c = Class.forName( className, false, classLoader );
      if ( Modifier.isAbstract( c.getModifiers() ) ) {
        throw new ReportDataFactoryException( "The specified class cannot be instantiated: it is abstract." ); //$NON-NLS-1$
      }

      final Constructor[] methods = c.getConstructors();
      for ( int i = 0; i < methods.length; i++ ) {
        final Constructor method = methods[i];
        if ( Modifier.isPublic( method.getModifiers() ) == false ) {
          continue;
        }
        if ( method.getParameterTypes().length != paramCount ) {
          continue;
        }
        return method;
      }
    } catch ( ClassNotFoundException e ) {
      throw new ReportDataFactoryException( "No such Class", e ); //$NON-NLS-1$
    }
    throw new ReportDataFactoryException( "There is no constructor in class " + className + //$NON-NLS-1$
        " that accepts " + paramCount + " parameters." ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Returns a copy of the data factory that is not affected by its anchestor and holds no connection to the anchestor
   * anymore. A data-factory will be derived at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public DataFactory derive() {
    return this;
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   * <p/>
   * This method is empty.
   */
  public void close() {

  }

  public String[] getQueryNames() {
    return EMPTY_NAMES;
  }

  public String translateQuery( final String queryName ) {
    return queryName;
  }
}

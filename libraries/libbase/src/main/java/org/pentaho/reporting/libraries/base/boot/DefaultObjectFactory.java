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


package org.pentaho.reporting.libraries.base.boot;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.lang.annotation.Annotation;
import java.util.HashMap;

public class DefaultObjectFactory implements ObjectFactory {
  private Configuration configuration;
  private HashMap<String, Object> singletons;

  public DefaultObjectFactory( final Configuration configuration ) {
    this.configuration = configuration;
    this.singletons = new HashMap<String, Object>();
  }

  public <T> T get( final Class<T> interfaceClass ) {
    return get( interfaceClass, interfaceClass.getName() );
  }

  public synchronized <T> T get( final Class<T> interfaceClass, final String key ) {
    final String value = configuration.getConfigProperty( key );
    if ( value == null ) {
      throw new ObjectFactoryException( interfaceClass.getName(), value );
    }

    try {
      final ClassLoader classLoader = ObjectUtilities.getClassLoader( interfaceClass );
      final Class clazz = (Class) Class.forName( value, false, classLoader );
      final Annotation annotation = clazz.getAnnotation( SingletonHint.class );
      if ( annotation == null ) {
        final T retval = ObjectUtilities.instantiateSafe( clazz, interfaceClass );
        if ( retval == null ) {
          throw new ObjectFactoryException( interfaceClass.getName(), value );
        }
        return retval;
      }

      final Object o = singletons.get( value );
      if ( o != null ) {
        return (T) o;
      }

      final T retval = ObjectUtilities.instantiateSafe( clazz, interfaceClass );
      if ( retval == null ) {
        throw new ObjectFactoryException( interfaceClass.getName(), value );
      }
      singletons.put( value, retval );
      return retval;
    } catch ( ClassNotFoundException e ) {
      throw new IllegalStateException( e );
    }

  }
}

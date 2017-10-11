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
 * Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...  
 * All rights reserved.
 */

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

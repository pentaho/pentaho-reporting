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

package org.pentaho.reporting.libraries.css.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.LinkedMap;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.css.LibCssBoot;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

/**
 * This class should not be static, or we might create a memory leak.
 *
 * @author Thomas Morgner
 */
public class StyleKeyRegistry {
  private static final Log logger = LogFactory.getLog( StyleKeyRegistry.class );
  private static StyleKeyRegistry registry;

  public static synchronized StyleKeyRegistry getRegistry() {
    if ( registry == null ) {
      throw new IllegalStateException( "You have to boot LibCSS to make all style-keys known" );
    }
    return registry;
  }

  public static void performBoot() {
    if ( registry != null ) {
      return;
    }

    registry = new StyleKeyRegistry();
    registry.registerDefaults();
    registry.locked = true;
  }

  private LinkedMap knownStyleKeys;
  private boolean locked;

  private StyleKeyRegistry() {
    knownStyleKeys = new LinkedMap();
  }

  public StyleKey findKeyByName( final String name ) {
    return (StyleKey) knownStyleKeys.get( name );
  }

  public int getKeyCount() {
    return knownStyleKeys.size();
  }

  public synchronized void registerDefaults() {
    if ( locked ) {
      throw new IllegalStateException
        ( "All StyleKeys must be registered during the bootup. The registry is locked now." );
    }
    final Configuration config = LibCssBoot.getInstance().getGlobalConfig();
    final Iterator it = config.findPropertyKeys( "org.pentaho.reporting.libraries.css.stylekeys." );
    final ClassLoader classLoader = ObjectUtilities.getClassLoader( StyleKeyRegistry.class );

    while ( it.hasNext() ) {
      final String key = (String) it.next();
      try {
        final String className = config.getConfigProperty( key );
        final Class c = Class.forName( className, false, classLoader );
        registerClass( c );
      } catch ( ClassNotFoundException e ) {
        // ignore that class
      } catch ( NullPointerException e ) {
        // ignore invalid values as well.
      }
    }

  }

  private void registerClass( final Class c ) {
    // Log.debug ("Registering stylekeys from " + c);
    try {
      final Field[] fields = c.getFields();
      for ( int i = 0; i < fields.length; i++ ) {
        final Field field = fields[ i ];
        final int modifiers = field.getModifiers();
        if ( Modifier.isPublic( modifiers ) &&
          Modifier.isStatic( modifiers ) ) {
          if ( Modifier.isFinal( modifiers ) == false ) {
            logger.warn( "Invalid implementation: StyleKeys should be 'public static final': " + c );
          }
          if ( field.getType().isAssignableFrom( StyleKey.class ) ) {
            final StyleKey value = (StyleKey) field.get( null );
            if ( value == null ) {
              logger.warn( "Invalid implementation: StyleKeys fields must not be null: " + c );
            }
            // ignore the returned value, all we want is to trigger the key
            // creation
            // Log.debug ("Loaded key " + value);
          }
        }
      }
    } catch ( IllegalAccessException e ) {
      // wont happen, we've checked it..
    }
  }

  public synchronized StyleKey createKey( final String name,
                                          final boolean trans,
                                          final boolean inherited,
                                          final int validity ) {
    final StyleKey existingKey = findKeyByName( name );
    if ( existingKey != null ) {
      return existingKey;
    }

    if ( locked ) {
      throw new IllegalStateException
        ( "All StyleKeys must be registered during the bootup. The registry is locked now." );
    }

    final StyleKey createdKey = new StyleKey
      ( name, trans, inherited, knownStyleKeys.size(), validity );
    knownStyleKeys.put( name, createdKey );
    return createdKey;
  }

  public synchronized StyleKey[] getKeys() {
    return (StyleKey[]) knownStyleKeys.values( new StyleKey[ knownStyleKeys.size() ] );
  }

  public synchronized StyleKey[] getKeys( final StyleKey[] input ) {
    return (StyleKey[]) knownStyleKeys.values( input );
  }


}

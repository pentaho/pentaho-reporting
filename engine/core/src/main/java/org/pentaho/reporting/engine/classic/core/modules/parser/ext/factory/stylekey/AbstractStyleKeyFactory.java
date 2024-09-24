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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An abstract class for implementing the {@link StyleKeyFactory} interface.
 *
 * @author Thomas Morgner.
 */
public abstract class AbstractStyleKeyFactory implements StyleKeyFactory {
  private static final Log logger = LogFactory.getLog( AbstractStyleKeyFactory.class );

  /**
   * Storage for the keys.
   */
  private final HashMap<String, StyleKey> knownKeys;

  /**
   * Creates a new factory.
   */
  protected AbstractStyleKeyFactory() {
    knownKeys = new HashMap<String, StyleKey>();
  }

  /**
   * Registers a key.
   *
   * @param key
   *          the key.
   */
  public void addKey( final StyleKey key ) {
    knownKeys.put( key.getName(), key );
  }

  /**
   * Returns the key with the given name.
   *
   * @param name
   *          the name.
   * @return The key.
   */
  public StyleKey getStyleKey( final String name ) {
    return knownKeys.get( name );
  }

  /**
   * Creates an object.
   *
   * @param k
   *          the style key.
   * @param value
   *          the value.
   * @param c
   *          the class.
   * @param fc
   *          the class factory used to create the basic object.
   * @return The object.
   */
  public Object createBasicObject( final StyleKey k, final String value, final Class c, final ClassFactory fc ) {
    if ( k == null ) {
      // no such key registered ...
      return null;
    }

    if ( c == null ) {
      throw new NullPointerException();
    }

    if ( fc == null ) {
      throw new NullPointerException( "Class " + getClass() );
    }

    ObjectDescription od = fc.getDescriptionForClass( c );
    if ( od == null ) {
      od = fc.getSuperClassObjectDescription( c, null );
      if ( od == null ) {
        return null;
      }
    }
    od.setParameter( "value", value );
    return od.createObject();
  }

  /**
   * Loads all public static stylekeys which are declared in the given class.
   *
   * @param c
   *          the class from where to load the stylekeys.
   * @throws SecurityException
   *           if the current security settings deny class access.
   */
  protected void loadFromClass( final Class c ) {
    final Field[] fields = c.getFields();
    for ( int i = 0; i < fields.length; i++ ) {
      final Field f = fields[i];
      if ( StyleKey.class.isAssignableFrom( f.getType() ) == false ) {
        // is no instance of stylekey...
        continue;
      }

      if ( Modifier.isPublic( f.getModifiers() ) && Modifier.isStatic( f.getModifiers() ) ) {
        try {
          addKey( (StyleKey) f.get( null ) );
        } catch ( IllegalAccessException ex ) {
          AbstractStyleKeyFactory.logger.warn( "Unexpected Exception while loading stylekeys", ex );
        }
      }
    }
  }

  /**
   * Returns an iterator that provides access to the registered keys.
   *
   * @return The iterator.
   */
  public Iterator getRegisteredKeys() {
    return knownKeys.keySet().iterator();
  }

  /**
   * Indicated whether an other object is equal to this one.
   *
   * @param o
   *          the other object.
   * @return true, if the object is equal, false otherwise.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof AbstractStyleKeyFactory ) ) {
      return false;
    }

    final AbstractStyleKeyFactory abstractStyleKeyFactory = (AbstractStyleKeyFactory) o;

    if ( !knownKeys.equals( abstractStyleKeyFactory.knownKeys ) ) {
      return false;
    }

    return true;
  }

  /**
   * Computes an hashcode for this factory.
   *
   * @return the hashcode.
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return knownKeys.hashCode();
  }
}

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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.HashMap;
import java.util.Iterator;

/**
 * An abstract class that implements the {@link ClassFactory} interface.
 *
 * @author Thomas Morgner.
 */
public abstract class ClassFactoryImpl implements ClassFactory {

  /**
   * Storage for the classes.
   */
  private HashMap classes;
  /**
   * The parser/report configuration
   */
  private Configuration config;

  /**
   * Creates a new class factory.
   */
  protected ClassFactoryImpl() {
    this.classes = new HashMap();
  }

  /**
   * Returns an object-description for a class.
   *
   * @param c
   *          the class.
   * @return An object description.
   */
  public ObjectDescription getDescriptionForClass( final Class c ) {
    final ObjectDescription od = (ObjectDescription) this.classes.get( c );
    if ( od == null ) {
      return null;
    }
    return od.getInstance();
  }

  /**
   * Returns the most concrete object-description for the super class of a class.
   *
   * @param d
   *          the class.
   * @param knownSuperClass
   *          a known supported superclass or null, if no superclass is known yet.
   * @return The object description.
   */
  public ObjectDescription getSuperClassObjectDescription( final Class d, ObjectDescription knownSuperClass ) {

    if ( d == null ) {
      throw new NullPointerException( "Description class must not be null." );
    }
    final Iterator iterator = this.classes.keySet().iterator();
    while ( iterator.hasNext() ) {
      final Class keyClass = (Class) iterator.next();
      if ( keyClass.isAssignableFrom( d ) ) {
        final ObjectDescription od = (ObjectDescription) this.classes.get( keyClass );
        if ( knownSuperClass == null ) {
          knownSuperClass = od;
        } else {
          if ( knownSuperClass.getObjectClass().isAssignableFrom( od.getObjectClass() ) ) {
            knownSuperClass = od;
          }
        }
      }
    }
    if ( knownSuperClass == null ) {
      return null;
    }
    return knownSuperClass.getInstance();
  }

  /**
   * Registers an object description with the factory.
   *
   * @param key
   *          the key.
   * @param od
   *          the object description.
   */
  protected void registerClass( final Class key, final ObjectDescription od ) {
    this.classes.put( key, od );
    if ( this.config != null ) {
      od.configure( this.config );
    }
  }

  /**
   * Returns an iterator that provides access to the registered object definitions.
   *
   * @return The iterator.
   */
  public Iterator getRegisteredClasses() {
    return this.classes.keySet().iterator();
  }

  /**
   * Configures this factory. The configuration contains several keys and their defined values. The given reference to
   * the configuration object will remain valid until the report parsing or writing ends.
   * <p/>
   * The configuration contents may change during the reporting.
   *
   * @param config
   *          the configuration, never null
   */
  public void configure( final Configuration config ) {
    if ( config == null ) {
      throw new NullPointerException( "The given configuration is null" );
    }
    if ( this.config != null ) {
      // already configured ... ignored
      return;
    }

    this.config = config;
    final Iterator it = this.classes.values().iterator();
    while ( it.hasNext() ) {
      final ObjectDescription od = (ObjectDescription) it.next();
      od.configure( config );
    }
  }

  /**
   * Returns the currently set configuration or null, if none was set.
   *
   * @return the configuration.
   */
  public Configuration getConfig() {
    return this.config;
  }

  /**
   * Tests for equality.
   *
   * @param o
   *          the object to test.
   * @return A boolean.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof ClassFactoryImpl ) ) {
      return false;
    }

    final ClassFactoryImpl classFactory = (ClassFactoryImpl) o;

    if ( !this.classes.equals( classFactory.classes ) ) {
      return false;
    }

    return true;
  }

  /**
   * Returns a hash code.
   *
   * @return A hash code.
   */
  public int hashCode() {
    return this.classes.hashCode();
  }
}

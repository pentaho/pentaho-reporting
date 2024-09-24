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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey;

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactory;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A style key factory.
 *
 * @author Thomas Morgner
 */
public class StyleKeyFactoryCollector implements StyleKeyFactory {
  /**
   * Storage for the factories.
   */
  private final ArrayList factories;

  /**
   * Creates a new factory.
   */
  public StyleKeyFactoryCollector() {
    factories = new ArrayList();
  }

  /**
   * Adds a factory.
   *
   * @param factory
   *          the factory.
   */
  public void addFactory( final StyleKeyFactory factory ) {
    if ( factory == null ) {
      throw new NullPointerException();
    }
    factories.add( factory );
  }

  /**
   * Returns an iterator that provides access to the factories.
   *
   * @return The iterator.
   */
  public Iterator getFactories() {
    return factories.iterator();
  }

  /**
   * Returns a style key.
   *
   * @param name
   *          the name.
   * @return The style key.
   */
  public StyleKey getStyleKey( final String name ) {
    for ( int i = 0; i < factories.size(); i++ ) {
      final StyleKeyFactory fact = (StyleKeyFactory) factories.get( i );
      final StyleKey o = fact.getStyleKey( name );
      if ( o != null ) {
        return o;
      }
    }
    return null;
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
   * @param cf
   *          the class factory used to create the basic object.
   * @return The object.
   */
  public Object createBasicObject( final StyleKey k, final String value, final Class c, final ClassFactory cf ) {
    for ( int i = 0; i < factories.size(); i++ ) {
      final StyleKeyFactory fact = (StyleKeyFactory) factories.get( i );
      final Object o = fact.createBasicObject( k, value, c, cf );
      if ( o != null ) {
        return o;
      }
    }
    return null;
  }

  /**
   * Returns an iterator that provides access to the registered keys.
   *
   * @return The iterator.
   */
  public Iterator getRegisteredKeys() {
    final ArrayList list = new ArrayList();
    for ( int i = 0; i < factories.size(); i++ ) {
      final StyleKeyFactory f = (StyleKeyFactory) factories.get( i );
      final Iterator keys = f.getRegisteredKeys();
      while ( keys.hasNext() ) {
        list.add( keys.next() );
      }
    }
    return list.iterator();
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
    if ( !( o instanceof StyleKeyFactoryCollector ) ) {
      return false;
    }

    final StyleKeyFactoryCollector styleKeyFactoryCollector = (StyleKeyFactoryCollector) o;

    if ( !factories.equals( styleKeyFactoryCollector.factories ) ) {
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
    return factories.hashCode();
  }
}

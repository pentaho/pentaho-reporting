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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An class that implements the {@link ClassFactory} interface to create Arrays of objects. The object descriptions are
 * created on demand.
 *
 * @author Thomas Morgner.
 */
public class ArrayClassFactory implements ClassFactory {

  /**
   * Default constructor.
   */
  public ArrayClassFactory() {
    super();
  }

  /**
   * Returns an object description for a class.
   *
   * @param c
   *          the class.
   * @return The object description.
   */
  public ObjectDescription getDescriptionForClass( final Class c ) {
    if ( c.isArray() ) {
      return new ArrayObjectDescription( c );
    } else {
      return null;
    }
  }

  /**
   * Returns an object description for the super class of a class. This method always returns null.
   *
   * @param d
   *          the class.
   * @param knownSuperClass
   *          the last known super class or null.
   * @return The object description.
   */
  public ObjectDescription getSuperClassObjectDescription( final Class d, final ObjectDescription knownSuperClass ) {
    return null;
  }

  /**
   * Returns an iterator for the registered classes. This returns a list of pre-registered classes known to this
   * ClassFactory. A class may be able to handle more than the registered classes.
   * <p/>
   * This method exists to support query tools for UI design, do not rely on it for day to day work.
   *
   * @return The iterator.
   */
  public Iterator getRegisteredClasses() {
    final ArrayList l = new ArrayList();
    l.add( Object[].class );
    return l.iterator();
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
    // nothing required
  }

  /**
   * ArrayClassFactories are always equal, there is nothing that could not be equal :)
   *
   * @param o
   *          the other object.
   * @return true, if both object factories describe the same objects, false otherwise.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof ArrayClassFactory ) ) {
      return false;
    }
    return true;
  }

  /**
   * Returns a hash code value for the object. This method is supported for the benefit of hashtables such as those
   * provided by <code>java.util.Hashtable</code>.
   *
   * @return the computed hashcode.
   */
  public int hashCode() {
    return getClass().hashCode();
  }
}

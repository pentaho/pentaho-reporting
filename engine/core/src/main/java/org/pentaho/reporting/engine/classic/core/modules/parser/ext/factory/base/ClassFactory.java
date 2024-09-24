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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import org.pentaho.reporting.libraries.base.config.Configuration;

import java.io.Serializable;
import java.util.Iterator;

/**
 * A class factory.
 *
 * @author Thomas Morgner
 */
public interface ClassFactory extends Serializable {

  /**
   * Returns an object description for a class.
   *
   * @param c
   *          the class.
   * @return The object description.
   */
  public ObjectDescription getDescriptionForClass( Class c );

  /**
   * Returns an object description for the super class of a class.
   *
   * @param d
   *          the class.
   * @param knownSuperClass
   *          the last known super class or null.
   * @return The object description.
   */
  public ObjectDescription getSuperClassObjectDescription( Class d, ObjectDescription knownSuperClass );

  /**
   * Returns an iterator for the registered classes. This returns a list of pre-registered classes known to this
   * ClassFactory. A class may be able to handle more than the registered classes.
   * <p/>
   * This method exists to support query tools for UI design, do not rely on it for day to day work.
   *
   * @return The iterator.
   */
  public Iterator getRegisteredClasses();

  /**
   * Configures this factory. The configuration contains several keys and their defined values. The given reference to
   * the configuration object will remain valid until the report parsing or writing ends.
   * <p/>
   * The configuration contents may change during the reporting.
   *
   * @param config
   *          the configuration, never null
   */
  public void configure( Configuration config );

  /**
   * Compares whether two object factories are equal. This method must be implemented!
   *
   * @param o
   *          the other object.
   * @return true, if both object factories describe the same objects, false otherwise.
   */
  public boolean equals( Object o );

  /**
   * Computes the hashCode for this ClassFactory. As equals() must be implemented, a corresponding hashCode() should be
   * implemented as well.
   *
   * @return the hashcode.
   */
  public int hashCode();
}

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

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactory;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.io.Serializable;
import java.util.Iterator;

/**
 * A style key factory.
 *
 * @author Thomas Morgner
 */
public interface StyleKeyFactory extends Serializable {
  /**
   * Returns a style key.
   *
   * @param name
   *          the name.
   * @return The style key.
   */
  public StyleKey getStyleKey( String name );

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
  public Object createBasicObject( StyleKey k, String value, Class c, ClassFactory cf );

  /**
   * Returns an iterator that provides access to the registered keys.
   *
   * @return The iterator.
   */
  public Iterator getRegisteredKeys();
}

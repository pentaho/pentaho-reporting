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

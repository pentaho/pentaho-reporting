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

package org.pentaho.reporting.libraries.base.config;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * A simple query interface for a configuration.
 *
 * @author Thomas Morgner
 */
public interface Configuration extends Serializable, Cloneable {

  /**
   * Returns the configuration property with the specified key.
   *
   * @param key the property key.
   * @return the property value.
   */
  public String getConfigProperty( String key );

  /**
   * Returns the configuration property with the specified key (or the specified default value if there is no such
   * property).
   * <p/>
   * If the property is not defined in this configuration, the code will lookup the property in the parent
   * configuration.
   *
   * @param key          the property key.
   * @param defaultValue the default value.
   * @return the property value.
   */
  public String getConfigProperty( String key, String defaultValue );

  /**
   * Returns all keys with the given prefix.
   *
   * @param prefix the prefix
   * @return the iterator containing all keys with that prefix
   */
  public Iterator<String> findPropertyKeys( String prefix );

  /**
   * Returns the configuration properties.
   *
   * @return The configuration properties.
   */
  public Enumeration<String> getConfigProperties();

  /**
   * Returns a clone of the object.
   *
   * @return A clone.
   * @throws CloneNotSupportedException if cloning is not supported for some reason.
   */
  public Object clone() throws CloneNotSupportedException;

}

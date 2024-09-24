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

package org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base;

import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Config storage implementations are used to store a set of properties to a certain key.
 * <p/>
 * A valid configuration path does not contain dots, semicolons or colons.
 * <p/>
 * A valid path obeys to the same rules as java identifiers ..
 *
 * @author Thomas Morgner
 */
public interface ConfigStorage {
  /**
   * Stores the given properties on the defined path.
   *
   * @param configPath
   *          the path on where to store the properties.
   * @param properties
   *          the properties which should be stored.
   * @throws ConfigStoreException
   *           if an error occured.
   */
  public void store( String configPath, Configuration properties ) throws ConfigStoreException;

  /**
   * Loads the properties from the given path, specifying the given properties as default.
   *
   * @param configPath
   *          the configuration path from where to read the properties.
   * @param defaults
   *          the property set that acts as fallback to provide default values.
   * @return the loaded properties
   * @throws ConfigStoreException
   *           if an error occured.
   */
  public Configuration load( String configPath, Configuration defaults ) throws ConfigStoreException;

  /**
   * Tests, whether some configuration data exists for the given configuration.
   *
   * @param configPath
   *          the configuration path to the property storage.
   * @return true, if there are properties under this path, false otherwise.
   */
  public boolean isAvailable( String configPath );
}

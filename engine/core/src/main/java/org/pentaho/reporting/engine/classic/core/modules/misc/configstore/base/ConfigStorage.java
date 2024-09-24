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

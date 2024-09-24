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
 * An empty default implementation. This config storare will not store any values and will provide no read access to
 * stored properties by denying their existence.
 *
 * @author Thomas Morgner
 */
public class NullConfigStorage implements ConfigStorage {
  /**
   * DefaultConstructor.
   */
  public NullConfigStorage() {
  }

  /**
   * This method does nothing.
   *
   * @param configPath
   *          this parameter is not used.
   * @param properties
   *          this parameter is not used.
   * @see org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStorage#store (java.lang.String,
   *      java.util.Properties)
   */
  public void store( final String configPath, final Configuration properties ) {
  }

  /**
   * Loads the properties from the given path, specifying the given properties as default.
   * <p/>
   * This implementation will always throw and ConfigStoreException as the specified resource is not available.
   *
   * @param configPath
   *          the configuration path from where to read the properties.
   * @param defaults
   *          the property set that acts as fallback to provide default values.
   * @return the loaded properties
   * @throws ConfigStoreException
   *           always throws this exception as the specified resource will be not available.
   */
  public Configuration load( final String configPath, final Configuration defaults ) throws ConfigStoreException {
    throw new ConfigStoreException( "This configuration path is not available." ); //$NON-NLS-1$
  }

  /**
   * Tests, whether some configuration data exists for the given configuration.
   * <p/>
   * This method returns always false and denies the existence of any resource.
   *
   * @param configPath
   *          the configuration path to the property storage.
   * @return always false as this implementation does not store anything.
   */
  public boolean isAvailable( final String configPath ) {
    return false;
  }
}

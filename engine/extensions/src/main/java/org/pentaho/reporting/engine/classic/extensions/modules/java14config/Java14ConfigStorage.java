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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.java14config;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStorage;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStoreException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;

/**
 * A configuration storage provider which stores the entries using the JDK 1.4 configuration API.
 *
 * @author Thomas Morgner
 */
public class Java14ConfigStorage implements ConfigStorage {
  /**
   * The preferences node used to store the configuration.
   */
  private Preferences base;

  /**
   * Creates a new storage, which uses the given preferences node as base for all operations.
   *
   * @param base
   *          the base node.
   */
  public Java14ConfigStorage( final Preferences base ) {
    this.base = base;
  }

  /**
   * Stores the given properties on the defined path.
   *
   * @param configPath
   *          the path on where to store the properties.
   * @param config
   *          the properties which should be stored.
   * @throws ConfigStoreException
   *           if an error occurred.
   */
  public void store( final String configPath, final Configuration config ) throws ConfigStoreException {
    if ( ConfigFactory.isValidPath( configPath ) == false ) {
      throw new IllegalArgumentException( "The give path is not valid." );
    }

    try {
      final Enumeration keys = config.getConfigProperties();
      final Preferences pref = base.node( configPath );
      pref.clear();
      while ( keys.hasMoreElements() ) {
        final String key = (String) keys.nextElement();
        final String value = config.getConfigProperty( key );
        if ( value != null ) {
          pref.put( key, value );
        }
      }
      pref.sync();
    } catch ( BackingStoreException be ) {
      throw new ConfigStoreException( "Failed to store config" + configPath, be );
    }
  }

  /**
   * Loads the properties from the given path, specifying the given properties as default.
   *
   * @param configPath
   *          the configuration path from where to read the properties.
   * @param defaults
   *          the property set that acts as fallback to provide default values.
   * @return the loaded properties
   * @throws ConfigStoreException
   *           if an error occurred.
   */
  public Configuration load( final String configPath, final Configuration defaults ) throws ConfigStoreException {
    if ( ConfigFactory.isValidPath( configPath ) == false ) {
      throw new IllegalArgumentException( "The give path is not valid." );
    }

    try {
      final Properties props = new Properties();
      final Preferences pref = base.node( configPath );
      final String[] keysArray = pref.keys();
      for ( int i = 0; i < keysArray.length; i++ ) {
        final String key = keysArray[i];
        final String value = pref.get( key, null );
        if ( value != null ) {
          props.setProperty( key, value );
        }
      }

      final ModifiableConfiguration config = new HierarchicalConfiguration( defaults );
      final Iterator keys = props.keySet().iterator();
      while ( keys.hasNext() ) {
        final String key = (String) keys.next();
        config.setConfigProperty( key, props.getProperty( key ) );
      }
      return config;
    } catch ( BackingStoreException be ) {
      throw new ConfigStoreException( "Failed to load config" + configPath, be );
    }
  }

  /**
   * Tests, whether some configuration data exists for the given configuration.
   *
   * @param configPath
   *          the configuration path to the property storage.
   * @return true, if there are properties under this path, false otherwise.
   */
  public boolean isAvailable( final String configPath ) {
    if ( ConfigFactory.isValidPath( configPath ) == false ) {
      throw new IllegalArgumentException( "The give path is not valid." );
    }

    try {
      return base.nodeExists( configPath );
    } catch ( BackingStoreException bse ) {
      return false;
    }
  }
}

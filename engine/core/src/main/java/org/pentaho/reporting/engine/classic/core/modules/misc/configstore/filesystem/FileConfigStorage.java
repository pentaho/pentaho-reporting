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

package org.pentaho.reporting.engine.classic.core.modules.misc.configstore.filesystem;

import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStorage;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStoreException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

/**
 * The FileConfigStorage is a storage provider that stores its content on the local filesystem. The directory used
 * contains the data as plain text property files.
 *
 * @author Thomas Morgner
 */
public class FileConfigStorage implements ConfigStorage {
  /**
   * The base directory of the storage provider.
   */
  private final File baseDirectory;
  /**
   * The configuration header text that is appended to all property files.
   */
  private static final String CONFIGHEADER = "part of the Pentaho Reporting filesystem config store"; //$NON-NLS-1$

  /**
   * Creates a new file config storage and stores the contents in the given directory.
   *
   * @param baseDirectory
   *          the directory that should contain the files.
   */
  public FileConfigStorage( final File baseDirectory ) {
    this.baseDirectory = baseDirectory;
  }

  /**
   * Stores the given properties on the defined path.
   * <p/>
   * This implementation stores the data as property files.
   *
   * @param configPath
   *          the configuration path that specifies where to store the properties.
   * @param config
   *          the configuration, that should be stored.
   * @throws ConfigStoreException
   *           if an error occured.
   * @see org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStorage
   *      #storeProperties(java.lang.String, java.util.Properties)
   */
  public void store( final String configPath, final Configuration config ) throws ConfigStoreException {
    if ( ConfigFactory.isValidPath( configPath ) == false ) {
      throw new IllegalArgumentException( "The give path is not valid." ); //$NON-NLS-1$
    }
    final Enumeration keys = config.getConfigProperties();
    final Properties properties = new Properties();
    while ( keys.hasMoreElements() ) {
      final String key = (String) keys.nextElement();
      final String value = config.getConfigProperty( key );
      if ( value != null && key != null ) {
        properties.setProperty( key, value );
      }
    }

    final File target = new File( baseDirectory, configPath );
    if ( target.exists() == true && target.canWrite() == false ) {
      return;
    }
    try {
      final OutputStream out = new BufferedOutputStream( new FileOutputStream( target ) );
      try {
        properties.store( out, FileConfigStorage.CONFIGHEADER );
      } finally {
        out.close();
      }
    } catch ( Exception e ) {
      throw new ConfigStoreException( "Failed to write config " + configPath, e ); //$NON-NLS-1$
    }
  }

  /**
   * Loads the properties from the given path, specifying the given properties as default.
   *
   * @param configPath
   *          the configuration path from where to load the properties.
   * @param defaults
   *          the property set that acts as fallback to provide default values.
   * @return the loaded properties.
   * @throws ConfigStoreException
   *           if an error occured.
   */
  public Configuration load( final String configPath, final Configuration defaults ) throws ConfigStoreException {
    if ( ConfigFactory.isValidPath( configPath ) == false ) {
      throw new IllegalArgumentException( "The given path is not valid." ); //$NON-NLS-1$
    }
    try {
      final Properties properties = new Properties();
      final File target = new File( baseDirectory, configPath );
      final InputStream in = new BufferedInputStream( new FileInputStream( target ) );
      try {
        properties.load( in );
      } finally {
        in.close();
      }

      final ModifiableConfiguration config = new HierarchicalConfiguration( defaults );
      final Iterator keys = properties.keySet().iterator();
      while ( keys.hasNext() ) {
        final String key = (String) keys.next();
        config.setConfigProperty( key, properties.getProperty( key ) );
      }
      return config;
    } catch ( Exception e ) {
      throw new ConfigStoreException( "Failed to read config" + configPath, e ); //$NON-NLS-1$
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
      throw new IllegalArgumentException( "The give path is not valid." ); //$NON-NLS-1$
    }

    final File target = new File( baseDirectory, configPath );
    return target.exists() && target.canRead();
  }

  public String toString() {
    return "FileConfigStorage={baseDir=" + baseDirectory + '}'; //$NON-NLS-1$ //$NON-NLS-2$
  }
}

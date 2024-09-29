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


package org.pentaho.reporting.engine.classic.core.modules.misc.configstore.filesystem;

import java.io.File;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigFactory;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializer;

/**
 * The initializer is used to setup the file system storage provider and to register the providers at the configfactory.
 * <p/>
 * The directories are specified in the report configuration at boot time. If an directory name starts with "~/", the
 * users home directory is used as base directory for that string.
 *
 * @author Thomas Morgner
 */
public class FileConfigStoreModuleInitializer implements ModuleInitializer {
  /**
   * The configuration key that specifies the base directory for the user configuration storage.
   */
  public static final String USER_BASEDIR_CONFIG_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.misc.configstore.filesystem.UserTargetDir"; //$NON-NLS-1$

  /**
   * The configuration key that specifies the base directory for the system configuration storage.
   */
  public static final String SYSTEM_BASEDIR_CONFIG_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.misc.configstore.filesystem.SystemTargetDir"; //$NON-NLS-1$

  /**
   * DefaultConstructor.
   */
  public FileConfigStoreModuleInitializer() {
  }

  /**
   * Performs the module initialization and registers the storage providers at the config factory.
   *
   * @throws ModuleInitializeException
   *           if an error occures
   */
  public void performInit() throws ModuleInitializeException {
    final String userBaseDirectory =
        ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            FileConfigStoreModuleInitializer.USER_BASEDIR_CONFIG_KEY, "~/.pentaho/classic-engine/user" ); //$NON-NLS-1$

    final String systemBaseDirectory =
        ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            FileConfigStoreModuleInitializer.SYSTEM_BASEDIR_CONFIG_KEY, "~/.pentaho/classic-engine/system" ); //$NON-NLS-1$

    final ConfigFactory factory = ConfigFactory.getInstance();
    factory.defineUserStorage( new FileConfigStorage( getStoragePath( userBaseDirectory ) ) );
    factory.defineSystemStorage( new FileConfigStorage( getStoragePath( systemBaseDirectory ) ) );
  }

  /**
   * Tries to find the specified directory and creates a new one if the directory does not yet exist. An occurence of
   * "~/" at the beginning of the name will be replaced with the users home directory.
   *
   * @param baseDirectory
   *          the base directory as specified in the configuration.
   * @return the file object pointing to that directory.
   * @throws ModuleInitializeException
   *           if an error occurred or the directory could not be created.
   * @throws IllegalArgumentException
   *           if the base directory is null.
   */
  private File getStoragePath( String baseDirectory ) throws ModuleInitializeException {
    if ( baseDirectory == null ) {
      throw new IllegalArgumentException( "The base directory must not be null" ); //$NON-NLS-1$
    }

    final File baseDirectoryFile;
    if ( baseDirectory.startsWith( "~/" ) == false ) { //$NON-NLS-1$
      baseDirectoryFile = new File( baseDirectory );
    } else {
      try {
        final String homeDirectory = System.getProperty( "user.home" ); //$NON-NLS-1$
        if ( "~/".equals( baseDirectory ) ) { //$NON-NLS-1$
          baseDirectoryFile = new File( homeDirectory );
        } else {
          baseDirectory = baseDirectory.substring( 2 );
          baseDirectoryFile = new File( homeDirectory, baseDirectory );
        }
      } catch ( Exception e ) {
        throw new ModuleInitializeException( "Failed to create the file config storage.", e ); //$NON-NLS-1$
      }
    }

    if ( baseDirectoryFile.exists() == false ) {
      if ( baseDirectoryFile.mkdirs() == false ) {
        throw new ModuleInitializeException( "Unable to create the specified directory." ); //$NON-NLS-1$
      }
    } else {
      if ( baseDirectoryFile.canRead() == false || baseDirectoryFile.canWrite() == false ) {
        throw new ModuleInitializeException( "Unable to access the specified directory." ); //$NON-NLS-1$
      }
    }
    return baseDirectoryFile;
  }
}

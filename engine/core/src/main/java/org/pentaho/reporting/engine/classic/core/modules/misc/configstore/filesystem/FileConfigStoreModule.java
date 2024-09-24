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

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The module definition for the filesystem config storage module. This module provides an configuration store
 * implementation that saves all properties to an configurable directory on the filesystem.
 *
 * @author Thomas Morgner
 */
public class FileConfigStoreModule extends AbstractModule {
  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public FileConfigStoreModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    final String value =
        subSystem.getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.ConfigStore", "<not defined>" ); //$NON-NLS-1$ //$NON-NLS-2$
    if ( value.equals( FileConfigStorage.class.getName() ) ) {
      performExternalInitialize( FileConfigStoreModuleInitializer.class.getName(), FileConfigStoreModule.class );
    }
  }
}

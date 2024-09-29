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


package org.pentaho.reporting.libraries.resourceloader;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.boot.PackageManager;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class LibLoaderBoot extends AbstractBoot {
  private static LibLoaderBoot singleton;

  public static LibLoaderBoot getInstance() {
    if ( singleton == null ) {
      singleton = new LibLoaderBoot();
    }
    return singleton;
  }

  private LibLoaderBoot() {
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return LibLoaderInfo.getInstance();
  }

  /**
   * Loads the configuration.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/resourceloader/loader.properties",
        "/loader.properties", true, LibLoaderBoot.class );
  }

  /**
   * Performs the boot.
   */
  protected void performBoot() {
    final PackageManager packageManager = getPackageManager();
    packageManager.load( "org.pentaho.reporting.libraries.resourceloader.modules." );
    packageManager.initializeModules();
  }
}

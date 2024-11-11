/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.base;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.SystemInformation;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * An utility class to safely boot and initialize the LibBase library.
 *
 * @author : Thomas Morgner
 */
public class LibBaseBoot extends AbstractBoot {
  /**
   * A singleton variable for the booter.
   */
  private static LibBaseBoot instance;

  /**
   * Returns the singleton instance of LibBaseBoot.
   *
   * @return the boot class for Libbase.
   */
  public static synchronized LibBaseBoot getInstance() {
    if ( instance == null ) {
      instance = new LibBaseBoot();
    }
    return instance;
  }

  /**
   * Private constructor prevents object creation.
   */
  private LibBaseBoot() {
  }

  /**
   * Loads the configuration for LibBase. This will be called exactly once. The configuration is loaded from a file
   * called "libbase.properties" located next to this class. A user overridable properties file is searched on the
   * classpath within all libraries using the name "/libbase.properties".
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/base/libbase.properties",
        "/libbase.properties", true, LibBaseBoot.class );
  }

  /**
   * Performs the boot. This method does nothing.
   */
  protected void performBoot() {
    // nothing required. Just gather the configuration.
    new SystemInformation().logSystemInformation();
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return LibBaseInfo.getInstance();
  }


}

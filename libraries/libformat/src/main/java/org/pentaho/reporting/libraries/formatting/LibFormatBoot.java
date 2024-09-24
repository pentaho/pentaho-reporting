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

package org.pentaho.reporting.libraries.formatting;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * The LibFormatBoot class is used to initialize the library before it is first used. This loads all configurations and
 * initializes all factories.
 * <p/>
 * Without booting, basic services like logging and the global configuration will not be availble.
 *
 * @author Thomas Morgner
 */
public class LibFormatBoot extends AbstractBoot {
  private static LibFormatBoot instance;

  /**
   * Returns the singleton instance of the boot-class.
   *
   * @return the singleton booter.
   */
  public static synchronized LibFormatBoot getInstance() {
    if ( LibFormatBoot.instance == null ) {
      LibFormatBoot.instance = new LibFormatBoot();
    }
    return LibFormatBoot.instance;
  }

  /**
   * Private constructor prevents object creation.
   */
  private LibFormatBoot() {
  }

  /**
   * Loads the configuration.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/formatting/libformat.properties",
        "/libformat.properties", true, LibFormatBoot.class );
  }

  /**
   * Performs the boot.
   */
  protected void performBoot() {
    // nothing required. Just gather the configuration.
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return LibFormatInfo.getInstance();
  }
}

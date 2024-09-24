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

package org.pentaho.reporting.libraries.xmlns;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * The LibXmlBoot class is used to initialize the library before it is first used. This loads all configurations and
 * initializes all factories.
 * <p/>
 * Without booting, basic services like logging and the global configuration will not be availble.
 *
 * @author Thomas Morgner
 */
public class LibXmlBoot extends AbstractBoot {
  private static LibXmlBoot singleton;

  /**
   * Returns the singleton instance of the boot-class.
   *
   * @return the singleton booter.
   */
  public static synchronized LibXmlBoot getInstance() {
    if ( singleton == null ) {
      singleton = new LibXmlBoot();
    }
    return singleton;
  }

  /**
   * Private constructor prevents object creation.
   */
  private LibXmlBoot() {
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return LibXmlInfo.getInstance();
  }

  /**
   * Loads the configuration.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/xmlns/libxml.properties",
        "/libxml.properties", true, LibXmlBoot.class );
  }

  /**
   * Performs the boot.
   */
  protected void performBoot() {
  }
}

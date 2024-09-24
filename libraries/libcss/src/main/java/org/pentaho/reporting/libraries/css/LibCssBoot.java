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

package org.pentaho.reporting.libraries.css;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;


/**
 * Creation-Date: 27.07.2007, 12:59:38
 *
 * @author Thomas Morgner
 */
public class LibCssBoot extends AbstractBoot {

  private static LibCssBoot singleton;

  /**
   * Returns the singleton instance of the boot-class.
   *
   * @return the singleton booter.
   */
  public static synchronized LibCssBoot getInstance() {
    if ( singleton == null ) {
      singleton = new LibCssBoot();
    }
    return singleton;
  }

  /**
   * Private constructor prevents object creation.
   */
  private LibCssBoot() {
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return LibCssInfo.getInstance();
  }

  /**
   * Loads the configuration.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/css/libcss.properties",
        "/libcss.properties", true, LibCssBoot.class );
  }

  /**
   * Performs the boot.
   */
  protected void performBoot() {
    StyleKeyRegistry.performBoot();
  }
}

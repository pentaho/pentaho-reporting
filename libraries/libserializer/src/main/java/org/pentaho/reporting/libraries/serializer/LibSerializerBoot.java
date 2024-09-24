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

package org.pentaho.reporting.libraries.serializer;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * The boot class guarantees a controlled initialization of the library.
 *
 * @author Thomas Morgner
 */
public class LibSerializerBoot extends AbstractBoot {
  private static LibSerializerBoot instance;

  /**
   * Returns a singleton instance of the boot class.
   *
   * @return the singleton booter.
   */
  public static synchronized LibSerializerBoot getInstance() {
    if ( instance == null ) {
      instance = new LibSerializerBoot();
    }
    return instance;
  }

  /**
   * Private constructor to prevent object creation.
   */
  private LibSerializerBoot() {
  }

  /**
   * Loads the configuration. This will be called exactly once.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/serializer/libserializer.properties",
        "/libserializer.properties", true, LibSerializerBoot.class );
  }

  /**
   * Performs the boot. This method is empty, as this library does not require any manual initializations.
   */
  protected void performBoot() {

  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return LibSerializerInfo.getInstance();
  }
}

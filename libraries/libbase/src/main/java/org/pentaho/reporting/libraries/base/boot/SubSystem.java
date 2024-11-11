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


package org.pentaho.reporting.libraries.base.boot;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;

/**
 * A sub-system holds a separate collection of modules.
 * <p/>
 * On a simple level, subsystems can be just libraries. Libraries offering services need a controlled way to initialize
 * these services before dependent code starts using the library. This can be achived by embedding the library services
 * into an own subsystem.
 *
 * @author Thomas Morgner
 */
public interface SubSystem {

  /**
   * Returns the global configuration.
   *
   * @return The global configuration.
   */
  public Configuration getGlobalConfig();

  /**
   * Returns the global configuration as ExtendedConfiguration instance.
   *
   * @return the extended configuration.
   */
  public ExtendedConfiguration getExtendedConfig();

  /**
   * Returns the package manager.
   *
   * @return The package manager.
   */
  public PackageManager getPackageManager();

}

/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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

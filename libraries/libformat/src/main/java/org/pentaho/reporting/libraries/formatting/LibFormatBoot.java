/*
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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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

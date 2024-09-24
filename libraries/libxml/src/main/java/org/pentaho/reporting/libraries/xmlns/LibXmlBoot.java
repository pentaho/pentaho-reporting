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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

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

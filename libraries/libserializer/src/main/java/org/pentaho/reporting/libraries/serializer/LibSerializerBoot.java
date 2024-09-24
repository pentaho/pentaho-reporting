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

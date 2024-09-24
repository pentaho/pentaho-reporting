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

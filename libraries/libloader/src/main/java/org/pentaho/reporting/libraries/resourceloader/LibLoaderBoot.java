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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.boot.PackageManager;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class LibLoaderBoot extends AbstractBoot {
  private static LibLoaderBoot singleton;

  public static LibLoaderBoot getInstance() {
    if ( singleton == null ) {
      singleton = new LibLoaderBoot();
    }
    return singleton;
  }

  private LibLoaderBoot() {
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return LibLoaderInfo.getInstance();
  }

  /**
   * Loads the configuration.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/resourceloader/loader.properties",
        "/loader.properties", true, LibLoaderBoot.class );
  }

  /**
   * Performs the boot.
   */
  protected void performBoot() {
    final PackageManager packageManager = getPackageManager();
    packageManager.load( "org.pentaho.reporting.libraries.resourceloader.modules." );
    packageManager.initializeModules();
  }
}

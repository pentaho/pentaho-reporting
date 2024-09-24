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

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

/**
 * The Module info class encapsulates metadata about a given module. It holds the list of dependencies and the module
 * version and description.
 *
 * @author Thomas Morgner
 */
public interface ModuleInfo {

  /**
   * Returns the module class of the desired base module.
   *
   * @return The module class.
   */
  public String getModuleClass();

  /**
   * Returns the major version of the base module. The string should contain a compareable character sequence so that
   * higher versions of the module are considered greater than lower versions.
   *
   * @return The major version of the module.
   */
  public String getMajorVersion();

  /**
   * Returns the minor version of the base module. The string should contain a compareable character sequence so that
   * higher versions of the module are considered greater than lower versions.
   *
   * @return The minor version of the module.
   */
  public String getMinorVersion();

  /**
   * Returns the patchlevel version of the base module. The patch level should be used to mark bugfixes. The string
   * should contain a compareable character sequence so that higher versions of the module are considered greater than
   * lower versions.
   *
   * @return The patch level version of the module.
   */
  public String getPatchLevel();

}

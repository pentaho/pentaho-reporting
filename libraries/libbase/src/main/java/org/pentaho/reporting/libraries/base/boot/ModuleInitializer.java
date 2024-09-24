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
 * The module initializer is used to separate the initialization process from the module definition. An invalid
 * classpath setup or an missing base module may throw an ClassCastException if the module class references this missing
 * resource. Separating them is the best way to make sure that the classloader does not interrupt the module loading
 * process.
 *
 * @author Thomas Morgner
 */
public interface ModuleInitializer {

  /**
   * Performs the initalization of the module.
   *
   * @throws ModuleInitializeException if an error occurs which prevents the module from being usable.
   */
  public void performInit() throws ModuleInitializeException;
}

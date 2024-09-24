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

package org.pentaho.reporting.libraries.formula.function;

/**
 * This category is used when a problem occured while loading a function metadata category. It usualy points a problem
 * in the configuration file or a missing function metadata class.
 *
 * @author Cedric Pronzato
 */
public class InvalidFunctionCategory extends AbstractFunctionCategory {
  public static final FunctionCategory CATEGORY = new InvalidFunctionCategory();

  private InvalidFunctionCategory() {
    super( "org.pentaho.reporting.libraries.formula.function.invalid.category" );
  }
}

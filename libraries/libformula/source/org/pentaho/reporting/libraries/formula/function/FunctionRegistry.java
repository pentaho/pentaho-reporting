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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function;

/**
 * The function registry contains all information about all function available.
 * It is also the central point from where to get function meta-data or where
 * to instantiate functions.
 *
 * All functions are queried by their cannonical name.
 *
 * @author Thomas Morgner
 */
public interface FunctionRegistry
{
  public FunctionCategory[] getCategories();
  public Function[] getFunctions();
  public Function[] getFunctionsByCategory(FunctionCategory category);
  public String[] getFunctionNames();
  public String[] getFunctionNamesByCategory(FunctionCategory category);

  public Function createFunction(String name);
  public FunctionDescription getMetaData (String name);
}

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

package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;

/**
 * Creation-Date: 04.11.2006, 18:59:11
 *
 * @author Gunter Rombauts
 */
public class YesterdayFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 7674329993702445275L;

  public YesterdayFunctionDescription() {
    super( "YESTERDAY", "org.pentaho.reporting.libraries.formula.function.datetime.Yesterday-Function" );
  }

  public Type getValueType() {
    return DateTimeType.DATE_TYPE;
  }

  public boolean isVolatile() {
    return true;
  }

  public int getParameterCount() {
    return 0;
  }

  public Type getParameterType( final int position ) {
    return null;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @return
   */
  public boolean isParameterMandatory( final int position ) {
    return true;
  }

  public FunctionCategory getCategory() {
    return DateTimeFunctionCategory.CATEGORY;
  }
}

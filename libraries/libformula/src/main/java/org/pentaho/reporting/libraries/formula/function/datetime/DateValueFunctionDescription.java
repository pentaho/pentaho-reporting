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
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Describes DateValueFunction function.
 *
 * @author Cedric Pronzato
 * @see DateValueFunction
 */
public class DateValueFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 3110217290825835653L;

  public DateValueFunctionDescription() {
    super( "DATEVALUE", "org.pentaho.reporting.libraries.formula.function.datetime.DateValue-Function" );
  }

  public Type getValueType() {
    return DateTimeType.DATE_TYPE;
  }

  public int getParameterCount() {
    return 1;
  }

  public Type getParameterType( final int position ) {
    return TextType.TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    return true;
  }

  public FunctionCategory getCategory() {
    return DateTimeFunctionCategory.CATEGORY;
  }
}

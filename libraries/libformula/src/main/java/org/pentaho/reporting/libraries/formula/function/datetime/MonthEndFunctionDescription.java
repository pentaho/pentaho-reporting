/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;

/**
 * Describes MonthEndFunction function.
 *
 * @author Gunter Rombauts
 * @see MonthEndFunction
 */
public class MonthEndFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = -2974907364693600555L;

  public MonthEndFunctionDescription() {
    super( "MONTHEND", "org.pentaho.reporting.libraries.formula.function.datetime.MonthEnd-Function" );
  }

  public Type getValueType() {
    return DateTimeType.DATE_TYPE;
  }

  public int getParameterCount() {
    return 1;
  }

  public Type getParameterType( final int position ) {
    return DateTimeType.DATE_TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    return true;
  }

  public FunctionCategory getCategory() {
    return DateTimeFunctionCategory.CATEGORY;
  }
}

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
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * Describes WeekDayFunction function.
 *
 * @author Cedric Pronzato
 * @see WeekDayFunction
 */
public class WeekDayFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = -2974907368693600555L;

  public WeekDayFunctionDescription() {
    super( "WEEKDAY", "org.pentaho.reporting.libraries.formula.function.datetime.WeekDay-Function" );
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public int getParameterCount() {
    return 2;
  }

  public Type getParameterType( final int position ) {
    if ( position == 0 ) {
      return DateTimeType.DATE_TYPE;
    }
    return NumberType.GENERIC_NUMBER;
  }

  public boolean isParameterMandatory( final int position ) {
    if ( position == 0 ) {
      return true;
    } else {
      return false;
    }
  }

  public FunctionCategory getCategory() {
    return DateTimeFunctionCategory.CATEGORY;
  }
}

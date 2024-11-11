/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * Describes HourFunction function.
 *
 * @author Cedric Pronzato
 * @see HourFunction
 */
public class HourFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 7083009440959512989L;

  public HourFunctionDescription() {
    super( "HOUR", "org.pentaho.reporting.libraries.formula.function.datetime.Hour-Function" );
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public int getParameterCount() {
    return 1;
  }

  public Type getParameterType( final int position ) {
    return DateTimeType.TIME_TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    return true;
  }

  public FunctionCategory getCategory() {
    return DateTimeFunctionCategory.CATEGORY;
  }
}

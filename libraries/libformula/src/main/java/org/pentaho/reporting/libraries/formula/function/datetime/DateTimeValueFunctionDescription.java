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
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Describes DateValueFunction function.
 *
 * @author Cedric Pronzato
 * @see org.pentaho.reporting.libraries.formula.function.datetime.DateValueFunction
 */
public class DateTimeValueFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 3110217290825835653L;

  public DateTimeValueFunctionDescription() {
    super( "DATETIMEVALUE", "org.pentaho.reporting.libraries.formula.function.datetime.DateTimeValue-Function" );
  }

  public Type getValueType() {
    return DateTimeType.DATETIME_TYPE;
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

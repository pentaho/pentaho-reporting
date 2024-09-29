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
 * Creation-Date: 04.11.2006, 18:59:11
 *
 * @author Gunter Rombauts
 */
public class PrevWeekdayFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 767559573702445275L;

  public PrevWeekdayFunctionDescription() {
    super( "PREVWEEKDAY", "org.pentaho.reporting.libraries.formula.function.datetime.PrevWeekday-Function" );
  }

  public Type getValueType() {
    return DateTimeType.DATE_TYPE;
  }


  public boolean isVolatile() {
    return true;
  }

  public int getParameterCount() {
    return 1;
  }

  public Type getParameterType( final int position ) {
    return NumberType.GENERIC_NUMBER;
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

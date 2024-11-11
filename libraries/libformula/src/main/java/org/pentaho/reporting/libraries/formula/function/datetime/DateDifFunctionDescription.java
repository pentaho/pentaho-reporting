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
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Describes DateDifFunction function.
 *
 * @author Cedric Pronzato
 * @see DateDifFunction
 */
public class DateDifFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 2428452931728654158L;

  public DateDifFunctionDescription() {
    super( "DATEDIF", "org.pentaho.reporting.libraries.formula.function.datetime.DateDif-Function" );
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public int getParameterCount() {
    return 3;
  }

  public Type getParameterType( final int position ) {
    if ( position == 2 ) {
      return TextType.TYPE;
    }
    return DateTimeType.DATE_TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    return true;
  }

  public FunctionCategory getCategory() {
    return DateTimeFunctionCategory.CATEGORY;
  }
}

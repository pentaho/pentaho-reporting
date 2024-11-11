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


package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * Describes AbsFunction function.
 *
 * @author Cedric Pronzato
 * @see AbsFunction
 */
public class AbsFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 743117553650008440L;

  public AbsFunctionDescription() {
    super( "ABS", "org.pentaho.reporting.libraries.formula.function.math.Abs-Function" );
  }

  public FunctionCategory getCategory() {
    return MathFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 1;
  }

  public Type getParameterType( final int position ) {
    return NumberType.GENERIC_NUMBER;
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public boolean isParameterMandatory( final int position ) {
    return true;
  }

}

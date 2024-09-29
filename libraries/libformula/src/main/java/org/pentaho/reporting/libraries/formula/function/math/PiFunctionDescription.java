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


package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * Describes PiFunction function.
 *
 * @author ocke
 * @see PiFunction
 */
public class PiFunctionDescription extends AbstractFunctionDescription {

  public PiFunctionDescription() {
    super( "PI", "org.pentaho.reporting.libraries.formula.function.math.Pi-Function" );
  }

  public FunctionCategory getCategory() {
    return MathFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 0;
  }

  public Type getParameterType( final int position ) {
    return null;
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public boolean isParameterMandatory( final int position ) {
    return false;
  }

}

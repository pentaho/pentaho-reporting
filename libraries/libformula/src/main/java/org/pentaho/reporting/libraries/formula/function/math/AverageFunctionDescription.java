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
 * Describes AverageFunction function.
 *
 * @author Cedric Pronzato
 * @see AverageAFunction
 */
public class AverageFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 1190836694344833890L;

  public AverageFunctionDescription() {
    super( "AVERAGE", "org.pentaho.reporting.libraries.formula.function.math.Average-Function" );
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public int getParameterCount() {
    return 0;
  }

  @Override
  public boolean isInfiniteParameterCount() {
    return true;
  }

  public Type getParameterType( final int position ) {
    return NumberType.GENERIC_NUMBER;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @return whether the parameter at the given position is mandatory.
   */
  public boolean isParameterMandatory( final int position ) {
    return false;
  }

  public FunctionCategory getCategory() {
    return MathFunctionCategory.CATEGORY;
  }
}

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

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.userdefined.UserDefinedFunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * The function-description class for the IsExportTypeFunction. This class holds meta-data for the formula function.
 *
 * @author Thomas Morgner
 */
public class ParameterTextFunctionDescription extends AbstractFunctionDescription {
  /**
   * Default Constructor.
   */
  public ParameterTextFunctionDescription() {
    super( "PARAMETERTEXT", "org.pentaho.reporting.engine.classic.core.function.formula.ParameterText-Function" );
  }

  /**
   * Returns the expected value type. This function returns a LogicalType.
   *
   * @return LogicalType.TYPE
   */
  public Type getValueType() {
    return TextType.TYPE;
  }

  /**
   * Returns the number of parameters expected by the function.
   *
   * @return 1.
   */
  public int getParameterCount() {
    return 3;
  }

  /**
   * Returns the parameter type of the function parameters.
   *
   * @param position
   *          the parameter index.
   * @return always TextType.TYPE.
   */
  public Type getParameterType( final int position ) {
    if ( position == 1 ) {
      return LogicalType.TYPE;
    }
    if ( position == 2 ) {
      return TextType.TYPE;
    }
    return AnyType.TYPE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @param position
   *          the position of the parameter.
   * @return true, as all parameters are mandatory.
   */
  public boolean isParameterMandatory( final int position ) {
    return position == 0;
  }

  /**
   * Returns the function category. The function category groups functions by their expected use.
   *
   * @return InformationFunctionCategory.CATEGORY.
   */
  public FunctionCategory getCategory() {
    return UserDefinedFunctionCategory.CATEGORY;
  }
}

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


package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.information.InformationFunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * The function-description class for the IsExportTypeFunction. This class holds meta-data for the formula function.
 *
 * @author Thomas Morgner
 */
public class RowCountFunctionDescription extends AbstractFunctionDescription {
  /**
   * Default Constructor.
   */
  public RowCountFunctionDescription() {
    super( "ROWCOUNT", "org.pentaho.reporting.engine.classic.core.function.formula.RowCount-Function" );
  }

  /**
   * Returns the expected value type. This function returns a LogicalType.
   *
   * @return LogicalType.TYPE
   */
  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  /**
   * Returns the number of parameters expected by the function.
   *
   * @return 1.
   */
  public int getParameterCount() {
    return 1;
  }

  /**
   * Returns the parameter type of the function parameters.
   *
   * @param position
   *          the parameter index.
   * @return always TextType.TYPE.
   */
  public Type getParameterType( final int position ) {
    return TextType.TYPE;
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
    return false;
  }

  /**
   * Returns the function category. The function category groups functions by their expected use.
   *
   * @return InformationFunctionCategory.CATEGORY.
   */
  public FunctionCategory getCategory() {
    return InformationFunctionCategory.CATEGORY;
  }
}

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


package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * Describes the CountFunction function.
 *
 * @author Cedric Pronzato
 * @see org.pentaho.reporting.libraries.formula.function.information.CountFunction
 */
public class CountFunctionDescription extends AbstractFunctionDescription {
  public CountFunctionDescription() {
    super( "COUNT", "org.pentaho.reporting.libraries.formula.function.information.Count-Function" );
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public FunctionCategory getCategory() {
    return InformationFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 0;
  }

  public boolean isInfiniteParameterCount() {
    return true;
  }

  /**
   * Returns the parameter type at the given position using the function metadata. The first parameter is at the
   * position 0;
   *
   * @param position The parameter index.
   * @return The parameter type.
   */
  public Type getParameterType( final int position ) {
    return NumberType.NUMBER_SEQUENCE;
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
}


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


package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

public class NormalizeArrayFunctionDescription extends AbstractFunctionDescription {
  public NormalizeArrayFunctionDescription() {
    super( "NORMALIZEARRAY", "org.pentaho.reporting.libraries.formula.function.userdefined.NormalizeArray-Function" );
  }

  public Type getValueType() {
    return AnyType.ANY_ARRAY;
  }

  public FunctionCategory getCategory() {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 2;
  }

  /**
   * Returns the parameter type at the given position using the function metadata. The first parameter is at the
   * position 0;
   *
   * @param position The parameter index.
   * @return The parameter type.
   */
  public Type getParameterType( final int position ) {
    if ( position == 0 ) {
      return AnyType.ANY_SEQUENCE;
    }

    return NumberType.GENERIC_NUMBER;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @return
   */
  public boolean isParameterMandatory( final int position ) {
    if ( position == 0 ) {
      return true;
    }
    return false;
  }
}

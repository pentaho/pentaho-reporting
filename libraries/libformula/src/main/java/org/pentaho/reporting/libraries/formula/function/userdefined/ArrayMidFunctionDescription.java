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


package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

public class ArrayMidFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 2207257367525146799L;

  public ArrayMidFunctionDescription() {
    super( "ARRAYMID", "org.pentaho.reporting.libraries.formula.function.userdefined.ArrayMid-Function" );
  }

  public FunctionCategory getCategory() {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 3;
  }

  public Type getParameterType( final int position ) {
    if ( position == 0 ) {
      return AnyType.ANY_ARRAY;
    }

    return NumberType.GENERIC_NUMBER;
  }

  public Type getValueType() {
    return AnyType.ANY_ARRAY;
  }

  public boolean isParameterMandatory( final int position ) {
    return true;
  }

}


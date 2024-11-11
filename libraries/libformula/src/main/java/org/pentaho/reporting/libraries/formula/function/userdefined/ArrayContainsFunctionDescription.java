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
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

public class ArrayContainsFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 6842849993225519941L;

  public ArrayContainsFunctionDescription() {
    super( "ARRAYCONTAINS", "org.pentaho.reporting.libraries.formula.function.userdefined.ArrayContains-Function" );
  }

  public FunctionCategory getCategory() {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 2;
  }

  public Type getParameterType( final int position ) {
    if ( position == 0 ) {
      return AnyType.ANY_SEQUENCE;
    }
    return AnyType.TYPE;
  }

  public boolean isInfiniteParameterCount() {
    return true;
  }

  public Type getValueType() {
    return LogicalType.TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    return position == 0;
  }
}

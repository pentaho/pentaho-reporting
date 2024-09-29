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


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * Describes LenFunction function.
 *
 * @author Cedric Pronzato
 * @see LenFunction
 */
public class LenFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 2266930971663214932L;

  public LenFunctionDescription() {
    super( "LEN", "org.pentaho.reporting.libraries.formula.function.text.Len-Function" );
  }

  public FunctionCategory getCategory() {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 1;
  }

  public Type getParameterType( final int position ) {
    return AnyType.TYPE;
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public boolean isParameterMandatory( final int position ) {
    return true;
  }

}

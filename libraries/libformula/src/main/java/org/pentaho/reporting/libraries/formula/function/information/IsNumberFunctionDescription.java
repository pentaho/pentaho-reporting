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


package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Describes IsNumberFunction function.
 *
 * @author Cedric Pronzato
 * @see IsNumberFunction
 */
public class IsNumberFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 5013209949963878951L;

  public IsNumberFunctionDescription() {
    super( "ISNUMBER", "org.pentaho.reporting.libraries.formula.function.information.IsNumber-Function" );
  }

  public FunctionCategory getCategory() {
    return InformationFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 1;
  }

  public Type getParameterType( final int position ) {
    return AnyType.TYPE;
  }

  public Type getValueType() {
    return LogicalType.TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    return true;
  }

}

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
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Describes IsLogicalFunction function.
 *
 * @author Cedric Pronzato
 * @see IsLogicalFunction
 */
public class IsLogicalFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = -4814148966968602320L;

  public IsLogicalFunctionDescription() {
    super( "ISLOGICAL", "org.pentaho.reporting.libraries.formula.function.information.IsLogical-Function" );
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

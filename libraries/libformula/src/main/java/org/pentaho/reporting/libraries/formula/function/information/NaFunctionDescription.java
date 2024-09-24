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
import org.pentaho.reporting.libraries.formula.typing.coretypes.ErrorType;


public class NaFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = -7921200887236616400L;

  public NaFunctionDescription() {
    super( "NA", "org.pentaho.reporting.libraries.formula.function.information.Na-Function" );
  }

  public FunctionCategory getCategory() {
    return InformationFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 0;
  }

  public Type getParameterType( final int position ) {
    return null;
  }

  public Type getValueType() {
    return ErrorType.TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    return false;
  }

}

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


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Describes ReplaceFunction function.
 *
 * @author Cedric Pronzato
 * @see ReplaceFunction
 */
public class ReplaceFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = -7429162918022452367L;

  public ReplaceFunctionDescription() {
    super( "REPLACE", "org.pentaho.reporting.libraries.formula.function.text.Replace-Function" );
  }

  public FunctionCategory getCategory() {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 4;
  }

  public Type getParameterType( final int position ) {
    if ( position == 0 || position == 3 ) {
      return TextType.TYPE;
    }
    return NumberType.GENERIC_NUMBER;
  }

  public Type getValueType() {
    return TextType.TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    return true;
  }

}

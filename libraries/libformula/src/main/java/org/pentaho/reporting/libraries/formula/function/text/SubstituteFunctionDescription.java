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
 * Describes SubstituteFunction function.
 *
 * @author Cedric Pronzato
 * @see SubstituteFunction
 */
public class SubstituteFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = -6053755288623137282L;

  public SubstituteFunctionDescription() {
    super( "SUBSTITUTE", "org.pentaho.reporting.libraries.formula.function.text.Substitute-Function" );
  }

  public FunctionCategory getCategory() {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 4;
  }

  public Type getParameterType( final int position ) {
    if ( position != 3 ) {
      return TextType.TYPE;
    }
    return NumberType.GENERIC_NUMBER;
  }

  public Type getValueType() {
    return TextType.TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    if ( position == 3 ) {
      return false;
    }
    return true;
  }

}

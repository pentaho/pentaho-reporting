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
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Describes FindFunction function.
 *
 * @author Cedric Pronzato
 * @see FindFunction
 */
public class FindFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 3300046053895569309L;

  public FindFunctionDescription() {
    super( "FIND", "org.pentaho.reporting.libraries.formula.function.text.Find-Function" );
  }

  public FunctionCategory getCategory() {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 3;
  }

  public Type getParameterType( final int position ) {
    if ( position == 0 || position == 1 ) {
      return TextType.TYPE;
    }
    if ( position == 2 ) {
      return NumberType.GENERIC_NUMBER;
    }
    return null;
  }

  public Type getValueType() {
    return NumberType.GENERIC_NUMBER;
  }

  public boolean isParameterMandatory( final int position ) {
    if ( position == 0 || position == 1 ) {
      return true;
    } else {
      return false;
    }
  }

}

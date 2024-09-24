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
 * Describes RightFunction function.
 *
 * @author Cedric Pronzato
 * @see RightFunction
 */
public class RightFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = -1227750116961019328L;

  public RightFunctionDescription() {
    super( "RIGHT", "org.pentaho.reporting.libraries.formula.function.text.Right-Function" );
  }

  public FunctionCategory getCategory() {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 2;
  }

  public Type getParameterType( final int position ) {
    if ( position == 0 ) {
      return TextType.TYPE;
    }

    return NumberType.GENERIC_NUMBER;
  }

  public Type getValueType() {
    return TextType.TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    if ( position == 2 ) {
      return false;
    }
    return true;
  }

}

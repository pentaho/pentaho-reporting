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
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * Describes ChooseFunction function.
 *
 * @author Cedric Pronzato
 * @see ChooseFunction
 */
public class ChooseFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = -9029773973577207676L;

  public ChooseFunctionDescription() {
    super( "CHOOSE", "org.pentaho.reporting.libraries.formula.function.information.Choose-Function" );
  }

  public Type getValueType() {
    return AnyType.TYPE;
  }

  public int getParameterCount() {
    return 2;
  }

  public boolean isInfiniteParameterCount() {
    return true;
  }

  public Type getParameterType( final int position ) {
    if ( position == 0 ) {
      return NumberType.GENERIC_NUMBER;
    }
    return AnyType.TYPE;
  }

  public boolean isParameterMandatory( final int position ) {
    if ( position == 0 ) {
      return true;
    }
    return false;
  }

  public FunctionCategory getCategory() {
    return InformationFunctionCategory.CATEGORY;
  }
}

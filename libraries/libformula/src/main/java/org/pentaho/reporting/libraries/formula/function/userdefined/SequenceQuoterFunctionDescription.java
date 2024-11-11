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
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class SequenceQuoterFunctionDescription extends AbstractFunctionDescription {

  private static final long serialVersionUID = -5731648885718950878L;

  public SequenceQuoterFunctionDescription() {
    super( "SEQUENCEQUOTER", "org.pentaho.reporting.libraries.formula.function.userdefined.SequenceQuoter-Function" );
  }

  @Override
  public Type getValueType() {
    return TextType.TYPE;
  }

  @Override
  public FunctionCategory getCategory() {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  @Override
  public int getParameterCount() {
    return 3;
  }

  @Override
  public Type getParameterType( int position ) {
    if ( position == 0 ) {
      return AnyType.ANY_ARRAY;
    }
    return TextType.TYPE;
  }

  @Override
  public boolean isParameterMandatory( int position ) {
    if ( position == 0 ) {
      return true;
    }
    return false;
  }

}

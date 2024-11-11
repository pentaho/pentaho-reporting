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
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Creation-Date: 31.10.2006, 17:41:12
 *
 * @author Thomas Morgner
 */
public class HasChangedFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 1739366268594059040L;

  public HasChangedFunctionDescription() {
    super( "HASCHANGED", "org.pentaho.reporting.libraries.formula.function.information.HasChanged-Function" );
  }

  public Type getValueType() {
    return LogicalType.TYPE;
  }

  public int getParameterCount() {
    return 1;
  }

  public boolean isInfiniteParameterCount() {
    return true;
  }

  public Type getParameterType( final int position ) {
    return TextType.TYPE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @return false
   */
  public boolean isParameterMandatory( final int position ) {
    return false;
  }

  public boolean isVolatile() {
    return true;
  }

  public FunctionCategory getCategory() {
    return InformationFunctionCategory.CATEGORY;
  }
}

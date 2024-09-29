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


package org.pentaho.reporting.libraries.formula.function.logical;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Creation-Date: 04.11.2006, 18:28:55
 *
 * @author Thomas Morgner
 */
public class IfFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 5553370162761578407L;

  public IfFunctionDescription() {
    super( "IF", "org.pentaho.reporting.libraries.formula.function.logical.If-Function" );
  }

  public int getParameterCount() {
    return 3;
  }

  public Type getParameterType( final int position ) {
    if ( position == 0 ) {
      return LogicalType.TYPE;
    } else {
      return AnyType.TYPE;
    }
  }

  public Type getValueType() {
    return AnyType.TYPE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @return
   */
  public boolean isParameterMandatory( final int position ) {
    if ( position == 1 || position == 2 ) {
      return true;
    } else {
      return false;
    }
  }

  public FunctionCategory getCategory() {
    return LogicalFunctionCategory.CATEGORY;
  }

}

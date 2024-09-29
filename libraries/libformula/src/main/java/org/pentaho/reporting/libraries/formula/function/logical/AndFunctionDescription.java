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
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Creation-Date: 04.11.2006, 18:28:55
 *
 * @author Thomas Morgner
 */
public class AndFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = -796481383341381910L;

  public AndFunctionDescription() {
    super( "AND", "org.pentaho.reporting.libraries.formula.function.logical.And-Function" );
  }

  public int getParameterCount() {
    return 1;
  }

  public boolean isInfiniteParameterCount() {
    return true;
  }

  public Type getParameterType( final int position ) {
    return LogicalType.TYPE;
  }

  public Type getValueType() {
    return LogicalType.TYPE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @return
   */
  public boolean isParameterMandatory( final int position ) {
    return false;
  }

  public FunctionCategory getCategory() {
    return LogicalFunctionCategory.CATEGORY;
  }
}

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

/**
 * Creation-Date: 04.11.2006, 18:28:55
 *
 * @author Thomas Morgner
 * @deprecated
 */
public class NullFunctionDescription extends AbstractFunctionDescription {
  private static final long serialVersionUID = 5710880620780379157L;

  public NullFunctionDescription() {
    super( "NULL", "org.pentaho.reporting.libraries.formula.function.userdefined.Null-Function" );
  }

  public int getParameterCount() {
    return 0;
  }

  public Type getParameterType( final int position ) {
    return AnyType.TYPE;
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
    return false;
  }

  public FunctionCategory getCategory() {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  public boolean isDeprecated() {
    return true;
  }
}

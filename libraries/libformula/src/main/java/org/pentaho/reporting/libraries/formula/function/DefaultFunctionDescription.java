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


package org.pentaho.reporting.libraries.formula.function;

import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

import java.util.Locale;

/**
 * Creation-Date: 05.11.2006, 15:13:03
 *
 * @author Thomas Morgner
 */
public class DefaultFunctionDescription implements FunctionDescription {
  private String name;
  private static final long serialVersionUID = 8718537288789701618L;

  public DefaultFunctionDescription( final String name ) {
    this.name = name;
  }

  public String getCanonicalName() {
    return name;
  }

  public Type getValueType() {
    return AnyType.TYPE;
  }

  public FunctionCategory getCategory() {
    return InvalidFunctionCategory.CATEGORY;
  }

  public int getParameterCount() {
    return 1;
  }

  public Type getParameterType( final int position ) {
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

  public String getDisplayName( final Locale locale ) {
    return name;
  }

  public String getDescription( final Locale locale ) {
    return "";
  }

  public boolean isVolatile() {
    // assume the worst ..
    return true;
  }

  public boolean isInfiniteParameterCount() {
    return true;
  }

  public String getParameterDisplayName( final int position, final Locale locale ) {
    // todo this is surely ugly ..
    return "Parameter " + String.valueOf( position );
  }

  public String getParameterDescription( final int position, final Locale locale ) {
    return "";
  }

  /**
   * Returns the default value for an optional parameter. If the value returned here is null, then this either means,
   * that the parameter is mandatory or that the default value is computed by the expression itself.
   *
   * @param position
   * @return
   */
  public Object getDefaultValue( final int position ) {
    return null;
  }

  public boolean isDeprecated() {
    return false;
  }

  public boolean isExperimental() {
    return false;
  }
}

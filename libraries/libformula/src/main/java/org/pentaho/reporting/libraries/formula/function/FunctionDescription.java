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

import java.io.Serializable;
import java.util.Locale;

/**
 * A static definition of the function's parameters, return values etc. This is a support class with emphasis on GUI
 * tools.
 * <p/>
 * However, the parameter declarations are also used when filling in the parameter values.
 * <p/>
 * Functions have a defined set of known parameters and can have a unlimited number of optional parameters. If a
 * function declares at least one parameter and declares that its parameter list is infinite, then the last parameter
 * type is used on all remaining parameters.
 *
 * @author Thomas Morgner
 */
public interface FunctionDescription extends Serializable {
  public String getCanonicalName();

  public String getDisplayName( Locale locale );

  public String getDescription( Locale locale );

  public boolean isVolatile();

  public Type getValueType();

  public FunctionCategory getCategory();

  public boolean isDeprecated();

  public boolean isExperimental();

  public int getParameterCount();

  public boolean isInfiniteParameterCount();

  /**
   * Returns the parameter type at the given position using the function metadata. The first parameter is at the
   * position 0;
   *
   * @param position The parameter index.
   * @return The parameter type.
   */
  public Type getParameterType( int position );

  public String getParameterDisplayName( int position, Locale locale );

  public String getParameterDescription( int position, Locale locale );

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @return
   */
  public boolean isParameterMandatory( int position );

  /**
   * Returns the default value for an optional parameter. If the value returned here is null, then this either means,
   * that the parameter is mandatory or that the default value is computed by the expression itself.
   *
   * @param position
   * @return
   */
  public Object getDefaultValue( int position );

}

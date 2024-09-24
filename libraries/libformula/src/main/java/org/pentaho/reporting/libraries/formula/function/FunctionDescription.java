/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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

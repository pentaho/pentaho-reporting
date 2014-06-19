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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function;

import java.io.Serializable;

import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.typing.TypeConversionException;

/**
 * A function is an arbitary computation. A return value type is not available
 * unless the function has been evaluated.
 *
 * Functions must be stateless, that means: Calling the same function with
 * exactly the same parameters must always result in the same computed value.
 *
 * @author Thomas Morgner
 */
public interface Function extends Serializable
{
  public String getCanonicalName();
  public TypeValuePair evaluate (FormulaContext context,
                                 ParameterCallback parameters)
      throws EvaluationException;
}

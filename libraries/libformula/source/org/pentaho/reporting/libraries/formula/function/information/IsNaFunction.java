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

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.ErrorValue;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.ErrorType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This function returns true if the parameter is of error type NA.
 *
 * @author Cedric Pronzato
 */
public class IsNaFunction implements Function
{
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair(LogicalType.TYPE, Boolean.FALSE);
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair(LogicalType.TYPE, Boolean.TRUE);
  private static final Log logger = LogFactory.getLog(IsNaFunction.class);
  private static final long serialVersionUID = 1205462839536368718L;

  public IsNaFunction()
  {
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters) throws EvaluationException
  {
    if (parameters.getParameterCount() != 1)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }

    try
    {
      final Type type = parameters.getType(0);
      final Object value = parameters.getValue(0);

      if (ErrorType.TYPE.equals(type) && value instanceof ErrorValue)
      {
        logger.warn ("Passing errors around is deprecated. Throw exceptions instead.");
        final ErrorValue na = (ErrorValue) value;
        if (na.getErrorCode() == LibFormulaErrorValue.ERROR_NA)
        {
          return RETURN_TRUE;
        }
      }
      else
      {
        if (value == null)
        {
          return RETURN_TRUE;
        }
      }
    }
    catch (EvaluationException e)
    {
      if (e.getErrorValue().getErrorCode() == LibFormulaErrorValue.ERROR_NA)
      {
        return RETURN_TRUE;
      }
    }

    return RETURN_FALSE;
  }

  public String getCanonicalName()
  {
    return "ISNA";
  }

}

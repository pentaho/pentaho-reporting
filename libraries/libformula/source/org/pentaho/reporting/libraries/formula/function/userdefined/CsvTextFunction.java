/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class CsvTextFunction implements Function
{
  public CsvTextFunction()
  {
  }

  public String getCanonicalName()
  {
    return "CSVTEXT";
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters) throws EvaluationException
  {
    final int parameterCount = parameters.getParameterCount();
    if (parameterCount < 1 || parameterCount > 4)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }

    final Sequence sequence =
        context.getTypeRegistry().convertToSequence(parameters.getType(0), parameters.getValue(0));
    if (sequence == null)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_NA_VALUE);
    }

    String quote = "\"";
    String separator = ",";
    boolean doQuoting = false;
    if (parameterCount > 1)
    {
      final Type indexType = parameters.getType(1);
      final Object indexValue = parameters.getValue(1);
      final Boolean indexNumber = context.getTypeRegistry().convertToLogical(indexType, indexValue);
      doQuoting = Boolean.TRUE.equals(indexNumber);
    }

    if (parameterCount > 2)
    {
      final Type indexType = parameters.getType(2);
      final Object indexValue = parameters.getValue(2);
      separator = context.getTypeRegistry().convertToText(indexType, indexValue);
    }

    if (parameterCount > 3)
    {
      final Type indexType = parameters.getType(3);
      final Object indexValue = parameters.getValue(3);
      quote = context.getTypeRegistry().convertToText(indexType, indexValue);
    }

    if (doQuoting == false)
    {
      StringBuffer b = new StringBuffer();
      while (sequence.hasNext())
      {
        final Object o = sequence.next();
        if (o != null)
        {
          b.append(o);
        }
        if (sequence.hasNext())
        {
          b.append(separator);
        }
      }
      return new TypeValuePair(TextType.TYPE, b.toString());
    }
    else
    {
      final CSVQuoter quoter = new CSVQuoter(quote.charAt(0), quote.charAt(0));
      StringBuffer b = new StringBuffer();
      while (sequence.hasNext())
      {
        final Object o = sequence.next();
        if (o != null)
        {
          b.append(quoter.doQuoting(String.valueOf(o)));
        }
        if (sequence.hasNext())
        {
          b.append(separator);
        }
      }
      return new TypeValuePair(TextType.TYPE, b.toString());
    }
  }
}

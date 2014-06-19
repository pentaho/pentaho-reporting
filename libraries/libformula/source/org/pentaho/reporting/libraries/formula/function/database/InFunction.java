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
* Copyright (c) 2008 - 2009 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.database;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.ExtendedComparator;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

public class InFunction implements Function
{
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair(LogicalType.TYPE, Boolean.TRUE);
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair(LogicalType.TYPE, Boolean.FALSE);
  private static final long serialVersionUID = -3737884974501253814L;

  public InFunction()
  {
  }

  public String getCanonicalName()
  {
    return "IN";
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters)
      throws EvaluationException
  {
    final int length = parameters.getParameterCount();
    if(length < 2)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }
    
    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Object value1Raw = parameters.getValue(0);
    final Type type1 = parameters.getType(0);    
    for (int i = 1; i < parameters.getParameterCount(); i++) {
      final Object value2Raw = parameters.getValue(i);
      if (value1Raw == null || value2Raw == null)
      {
        throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_NA_VALUE);
      }

      final Type type2 = parameters.getType(i);
      final ExtendedComparator comparator = typeRegistry.getComparator(type1, type2);
      final boolean result = comparator.isEqual (type1, value1Raw, type2, value2Raw);
      if (result)
      {
        return RETURN_TRUE;
      }
    }
    return RETURN_FALSE;
  }
}

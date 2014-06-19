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

package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * This function counts the occurences of search_text in text.
 *
 * @author Gunter Rombauts
 */
public class StringCountFunction implements Function
{
  private static final long serialVersionUID = -1557813953499941337L;

  public StringCountFunction()
  {
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters)
      throws EvaluationException
  {
    final int parameterCount = parameters.getParameterCount();
    if (parameterCount < 1 || parameterCount > 2)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type textType = parameters.getType(0);
    final Object textValue = parameters.getValue(0);
    final Type searchTextType = parameters.getType(1);
    final Object searchTextValue = parameters.getValue(1);

    final String text = typeRegistry.convertToText(textType, textValue);
    final String searchText = typeRegistry.convertToText(searchTextType, searchTextValue);
    if (searchText.length() == 0)
    {
      return new TypeValuePair(NumberType.GENERIC_NUMBER, 0);
    }

    int index = text.indexOf(searchText);
    if (index == -1)
    {
      return new TypeValuePair(NumberType.GENERIC_NUMBER, 0);
    }

    int occcounter = 0;
    while (index >= 0)
    {
      final int oldIndex = index + searchText.length();

      index = text.indexOf(searchText, oldIndex);
      occcounter += 1;
    }
    return new TypeValuePair(NumberType.GENERIC_NUMBER, occcounter);
  }

  public String getCanonicalName()
  {
    return "STRINGCOUNT";
  }

}

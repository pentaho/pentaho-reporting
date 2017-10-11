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

package org.pentaho.reporting.libraries.formula.demo;

import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

public class FormulaParsePosition
{
  public static void main(String[] args) throws EvaluationException, ParseException
  {
    final Formula f = new Formula("IF(NOT(TRUE()));\"Testing\"; \"Other\"");

    // connects the parsed formula to the context. The context provides the
    // operator and function implementations and resolves the references.
    f.initialize(new DefaultFormulaContext());

    final LValue rootReference = f.getRootReference();
    print(rootReference, 0);
  }

  public static void print (LValue lValue, int level)
  {
    System.out.println (level + " " + lValue.getClass() + " " + lValue.getParsePosition());
    final LValue[] lValues = lValue.getChildValues();
    for (int i = 0; i < lValues.length; i++)
    {
      LValue value = lValues[i];
      print (value, level +1);
    }
  }
}

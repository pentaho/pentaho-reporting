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

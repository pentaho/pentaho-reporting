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

import javax.swing.JOptionPane;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

/**
 * Creation-Date: Feb 21, 2007, 2:55:48 PM
 *
 * @author Thomas Morgner
 */
public class DemoApplication
{
  private DemoApplication()
  {
  }

  public static void main(final String[] args)
      throws ParseException, EvaluationException
  {
    LibFormulaBoot.getInstance().start();

    final String formula = JOptionPane.showInputDialog("Please enter a formula.");

    if (formula == null)
    {
      return;
    }

    // first parse the formula. This checks the general syntax, but does not
    // check whether the used functions or references are actually valid.
    final Formula f = new Formula(formula);

    // connects the parsed formula to the context. The context provides the
    // operator and function implementations and resolves the references.
    f.initialize(new DemoFormulaContext());

    final Object o = f.evaluate();
    JOptionPane.showMessageDialog(null, "The result is " + o,
        "Result", JOptionPane.INFORMATION_MESSAGE);

    System.exit(0);
  }
}

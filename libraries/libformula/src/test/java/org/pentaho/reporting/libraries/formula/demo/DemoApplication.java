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

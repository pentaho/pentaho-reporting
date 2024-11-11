/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.demo;

import java.util.Arrays;
import java.util.Locale;

import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;

/**
 * Creation-Date: 02.11.2007, 13:28:42
 *
 * @author Thomas Morgner
 */
public class PrintAllFunctions
{
  private PrintAllFunctions()
  {
  }

  public static void main(String[] args)
  {
    LibFormulaBoot.getInstance().start();

    final DemoFormulaContext context = new DemoFormulaContext();
    final FunctionRegistry functionRegistry = context.getFunctionRegistry();
    final FunctionCategory[] categories = functionRegistry.getCategories();
    for (int c = 0; c < categories.length; c++)
    {
      final FunctionCategory category = categories[c];
      System.out.println();
      System.out.println("Category " + category.getDisplayName(Locale.US));
      final String[] strings = functionRegistry.getFunctionNamesByCategory(category);
      Arrays.sort(strings);
      for (int i = 0; i < strings.length; i++)
      {
        if (i != 0)
        {
          System.out.print(", ");
        }
        final String string = strings[i];
        System.out.print(string);
      }

    }
  }
}

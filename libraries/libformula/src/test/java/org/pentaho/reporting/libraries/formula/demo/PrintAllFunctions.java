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

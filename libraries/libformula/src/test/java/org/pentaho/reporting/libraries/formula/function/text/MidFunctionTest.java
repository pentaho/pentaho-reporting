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

package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

/**
 * @author Cedric Pronzato
 */
public class MidFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "MID(\"123456789\";5;3)", "567" },
        { "MID(\"123456789\";20;3)", "" },
        { "MID(\"123456789\";-1;0)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "MID(\"123456789\";1;0)", "" },
        { "MID(\"123456789\";2.9;1)", "2" },
        { "MID(\"123456789\";2;2.9)", "23" },

        // custom tests
        { "MID(\"123456789\";5;10)", "56789" },
        { "MID(\"123456789\";1;9)", "123456789" },
        { "MID(\"text\";2;2)", "ex" },
        { "MID(123456789;\"3\";4)", "3456" },
      };
  }

}

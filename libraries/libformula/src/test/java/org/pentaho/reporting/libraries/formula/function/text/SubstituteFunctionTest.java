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

/**
 * @author Cedric Pronzato
 */
public class SubstituteFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "SUBSTITUTE(\"121212\";\"2\";\"ab\")", "1ab1ab1ab" },
        { "SUBSTITUTE(\"121212\";\"2\";\"ab\";2)", "121ab12" },
        { "SUBSTITUTE(\"Hello\";\"x\";\"ab\")", "Hello" },
        { "SUBSTITUTE(\"Annna\";\"nn\";\"N\";2)", "AnNa" },
        { "SUBSTITUTE(\"1212121\";\"2\";\"ab\")", "1ab1ab1ab1" },
      };
  }
}

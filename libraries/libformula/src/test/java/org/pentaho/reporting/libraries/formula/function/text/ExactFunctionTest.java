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
public class ExactFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "EXACT(\"A\";\"A\")", Boolean.TRUE },
        { "EXACT(\"A\";\"a\")", Boolean.FALSE },
        { "EXACT(1;1)", Boolean.TRUE },
        { "EXACT((1/3)*3;1)", Boolean.TRUE },
        { "EXACT(TRUE();TRUE())", Boolean.TRUE },
        { "EXACT(\"1\";2)", Boolean.FALSE },
        { "EXACT(\"h\";1)", Boolean.FALSE },
        { "EXACT(\"1\";1)", Boolean.TRUE },
        { "EXACT(\" 1\";1)", Boolean.FALSE },
        { "EXACT(\"12a 456 788\";\"12a 456 789\")", Boolean.FALSE },
      };
  }
}

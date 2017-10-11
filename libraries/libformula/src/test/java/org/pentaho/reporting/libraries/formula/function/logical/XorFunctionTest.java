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

package org.pentaho.reporting.libraries.formula.function.logical;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

/**
 * @author Cedric Pronzato
 */
public class XorFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "XOR(FALSE();FALSE())", Boolean.FALSE },
        { "XOR(FALSE();TRUE())", Boolean.TRUE },
        { "XOR(TRUE();FALSE())", Boolean.TRUE },
        { "XOR(TRUE();TRUE())", Boolean.FALSE },
        //TODO { "XOR(FALSE();NA())", Boolean. },
        { "XOR(FALSE();FALSE();TRUE())", Boolean.TRUE },
        { "XOR(FALSE();TRUE();TRUE())", Boolean.FALSE },
        { "XOR(TRUE())", Boolean.TRUE },
      };
  }
}

/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.userdefined;

import java.math.BigDecimal;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

public class ArrayRightFunctionTest extends FormulaTestBase
{
  public void testDefault() throws Exception
  {
    runDefaultTest();
  }

  public Object[][] createDataTest()
  {
    return new Object[][]
        {
            {"NORMALIZEARRAY(ARRAYRIGHT({11 | 21 | [.B18]}; 2))", new Object[]
                { new BigDecimal(2),  new BigDecimal(3)}},
            {"NORMALIZEARRAY(ARRAYRIGHT([.B3:.B7]; 2))", new Object[]
                { Boolean.TRUE, "Hello" }},
            {"NORMALIZEARRAY(ARRAYRIGHT([.B3:.B7]; 0))", new Object[]{}},
            {"NORMALIZEARRAY(ARRAYRIGHT([.B3:.B7]; 10))", new Object[]
                { "7", new BigDecimal(2), new BigDecimal(3), Boolean.TRUE, "Hello"}},
        };
  }

}

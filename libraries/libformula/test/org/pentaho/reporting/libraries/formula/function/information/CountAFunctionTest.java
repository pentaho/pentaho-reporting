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
* Copyright (c) 2008 - 2009 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.information;

import java.math.BigDecimal;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

/**
 * @author Cedric Pronzato
 */
public class CountAFunctionTest extends FormulaTestBase
{
  public void testDefault() throws Exception
  {
    runDefaultTest();
  }

  public Object[][] createDataTest()
  {
    return new Object[][]
        {
            {"COUNTA(\"1\";2;TRUE())", new BigDecimal(3)},
            {"COUNTA([.B3:.B5])", new BigDecimal(3)},
            {"COUNTA([.B3:.B5];[.B3:.B5])", new BigDecimal(6)},
            {"COUNTA([.B3:.B9])", new BigDecimal(6)},
            {"COUNTA(\"1\";2;1/0)", new BigDecimal(3)},
            {"COUNTA(\"1\";2;SUM([.B3:.B9]))", new BigDecimal(3)},
            {"COUNTA(\"1\";2;[.B3:.B9])", new BigDecimal(8)},
            {"COUNTA({\"1\";2;[.B3:.B9]})", new BigDecimal(8)},
            {"COUNTA({[.B3:.B9]})", new BigDecimal(6)},
        };
  }

}

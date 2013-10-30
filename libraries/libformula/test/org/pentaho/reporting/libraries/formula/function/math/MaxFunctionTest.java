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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.math;

import java.math.BigDecimal;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

/**
 * @author Cedric Pronzato
 */
public class MaxFunctionTest extends FormulaTestBase
{
  public void testDefault() throws Exception
  {
    runDefaultTest();
  }

  public Object[][] createDataTest()
  {
    return new Object[][]
        {
            {"MAX(2;4;1;-8)", new BigDecimal(4)},
//            {"MAX([.B4:.B5])", new BigDecimal(3)},
            {"ISNA(MAX(NA()))", Boolean.TRUE},
//            {"MAX([.B3:.B5])", new BigDecimal(3)},
//            {"MAX([.B3:.B7])", new BigDecimal(3)},
//            //TODO { "MAX([.B3:.B9])", new BigDecimal(4)}, this one sounds fault, there is no '4'
//            {"MAX([.B3:.B9])", new BigDecimal(3)},
            {"MAX(\"5\";\"7\")", new BigDecimal(7)},
        };
  }
}

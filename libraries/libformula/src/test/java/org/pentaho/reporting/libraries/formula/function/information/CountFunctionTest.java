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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;


/**
 * @author Cedric Pronzato
 */
public class CountFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "COUNT(1;2;3)", new BigDecimal( 3 ) },
        //            {"COUNT([.B4:.B5])", new BigDecimal(2)},
        //            {"COUNT([.B4:.B5];[.B4:.B5])", new BigDecimal(4)},
        //            {"COUNT([.B4:.B9])", new BigDecimal(2)},
        //            {"COUNT([.B4:.B8];1/0)", new BigDecimal(2)},
        //            {"COUNT([.B3:.B5])", new BigDecimal(2)},
      };
  }
}

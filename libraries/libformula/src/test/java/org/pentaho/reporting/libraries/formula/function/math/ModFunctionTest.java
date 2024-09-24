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

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class ModFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "MOD(10;3)", new BigDecimal( 1 ) },
        { "MOD(2;8)", new BigDecimal( 2 ) },
        { "MOD(5.5;2.5)", new BigDecimal( 0.5 ) },
        { "MOD(-2;3)", new BigDecimal( 1 ) },
        { "MOD(2;-3)", new BigDecimal( -1 ) },
        { "MOD(-2;-3)", new BigDecimal( -2 ) },
        { "MOD(10;0)", LibFormulaErrorValue.ERROR_ARITHMETIC_VALUE },

        // custom tests
        { "MOD(40;50)", new BigDecimal( 40 ) },
        { "MOD(-40;50)", new BigDecimal( 10 ) },
        { "MOD(40;-50)", new BigDecimal( -10 ) },
        { "MOD(-40;-50)", new BigDecimal( -40 ) },
      };
  }


}

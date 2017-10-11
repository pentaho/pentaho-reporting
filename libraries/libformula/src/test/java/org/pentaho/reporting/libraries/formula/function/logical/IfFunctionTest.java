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
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class IfFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "IF(FALSE();7;8)", new BigDecimal( 8 ) },
        { "IF(TRUE();7;8)", new BigDecimal( 7 ) },
        { "IF(TRUE();\"HI\";8)", "HI" },
        { "IF(1;7;8)", new BigDecimal( 7 ) },
        { "IF(5;7;8)", new BigDecimal( 7 ) },
        { "IF(0;7;8)", new BigDecimal( 8 ) },
        { "IF(TRUE();[.B4];8)", new BigDecimal( 2 ) },
        { "IF(TRUE();[.B4]+5;8)", new BigDecimal( 7 ) },
        { "IF(\"x\";7;8)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "IF(\"1\";7;8)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "IF(\"\";7;8)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "IF(FALSE();7)", Boolean.FALSE },
        { "IF(FALSE();7;)", Boolean.FALSE },
        { "IF(FALSE();;7)", new BigDecimal( 7 ) },
        //TODO { "IF(FALSE();7;)", new BigDecimal(0) }, we will not allow this syntax
        { "IF(TRUE();4;1/0)", new BigDecimal( 4 ) },
        { "IF(FALSE();1/0;5)", new BigDecimal( 5 ) },
      };
  }
}

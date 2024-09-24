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

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class FindFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "FIND(\"b\";\"abcabc\")", new BigDecimal( 2 ) },
        { "FIND(\"b\";\"abcabcabc\"; 3)", new BigDecimal( 5 ) },
        { "FIND(\"b\";\"ABC\";1)", LibFormulaErrorValue.ERROR_NOT_FOUND_VALUE },
        { "FIND(\"b\";\"bbbb\")", new BigDecimal( 1 ) },
        { "FIND(\"b\";\"bbbb\";2)", new BigDecimal( 2 ) },
        { "FIND(\"b\";\"bbbb\";2.9)", new BigDecimal( 2 ) },
        { "FIND(\"b\";\"bbbb\";0)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
        { "FIND(\"b\";\"bbbb\";0.9)", LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE },
      };
  }
}

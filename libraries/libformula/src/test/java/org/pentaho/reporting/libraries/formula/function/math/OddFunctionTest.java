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

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class OddFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "ODD(5)", new BigDecimal( 5 ) },
        { "ODD(-5)", new BigDecimal( -5 ) },
        { "ODD(2)", new BigDecimal( 3 ) },
        { "ODD(0.3)", new BigDecimal( 1 ) },
        { "ODD(-2)", new BigDecimal( -3 ) },
        { "ODD(-0.3)", new BigDecimal( -1 ) },
        { "ODD(0)", new BigDecimal( 1 ) },

        { "ODD(0.0)", new BigDecimal( 1 ) },
        { "ODD(0.05)", new BigDecimal( 1 ) },
        { "ODD(0.95)", new BigDecimal( 1 ) },
        { "ODD(1.0)", new BigDecimal( 1 ) },
        { "ODD(1.05)", new BigDecimal( 3 ) },
        { "ODD(1.9)", new BigDecimal( 3 ) },
        { "ODD(2.0)", new BigDecimal( 3 ) },
        { "ODD(2.05)", new BigDecimal( 3 ) },
        { "ODD(2.95)", new BigDecimal( 3 ) },
        { "ODD(3.0)", new BigDecimal( 3 ) },
        { "ODD(3.05)", new BigDecimal( 5 ) },
        { "ODD(-0.05)", new BigDecimal( -1 ) },
        { "ODD(-0.95)", new BigDecimal( -1 ) },
        { "ODD(-1.0)", new BigDecimal( -1 ) },
        { "ODD(-1.05)", new BigDecimal( -3 ) },
        { "ODD(-1.9)", new BigDecimal( -3 ) },
        { "ODD(-2.0)", new BigDecimal( -3 ) },
        { "ODD(-2.05)", new BigDecimal( -3 ) },
        { "ODD(-2.95)", new BigDecimal( -3 ) },
        { "ODD(-3.0)", new BigDecimal( -3 ) },
        { "ODD(-3.05)", new BigDecimal( -5 ) },
      };
  }
}

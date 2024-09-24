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
public class EvenFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "EVEN(6)", new BigDecimal( 6 ) },
        { "EVEN(-4)", new BigDecimal( -4 ) },
        { "EVEN(1)", new BigDecimal( 2 ) },
        { "EVEN(0.3)", new BigDecimal( 2 ) },
        { "EVEN(-1)", new BigDecimal( -2 ) },
        { "EVEN(-0.3)", new BigDecimal( -2 ) },
        { "EVEN(0)", new BigDecimal( 0 ) },

        // test with border cases
        { "EVEN(0.05)", new BigDecimal( 2 ) },
        { "EVEN(2.05)", new BigDecimal( 4 ) },
        { "EVEN(3.05)", new BigDecimal( 4 ) },
        { "EVEN(4.05)", new BigDecimal( 6 ) },
        { "EVEN(5.0)", new BigDecimal( 6 ) },
        { "EVEN(6.0)", new BigDecimal( 6 ) },
        { "EVEN(7.95)", new BigDecimal( 8 ) },
        { "EVEN(8.95)", new BigDecimal( 10 ) },
        { "EVEN(-0.05)", new BigDecimal( -2 ) },
        { "EVEN(-2.05)", new BigDecimal( -4 ) },
        { "EVEN(-3.05)", new BigDecimal( -4 ) },
        { "EVEN(-4.05)", new BigDecimal( -6 ) },
        { "EVEN(-5.0)", new BigDecimal( -6 ) },
        { "EVEN(-6.0)", new BigDecimal( -6 ) },
        { "EVEN(-7.95)", new BigDecimal( -8 ) },
        { "EVEN(-8.95)", new BigDecimal( -10 ) },


      };
  }

}

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

package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

/**
 * @author Cedric Pronzato
 */
public class MinuteFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "MINUTE(1/(24*60))", new BigDecimal( 0 ) }, // Changed as a result of PRD-5499 - no longer rounding up
        { "MINUTE(TODAY()+1/(24*60))", new BigDecimal( 0 ) }, // Changed as a result of PRD-5499 - no longer rounding up
        { "MINUTE(1/24)", new BigDecimal( 0 ) },
        { "MINUTE(TIME(11;37;05))", new BigDecimal( 37 ) },
        { "MINUTE(TIME(11;37;52))", new BigDecimal( 37 ) }, // No rounding ... PRD-5499
      };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}

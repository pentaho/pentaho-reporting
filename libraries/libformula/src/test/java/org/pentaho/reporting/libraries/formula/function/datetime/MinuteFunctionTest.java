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
        { "MINUTE(1/(24*60))", new BigDecimal( 1 ) }, // Changed as a result of PRD-5734 - should be the same as =MINUTE("00:01:00")
        { "MINUTE(TODAY()+1/(24*60))", new BigDecimal( 1 ) }, // Changed as a result of PRD-5734 - should be the same as =MINUTE("00:01:00")
        { "MINUTE(1/24)", new BigDecimal( 0 ) },
        { "MINUTE(TIME(11;37;05))", new BigDecimal( 37 ) },
        { "MINUTE(TIME(11;37;52))", new BigDecimal( 37 ) }, // No rounding ... PRD-5499
        { "MINUTE(TIME(00;00;59))", new BigDecimal( 0 ) },
        { "MINUTE(TIME(00;01;00))", new BigDecimal( 1 ) },
        { "MINUTE(TIME(00;01;59))", new BigDecimal( 1 ) },
        { "MINUTE(\"00:00:59\")", new BigDecimal( 0 ) },
        { "MINUTE(\"00:01:00\")", new BigDecimal( 1 ) },
        { "MINUTE(\"00:01:59\")", new BigDecimal( 1 ) },
        { "MINUTE(\"00:29:59\")", new BigDecimal( 29 ) },
        { "MINUTE(\"00:30:00\")", new BigDecimal( 30 ) },
        { "MINUTE(\"00:30:59\")", new BigDecimal( 30 ) },
        { "MINUTE(TIMEVALUE(\"00:00:59\"))", new BigDecimal( 0 ) },
        { "MINUTE(TIMEVALUE(\"00:01:00\"))", new BigDecimal( 1 ) },
        { "MINUTE(TIMEVALUE(\"00:01:59\"))", new BigDecimal( 1 ) },
        { "MINUTE(TIMEVALUE(\"00:29:59\"))", new BigDecimal( 29 ) },
        { "MINUTE(TIMEVALUE(\"00:30:00\"))", new BigDecimal( 30 ) },
        { "MINUTE(TIMEVALUE(\"00:30:59\"))", new BigDecimal( 30 ) },
        { "MINUTE(15/24/60/60+timevalue(\"00:30:00\"))", new BigDecimal( 30 ) }
      };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}

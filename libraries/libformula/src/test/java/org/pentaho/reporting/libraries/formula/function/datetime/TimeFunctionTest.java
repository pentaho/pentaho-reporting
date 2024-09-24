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
public class TimeFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }


  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "0.9999884259259259259259259259259259259*27", new BigDecimal( "26.9996875000000000000" ) },
        { "TIME(0;0;0)+0", new BigDecimal( 0 ) },
        { "TIME(23;59;59)*60*60*24", new BigDecimal( 86399 ) },
            /*{ "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
                    { "", Boolean.TRUE },
            */
      };
  }
}

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
public class DateDifFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "DATEDIF(DATE(1990;2;15);DATE(1993;9;15); \"y\")", new BigDecimal( 3 ) },
        { "DATEDIF(DATE(1990;2;15);DATE(1993;9;15); \"m\")", new BigDecimal( 43 ) },
        //TODO result not found in spec { "DATEDIF(DATE(1990;2;15);DATE(1993;9;15); \"d\")", new Integer()},
        { "DATEDIF(DATE(1990;2;15);DATE(1993;9;15); \"md\")", new BigDecimal( 0 ) },
        { "DATEDIF(DATE(1990;2;15);DATE(1993;9;15); \"ym\")", new BigDecimal( 7 ) },
        //TODO result not found in spec { "DATEDIF(DATE(1990;2;15);DATE(1993;9;15); \"yd\")", new Integer()},

        // Additional specs from
        // http://www.cpearson.com/excel/datedif.htm
        { "DATEDIF(DATE(1995;1;1);DATE(1999;6;15); \"d\")", new BigDecimal( 1626 ) },
        { "DATEDIF(DATE(1995;1;1);DATE(1999;6;15); \"m\")", new BigDecimal( 53 ) },
        { "DATEDIF(DATE(1995;1;1);DATE(1999;6;15); \"y\")", new BigDecimal( 4 ) },
        { "DATEDIF(DATE(1995;1;1);DATE(1999;6;15); \"ym\")", new BigDecimal( 5 ) },
        { "DATEDIF(DATE(1995;1;1);DATE(1999;6;15); \"yd\")", new BigDecimal( 165 ) },
        { "DATEDIF(DATE(1995;1;1);DATE(1999;6;15); \"md\")", new BigDecimal( 14 ) },

        { "DATEDIF(DATE(2011;2;5);DATE(2012;2;4); \"yd\")", new BigDecimal( 364 ) },

        { "DATEDIF(DATE(2012;2;5); DATE(2014;1;1); \"ym\")", new BigDecimal( 10 ) },
        { "DATEDIF(DATE(2012;2;5); DATE(2014;1;1); \"yd\")", new BigDecimal( 331 ) },
        { "DATEDIF(DATE(2012;2;5); DATE(2014;1;1); \"md\")", new BigDecimal( 27 ) },
        { "DATEDIF(DATE(2012;2;5); DATE(2014;1;1); \"y\")", new BigDecimal( 1 ) },
        { "DATEDIF(DATE(2012;2;5); DATE(2014;1;1); \"m\")", new BigDecimal( 22 ) },

        { "DATEDIF(DATE(2012;2;5); DATE(2014;1;1); \"d\")", new BigDecimal( 696 ) },
        { "DATEDIF(DATE(2012;2;5); DATE(2014;1;1); \"m\")", new BigDecimal( 22 ) },

        { "DATEDIF(DATE(2012;2;5);DATE(2013;2;4); \"y\")", new BigDecimal( 0 ) },
        { "DATEDIF(DATE(2012;2;5);DATE(2013;2;5); \"y\")", new BigDecimal( 1 ) },
        { "DATEDIF(DATE(2012;2;5);DATE(2013;2;6); \"y\")", new BigDecimal( 1 ) },

        { "DATEDIF(DATE(2012;2;5);DATE(2013;2;4); \"ym\")", new BigDecimal( 11 ) },
        { "DATEDIF(DATE(2012;2;5);DATE(2013;2;5); \"ym\")", new BigDecimal( 0 ) },
        { "DATEDIF(DATE(2012;2;5);DATE(2013;2;6); \"ym\")", new BigDecimal( 0 ) },

        { "DATEDIF(DATE(2012;2;5);DATE(2013;2;4); \"yd\")", new BigDecimal( 365 ) },
        { "DATEDIF(DATE(2012;2;5);DATE(2013;2;5); \"yd\")", new BigDecimal( 0 ) },
        { "DATEDIF(DATE(2012;2;5);DATE(2013;2;6); \"yd\")", new BigDecimal( 1 ) },
        { "DATEDIF(DATE(2011;2;5);DATE(2012;2;5); \"yd\")", new BigDecimal( 0 ) },
        { "DATEDIF(DATE(2011;2;5);DATE(2012;2;6); \"yd\")", new BigDecimal( 1 ) },
        { "DATEDIF(DATE(2011;2;5);DATE(2013;2;4); \"yd\")", new BigDecimal( 364 ) },
        { "DATEDIF(DATE(2011;2;5);DATE(2013;2;5); \"yd\")", new BigDecimal( 0 ) },
        { "DATEDIF(DATE(2011;2;5);DATE(2013;2;6); \"yd\")", new BigDecimal( 1 ) },
      };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}

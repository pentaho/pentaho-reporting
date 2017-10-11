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
* Copyright (c) 2015 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.datetime;
import java.util.Date;
import java.util.TimeZone;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

public class TimeValueFunctionTest extends FormulaTestBase {

  /**
   * @author Marc Batchelor
   */
  
  public Object[][] createDataTest() {
    // Account for TimeZone in TIMEVALUE -vs- TIME below...
    // Notes:
    // TIME() returns TimeZone offset values, and this will
    // change depending upon whether you're "in DST" and what
    // your timezone is. The below tests that TIMEVALUE(DATETIMEVALUE)
    // returns correct time-zone and DST offset values.
    TimeZone tz = TimeZone.getDefault();
    // 2015-09-08 (YYYY-MM-DD) in Americas/New York has a -4 hour offset because
    // because DST is observed.
    int hrOffset1 = tz.getOffset(new Date(115, 8, 8).getTime()) / 1000 / 60 / 60;
    int base1 = 17 + hrOffset1;
    int base2 = 9 + hrOffset1;
    String t1 = "TIME(" + Integer.toString( base1 ) + ";15;00)";
    String t2 = "TIME(" + Integer.toString( base2 ) + ";30;00)";
    // 2015-11-08 (YYYY-MM-DD) in Americas/New York has a -5 hour offset because
    // because DST is not in effect.
    int hrOffset2 = tz.getOffset(new Date(115, 11, 8).getTime()) / 1000 / 60 / 60;
    int base3 = 17 + hrOffset2;
    int base4 = 9 + hrOffset2;
    String t3 = "TIME(" + Integer.toString( base3 ) + ";15;00)";
    String t4 = "TIME(" + Integer.toString( base4 ) + ";30;00)";
    return new Object[][]
      {
        { "TIMEVALUE(DATETIMEVALUE(\"2015-09-08T17:15:00.00+0000\")) = " + t1,
          Boolean.TRUE },
        { "TIMEVALUE(DATETIMEVALUE(\"2015-09-08T09:30:00.00+0000\")) = " + t2,
          Boolean.TRUE },
        { "TIMEVALUE(DATETIMEVALUE(\"2015-11-08T17:15:00.00+0000\")) = " + t3,
            Boolean.TRUE },
          { "TIMEVALUE(DATETIMEVALUE(\"2015-11-08T09:30:00.00+0000\")) = " + t4,
            Boolean.TRUE },
      };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }
}

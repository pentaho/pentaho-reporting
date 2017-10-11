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

package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;
import java.util.TimeZone;

/**
 * Creation-Date: 10.04.2007, 15:31:58
 *
 * @author Thomas Morgner
 */
public class DateOperatorTest extends FormulaTestBase {

  private TimeZone origTz;

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "DATETIMEVALUE(\"2009-10-10T17:15:00.00+0000\") + 0",
          new BigDecimal( "40094.7604166666666666666666666666666667" ) },
        { "DATETIMEVALUE(\"2009-10-10T17:30:00.00+0000\") - 0",
          new BigDecimal( "40094.7708333333333333333333333333333333" ) },
        { "DATETIMEVALUE(\"2009-10-10T17:30:00.00+0000\") - DATETIMEVALUE(\"2009-10-10T17:30:00.00+0000\")",
          new BigDecimal( 0 ) },
        { "DATETIMEVALUE(\"2009-10-10T17:30:00.00+0000\") - DATETIMEVALUE(\"2009-10-10T17:15:00.00+0000\")",
          new BigDecimal( "0.0104166666666666666666666666666667" ) },
      };
  }

  public DateOperatorTest() {
  }

  public DateOperatorTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    origTz = TimeZone.getDefault();
    TimeZone.setDefault( TimeZone.getTimeZone( "GMT+01:00" ) );
    super.setUp();
  }


  protected void tearDown() throws Exception {
    TimeZone.setDefault( origTz );
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public void testDefaultTZ() throws Exception {
    runDefaultTest();
  }


}

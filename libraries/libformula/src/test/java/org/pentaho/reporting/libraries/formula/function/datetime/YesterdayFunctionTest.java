/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class YesterdayFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "YESTERDAY()", createYesterdaysDate() },
      };
  }

  private Date createYesterdaysDate() {
    final GregorianCalendar gcal = new GregorianCalendar( 2011, Calendar.APRIL, 6, 0, 0, 0 );
    gcal.setTimeZone( getContext().getLocalizationContext().getTimeZone() );
    return gcal.getTime();
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

}

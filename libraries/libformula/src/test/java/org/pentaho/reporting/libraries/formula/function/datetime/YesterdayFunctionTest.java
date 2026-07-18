/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



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

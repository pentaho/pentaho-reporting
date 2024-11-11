/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formatting;

import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatTest {
  @Test
  public void testTimeZoneIsApplied() {
    FastDateFormat fd =
      new FastDateFormat( "yyyy-MM-dd HH:mm:ss,SSS ZZZ", Locale.ENGLISH, TimeZone.getTimeZone( "PST" ) );
    Assert.assertEquals( "2009-01-06 00:40:31,000 -0800", fd.format( new Date( 1231231231000l ) ) );
  }

  @Test
  public void testTimeZoneIsAppliedOnPreset() {
    FastDateFormat fd =
      new FastDateFormat( DateFormat.FULL, DateFormat.FULL, Locale.ENGLISH, TimeZone.getTimeZone( "PST" ) );
    Assert.assertEquals( "Tuesday, January 6, 2009 12:40:31 AM PST", fd.format( new Date( 1231231231000l ) ) );
  }
}

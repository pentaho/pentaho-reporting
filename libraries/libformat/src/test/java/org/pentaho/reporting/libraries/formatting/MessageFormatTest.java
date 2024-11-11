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

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MessageFormatTest {
  public MessageFormatTest() {
  }

  @Test
  public void testNonCrash() {
    FastMessageFormat messageFormat = new FastMessageFormat( "{0,date,dd MMM yyyy}" );
    final Date tempDate = new Date( 123123123123l );
    final Object[] tempDateArray = { tempDate };
    final String tempDateString = messageFormat.format( tempDateArray );

    // Added check for 25 or 26 Nov to handle the different timezones
    boolean result = "26 Nov 1973".equals( tempDateString );
    if ( !result ) {
      result = "25 Nov 1973".equals( tempDateString );
    }
    Assert
      .assertTrue( "Resuls should equal '25 Nov 1973' or '26 Nov 1973' depending on timezone: actual=" + tempDateString,
        result );
  }

  @Test
  public void testLocaleAndTimezoneApplied() {
    FastMessageFormat messageFormat =
      new FastMessageFormat( "{0,date,full} {1,number,#,###.##}", Locale.GERMAN, TimeZone.getTimeZone( "PST" ) );
    Assert.assertEquals( "Sonntag, 25. November 1973 16:52 Uhr PST 1.234,57",
      messageFormat.format( new Object[] { new Date( 123123123123l ), new Double( 1234.567 ) } ) );

  }

}

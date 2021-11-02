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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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

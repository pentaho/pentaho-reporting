/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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

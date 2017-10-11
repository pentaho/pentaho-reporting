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

package org.pentaho.reporting.libraries.formula.util;

import org.pentaho.reporting.libraries.formula.LibFormulaBoot;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Contains methods for dealing with Excel dates. <br/> Modified by Cedric Pronzato
 *
 * @author Michael Harhen
 * @author Glen Stampoultzis (glens at apache.org)
 * @author Dan Sherman (dsherman at isisph.com)
 * @author Hack Kampbjorn (hak at 2mba.dk)
 */

public class HSSFDateUtil {
  private static final BigDecimal DAY_MILLISECONDS = new BigDecimal( 24 * 60 * 60 * 1000 );
  private static final int DAYS_TO_1900 = daysInPriorYears( 1900 - 1 ) + 1;

  private HSSFDateUtil() {
  }

  public static int computeZeroDate( final String config,
                                     final boolean excelBugCompatible ) {
    if ( "1899".equals( config ) ) {
      return 2;
    }
    if ( "1900".equals( config ) ) {
      return 0;
    }
    if ( excelBugCompatible ) {
      // 1900 is a leap year for excel
      return -( 4 * 365 + 1 );
    } else {
      // 1900 is no leap year for everyone else. 
      return -( 4 * 365 );
    }
  }

  public static BigDecimal getExcelDate( final Date date ) {
    final String dateSystem = LibFormulaBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.libraries.formula.ZeroDate", "1900" );
    final boolean excelBugCompatible =
      "true".equals( LibFormulaBoot.getInstance().getGlobalConfig().getConfigProperty
        ( "org.pentaho.reporting.libraries.formula.ExcelDateBugAware", "false" ) );
    return getExcelDate( date, excelBugCompatible, computeZeroDate( dateSystem, excelBugCompatible ) );
  }

  public static BigDecimal getExcelDate( final Date date,
                                         final boolean excelBugCompatible,
                                         final int zeroDate ) {
    final Calendar calStart = new GregorianCalendar();
    calStart.setTime( date );

    // Because of daylight time saving we cannot use
    //     date.getTime() - calStart.getTimeInMillis()
    // as the difference in milliseconds between 00:00 and 04:00
    // can be 3, 4 or 5 hours but Excel expects it to always
    // be 4 hours.
    // E.g. 2004-03-28 04:00 CEST - 2004-03-28 00:00 CET is 3 hours
    // and 2004-10-31 04:00 CET - 2004-10-31 00:00 CEST is 5 hours
    long time = calStart.get( Calendar.HOUR_OF_DAY );
    time = ( time * 60 ) + calStart.get( Calendar.MINUTE );
    time = ( time * 60 ) + calStart.get( Calendar.SECOND );
    time = ( time * 1000 ) + calStart.get( Calendar.MILLISECOND );
    // scale of 15 should be enough to cover a millisecond
    final BigDecimal fraction = new BigDecimal( time ).divide
      ( DAY_MILLISECONDS, LibFormulaBoot.GLOBAL_SCALE, BigDecimal.ROUND_HALF_UP );


    final int year = calStart.get( Calendar.YEAR );
    int daysInYear = calStart.get( Calendar.DAY_OF_YEAR );
    if ( excelBugCompatible ) {
      if ( zeroDate > 59 ) {
        // if we handle dates which are before the 1.3.1900, we have to take the invalid
        // leap-year computation into account.
        if ( year < 1900 || ( year == 1900 && daysInYear > 59 ) ) {
          daysInYear += 1;
        }
      } else if ( year > 1900 || ( year == 1900 && daysInYear > 59 ) ) {
        // excel firmly believes that the 29th February 1900 exists.
        daysInYear += 1;
      }
    }

    final int daysStart = daysInPriorYears( year - 1 ) - DAYS_TO_1900;
    final int daysSinceYear = daysInYear + daysStart;
    return fraction.add( new BigDecimal( daysSinceYear + zeroDate ) );
  }

  /**
   * Return the number of days in the years from 0 AD to December 31th of the given year.
   *
   * @param y a year
   * @return days  number of days in years prior to yr.
   * @throws IllegalArgumentException if year is outside of range.
   */

  private static int daysInPriorYears( final int y ) {
    return 365 * y      // days in prior years
      + y / 4      // plus julian leap days in prior years
      - y / 100    // minus prior century years
      + y / 400;
  }

  /**
   * Given a excel date, converts it into a Date. Assumes 1900 date windowing.
   *
   * @param date the Excel Date
   * @return Java representation of a date (null if error)
   */
  public static Date getJavaDate( final BigDecimal date ) {
    final String dateSystem = LibFormulaBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.libraries.formula.ZeroDate", "1900" );
    final boolean excelBugCompatible =
      "true".equals( LibFormulaBoot.getInstance().getGlobalConfig().getConfigProperty
        ( "org.pentaho.reporting.libraries.formula.ExcelDateBugAware", "false" ) );
    return getJavaDate( date, excelBugCompatible, computeZeroDate( dateSystem, excelBugCompatible ) );
  }

  public static Date getJavaDate( final BigDecimal date, final boolean excelBugCompatible, final int zeroDate ) {
    int correction = 1;

    final BigDecimal wholeDays = NumberUtil.performIntRounding( date );
    final int wholeDaysInt = wholeDays.intValue() - zeroDate;

    if ( excelBugCompatible ) {
      // if we deal with a date that is after the 28th februar, adjust the date by one to handle the fact
      // that excel thinks the 29th February 1900 exists.
      // by tuning this variable, we map the int-value for the 29th to the next day.
      if ( wholeDaysInt > 59 ) {
        correction = 0;
      }
    }

    final BigDecimal fractionNum = date.subtract( wholeDays );
    final BigDecimal fraction = fractionNum.multiply( DAY_MILLISECONDS );

    // the use of the calendar could be probably removed, as there is no magic in converting
    // a running number into a date.
    final GregorianCalendar calendar = new GregorianCalendar( 1900, 0, wholeDaysInt + correction );
    calendar.set( Calendar.MILLISECOND, fraction.setScale( 0, BigDecimal.ROUND_HALF_UP ).intValue() );
    return calendar.getTime();
  }
}

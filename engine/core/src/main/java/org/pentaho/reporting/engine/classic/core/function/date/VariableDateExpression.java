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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function.date;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Constructs a new date by specifying the fields for the calendar either as static parameters or as parameters read
 * from a field.
 *
 * @author Thomas Morgner
 */
public class VariableDateExpression extends DateExpression {
  /**
   * The name of the field that contains the month.
   */
  private String monthField;
  /**
   * The name of the field that contains the year.
   */
  private String yearField;
  /**
   * The name of the field that contains the hour (using the 24-hours system).
   */
  private String hourField;
  /**
   * The name of the field that contains the minute.
   */
  private String minuteField;
  /**
   * The name of the field that contains the second.
   */
  private String secondField;
  /**
   * The name of the field that contains the milli-seconds.
   */
  private String milliSecondField;
  /**
   * The name of the field that contains the number of milliseconds since 01-Jan-1970.
   */
  private String timeField;
  /**
   * The name of the field that contains the day of the week.
   */
  private String dayOfWeekField;
  /**
   * The name of the field that contains the day of the year.
   */
  private String dayOfYearField;
  /**
   * The name of the field that contains the day of the month.
   */
  private String dayOfMonthField;
  /**
   * The name of the field that contains the day of the week in the current month.
   */
  private String dayOfWeekInMonthField;
  /**
   * The name of the field that contains the time-zone.
   */
  private String timeZoneField;
  /**
   * The name of the field that contains the week of the year.
   */
  private String weekOfYearField;
  /**
   * The name of the field that contains the week of the month.
   */
  private String weekOfMonthField;
  /**
   * The name of the field that contains the epoch time, that is the time in seconds since 01-Jan-1970.
   */
  private String epochTimeField;

  /**
   * Default Constructor.
   */
  public VariableDateExpression() {
  }

  /**
   * Returns the name of the field that contains the epoch time, that is the time in seconds since 01-Jan-1970.
   *
   * @return a fieldname.
   */
  public String getEpochTimeField() {
    return epochTimeField;
  }

  /**
   * Defines the name of the field that contains the epoch time, that is the time in seconds since 01-Jan-1970.
   *
   * @param epochTimeField
   *          a fieldname.
   */
  public void setEpochTimeField( final String epochTimeField ) {
    this.epochTimeField = epochTimeField;
  }

  /**
   * Returns the name of the field that contains the month.
   *
   * @return a fieldname.
   */
  public String getMonthField() {
    return monthField;
  }

  /**
   * Defines the name of the field that contains the month.
   *
   * @param monthField
   *          a fieldname.
   */
  public void setMonthField( final String monthField ) {
    this.monthField = monthField;
  }

  /**
   * Returns the name of the field that contains the year.
   *
   * @return a fieldname.
   */
  public String getYearField() {
    return yearField;
  }

  /**
   * Defines the name of the field that contains the year.
   *
   * @param yearField
   *          a fieldname.
   */
  public void setYearField( final String yearField ) {
    this.yearField = yearField;
  }

  /**
   * Returns the name of the field that contains the hour of the day (using the 24-hour system).
   *
   * @return a fieldname.
   */
  public String getHourField() {
    return hourField;
  }

  /**
   * Defines the name of the field that contains the hour of the day.
   *
   * @param hourField
   *          a fieldname.
   */
  public void setHourField( final String hourField ) {
    this.hourField = hourField;
  }

  /**
   * Returns the name of the field that contains the minute.
   *
   * @return a fieldname.
   */
  public String getMinuteField() {
    return minuteField;
  }

  /**
   * Defines the name of the field that contains the minute.
   *
   * @param minuteField
   *          a fieldname.
   */
  public void setMinuteField( final String minuteField ) {
    this.minuteField = minuteField;
  }

  /**
   * Returns the name of the field that contains the second.
   *
   * @return a fieldname.
   */
  public String getSecondField() {
    return secondField;
  }

  /**
   * Defines the name of the field that contains the second.
   *
   * @param secondField
   *          a fieldname.
   */
  public void setSecondField( final String secondField ) {
    this.secondField = secondField;
  }

  /**
   * Returns the name of the field that contains the milliseconds.
   *
   * @return a fieldname.
   */
  public String getMilliSecondField() {
    return milliSecondField;
  }

  /**
   * Defines the name of the field that contains the milliseconds.
   *
   * @param milliSecondField
   *          a fieldname.
   */
  public void setMilliSecondField( final String milliSecondField ) {
    this.milliSecondField = milliSecondField;
  }

  /**
   * Returns the name of the field that contains the time in milli-seconds since 01-Jan-1970.
   *
   * @return a fieldname.
   */
  public String getTimeField() {
    return timeField;
  }

  /**
   * Defines the name of the field that contains the time in milli-seconds since 01-Jan-1970.
   *
   * @param timeField
   *          a fieldname.
   */
  public void setTimeField( final String timeField ) {
    this.timeField = timeField;
  }

  /**
   * Returns the name of the field that contains the day-of-the-week.
   *
   * @return a fieldname.
   */
  public String getDayOfWeekField() {
    return dayOfWeekField;
  }

  /**
   * Defines the name of the field that contains the day of the week.
   *
   * @param dayOfWeekField
   *          a fieldname.
   */
  public void setDayOfWeekField( final String dayOfWeekField ) {
    this.dayOfWeekField = dayOfWeekField;
  }

  /**
   * Returns the name of the field that contains the day of the year.
   *
   * @return a fieldname.
   */
  public String getDayOfYearField() {
    return dayOfYearField;
  }

  /**
   * Defines the name of the field that contains the day of the year.
   *
   * @param dayOfYearField
   *          a fieldname.
   */
  public void setDayOfYearField( final String dayOfYearField ) {
    this.dayOfYearField = dayOfYearField;
  }

  /**
   * Returns the name of the field that contains the day of the month.
   *
   * @return a fieldname.
   */
  public String getDayOfMonthField() {
    return dayOfMonthField;
  }

  /**
   * Defines the name of the field that contains the day of the month.
   *
   * @param dayOfMonthField
   *          a fieldname.
   */
  public void setDayOfMonthField( final String dayOfMonthField ) {
    this.dayOfMonthField = dayOfMonthField;
  }

  /**
   * Returns the name of the field that contains the day of the week in the current month.
   *
   * @return a fieldname.
   */
  public String getDayOfWeekInMonthField() {
    return dayOfWeekInMonthField;
  }

  /**
   * Defines the name of the field that contains the day of the week in the current month.
   *
   * @param dayOfWeekInMonthField
   *          a fieldname.
   */
  public void setDayOfWeekInMonthField( final String dayOfWeekInMonthField ) {
    this.dayOfWeekInMonthField = dayOfWeekInMonthField;
  }

  /**
   * Returns the name of the field that contains the time-zone.
   *
   * @return a fieldname.
   */
  public String getTimeZoneField() {
    return timeZoneField;
  }

  /**
   * Defines the name of the field that contains the time-zone.
   *
   * @param timeZoneField
   *          a fieldname.
   */
  public void setTimeZoneField( final String timeZoneField ) {
    this.timeZoneField = timeZoneField;
  }

  /**
   * Defines the name of the field that contains the week-of-the-year.
   *
   * @return a fieldname.
   */
  public String getWeekOfYearField() {
    return weekOfYearField;
  }

  /**
   * Defines the name of the field that contains the week of the year.
   *
   * @param weekOfYearField
   *          a fieldname.
   */
  public void setWeekOfYearField( final String weekOfYearField ) {
    this.weekOfYearField = weekOfYearField;
  }

  /**
   * Defines the name of the field that contains the week of the month.
   *
   * @return a fieldname.
   */
  public String getWeekOfMonthField() {
    return weekOfMonthField;
  }

  /**
   * Returns the name of the field that contains the week of the month.
   *
   * @param weekOfMonthField
   *          a fieldname.
   */
  public void setWeekOfMonthField( final String weekOfMonthField ) {
    this.weekOfMonthField = weekOfMonthField;
  }

  /**
   * Configures the given Calendar instance by applying all defined fields to it.
   *
   * @param calendar
   *          the week of the year property.
   */
  protected void configureCalendar( final Calendar calendar ) {
    // first add the hardcoded values, if any ...
    super.configureCalendar( calendar );

    // then the variable values ..
    if ( timeField != null ) {
      final Object o = getDataRow().get( timeField );
      if ( o instanceof Number ) {
        final Number n = (Number) o;
        calendar.setTime( new Date( n.longValue() ) );
      } else if ( o instanceof Date ) {
        final Date d = (Date) o;
        calendar.setTime( d );
      }
    }

    if ( epochTimeField != null ) {
      final Object o = getDataRow().get( epochTimeField );
      if ( o instanceof Number ) {
        final Number n = (Number) o;
        calendar.setTime( new Date( n.longValue() * 1000 ) );
      }
    }
    if ( monthField != null ) {
      trySetField( calendar, Calendar.MONTH, monthField );
    }
    if ( dayOfMonthField != null ) {
      trySetField( calendar, Calendar.DAY_OF_MONTH, dayOfMonthField );
    }
    if ( yearField != null ) {
      trySetField( calendar, Calendar.YEAR, yearField );
    }
    if ( hourField != null ) {
      trySetField( calendar, Calendar.HOUR_OF_DAY, hourField );
    }
    if ( minuteField != null ) {
      trySetField( calendar, Calendar.MINUTE, minuteField );
    }
    if ( secondField != null ) {
      trySetField( calendar, Calendar.SECOND, secondField );
    }
    if ( milliSecondField != null ) {
      trySetField( calendar, Calendar.MILLISECOND, milliSecondField );
    }
    if ( dayOfWeekField != null ) {
      trySetField( calendar, Calendar.DAY_OF_WEEK, dayOfWeekField );
    }
    if ( dayOfYearField != null ) {
      trySetField( calendar, Calendar.DAY_OF_YEAR, dayOfYearField );
    }
    if ( dayOfWeekInMonthField != null ) {
      trySetField( calendar, Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInMonthField );
    }
    if ( weekOfMonthField != null ) {
      trySetField( calendar, Calendar.WEEK_OF_MONTH, weekOfMonthField );
    }
    if ( weekOfYearField != null ) {
      trySetField( calendar, Calendar.WEEK_OF_YEAR, weekOfYearField );
    }
    if ( timeZoneField != null ) {
      final Object o = getDataRow().get( getTimeZoneField() );
      if ( o instanceof String ) {
        calendar.setTimeZone( TimeZone.getTimeZone( (String) o ) );
      } else if ( o instanceof TimeZone ) {
        calendar.setTimeZone( (TimeZone) o );
      }
    }
  }

  /**
   * A helper method tha tries to update a field from a column in the data-row. The calendar is only updated, if the
   * field contains a Number.
   *
   * @param calendar
   *          the calendar that should be updated
   * @param field
   *          the field as specified in the Calendar class
   * @param column
   *          the data-row column from where to read the number
   */
  private void trySetField( final Calendar calendar, final int field, final String column ) {
    if ( column == null ) {
      return;
    }
    final Object o = getDataRow().get( column );
    if ( o instanceof Number == false ) {
      return;
    }
    final Number n = (Number) o;
    calendar.set( field, n.intValue() );
  }
}

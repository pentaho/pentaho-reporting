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

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * The DateExpression can be used to construct a static date object. The Calendar object used as base is initialized to
 * the current date. Fields that are not set, are ignored.
 * <p/>
 * To construct dates from values read from the data-row, use the {@link VariableDateExpression} instead.
 *
 * @author Thomas Morgner
 */
public class DateExpression extends AbstractExpression {
  /**
   * A property holding the month of the year.
   */
  private Integer month;
  /**
   * A property holding the year.
   */
  private Integer year;
  /**
   * A property holding the hour of the day. This property uses the 24-hour system.
   */
  private Integer hour;
  /**
   * A property holding the minute.
   */
  private Integer minute;
  /**
   * A property holding the second.
   */
  private Integer second;
  /**
   * A property holding milli-seconds.
   */
  private Integer milliSecond;
  /**
   * A property holding time in milli-seconds since 01-01-1970.
   */
  private Long time;
  /**
   * A property holding time in seconds since 01-01-1970.
   */
  private Long epochTime;
  /**
   * A property holding the day of the week.
   */
  private Integer dayOfWeek;
  /**
   * A property holding the day of the year.
   */
  private Integer dayOfYear;
  /**
   * A property holding the day of the month.
   */
  private Integer dayOfMonth;
  /**
   * A property holding the day of the week in the month.
   */
  private Integer dayOfWeekInMonth;
  /**
   * A property holding the time-zone.
   */
  private TimeZone timeZone;
  /**
   * A property holding the week of the year.
   */
  private Integer weekOfYear;
  /**
   * A property holding the week of the month.
   */
  private Integer weekOfMonth;

  /**
   * Default Constructor.
   */
  public DateExpression() {
  }

  /**
   * Returns the current time-zone.
   *
   * @return the time-zone or null, if none is set.
   */
  public TimeZone getTimeZone() {
    return timeZone;
  }

  /**
   * Defines the timezone. If none is defined here, the locale's default timezone is used instead.
   *
   * @param timeZone
   *          the time-zone.
   */
  public void setTimeZone( final TimeZone timeZone ) {
    this.timeZone = timeZone;
  }

  /**
   * Returns the month property.
   *
   * @return the month property.
   */
  public Integer getMonth() {
    return month;
  }

  /**
   * Defines the month property.
   *
   * @param month
   *          the month property.
   */
  public void setMonth( final Integer month ) {
    this.month = month;
  }

  /**
   * Returns the day property. This returns the day within the current month.
   *
   * @return the day of the month property.
   */
  public Integer getDay() {
    return dayOfMonth;
  }

  /**
   * Defines the day property. This defines the day within the current month.
   *
   * @param day
   *          the day of the month property.
   */
  public void setDay( final Integer day ) {
    this.dayOfMonth = day;
  }

  /**
   * Returns the year property.
   *
   * @return the year property.
   */
  public Integer getYear() {
    return year;
  }

  /**
   * Defines the year property.
   *
   * @param year
   *          the year property.
   */
  public void setYear( final Integer year ) {
    this.year = year;
  }

  /**
   * Returns the hour of the day property. This uses the 24-hour system.
   *
   * @return the hour property.
   */
  public Integer getHour() {
    return hour;
  }

  /**
   * Defines the hour property. This uses the 24-hour system
   *
   * @param hour
   *          the hour property.
   */
  public void setHour( final Integer hour ) {
    this.hour = hour;
  }

  /**
   * Returns the minute property.
   *
   * @return the minute property.
   */
  public Integer getMinute() {
    return minute;
  }

  /**
   * Defines the minute property.
   *
   * @param minute
   *          the minute property.
   */
  public void setMinute( final Integer minute ) {
    this.minute = minute;
  }

  /**
   * Returns the second property.
   *
   * @return the second property.
   */
  public Integer getSecond() {
    return second;
  }

  /**
   * Defines the second property.
   *
   * @param second
   *          the second property.
   */
  public void setSecond( final Integer second ) {
    this.second = second;
  }

  /**
   * Returns the milli-second property.
   *
   * @return the milli-second property.
   */
  public Integer getMilliSecond() {
    return milliSecond;
  }

  /**
   * Defines the year property.
   *
   * @param milliSecond
   *          the milli-seconds property.
   */
  public void setMilliSecond( final Integer milliSecond ) {
    this.milliSecond = milliSecond;
  }

  /**
   * Returns the time in milli-seconds since 01-Jan-1970.
   *
   * @return the time in milli-seconds since 01-Jan-1970.
   */
  public Long getTime() {
    return time;
  }

  /**
   * Defines the time in milli-seconds since 01-Jan-1970.
   *
   * @param time
   *          the time in milli-seconds since 01-Jan-1970.
   */
  public void setTime( final Long time ) {
    this.time = time;
  }

  /**
   * Returns the time in seconds since 01-Jan-1970.
   *
   * @return the time in seconds since 01-Jan-1970.
   */
  public Long getEpochTime() {
    return epochTime;
  }

  /**
   * Defines the time in seconds since 01-Jan-1970.
   *
   * @param epochTime
   *          the time in seconds since 01-Jan-1970.
   */
  public void setEpochTime( final Long epochTime ) {
    this.epochTime = epochTime;
  }

  /**
   * Returns the day of the week property.
   *
   * @return the day-of-the-week property.
   */
  public Integer getDayOfWeek() {
    return dayOfWeek;
  }

  /**
   * Defines the day of the week property.
   *
   * @param dayOfWeek
   *          the day-of-the-week property.
   */
  public void setDayOfWeek( final Integer dayOfWeek ) {
    this.dayOfWeek = dayOfWeek;
  }

  /**
   * Returns the day of the year property.
   *
   * @return the day-of-the-year property.
   */
  public Integer getDayOfYear() {
    return dayOfYear;
  }

  /**
   * Defines the day of the year property.
   *
   * @param dayOfYear
   *          the day-of-the-year property.
   */
  public void setDayOfYear( final Integer dayOfYear ) {
    this.dayOfYear = dayOfYear;
  }

  /**
   * Returns the day of the month property.
   *
   * @return the day-of-the-month property.
   */
  public Integer getDayOfMonth() {
    return dayOfMonth;
  }

  /**
   * Defines the day of the month property.
   *
   * @param dayOfMonth
   *          the day-of-the-month property.
   */
  public void setDayOfMonth( final Integer dayOfMonth ) {
    this.dayOfMonth = dayOfMonth;
  }

  /**
   * Returns the day of the week in the month property.
   *
   * @return the day of the week in the month property.
   */
  public Integer getDayOfWeekInMonth() {
    return dayOfWeekInMonth;
  }

  /**
   * Defines the day of the week in the month property.
   *
   * @param dayOfWeekInMonth
   *          the day of the week in the month property.
   */
  public void setDayOfWeekInMonth( final Integer dayOfWeekInMonth ) {
    this.dayOfWeekInMonth = dayOfWeekInMonth;
  }

  /**
   * Returns the week of the year property.
   *
   * @return the week of the year property.
   */
  public Integer getWeekOfYear() {
    return weekOfYear;
  }

  /**
   * Defines the week of the year property.
   *
   * @param weekOfYear
   *          the week of the year property.
   */
  public void setWeekOfYear( final Integer weekOfYear ) {
    this.weekOfYear = weekOfYear;
  }

  public Integer getWeekOfMonth() {
    return weekOfMonth;
  }

  public void setWeekOfMonth( final Integer weekOfMonth ) {
    this.weekOfMonth = weekOfMonth;
  }

  /**
   * Return the current expression value.
   * <P>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Calendar calendar = getCalendar();
    configureCalendar( calendar );
    return calendar.getTime();
  }

  /**
   * Configures the given Calendar instance by applying all defined fields to it.
   *
   * @param calendar
   *          the week of the year property.
   */
  protected void configureCalendar( final Calendar calendar ) {
    if ( time != null ) {
      calendar.setTime( new Date( time.longValue() ) );
    }
    if ( epochTime != null ) {
      calendar.setTime( new Date( epochTime.longValue() * 1000 ) );
    }
    if ( month != null ) {
      calendar.set( Calendar.MONTH, month.intValue() );
    }
    if ( dayOfMonth != null ) {
      calendar.set( Calendar.DAY_OF_MONTH, dayOfMonth.intValue() );
    }
    if ( year != null ) {
      calendar.set( Calendar.YEAR, year.intValue() );
    }
    if ( hour != null ) {
      calendar.set( Calendar.HOUR_OF_DAY, hour.intValue() );
    }
    if ( minute != null ) {
      calendar.set( Calendar.MINUTE, minute.intValue() );
    }
    if ( second != null ) {
      calendar.set( Calendar.SECOND, second.intValue() );
    }
    if ( milliSecond != null ) {
      calendar.set( Calendar.MILLISECOND, milliSecond.intValue() );
    }
    if ( dayOfWeek != null ) {
      calendar.set( Calendar.DAY_OF_WEEK, dayOfWeek.intValue() );
    }
    if ( dayOfYear != null ) {
      calendar.set( Calendar.DAY_OF_YEAR, dayOfYear.intValue() );
    }
    if ( dayOfWeekInMonth != null ) {
      calendar.set( Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInMonth.intValue() );
    }
    if ( weekOfYear != null ) {
      calendar.set( Calendar.WEEK_OF_YEAR, weekOfYear.intValue() );
    }
    if ( weekOfMonth != null ) {
      calendar.set( Calendar.WEEK_OF_MONTH, weekOfMonth.intValue() );
    }
    if ( timeZone != null ) {
      calendar.setTimeZone( getTimeZone() );
    }
  }

  /**
   * Create a new calendar instance. This implementation uses Calendar.getInstance(..) to create the Calendar, and
   * therefore the result depends on the locale of the report.
   *
   * @return the calendar.
   */
  protected Calendar getCalendar() {
    final ResourceBundleFactory rf = getResourceBundleFactory();
    return Calendar.getInstance( rf.getLocale() );
  }
}

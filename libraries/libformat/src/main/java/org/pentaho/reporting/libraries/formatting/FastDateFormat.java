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

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * A wrapper around the java.text.SimpleDateFormat class. This wrapper limits the possible interactions with the wrapped
 * format class and therefore we can treat the implementation as immutable.
 *
 * @author Thomas Morgner
 */
public class FastDateFormat implements FastFormat {
  private TimeZone timeZone;
  private Locale locale;
  private SimpleDateFormat dateFormat;
  private String pattern;
  private transient StringBuffer buffer;
  private transient DummyFieldPosition fieldPosition;

  /**
   * Creates a new date-format for the given pattern.
   *
   * @param pattern the pattern string.
   */
  public FastDateFormat( final String pattern ) {
    this( pattern, Locale.getDefault(), TimeZone.getDefault() );
  }

  /**
   * Creates a new date-format for the given pattern and locale.
   *
   * @param pattern the pattern string.
   * @param locale  the locale.
   */
  public FastDateFormat( final String pattern, final Locale locale ) {
    this( pattern, locale, TimeZone.getDefault() );
  }

  public FastDateFormat( final String pattern, final Locale locale, final TimeZone timeZone ) {
    if ( pattern == null ) {
      throw new NullPointerException();
    }
    if ( locale == null ) {
      throw new NullPointerException();
    }
    if ( timeZone == null ) {
      throw new NullPointerException();
    }

    this.timeZone = timeZone;
    this.pattern = pattern;
    this.locale = locale;
    this.dateFormat = new SimpleDateFormat( pattern, new DateFormatSymbols( locale ) );
    this.dateFormat.setTimeZone( timeZone );
  }

  /**
   * Creates a new date-format for the given default date and time style.
   *
   * @param dateStyle the date-style, one of DateFormat#SHORT, DateFormat#MEDIUM, DateFormat#LONG, DateFormat#FULL or -1
   *                  for none.
   * @param timeStyle the date-style, one of DateFormat#SHORT, DateFormat#MEDIUM, DateFormat#LONG, DateFormat#FULL or -1
   *                  for none.
   * @param locale    the locale.
   * @throws IllegalArgumentException if both date and time-style are set to -1.
   * @see DateFormat#getDateTimeInstance(int, int, Locale)
   */
  public FastDateFormat( final int dateStyle, final int timeStyle, final Locale locale ) {
    this( dateStyle, timeStyle, locale, TimeZone.getDefault() );
  }

  /**
   * Creates a new date-format for the given default date and time style along with a TimeZone.
   *
   * @param dateStyle the date-style, one of DateFormat#SHORT, DateFormat#MEDIUM, DateFormat#LONG, DateFormat#FULL or -1
   *                  for none.
   * @param timeStyle the date-style, one of DateFormat#SHORT, DateFormat#MEDIUM, DateFormat#LONG, DateFormat#FULL or -1
   *                  for none.
   * @param locale    the locale.
   * @param timeZone  the timeZone to which dates are interpreted.
   * @throws IllegalArgumentException if both date and time-style are set to -1.
   * @see DateFormat#getDateTimeInstance(int, int, Locale)
   */
  public FastDateFormat( final int dateStyle, final int timeStyle, final Locale locale, final TimeZone timeZone ) {
    if ( locale == null ) {
      throw new NullPointerException();
    }

    this.locale = locale;
    this.timeZone = timeZone;

    final DateFormat dateFormat = DateFormat.getDateTimeInstance( dateStyle, timeStyle, locale );
    if ( dateFormat instanceof SimpleDateFormat ) {
      this.dateFormat = (SimpleDateFormat) dateFormat;
      this.pattern = this.dateFormat.toPattern();
    } else {
      // this should not happen in Sun JDKs, but you never know how the implementation looks like ..
      final ResourceBundle datePatterns = ResourceBundle.getBundle
        ( "org.pentaho.reporting.libraries.formatting.format-patterns" );

      final String dateText;
      switch( dateStyle ) {
        case DateFormat.SHORT: {
          dateText = datePatterns.getString( "format.date.short" );
          break;
        }
        case DateFormat.MEDIUM: {
          dateText = datePatterns.getString( "format.date.medium" );
          break;
        }
        case DateFormat.LONG: {
          dateText = datePatterns.getString( "format.date.long" );
          break;
        }
        case DateFormat.FULL: {
          dateText = datePatterns.getString( "format.date.full" );
          break;
        }
        default:
          dateText = null;
      }
      final String timeText;
      switch( timeStyle ) {
        case DateFormat.SHORT: {
          timeText = datePatterns.getString( "format.time.short" );
          break;
        }
        case DateFormat.MEDIUM: {
          timeText = datePatterns.getString( "format.time.medium" );
          break;
        }
        case DateFormat.LONG: {
          timeText = datePatterns.getString( "format.time.long" );
          break;
        }
        case DateFormat.FULL: {
          timeText = datePatterns.getString( "format.time.full" );
          break;
        }
        default:
          timeText = null;
      }
      if ( dateText == null && timeText == null ) {
        throw new IllegalArgumentException();
      }
      if ( dateText == null ) {
        this.pattern = timeText;
      } else if ( timeText == null ) {
        this.pattern = dateText;
      } else {
        final String messagePattern = datePatterns.getString( "format.datetime" );
        this.pattern = MessageFormat.format( messagePattern, new Object[] { dateText, timeText } );
      }
      this.dateFormat = new SimpleDateFormat( pattern, new DateFormatSymbols( locale ) );
    }

    this.dateFormat.setTimeZone( timeZone );
  }

  /**
   * Updates the locale of  the choice format. This has no impact on the result of the choice-format computation.
   *
   * @param locale the locale, never null.
   */
  public void setLocale( final Locale locale ) {
    if ( locale == null ) {
      throw new NullPointerException();
    }
    if ( this.locale.equals( locale ) ) {
      return;
    }
    this.locale = locale;
    this.dateFormat = (SimpleDateFormat) dateFormat.clone();
    this.dateFormat.setDateFormatSymbols( new DateFormatSymbols( locale ) );
  }

  /**
   * Returns the current locale of the formatter.
   *
   * @return the current locale, never null.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Returns the pattern for the date-format.
   *
   * @return the pattern.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Formats the given object in a formatter-specific way.
   *
   * @param parameters the parameters for the formatting.
   * @return the formatted string.
   */
  public String format( final Object parameters ) {
    if ( parameters == null ) {
      throw new NullPointerException();
    }
    if ( buffer == null ) {
      buffer = new StringBuffer();
    } else {
      buffer.delete( 0, buffer.length() );
    }
    if ( fieldPosition == null ) {
      fieldPosition = new DummyFieldPosition();
    } else {
      fieldPosition.clear();
    }
    final StringBuffer stringBuffer = dateFormat.format( parameters, buffer, new DummyFieldPosition() );
    return stringBuffer.toString();
  }

  /**
   * Clones the formatter.
   *
   * @return the clone.
   */
  public FastDateFormat clone() {
    try {
      final FastDateFormat clone = (FastDateFormat) super.clone();
      clone.dateFormat = (SimpleDateFormat) dateFormat.clone();
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public void setTimeZone( final TimeZone timeZone ) {
    this.timeZone = timeZone;
    this.dateFormat = (SimpleDateFormat) dateFormat.clone();
    this.dateFormat.setTimeZone( timeZone );
  }
}

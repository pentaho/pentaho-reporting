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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Parses a string into a date using the given date-format.
 *
 * @author Thomas Morgner
 * @see java.text.SimpleDateFormat
 * @deprecated use a formula function (ParseDate) instead.
 */
public class ConvertToDateExpression extends AbstractExpression {
  /**
   * The name of the data-row column from where to read the string that should be parsed.
   */
  private String field;
  /**
   * The date-format that is used for the parsing.
   */
  private String format;
  /**
   * The locale. If undefined, the report's locale is used.
   */
  private Locale locale;

  private TimeZone timeZone;

  /**
   * A variable that caches the last dateformat object used.
   */
  private transient DateFormat dateFormat;
  /**
   * A variable that caches the last locale object used.
   */
  private transient Locale lastLocale;
  private transient TimeZone lastTimeZone;

  /**
   * Default Constructor.
   */
  public ConvertToDateExpression() {
  }

  /**
   * Returns the name of the data-row column from where to read the string that should be parsed.
   *
   * @return the field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the data-row column from where to read the string that should be parsed.
   *
   * @param field
   *          the name of the field.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Returns the SimpleDateFormat pattern that is used for the parsing.
   *
   * @return the pattern string.
   * @see java.text.SimpleDateFormat
   */
  public String getFormat() {
    return format;
  }

  /**
   * Defines the SimpleDateFormat pattern that is used for the parsing.
   *
   * @param format
   *          the pattern string.
   * @see java.text.SimpleDateFormat
   */
  public void setFormat( final String format ) {
    this.format = format;
    this.lastLocale = null;
    this.dateFormat = null;
  }

  /**
   * Returns the locale that is used for the parsing.
   *
   * @return the locale.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Defines the locale that is used for the parsing.
   *
   * @param locale
   *          the locale.
   */
  public void setLocale( final Locale locale ) {
    this.locale = locale;
    this.lastLocale = null;
    this.dateFormat = null;
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public void setTimeZone( final TimeZone timeZone ) {
    this.timeZone = timeZone;
    this.lastTimeZone = null;
  }

  /**
   * Parses the value read from the column specified by the given field-name and tries to parse it into a Date using the
   * given SimpleDateFormat-pattern.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final DataRow dataRow = getDataRow();
    // get the row directly as a Number
    final Object o = dataRow.get( field );
    // check if that thing is a Number
    if ( o instanceof Date ) {
      return o;
    }

    // get a string and convert
    try {
      Locale localeUsed = locale;
      if ( localeUsed == null ) {
        localeUsed = getResourceBundleFactory().getLocale();
      }

      final DateFormat format;
      if ( dateFormat == null || ObjectUtilities.equal( localeUsed, lastLocale ) == false ) {
        final String formatString = getFormat();
        if ( formatString == null || formatString.length() == 0 ) {
          format = DateFormat.getDateInstance( DateFormat.DEFAULT, localeUsed );
          dateFormat = format;
          lastLocale = localeUsed;
        } else {
          final SimpleDateFormat sformat = new SimpleDateFormat( formatString );
          if ( locale != null ) {
            sformat.setDateFormatSymbols( new DateFormatSymbols( locale ) );
          } else {
            final ResourceBundleFactory factory = getResourceBundleFactory();
            sformat.setDateFormatSymbols( new DateFormatSymbols( factory.getLocale() ) );
          }
          format = sformat;
          dateFormat = sformat;
          lastLocale = localeUsed;
        }
      } else {
        format = dateFormat;
      }
      if ( ObjectUtilities.equal( timeZone, lastTimeZone ) == false ) {
        lastTimeZone = timeZone;
        format.setTimeZone( timeZone );
      }
      return format.parse( String.valueOf( o ) );
    } catch ( ParseException e ) {
      return null;
    }
  }

}

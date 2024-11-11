/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.filter;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.text.DateFormat;
import java.text.Format;
import java.util.Date;
import java.util.TimeZone;

/**
 * Parses a String into a java.util.Date. The string is read from the given datasource and then parsed by the dateformat
 * contained in this FormatParser.
 *
 * @author Thomas Morgner
 */
public class DateFormatParser extends FormatParser {
  private TimeZone timeZone;

  /**
   * Creates a new 'date format parser'.
   */
  public DateFormatParser() {
    setDateFormat( DateFormat.getInstance() );
  }

  /**
   * Returns the format for this filter. The format object is returned as DateFormat.
   *
   * @return the formatter.
   * @throws NullPointerException
   *           if the given format is null
   */
  public DateFormat getDateFormat() {
    return (DateFormat) getFormatter();
  }

  /**
   * Sets the format for the filter.
   *
   * @param format
   *          The format.
   * @throws NullPointerException
   *           if the given format is null
   */
  public void setDateFormat( final DateFormat format ) {
    super.setFormatter( format );
  }

  /**
   * Sets the format for the filter. The formater is required to be of type DateFormat.
   *
   * @param format
   *          The format.
   * @throws NullPointerException
   *           if the given format is null
   * @throws ClassCastException
   *           if an invalid formater is set.
   */
  public void setFormatter( final Format format ) {
    final DateFormat dfmt = (DateFormat) format;
    super.setFormatter( dfmt );
  }

  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final TimeZone timeZone = runtime.getResourceBundleFactory().getTimeZone();
    if ( ObjectUtilities.equal( timeZone, this.timeZone ) == false ) {
      this.timeZone = timeZone;
      getDateFormat().setTimeZone( timeZone );
    }
    return super.getValue( runtime, element );
  }

  /**
   * Sets the value that will be displayed if the data source supplies a null value. The nullValue itself can be null to
   * cover the case when no reasonable default value can be defined.
   * <p/>
   * The null value for date format parsers is required to be either null or a java.util.Date.
   *
   * @param nullvalue
   *          the nullvalue returned when parsing failed.
   * @throws ClassCastException
   *           if the value is no date or not null.
   */
  public void setNullValue( final Object nullvalue ) {
    final Date dt = (Date) nullvalue;
    super.setNullValue( dt );
  }

  /**
   * Checks whether the given value is already a valid result. IF the datasource already returned a valid value, and no
   * parsing is required, a parser can skip the parsing process by returning true in this function.
   *
   * @param o
   *          the value.
   * @return true, if the given value is already an instance of date.
   */
  protected boolean isValidOutput( final Object o ) {
    return o instanceof Date;
  }
}

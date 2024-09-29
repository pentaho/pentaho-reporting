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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;

/**
 * A filter that parses string values from a data source to a number using the decimal numeric system as base.
 * <p/>
 * This filter will parse strings using a java.text.DecimalFormat to create the numeric value for the string from the
 * datasource. If the datasource does not return a string, the required string is formed by applying String.valueOf
 * (Object).
 * <p/>
 * If the string read from the datasource could not be parsed into a number, the NullValue defined by
 * setNullValue(Object) is returned.
 *
 * @author Thomas Morgner
 * @see java.text.NumberFormat
 * @see java.lang.Number
 */
public class DecimalFormatParser extends NumberFormatParser {
  /**
   * The last locale used to convert numbers.
   */
  private Locale lastLocale;
  /**
   * A flag indicating whether this filter should try to detect locales changes.
   */
  private boolean keepState;

  /**
   * DefaultConstructor, this object is initialized using a DecimalFormat with the default pattern for this locale.
   */
  public DecimalFormatParser() {
    setFormatter( new DecimalFormat() );
  }

  /**
   * Returns the format for the filter. The DecimalFormatParser has only DecimalFormat objects assigned.
   *
   * @return the formatter.
   */
  public DecimalFormat getDecimalFormat() {
    return (DecimalFormat) getFormatter();
  }

  /**
   * Sets the format for the filter.
   *
   * @param format
   *          the format.
   * @throws NullPointerException
   *           if the given format is null.
   */
  public void setDecimalFormat( final DecimalFormat format ) {
    setFormatter( format );
  }

  /**
   * Sets the format for the filter. If the given format is no Decimal format, a ClassCastException is thrown
   *
   * @param format
   *          The format.
   * @throws NullPointerException
   *           if the given format is null
   * @throws ClassCastException
   *           if the format is no decimal format
   */
  public void setFormatter( final Format format ) {
    if ( format == null ) {
      throw new NullPointerException( "The number format given must not be null." );
    }
    final DecimalFormat dfmt = (DecimalFormat) format;
    dfmt.setParseBigDecimal( true );
    super.setFormatter( dfmt );
  }

  /**
   * Synthesizes a pattern string that represents the current state of this Format object.
   *
   * @return the pattern string of the format object contained in this filter.
   */
  public String getFormatString() {
    return getDecimalFormat().toPattern();
  }

  /**
   * Applies a format string to the internal <code>DecimalFormat</code> instance.
   *
   * @param format
   *          the format string.
   */
  public void setFormatString( final String format ) {
    getDecimalFormat().applyPattern( format );
  }

  /**
   * Synthesizes a localized pattern string that represents the current state of this Format object.
   *
   * @return the localized pattern string of the format-object.
   */
  public String getLocalizedFormatString() {
    return getDecimalFormat().toLocalizedPattern();
  }

  /**
   * Applies a localised format string to the internal <code>DecimalFormat</code> instance.
   *
   * @param format
   *          the format string.
   */
  public void setLocalizedFormatString( final String format ) {
    getDecimalFormat().applyLocalizedPattern( format );
  }

  /**
   * Defines, whether the filter should keep its state, if a locale change is detected. This will effectivly disable the
   * locale update.
   *
   * @return true, if the locale should not update the DateSymbols, false otherwise.
   */
  public boolean isKeepState() {
    return keepState;
  }

  /**
   * Defines, whether the filter should keep its state, if a locale change is detected. This will effectivly disable the
   * locale update.
   *
   * @param keepState
   *          set to true, if the locale should not update the DateSymbols, false otherwise.
   */
  public void setKeepState( final boolean keepState ) {
    this.keepState = keepState;
  }

  /**
   * Returns the formatted string. The value is read using the data source given and formated using the formatter of
   * this object. The formating is guaranteed to completly form the object to an string or to return the defined
   * NullValue.
   * <p/>
   * If format, datasource or object are null, the NullValue is returned.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return The formatted value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( keepState == false && runtime != null ) {
      final Locale locale = runtime.getResourceBundleFactory().getLocale();
      if ( locale != null && locale.equals( lastLocale ) == false ) {
        lastLocale = locale;
        getDecimalFormat().setDecimalFormatSymbols( new DecimalFormatSymbols( locale ) );
      }
    }
    return super.getValue( runtime, element );
  }
}

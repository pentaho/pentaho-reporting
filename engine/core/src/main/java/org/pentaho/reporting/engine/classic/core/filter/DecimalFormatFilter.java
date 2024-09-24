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

package org.pentaho.reporting.engine.classic.core.filter;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;

/**
 * A filter that formats the numeric value from a data source to a string representation using the decimal number system
 * as base.
 * <p/>
 * This filter will format java.lang.Number objects using a java.text.DecimalFormat to create the string representation
 * for the date obtained from the datasource.
 * <p/>
 * If the object read from the datasource is no date, the NullValue defined by setNullValue(Object) is returned.
 *
 * @author Thomas Morgner
 * @see java.text.NumberFormat
 * @see java.lang.Number
 */
public class DecimalFormatFilter extends NumberFormatFilter {
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
  public DecimalFormatFilter() {
    setFormatter( new DecimalFormat() );
  }

  /**
   * Returns the format for the filter. The DecimalFormatParser has only DecimalFormat objects assigned.
   *
   * @return the formatter.
   * @throws NullPointerException
   *           if the given format is null
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
   *           if the given format is null
   */
  public void setDecimalFormat( final DecimalFormat format ) {
    setFormatter( format );
  }

  /**
   * Sets the format for the filter. If the given format is no Decimal format, a ClassCastException is thrown
   *
   * @param format
   *          the format.
   * @throws NullPointerException
   *           if the given format is null
   * @throws ClassCastException
   *           if the format is no decimal format
   */
  public void setFormatter( final Format format ) {
    final DecimalFormat dfmt = (DecimalFormat) format;
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
    invalidateCache();
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
    invalidateCache();
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
        invalidateCache();
      }
    }
    return super.getValue( runtime, element );
  }

  public FormatSpecification getFormatString( final ExpressionRuntime runtime, final ReportElement element,
      FormatSpecification formatSpecification ) {
    if ( formatSpecification == null ) {
      formatSpecification = new FormatSpecification();
    }
    formatSpecification.redefine( FormatSpecification.TYPE_DECIMAL_FORMAT, getFormatString() );
    return formatSpecification;
  }

}

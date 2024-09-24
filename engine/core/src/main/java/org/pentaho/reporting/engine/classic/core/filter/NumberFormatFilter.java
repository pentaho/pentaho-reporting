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

import java.text.Format;
import java.text.NumberFormat;

/**
 * A filter that formats the numeric value from a data source to a string representation.
 * <p/>
 * This filter will format java.lang.Number objects using a java.text.NumericFormat to create the string representation
 * for the number obtained from the datasource.
 * <p/>
 * If the object read from the datasource is no number, the NullValue defined by setNullValue(Object) is returned.
 *
 * @author Thomas Morgner
 * @see java.text.NumberFormat
 */
public class NumberFormatFilter extends FormatFilter {
  /**
   * Default constructor.
   * <P>
   * Uses a general number format for the current locale.
   */
  public NumberFormatFilter() {
    setNumberFormat( NumberFormat.getInstance() );
  }

  /**
   * Sets the number format.
   *
   * @param nf
   *          The number format.
   */
  public void setNumberFormat( final NumberFormat nf ) {
    setFormatter( nf );
  }

  /**
   * Returns the number format.
   *
   * @return The number format.
   */
  public NumberFormat getNumberFormat() {
    return (NumberFormat) getFormatter();
  }

  /**
   * Sets the formatter.
   *
   * @param f
   *          The format.
   */
  public void setFormatter( final Format f ) {
    final NumberFormat fm = (NumberFormat) f;
    super.setFormatter( fm );
  }

  /**
   * Turns grouping on or off for the current number format.
   *
   * @param newValue
   *          The new value of the grouping flag.
   */
  public void setGroupingUsed( final boolean newValue ) {
    getNumberFormat().setGroupingUsed( newValue );
    invalidateCache();
  }

  /**
   * Returns the value of the grouping flag for the current number format.
   *
   * @return The grouping flag.
   */
  public boolean isGroupingUsed() {
    return getNumberFormat().isGroupingUsed();
  }

  /**
   * Sets the maximum number of fraction digits for the current number format.
   *
   * @param newValue
   *          The number of digits.
   */
  public void setMaximumFractionDigits( final int newValue ) {
    getNumberFormat().setMaximumFractionDigits( newValue );
    invalidateCache();
  }

  /**
   * Returns the maximum number of fraction digits.
   *
   * @return The digits.
   */
  public int getMaximumFractionDigits() {
    return getNumberFormat().getMaximumFractionDigits();
  }

  /**
   * Sets the maximum number of digits in the integer part of the current number format.
   *
   * @param newValue
   *          The number of digits.
   */
  public void setMaximumIntegerDigits( final int newValue ) {
    getNumberFormat().setMaximumFractionDigits( newValue );
    invalidateCache();
  }

  /**
   * Returns the maximum number of integer digits.
   *
   * @return The digits.
   */
  public int getMaximumIntegerDigits() {
    return getNumberFormat().getMaximumFractionDigits();
  }

  /**
   * Sets the minimum number of fraction digits for the current number format.
   *
   * @param newValue
   *          The number of digits.
   */
  public void setMinimumFractionDigits( final int newValue ) {
    getNumberFormat().setMaximumFractionDigits( newValue );
    invalidateCache();
  }

  /**
   * Returns the minimum number of fraction digits.
   *
   * @return The digits.
   */
  public int getMinimumFractionDigits() {
    return getNumberFormat().getMaximumFractionDigits();
  }

  /**
   * Sets the minimum number of digits in the integer part of the current number format.
   *
   * @param newValue
   *          The number of digits.
   */
  public void setMinimumIntegerDigits( final int newValue ) {
    getNumberFormat().setMaximumFractionDigits( newValue );
    invalidateCache();
  }

  /**
   * Returns the minimum number of integer digits.
   *
   * @return The digits.
   */
  public int getMinimumIntegerDigits() {
    return getNumberFormat().getMaximumFractionDigits();
  }

  public FormatSpecification getFormatString( final ExpressionRuntime runtime, final ReportElement element,
      FormatSpecification formatSpecification ) {
    final DataSource source = getDataSource();
    if ( source instanceof RawDataSource ) {
      final RawDataSource rds = (RawDataSource) source;
      return rds.getFormatString( runtime, element, formatSpecification );
    }
    if ( formatSpecification == null ) {
      formatSpecification = new FormatSpecification();
    }
    formatSpecification.redefine( FormatSpecification.TYPE_UNDEFINED, null );
    return formatSpecification;
  }

}

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

package org.pentaho.reporting.engine.classic.core.filter.templates;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.DataRowDataSource;
import org.pentaho.reporting.engine.classic.core.filter.FormatSpecification;
import org.pentaho.reporting.engine.classic.core.filter.RawDataSource;
import org.pentaho.reporting.engine.classic.core.filter.SimpleDateFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.StringFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.text.SimpleDateFormat;

/**
 * A date field template.
 *
 * @author Thomas Morgner
 */
public class DateFieldTemplate extends AbstractTemplate implements RawDataSource {
  /**
   * The date format filter.
   */
  private SimpleDateFormatFilter dateFilter;

  /**
   * The data-row datasource.
   */
  private DataRowDataSource dataRowDataSource;

  /**
   * A string filter.
   */
  private StringFilter stringFilter;

  /**
   * Creates a new date field template.
   */
  public DateFieldTemplate() {
    dataRowDataSource = new DataRowDataSource();
    dateFilter = new SimpleDateFormatFilter();
    dateFilter.setDataSource( dataRowDataSource );
    stringFilter = new StringFilter();
    stringFilter.setDataSource( dateFilter );
  }

  /**
   * Returns the date format string.
   *
   * @return The date format string.
   */
  public String getFormat() {
    return getDateFilter().getFormatString();
  }

  /**
   * Sets the date format string.
   *
   * @param format
   *          the format string.
   */
  public void setFormat( final String format ) {
    getDateFilter().setFormatString( format );
  }

  /**
   * Returns the field name.
   *
   * @return The field name.
   */
  public String getField() {
    return getDataRowDataSource().getDataSourceColumnName();
  }

  /**
   * Sets the field name.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    getDataRowDataSource().setDataSourceColumnName( field );
  }

  /**
   * Returns the formula used to compute the value of the data source.
   *
   * @return the formula.
   */
  public String getFormula() {
    return dataRowDataSource.getFormula();
  }

  /**
   * Defines the formula used to compute the value of this data source.
   *
   * @param formula
   *          the formula for the data source.
   */
  public void setFormula( final String formula ) {
    dataRowDataSource.setFormula( formula );
  }

  /**
   * Returns the string that represents <code>null</code> values.
   *
   * @return A string.
   */
  public String getNullValue() {
    return getStringFilter().getNullValue();
  }

  /**
   * Sets the string that represents <code>null</code> values.
   *
   * @param nullValue
   *          the string that represents <code>null</code> values.
   */
  public void setNullValue( final String nullValue ) {
    getStringFilter().setNullValue( nullValue );
  }

  /**
   * Returns the date formatter.
   *
   * @return The date formatter.
   */
  public SimpleDateFormat getDateFormat() {
    return (SimpleDateFormat) getDateFilter().getFormatter();
  }

  /**
   * Sets the date formatter.
   *
   * @param dateFormat
   *          the date formatter.
   */
  public void setDateFormat( final SimpleDateFormat dateFormat ) {
    getDateFilter().setFormatter( dateFormat );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return getStringFilter().getValue( runtime, element );
  }

  /**
   * Clones this template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public DateFieldTemplate clone() throws CloneNotSupportedException {
    final DateFieldTemplate template = (DateFieldTemplate) super.clone();
    template.stringFilter = stringFilter.clone();
    template.dateFilter = (SimpleDateFormatFilter) template.stringFilter.getDataSource();
    template.dataRowDataSource = (DataRowDataSource) template.dateFilter.getDataSource();
    return template;
  }

  /**
   * Returns the date filter.
   *
   * @return The date filter.
   */
  protected SimpleDateFormatFilter getDateFilter() {
    return dateFilter;
  }

  /**
   * Returns the data-row datasource.
   *
   * @return The data-row datasource.
   */
  protected DataRowDataSource getDataRowDataSource() {
    return dataRowDataSource;
  }

  /**
   * Returns the string filter.
   *
   * @return The string filter.
   */
  protected StringFilter getStringFilter() {
    return stringFilter;
  }

  public Object getRawValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return dateFilter.getRawValue( runtime, element );
  }

  public FormatSpecification getFormatString( final ExpressionRuntime runtime, final ReportElement element,
      final FormatSpecification formatSpecification ) {
    return dateFilter.getFormatString( runtime, element, formatSpecification );
  }
}

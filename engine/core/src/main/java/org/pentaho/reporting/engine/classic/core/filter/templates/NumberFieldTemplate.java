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
import org.pentaho.reporting.engine.classic.core.filter.DecimalFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.FormatSpecification;
import org.pentaho.reporting.engine.classic.core.filter.RawDataSource;
import org.pentaho.reporting.engine.classic.core.filter.StringFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.text.DecimalFormat;

/**
 * A number field template.
 *
 * @author Thomas Morgner
 */
public class NumberFieldTemplate extends AbstractTemplate implements RawDataSource {
  /**
   * A decimal format filter.
   */
  private DecimalFormatFilter decimalFormatFilter;

  /**
   * A data-row accessor.
   */
  private DataRowDataSource dataRowDataSource;

  /**
   * A string filter.
   */
  private StringFilter stringFilter;

  /**
   * Creates a new number field template.
   */
  public NumberFieldTemplate() {
    dataRowDataSource = new DataRowDataSource();
    decimalFormatFilter = new DecimalFormatFilter();
    decimalFormatFilter.setDataSource( dataRowDataSource );
    stringFilter = new StringFilter();
    stringFilter.setDataSource( decimalFormatFilter );
  }

  /**
   * Returns the number formatter.
   *
   * @return The number formatter.
   */
  public DecimalFormat getDecimalFormat() {
    return (DecimalFormat) decimalFormatFilter.getFormatter();
  }

  /**
   * Sets the number formatter.
   *
   * @param decimalFormat
   *          the number formatter.
   */
  public void setDecimalFormat( final DecimalFormat decimalFormat ) {
    decimalFormatFilter.setFormatter( decimalFormat );
  }

  /**
   * Returns the format string.
   *
   * @return The format string.
   */
  public String getFormat() {
    return decimalFormatFilter.getFormatString();
  }

  /**
   * Sets the format string.
   *
   * @param format
   *          the format string.
   */
  public void setFormat( final String format ) {
    decimalFormatFilter.setFormatString( format );
  }

  /**
   * Returns the field name.
   *
   * @return The field name.
   */
  public String getField() {
    return dataRowDataSource.getDataSourceColumnName();
  }

  /**
   * Sets the field name.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    dataRowDataSource.setDataSourceColumnName( field );
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
   * Returns the string that represents a <code>null</code> value.
   *
   * @return A string.
   */
  public String getNullValue() {
    return stringFilter.getNullValue();
  }

  /**
   * Sets the string that represents a <code>null</code> value.
   *
   * @param nullValue
   *          the string that represents a <code>null</code> value.
   */
  public void setNullValue( final String nullValue ) {
    stringFilter.setNullValue( nullValue );
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
    return stringFilter.getValue( runtime, element );
  }

  /**
   * Clones the template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public NumberFieldTemplate clone() throws CloneNotSupportedException {
    final NumberFieldTemplate template = (NumberFieldTemplate) super.clone();
    template.stringFilter = stringFilter.clone();
    template.decimalFormatFilter = (DecimalFormatFilter) template.stringFilter.getDataSource();
    template.dataRowDataSource = (DataRowDataSource) template.decimalFormatFilter.getDataSource();
    return template;
  }

  /**
   * Returns the datarow data source used in this template.
   *
   * @return the datarow data source.
   */
  protected DataRowDataSource getDataRowDataSource() {
    return dataRowDataSource;
  }

  public Object getRawValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return decimalFormatFilter.getRawValue( runtime, element );
  }

  public FormatSpecification getFormatString( final ExpressionRuntime runtime, final ReportElement element,
      final FormatSpecification formatSpecification ) {
    return decimalFormatFilter.getFormatString( runtime, element, formatSpecification );
  }
}

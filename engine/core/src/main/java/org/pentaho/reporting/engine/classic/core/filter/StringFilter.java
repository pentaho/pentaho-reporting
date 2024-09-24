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
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * A filter that returns the value from a data source as a String. The value is converted to an String using
 * String.valueOf () which uses Object.toString() to convert the object into the string.
 * <p/>
 * You can specify a default string to return when the value from the data source is <code>null</code>. Initially the
 * string 'null' is used.
 *
 * @author Thomas Morgner
 */
public class StringFilter implements DataFilter, RawDataSource {
  /**
   * The data source for this filter.
   */
  private DataSource source;

  /**
   * The string used to represent a null value.
   */
  private String nullvalue;

  /**
   * Default constructor.
   */
  public StringFilter() {
  }

  /**
   * Sets the string used to represent a null value.
   *
   * @param nullvalue
   *          the null value.
   */
  public void setNullValue( final String nullvalue ) {
    this.nullvalue = nullvalue;
  }

  /**
   * Returns the string used to represent a null value.
   *
   * @return the string.
   */
  public String getNullValue() {
    return nullvalue;
  }

  /**
   * Returns the value obtained from the data source.
   * <P>
   * The filter ensures that the returned value is a String, even though the return type is Object (as required by the
   * DataSource interface).
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the string.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final DataSource ds = getDataSource();
    if ( ds == null ) {
      return getNullValue();
    }
    final Object o = ds.getValue( runtime, element );
    if ( o == null ) {
      return getNullValue();
    }
    // String is final, so it is safe to do this ...
    final String retval = ElementTypeUtils.toString( o );
    if ( retval == null ) {
      return getNullValue();
    }
    return retval;
  }

  /**
   * Returns the data source for this filter.
   *
   * @return the data source.
   */
  public DataSource getDataSource() {
    return source;
  }

  /**
   * Sets the data source for this filter.
   *
   * @param ds
   *          the data source.
   */
  public void setDataSource( final DataSource ds ) {
    if ( ds == null ) {
      throw new NullPointerException();
    }
    source = ds;
  }

  /**
   * Clones the filter.
   *
   * @return a clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public StringFilter clone() throws CloneNotSupportedException {
    final StringFilter f = (StringFilter) super.clone();
    if ( source != null ) {
      f.source = source.clone();
    }
    return f;
  }

  public Object getRawValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( source instanceof RawDataSource ) {
      final RawDataSource rawDataSource = (RawDataSource) source;
      return rawDataSource.getRawValue( runtime, element );
    }
    return source.getValue( runtime, element );
  }

  public FormatSpecification getFormatString( final ExpressionRuntime runtime, final ReportElement element,
      FormatSpecification formatSpecification ) {
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

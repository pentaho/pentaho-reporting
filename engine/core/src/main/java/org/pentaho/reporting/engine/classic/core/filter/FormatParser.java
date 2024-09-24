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
import java.text.ParseException;

/**
 * A format parser tries to parse a string into an object. If the value returned by the datasource is no string, a
 * string is formed using String.valueOf (Object). This string is fed into the java.text.Format of this FormatParser and
 * the parsed object is returned.
 * <p/>
 * What class of object is returned, is determined by the given format. If parsing failed, the defined NullValue is
 * returned.
 *
 * @author Thomas Morgner
 */
public class FormatParser implements DataFilter {
  /**
   * The format used to create the string representation of the data.
   */
  private Format format;

  /**
   * The datasource from where the data is obtained.
   */
  private DataSource datasource;

  /**
   * The object used to represent null.
   */
  private Object nullvalue;

  /**
   * DefaultConstructor.
   */
  public FormatParser() {
  }

  /**
   * Sets the format for the filter.
   *
   * @param format
   *          The format.
   * @throws NullPointerException
   *           if the given format is null
   */
  public void setFormatter( final Format format ) {
    if ( format == null ) {
      throw new NullPointerException();
    }
    this.format = format;
  }

  /**
   * Returns the format for the filter.
   *
   * @return The format.
   */
  public Format getFormatter() {
    return this.format;
  }

  /**
   * Returns the parsed object. The value is read using the data source given and parsed using the formatter of this
   * object. The parsing is guaranteed to completly form the target object or to return the defined NullValue.
   * <p/>
   * If the given datasource does not return a string, the returned object is transformed into a string using
   * String.valueOf (Object) and then parsed.
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
    final Format f = getFormatter();
    if ( f == null ) {
      return getNullValue();
    }

    final DataSource ds = getDataSource();
    if ( ds == null ) {
      return getNullValue();
    }

    final Object o = ds.getValue( runtime, element );
    if ( o == null ) {
      return getNullValue();
    }

    if ( isValidOutput( o ) ) {
      return o;
    }

    try {
      return f.parseObject( String.valueOf( o ) );
    } catch ( ParseException e ) {
      return null;
    }
  }

  /**
   * Checks whether the given value is already a valid result. IF the datasource already returned a valid value, and no
   * parsing is required, a parser can skip the parsing process by returning true in this function.
   *
   * @param o
   *          the object to parse.
   * @return false as this class does not know anything about the format of input or result objects.
   */
  protected boolean isValidOutput( final Object o ) {
    return false;
  }

  /**
   * Returns the data source for the filter.
   *
   * @return The data source.
   */
  public DataSource getDataSource() {
    return datasource;
  }

  /**
   * Sets the data source.
   *
   * @param ds
   *          The data source.
   */
  public void setDataSource( final DataSource ds ) {
    if ( ds == null ) {
      throw new NullPointerException();
    }
    this.datasource = ds;
  }

  /**
   * Sets the value that will be displayed if the data source supplies a null value. The nullValue itself can be null to
   * cover the case when no reasonable default value can be defined.
   *
   * @param nullvalue
   *          The value returned when the parsing failed.
   */
  public void setNullValue( final Object nullvalue ) {
    this.nullvalue = nullvalue;
  }

  /**
   * Returns the object representing a null value from the data source. This value will also be returned when parsing
   * failed or no parser or datasource is set at all.
   *
   * @return The value returned when the parsing failed.
   */
  public Object getNullValue() {
    return nullvalue;
  }

  /**
   * Clones the parser.
   *
   * @return a clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public FormatParser clone() throws CloneNotSupportedException {
    final FormatParser p = (FormatParser) super.clone();
    if ( datasource != null ) {
      p.datasource = datasource.clone();
    }
    if ( format != null ) {
      p.format = (Format) format.clone();
    }
    return p;
  }

}

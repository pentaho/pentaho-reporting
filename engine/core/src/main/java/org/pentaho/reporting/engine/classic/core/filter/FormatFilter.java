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

import java.text.Format;

/**
 * The base class for filters that format data. Data is received from a DataSource and formatted. The data source might
 * be a field in the TableModel or a report function, or even another format filter (since filters implement the
 * DataSource interface).
 * <p/>
 * Formating is done by a java.text.Format object. This filter will always return a String object on getValue().
 * <p/>
 * If the formater does not understand the object returned by the defined datasource, the defined null value is
 * returned.
 * <p/>
 * The nullValue is set to "-" by default.
 *
 * @author Thomas Morgner
 */
public class FormatFilter implements DataFilter, RawDataSource {
  /**
   * The format used to create the string representation of the data.
   */
  private Format format;

  /**
   * The datasource from where the data is obtained.
   */
  private DataSource datasource;

  /**
   * The string used to represent null.
   */
  private String nullvalue;

  /**
   * A string holding the formatted result.
   */
  private transient String cachedResult;
  /**
   * The cached raw-value. This value is used to compare the incomming value with the cached result.
   */
  private transient Object cachedValue;
  /**
   * The cached Formatter.
   */
  private transient Format cachedFormat;

  /**
   * Default constructor.
   */
  public FormatFilter() {
    nullvalue = null;
  }

  /**
   * Clears all cached values and forces a complete recomputation of all formattings.
   */
  protected void invalidateCache() {
    cachedFormat = null;
    cachedValue = null;
    cachedResult = null;
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

    if ( cachedResult != null && ( cachedFormat != f ) && ObjectUtilities.equal( cachedValue, o ) ) {
      return cachedResult;
    }

    try {
      cachedResult = f.format( o );
    } catch ( IllegalArgumentException e ) {
      cachedResult = getNullValue();
    }

    cachedFormat = f;
    cachedValue = o;
    return cachedResult;
  }

  /**
   * Sets the value that will be displayed if the data source supplies a null value.
   *
   * @param nullvalue
   *          The string.
   */
  public void setNullValue( final String nullvalue ) {
    if ( nullvalue == null ) {
      throw new NullPointerException();
    }
    this.nullvalue = nullvalue;
  }

  /**
   * Returns the string representing a null value from the data source.
   *
   * @return The string.
   */
  public String getNullValue() {
    return nullvalue;
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
   * Clones the filter.
   *
   * @return a clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public FormatFilter clone() throws CloneNotSupportedException {
    final FormatFilter f = (FormatFilter) super.clone();
    if ( datasource != null ) {
      f.datasource = datasource.clone();
    }
    if ( format != null ) {
      f.format = (Format) format.clone();
      if ( cachedFormat == format ) {
        f.cachedFormat = f.format;
      }
    }
    return f;
  }

  public Object getRawValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return datasource.getValue( runtime, element );
  }

  public FormatSpecification getFormatString( final ExpressionRuntime runtime, final ReportElement element,
      FormatSpecification formatSpecification ) {
    if ( datasource instanceof RawDataSource ) {
      final RawDataSource rds = (RawDataSource) datasource;
      return rds.getFormatString( runtime, element, formatSpecification );
    }
    if ( formatSpecification == null ) {
      formatSpecification = new FormatSpecification();
    }
    formatSpecification.redefine( FormatSpecification.TYPE_UNDEFINED, null );
    return formatSpecification;
  }

}

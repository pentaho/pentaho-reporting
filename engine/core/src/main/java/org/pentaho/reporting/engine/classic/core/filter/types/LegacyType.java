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


package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.FormatSpecification;
import org.pentaho.reporting.engine.classic.core.filter.RawDataSource;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

public class LegacyType extends AbstractElementType implements RawDataSource {
  public static final LegacyType INSTANCE = new LegacyType();

  public LegacyType() {
    super( "legacy-element" );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element.
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    if ( element instanceof Element ) {
      final Element e = (Element) element;
      return e.getDataSource().getValue( runtime, element );
    }
    return null;
  }

  /**
   * Returns the unformated raw value. Whether that raw value is useable for the export is beyond the scope of this API
   * definition, but providing access to {@link Number} or {@link java.util.Date} objects is a good idea.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the raw data.
   */
  public Object getRawValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( !( element instanceof Element ) ) {
      return null;
    }
    final Element e = (Element) element;
    final DataSource source = e.getDataSource();
    if ( source instanceof RawDataSource ) {
      final RawDataSource rds = (RawDataSource) source;
      return rds.getRawValue( runtime, element );
    }
    return e.getDataSource().getValue( runtime, element );
  }

  /**
   * Returns information about the formatstring that was used to transform a raw-value into a formatted text. Not all
   * elements will make use of a format-string. These elements will return
   * {@link org.pentaho.reporting.engine .classic.core.filter.FormatSpecification#TYPE_UNDEFINED} in that case.
   *
   * @param runtime
   *          the Expression runtime used to possibly compute the raw-value.
   * @param element
   *          the element to which this datasource is added.
   * @param formatSpecification
   *          the format specification (can be null). @return a filled format specififcation. If the
   *          <code>formatSpecification</code> parameter was not null, this given instance is reused.
   */
  public FormatSpecification getFormatString( final ExpressionRuntime runtime, final ReportElement element,
      FormatSpecification formatSpecification ) {
    if ( !( element instanceof Element ) ) {
      return null;
    }
    final Element e = (Element) element;
    final DataSource source = e.getDataSource();
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

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return "legacy-element";
  }
}

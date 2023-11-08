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
 * Copyright (c) 2001 - 2023 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.FormatSpecification;
import org.pentaho.reporting.engine.classic.core.filter.RawDataSource;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.formatting.FastDateFormat;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFieldType extends AbstractElementType implements RawDataSource, RotatableText {
  public static class DateFieldTypeContext {
    public transient FastDateFormat dateFormat;
    public transient Locale locale;
    public transient String formatString;
    public transient TimeZone timeZone;
  }

  public static final DateFieldType INSTANCE = new DateFieldType();
  public static final String DEFAULT_FORMAT = "dd.MM.yyyy HH:mm:ss";

  public DateFieldType() {
    super( "date-field" );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    Object formatStringRaw = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING );
    final Object staticValue = ElementTypeUtils.queryStaticValue( element );
    if ( staticValue instanceof Date ) {
      if ( formatStringRaw == null ) {
        // return the default to-string behavior of java.util.Date
        formatStringRaw = DEFAULT_FORMAT;
      }
      final Locale locale = runtime.getResourceBundleFactory().getLocale();
      final TimeZone timeZone = runtime.getResourceBundleFactory().getTimeZone();
      final SimpleDateFormat dateFormat = new SimpleDateFormat( String.valueOf( formatStringRaw ), locale );
      dateFormat.setDateFormatSymbols( new DateFormatSymbols( locale ) );
      dateFormat.setTimeZone( timeZone );
      return rotate( element, dateFormat.format( staticValue ), runtime );
    }
    final Object value = ElementTypeUtils.queryFieldName( element );
    return rotate( element, value != null ? value : getId(), runtime );
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
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object retval = ElementTypeUtils.queryFieldOrValue( runtime, element );
    if ( retval instanceof Date == false ) {
      return element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
    }
    return retval;
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
   *          the format specification (can be null).
   * @return a filled format specififcation. If the <code>formatSpecification</code> parameter was not null, this given
   *         instance is reused.
   */
  public FormatSpecification getFormatString( final ExpressionRuntime runtime, final ReportElement element,
      FormatSpecification formatSpecification ) {
    if ( formatSpecification == null ) {
      formatSpecification = new FormatSpecification();
    }

    final Object formatStringRaw =
      element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING );

    if ( formatStringRaw == null ) {
      // return the default to-string behavior of java.util.Date
      formatSpecification.redefine( FormatSpecification.TYPE_DATE_FORMAT, DEFAULT_FORMAT );
    } else {
      formatSpecification.redefine( FormatSpecification.TYPE_DATE_FORMAT, String.valueOf( formatStringRaw ) );
    }
    return formatSpecification;
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element from which to read attribute.
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object retval = ElementTypeUtils.queryFieldOrValue( runtime, element );
    if ( retval instanceof Date == false ) {
      return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );
    }

    Object formatStringRaw = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING );
    if ( formatStringRaw == null ) {
      // return the default to-string behavior of java.util.Date
      formatStringRaw = "EEE MMM dd HH:mm:ss zzz yyyy";
    }

    return getFormattedObject(runtime, element, retval, formatStringRaw);
  }

  public Object getFormattedObject( ExpressionRuntime runtime, ReportElement element, Object retval, Object formatStringRaw ) {
    try {
      final Locale locale = runtime.getResourceBundleFactory().getLocale();
      final TimeZone timeZone = runtime.getResourceBundleFactory().getTimeZone();
      final DateFieldTypeContext context = element.getElementContext( DateFieldTypeContext.class );
      if ( context.dateFormat == null ) {
        context.formatString = String.valueOf( formatStringRaw );
        context.locale = locale;
        context.timeZone = timeZone;
        context.dateFormat = new FastDateFormat( context.formatString, locale, timeZone );
      } else {
        if ( ObjectUtilities.equal( context.formatString, formatStringRaw ) == false
            || ObjectUtilities.equal( context.locale, locale ) == false
            || ObjectUtilities.equal( context.timeZone, timeZone ) == false ) {
          context.timeZone = timeZone;
          context.locale = locale;
          context.formatString = String.valueOf( formatStringRaw );
          context.dateFormat = new FastDateFormat( context.formatString, locale, timeZone );
        }
      }

      return rotate( element, context.dateFormat.format( retval ), runtime );
    } catch ( final Exception e ) {
      return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );
    }
  }
}

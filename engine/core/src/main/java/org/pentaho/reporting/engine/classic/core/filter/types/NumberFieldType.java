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
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;

import java.util.Locale;

public class NumberFieldType extends AbstractElementType implements RawDataSource, RotatableText {
  public static final NumberFieldType INSTANCE = new NumberFieldType();

  private static final String DECIMALFORMAT_DEFAULT_PATTERN =
      "#,###.###################################################"
          + "#########################################################"
          + "#########################################################"
          + "#########################################################";

  public static class NumberFieldTypeContext {
    public ElementMetaData elementType;
    public FastDecimalFormat decimalFormat;
    public Locale locale;
    public String formatString;
  }

  public NumberFieldType() {
    super( "number-field" );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object staticValue = ElementTypeUtils.queryStaticValue( element );
    if ( staticValue instanceof Number ) {
      Object formatStringRaw = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING );
      if ( formatStringRaw == null || "".equals( formatStringRaw ) ) {
        // return the default behavior of BigDecimal.toString() but localized.
        formatStringRaw = NumberFieldType.DECIMALFORMAT_DEFAULT_PATTERN;
      }

      try {
        final Locale locale = runtime.getResourceBundleFactory().getLocale();
        final FastDecimalFormat decimalFormat = new FastDecimalFormat( String.valueOf( formatStringRaw ), locale );

        return rotate( element, decimalFormat.format( staticValue ), runtime );
      } catch ( Exception e ) {
        // ignore .. fallback to show the fieldname
      }
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
    if ( retval instanceof Number == false ) {
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
   *          the format specification (can be null). @return a filled format specififcation. If the
   *          <code>formatSpecification</code> parameter was not null, this given instance is reused.
   */
  public FormatSpecification getFormatString( final ExpressionRuntime runtime, final ReportElement element,
      FormatSpecification formatSpecification ) {
    if ( formatSpecification == null ) {
      formatSpecification = new FormatSpecification();
    }

    final Object formatStringRaw =
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING );
    if ( formatStringRaw == null ) {
      formatSpecification.redefine( FormatSpecification.TYPE_UNDEFINED, null );

      // The following code causes strange Excel behaviour
      // return the default to-string behavior of java.util.Date
      // formatStringRaw = NumberFieldType.DECIMALFORMAT_DEFAULT_PATTERN;
    } else {
      formatSpecification.redefine( FormatSpecification.TYPE_DECIMAL_FORMAT, String.valueOf( formatStringRaw ) );
    }
    return formatSpecification;
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {

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
    if ( retval instanceof Number == false ) {
      return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );
    }

    Object formatStringRaw = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING );
    if ( formatStringRaw == null || "".equals( formatStringRaw ) ) {
      // return the default behavior of BigDecimal.toString() but localized.
      formatStringRaw = NumberFieldType.DECIMALFORMAT_DEFAULT_PATTERN;
    }

    return getFormattedObject(runtime, element, retval, formatStringRaw);
  }

  public Object getFormattedObject( ExpressionRuntime runtime, ReportElement element, Object retval, Object formatStringRaw ) {
    try {
      final Locale locale = runtime.getResourceBundleFactory().getLocale();
      final NumberFieldTypeContext context = element.getElementContext( NumberFieldTypeContext.class );
      if ( context.decimalFormat == null ) {
        context.formatString = String.valueOf( formatStringRaw );
        context.locale = locale;
        context.decimalFormat = new FastDecimalFormat( context.formatString, locale );
      } else {
        if ( ObjectUtilities.equal( context.formatString, formatStringRaw ) == false
            || ObjectUtilities.equal( context.locale, locale ) == false ) {
          context.formatString = String.valueOf( formatStringRaw );
          context.locale = locale;
          context.decimalFormat = new FastDecimalFormat( context.formatString, locale );
        }
      }

      return rotate( element, context.decimalFormat.format( retval ), runtime );
    } catch ( Exception e ) {
      return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );
    }
  }

}

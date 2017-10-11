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

package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes;

import net.sourceforge.barbecue.Barcode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.util.Locale;

/**
 * This <code>ElementType</code> is responsible to create the barcode object as defined by its definition named
 * <code>simple-barcodes</code>.
 *
 * @author Cedric Pronzato
 */
public class SimpleBarcodesType extends ContentType {
  public static final SimpleBarcodesType INSTANCE = new SimpleBarcodesType();

  private static final Log logger = LogFactory.getLog( SimpleBarcodesType.class );

  public SimpleBarcodesType() {
    super( "simple-barcodes" );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    Object value = ElementTypeUtils.queryStaticValue( element );
    if ( value == null ) {
      final String type =
          (String) element.getAttribute( SimpleBarcodesAttributeNames.NAMESPACE,
              SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE );

      value = SimpleBarcodesUtility.getBarcodeSampleData( type );
    }

    return createBarcode( runtime, element, value );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element for which the data is computed.
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object value = ElementTypeUtils.queryFieldOrValue( runtime, element );
    if ( value == null ) {
      final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
      return filter( runtime, element, nullValue );
    }
    return createBarcode( runtime, element, value );
  }

  private Object createBarcode( final ExpressionRuntime runtime, final ReportElement element, final Object value ) {
    // retrieve custom barcode attributes
    final String type =
        (String) element.getAttribute( SimpleBarcodesAttributeNames.NAMESPACE,
            SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE );
    if ( type == null ) {
      final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
      return filter( runtime, element, nullValue );
    }

    // retrieve custom barcode styles
    final Number barHeight =
        ElementTypeUtils.getNumberAttribute( element, SimpleBarcodesAttributeNames.NAMESPACE,
            SimpleBarcodesAttributeNames.BAR_HEIGHT_ATTRIBUTE, null );
    final Number barWidth =
        ElementTypeUtils.getNumberAttribute( element, SimpleBarcodesAttributeNames.NAMESPACE,
            SimpleBarcodesAttributeNames.BAR_WIDTH_ATTRIBUTE, null );
    final boolean showText =
        ElementTypeUtils.getBooleanAttribute( element, SimpleBarcodesAttributeNames.NAMESPACE,
            SimpleBarcodesAttributeNames.SHOW_TEXT_ATTRIBUTE, true );
    final boolean checksum =
        ElementTypeUtils.getBooleanAttribute( element, SimpleBarcodesAttributeNames.NAMESPACE,
            SimpleBarcodesAttributeNames.CHECKSUM_ATTRIBUTE, true );

    try {
      final BarcodeGenerator generator = SimpleBarcodesUtility.createBarcode4J( type, showText, checksum, barHeight );
      if ( generator != null ) {
        return new BarcodeDrawable( generator, value.toString() );
      }

      // create barcde and set its properties
      final Barcode barcode = SimpleBarcodesUtility.createBarcode( value.toString(), type, checksum );
      if ( barcode == null ) {
        final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
        return filter( runtime, element, nullValue );
      }
      barcode.setDrawingText( showText );
      if ( barWidth != null ) {
        barcode.setBarWidth( barWidth.intValue() );
      } else {
        barcode.setBarWidth( 1 );
      }
      if ( barHeight != null ) {
        barcode.setBarHeight( barHeight.intValue() );
      } else {
        barcode.setBarHeight( 18 );
      }
      barcode.setDrawingText( showText );
      return new BarcodeWrapper( barcode );
    } catch ( Exception e ) {
      if ( logger.isInfoEnabled() ) {
        logger.info( "Error creating barcode, falling back to null value", e );
      }
      final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
      return filter( runtime, element, nullValue );
    }
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {
    element.setAttribute( SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE,
        SimpleBarcodesUtility.BARCODE_CODE128 );
  }
}

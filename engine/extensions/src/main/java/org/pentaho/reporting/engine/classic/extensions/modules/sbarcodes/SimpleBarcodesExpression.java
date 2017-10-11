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
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

/**
 * This single expression handling all types of barcode can be used in Simple XML report definition using a
 * drawable-field element.<br/>
 * The barcode type can be: 3of9, 3of9ext, code39, code39ext, usd3, usd3ext, usd-3, usd-3ext, codabar, code27, usd4,
 * 2of7, monarch, nw7, usd-4, nw-7, ean13, ean-13, upca, upc-a, isbn, bookland, code128, code128a, code128b, code128c,
 * uccean128, 2of5, std2of5, int2of5, postnet or pdf417.
 * <p/>
 * The data as well as type can be retrieved from the datasource fields using <code>rawDataField</code> and
 * <code>rawTypeField</code>. The static type supersedes the type retrieved from the datasource field.
 *
 * @author Cedric Pronzato
 */
public class SimpleBarcodesExpression extends AbstractExpression {
  private static final Log logger = LogFactory.getLog( SimpleBarcodesExpression.class );

  private String type;
  private int barHeight;
  private int barWidth;
  private boolean checksum;
  private boolean showText;

  private String rawDataField;
  private String rawTypeField;

  public SimpleBarcodesExpression() {
    showText = true;
    checksum = false;
    barWidth = 1;
    barHeight = 10;
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final String data = getData();
    if ( data == null ) {
      logger.info( "No data supplied to barcode element." );
      return null;
    }
    String ltype;
    if ( type == null ) {
      // if no static type, retrieve it from the type field
      ltype = computeRawType();
    } else {
      ltype = type;
    }
    if ( ltype == null ) {
      logger.info( "No type supplied to barcode element." );
      return null;
    }

    try {
      final BarcodeGenerator generator =
          SimpleBarcodesUtility.createBarcode4J( type, showText, checksum, Integer.valueOf( barHeight ) );
      if ( generator != null ) {
        return new BarcodeDrawable( generator, data );
      }
    } catch ( Exception e ) {
      if ( logger.isInfoEnabled() ) {
        logger.info( "Error creating barcode, falling back to null value", e );
      }
      return null;
    }

    final Barcode barcode = SimpleBarcodesUtility.createBarcode( data, ltype, checksum );
    if ( barcode == null ) {
      return null;
    }
    barcode.setDrawingText( showText );
    barcode.setBarWidth( barWidth );
    barcode.setBarHeight( barHeight );
    return new BarcodeWrapper( barcode );
  }

  private String getData() {
    if ( rawDataField == null ) {
      return null;
    }
    final Object o = getDataRow().get( rawDataField );
    if ( o == null ) {
      return null;
    }

    if ( o instanceof String ) {
      return (String) o;
    } else {
      logger.info( "Barcode input is not a String? Using toString() method." );
      return o.toString();
    }
  }

  private String computeRawType() {
    if ( rawTypeField == null ) {
      return null;
    }
    final Object o = getDataRow().get( rawTypeField );
    if ( o instanceof String ) {
      return (String) o;
    } else {
      return null;
    }
  }

  public String getType() {
    return type;
  }

  public void setType( final String type ) {
    this.type = type;
  }

  public int getBarHeight() {
    return barHeight;
  }

  public void setBarHeight( final int barHeight ) {
    this.barHeight = barHeight;
  }

  public int getBarWidth() {
    return barWidth;
  }

  public void setBarWidth( final int barWidth ) {
    this.barWidth = barWidth;
  }

  public boolean isChecksum() {
    return checksum;
  }

  public void setChecksum( final boolean checksum ) {
    this.checksum = checksum;
  }

  public boolean isShowText() {
    return showText;
  }

  public void setShowText( final boolean showText ) {
    this.showText = showText;
  }

  public String getRawDataField() {
    return rawDataField;
  }

  public void setRawDataField( final String rawDataField ) {
    this.rawDataField = rawDataField;
  }

  public String getRawTypeField() {
    return rawTypeField;
  }

  public void setRawTypeField( String rawTypeField ) {
    this.rawTypeField = rawTypeField;
  }
}

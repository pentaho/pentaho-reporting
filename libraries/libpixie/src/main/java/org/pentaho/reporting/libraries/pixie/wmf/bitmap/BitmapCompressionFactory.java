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

package org.pentaho.reporting.libraries.pixie.wmf.bitmap;

public class BitmapCompressionFactory {
  private BitmapCompressionFactory() {
  }

  public static BitmapCompression getHandler( final int comp ) {
    switch( comp ) {
      case BitmapHeader.BI_RGB:
        return new RGBCompression();
      case BitmapHeader.BI_RLE4:
        return new RLE4Compression();
      case BitmapHeader.BI_RLE8:
        return new RLE8Compression();
      case BitmapHeader.BI_BITFIELDS:
        return new BitFieldsCompression();
      default:
        throw new IllegalArgumentException( "Unknown compression: " + comp );
    }
  }
}

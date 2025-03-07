/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.pixie.wmf.bitmap;

import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;

public final class BitmapHeader {
  public static final int BI_RGB = 0;
  public static final int BI_RLE8 = 1;
  public static final int BI_RLE4 = 2;
  public static final int BI_BITFIELDS = 3;

  private static final int BMPH_DATA_OFFSET = MfRecord.RECORD_HEADER_SIZE;
  private static final int BMPH_HEADERSIZE = MfRecord.RECORD_HEADER_SIZE + 4;
  private static final int BMPH_WIDTH = MfRecord.RECORD_HEADER_SIZE + 8;
  private static final int BMPH_HEIGHT = MfRecord.RECORD_HEADER_SIZE + 12;
  private static final int BMPH_NO_PLANES = MfRecord.RECORD_HEADER_SIZE + 14;
  private static final int BMPH_BPP = MfRecord.RECORD_HEADER_SIZE + 16;
  private static final int BMPH_COMPRESSION = MfRecord.RECORD_HEADER_SIZE + 20;
  private static final int BMPH_DATASIZE = MfRecord.RECORD_HEADER_SIZE + 24;
  private static final int BMPH_HRES = MfRecord.RECORD_HEADER_SIZE + 28;
  private static final int BMPH_VRES = MfRecord.RECORD_HEADER_SIZE + 32;
  private static final int BMPH_NO_COLORS = MfRecord.RECORD_HEADER_SIZE + 36;
  private static final int BMPH_NO_IMPORTANT_COLORS = MfRecord.RECORD_HEADER_SIZE + 40;

  private int dataOffset;
  private int headerSize;
  private int width;
  private int height;
  private int noPlanes;
  private int bitPerPixel;
  private int compression;
  private int dataSize;
  private int hres; // ignored
  private int vres; // ignored
  private int noColors; // in palette
  private int noImportantColors; // is <= noColors
  private boolean isTopDown;

  public BitmapHeader() {
  }

  public void setRecord( final MfRecord record, final int offset ) {
    dataOffset = record.getInt( offset + BMPH_DATA_OFFSET );
    headerSize = record.getInt( offset + BMPH_HEADERSIZE );
    width = record.getInt( offset + BMPH_WIDTH );
    height = record.getInt( offset + BMPH_HEIGHT );
    noPlanes = record.getShort( offset + BMPH_NO_PLANES );
    bitPerPixel = record.getShort( offset + BMPH_BPP );
    compression = record.getInt( offset + BMPH_COMPRESSION );
    dataSize = record.getInt( offset + BMPH_DATASIZE );
    hres = record.getInt( offset + BMPH_HRES );
    vres = record.getInt( offset + BMPH_VRES );
    noColors = record.getInt( offset + BMPH_NO_COLORS );
    noImportantColors = record.getInt( offset + BMPH_NO_IMPORTANT_COLORS );

    if ( height < 0 ) {
      isTopDown = true;
      height = -height;
    }

    fixPalette();
  }

  private void fixPalette() {
    if ( bitPerPixel < 16 ) {
      if ( noColors == 0 ) {
        noColors = (int) StrictMath.pow( 2, bitPerPixel );
      }

      if ( noImportantColors == 0 ) {
        noImportantColors = (int) StrictMath.pow( 2, bitPerPixel );
      }
    }
  }

  public int getHeaderSize() {
    return headerSize;
  }

  public int getCompression() {
    return compression;
  }

  public int getBitsPerPixel() {
    return bitPerPixel;
  }

  public int getHRes() {
    return hres;
  }

  public int getVRes() {
    return vres;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getNoOfColors() {
    return noColors;
  }

  public int getNoOfImportantColors() {
    return noImportantColors;
  }

  public boolean isTopDown() {
    return isTopDown;
  }

  public int getDataOffset() {
    return dataOffset;
  }

  public int getDataSize() {
    return dataSize;
  }

  public int getNoPlanes() {
    return noPlanes;
  }
}

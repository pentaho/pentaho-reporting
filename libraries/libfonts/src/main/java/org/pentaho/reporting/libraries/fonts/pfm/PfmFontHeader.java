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


package org.pentaho.reporting.libraries.fonts.pfm;

import org.pentaho.reporting.libraries.fonts.LEByteAccessUtilities;

/**
 * Creation-Date: 21.07.2007, 15:43:15
 *
 * @author Thomas Morgner
 */
public class PfmFontHeader {
  public static final int LENGTH = 117;

  // header information
  private short version;
  private long size;
  private short type;
  private short point;
  private short vertRes;
  private short horizRes;
  private short ascent;
  private short internalLeading;
  private short externalLeading;
  private boolean italic;
  private boolean underline;
  private boolean strikeout;
  private short weight;
  private byte charset;
  private short pixelWidth;
  private short pixelHeight;
  private byte pitchAndFamily;
  private short avgWidth;
  private short maxWidth;
  private short firstChar; // between 0 and 255
  private short lastChar; // between 0 and 255
  private short defaultChar; // between 0 and 255
  private short breakChar; // between 0 and 255
  private short widthBytes;
  private int devicePtr;
  private int facePtr;

  public PfmFontHeader( final byte[] data ) {
    version = LEByteAccessUtilities.readShort( data, 0 );
    size = LEByteAccessUtilities.readULong( data, 2 );
    // skip the copyright, we dont care about that ...
    type = LEByteAccessUtilities.readShort( data, 66 );
    point = LEByteAccessUtilities.readShort( data, 68 );
    vertRes = LEByteAccessUtilities.readShort( data, 70 );
    horizRes = LEByteAccessUtilities.readShort( data, 72 );
    ascent = LEByteAccessUtilities.readShort( data, 74 );

    internalLeading = LEByteAccessUtilities.readShort( data, 76 );
    externalLeading = LEByteAccessUtilities.readShort( data, 78 );
    italic = data[ 80 ] != 0;
    underline = data[ 81 ] != 0;
    strikeout = data[ 82 ] != 0;
    weight = LEByteAccessUtilities.readShort( data, 83 );
    charset = data[ 85 ];
    pixelWidth = LEByteAccessUtilities.readShort( data, 86 );
    pixelHeight = LEByteAccessUtilities.readShort( data, 88 );
    pitchAndFamily = data[ 90 ];
    avgWidth = LEByteAccessUtilities.readShort( data, 91 );
    maxWidth = LEByteAccessUtilities.readShort( data, 93 );
    firstChar = (short) ( 0xff & data[ 95 ] );
    lastChar = (short) ( 0xff & data[ 96 ] );
    defaultChar = (short) ( 0xff & data[ 97 ] );
    breakChar = (short) ( 0xff & data[ 98 ] );
    widthBytes = LEByteAccessUtilities.readShort( data, 99 );
    devicePtr = LEByteAccessUtilities.readLong( data, 101 );
    facePtr = LEByteAccessUtilities.readLong( data, 105 );
  }

  public int getDevicePtr() {
    return devicePtr;
  }

  public int getFacePtr() {
    return facePtr;
  }

  public short getVersion() {
    return version;
  }

  public long getSize() {
    return size;
  }

  public short getType() {
    return type;
  }

  public short getPoint() {
    return point;
  }

  public short getVertRes() {
    return vertRes;
  }

  public short getHorizRes() {
    return horizRes;
  }

  public short getAscent() {
    return ascent;
  }

  public short getInternalLeading() {
    return internalLeading;
  }

  public short getExternalLeading() {
    return externalLeading;
  }

  public boolean isItalic() {
    return italic;
  }

  public boolean isUnderline() {
    return underline;
  }

  public boolean isStrikeout() {
    return strikeout;
  }

  public short getWeight() {
    return weight;
  }

  public byte getCharset() {
    return charset;
  }

  public short getPixelWidth() {
    return pixelWidth;
  }

  public short getPixelHeight() {
    return pixelHeight;
  }

  public byte getPitchAndFamily() {
    return pitchAndFamily;
  }

  public short getAvgWidth() {
    return avgWidth;
  }

  public short getMaxWidth() {
    return maxWidth;
  }

  public short getFirstChar() {
    return firstChar;
  }

  public short getLastChar() {
    return lastChar;
  }

  public short getDefaultChar() {
    return defaultChar;
  }

  public short getBreakChar() {
    return breakChar;
  }

  public short getWidthBytes() {
    return widthBytes;
  }

  public String getEncoding() {
    switch( (int) charset ) {
      case 128:
        return "SJIS";
      case 129:
        return "EUC_KR";
      case 134:
        return "GBK";
      case 136:
        return "Big5";
      default:
        // Assume the western-european codepage if no other page has been specified.
        return "Cp1252";
    }
  }
}

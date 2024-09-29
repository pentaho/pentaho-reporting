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


package org.pentaho.reporting.libraries.fonts.pfm;

import org.pentaho.reporting.libraries.fonts.LEByteAccessUtilities;

/**
 * Creation-Date: 21.07.2007, 16:34:25
 *
 * @author Thomas Morgner
 */
public class PfmExtendedTextMetrics {
  private short pointSize;
  private short orientation;
  private short masterHeight;
  private short minScale;
  private short maxScale;
  private short masterUnits;
  private short capHeight;
  private short xHeight;
  private short lowerCaseAscent;
  private short lowerCaseDescent;
  private short slant;
  private short superScript;
  private short subScript;
  private short superScriptSize;
  private short subScriptSize;
  private short underlineOffset;
  private short underlineWidth;
  private short doubleLowerUnderlineOffset;
  private short doubleLowerUnderlineWidth;
  private short doubleUpperUnderlineOffset;
  private short doubleUpperUnderlineWidth;
  private short strikeOutOffset;
  private short strikeOutWidth;
  private short kernPairs;
  private short kernTracks;

  public PfmExtendedTextMetrics( final byte[] data ) {
    pointSize = LEByteAccessUtilities.readShort( data, 2 );
    orientation = LEByteAccessUtilities.readShort( data, 4 );
    masterHeight = LEByteAccessUtilities.readShort( data, 6 );
    minScale = LEByteAccessUtilities.readShort( data, 8 );
    maxScale = LEByteAccessUtilities.readShort( data, 10 );
    masterUnits = LEByteAccessUtilities.readShort( data, 12 );
    capHeight = LEByteAccessUtilities.readShort( data, 14 );
    xHeight = LEByteAccessUtilities.readShort( data, 16 );
    lowerCaseAscent = LEByteAccessUtilities.readShort( data, 18 );
    lowerCaseDescent = LEByteAccessUtilities.readShort( data, 20 );
    slant = LEByteAccessUtilities.readShort( data, 22 );
    superScript = LEByteAccessUtilities.readShort( data, 24 );
    subScript = LEByteAccessUtilities.readShort( data, 26 );
    superScriptSize = LEByteAccessUtilities.readShort( data, 28 );
    subScriptSize = LEByteAccessUtilities.readShort( data, 30 );
    underlineOffset = LEByteAccessUtilities.readShort( data, 32 );
    underlineWidth = LEByteAccessUtilities.readShort( data, 34 );
    doubleLowerUnderlineOffset = LEByteAccessUtilities.readShort( data, 36 );
    doubleLowerUnderlineWidth = LEByteAccessUtilities.readShort( data, 38 );
    doubleUpperUnderlineOffset = LEByteAccessUtilities.readShort( data, 40 );
    doubleUpperUnderlineWidth = LEByteAccessUtilities.readShort( data, 42 );
    strikeOutOffset = LEByteAccessUtilities.readShort( data, 44 );
    strikeOutWidth = LEByteAccessUtilities.readShort( data, 46 );
    kernPairs = LEByteAccessUtilities.readShort( data, 48 );
    kernTracks = LEByteAccessUtilities.readShort( data, 50 );
  }

  public short getPointSize() {
    return pointSize;
  }

  public short getOrientation() {
    return orientation;
  }

  public short getMasterHeight() {
    return masterHeight;
  }

  public short getMinScale() {
    return minScale;
  }

  public short getMaxScale() {
    return maxScale;
  }

  public short getMasterUnits() {
    return masterUnits;
  }

  public short getCapHeight() {
    return capHeight;
  }

  public short getxHeight() {
    return xHeight;
  }

  public short getLowerCaseAscent() {
    return lowerCaseAscent;
  }

  public short getLowerCaseDescent() {
    return lowerCaseDescent;
  }

  public short getSlant() {
    return slant;
  }

  public short getSuperScript() {
    return superScript;
  }

  public short getSubScript() {
    return subScript;
  }

  public short getSuperScriptSize() {
    return superScriptSize;
  }

  public short getSubScriptSize() {
    return subScriptSize;
  }

  public short getUnderlineOffset() {
    return underlineOffset;
  }

  public short getUnderlineWidth() {
    return underlineWidth;
  }

  public short getDoubleLowerUnderlineOffset() {
    return doubleLowerUnderlineOffset;
  }

  public short getDoubleLowerUnderlineWidth() {
    return doubleLowerUnderlineWidth;
  }

  public short getDoubleUpperUnderlineOffset() {
    return doubleUpperUnderlineOffset;
  }

  public short getDoubleUpperUnderlineWidth() {
    return doubleUpperUnderlineWidth;
  }

  public short getStrikeOutOffset() {
    return strikeOutOffset;
  }

  public short getStrikeOutWidth() {
    return strikeOutWidth;
  }

  public short getKernPairs() {
    return kernPairs;
  }

  public short getKernTracks() {
    return kernTracks;
  }
}

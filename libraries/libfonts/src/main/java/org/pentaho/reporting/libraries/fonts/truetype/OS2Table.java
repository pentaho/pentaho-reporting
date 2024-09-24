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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.truetype;

import org.pentaho.reporting.libraries.fonts.ByteAccessUtilities;

/**
 * Creation-Date: 06.11.2005, 20:24:42
 *
 * @author Thomas Morgner
 */
public class OS2Table implements FontTable {
  public static final long TABLE_ID =
    ( 'O' << 24 | 'S' << 16 | '/' << 8 | '2' );

  private int version;
  /**
   * This value is deprecated in version 3
   */
  private short xAvgCharWidth;
  private int weightClass;
  private int widthClass;
  private int fsType;
  private short ySubscriptXSize;
  private short ySubscriptYSize;
  private short ySubscriptXOffset;
  private short ySubscriptYOffset;
  private short ySuperscriptXSize;
  private short ySuperscriptYSize;
  private short ySuperscriptXOffset;
  private short ySuperscriptYOffset;
  private short yStrikeoutSize;
  private short yStrikeoutPosition;
  private short familyClass;
  private byte[] panose;
  // This field is not set for Version 0 records.
  private byte[] unicodeRange;
  private byte[] vendorId;
  private int fsSelection;
  private int firstCharIndex;
  private int lastCharIndex;
  private int typoAscender;
  private int typoDescender;
  private int typoLineGap;
  private int winAscent;
  private int winDescent;
  private byte[] codepageRange;
  private short xHeight;
  private short capHeight;
  private int defaultChar;
  private int breakChar;
  private int maxContext;
  private static final int TYPE_RESTRICTED_LICENSE = 0x002;

  public OS2Table( final byte[] data, final int unitsPerEm ) {
    // create a sensible default ...

    // the height of the uppercase characters
    capHeight = (short) ( 0.7 * unitsPerEm );
    // the height of the lowercase-x
    xHeight = (short) ( 0.5 * unitsPerEm );
    // used for justification and to separate words
    breakChar = ' ';

    version = ByteAccessUtilities.readUShort( data, 0 );
    if ( version == 3 || version == 2 ) {
      // in version 3 the *meaning* of some fields has been clarified. We use
      // the updated semantics.
      loadVersion2Table( data );
    } else if ( version == 1 ) {
      loadVersion1Table( data );
    } else if ( version == 0 ) {
      loadVersion0Table( data );
    }
  }

  private void loadVersion0Table( final byte[] data ) {
    xAvgCharWidth = ByteAccessUtilities.readShort( data, 2 );
    weightClass = ByteAccessUtilities.readUShort( data, 4 );
    widthClass = ByteAccessUtilities.readUShort( data, 6 );
    fsType = ByteAccessUtilities.readUShort( data, 8 );
    ySubscriptXSize = ByteAccessUtilities.readShort( data, 10 );
    ySubscriptYSize = ByteAccessUtilities.readShort( data, 12 );
    ySubscriptXOffset = ByteAccessUtilities.readShort( data, 14 );
    ySubscriptYOffset = ByteAccessUtilities.readShort( data, 16 );
    ySuperscriptXSize = ByteAccessUtilities.readShort( data, 18 );
    ySuperscriptYSize = ByteAccessUtilities.readShort( data, 20 );
    ySuperscriptXOffset = ByteAccessUtilities.readShort( data, 22 );
    ySuperscriptYOffset = ByteAccessUtilities.readShort( data, 24 );
    yStrikeoutSize = ByteAccessUtilities.readShort( data, 26 );
    yStrikeoutPosition = ByteAccessUtilities.readShort( data, 28 );
    familyClass = ByteAccessUtilities.readShort( data, 30 );
    panose = ByteAccessUtilities.readBytes( data, 32, 10 );
    unicodeRange = ByteAccessUtilities.readBytes( data, 42, 16 );
    vendorId = ByteAccessUtilities.readBytes( data, 58, 4 );
    fsSelection = ByteAccessUtilities.readUShort( data, 62 );
    firstCharIndex = ByteAccessUtilities.readUShort( data, 64 );
    lastCharIndex = ByteAccessUtilities.readUShort( data, 66 );
    typoAscender = ByteAccessUtilities.readShort( data, 68 );
    typoDescender = ByteAccessUtilities.readShort( data, 70 );
    typoLineGap = ByteAccessUtilities.readShort( data, 72 );
    winAscent = ByteAccessUtilities.readUShort( data, 74 );
    winDescent = ByteAccessUtilities.readUShort( data, 76 );
  }

  private void loadVersion1Table( final byte[] data ) {
    loadVersion0Table( data );
    codepageRange = ByteAccessUtilities.readBytes( data, 78, 8 );
  }

  private void loadVersion2Table( final byte[] data ) {
    loadVersion1Table( data );

    xHeight = ByteAccessUtilities.readShort( data, 86 );
    capHeight = ByteAccessUtilities.readShort( data, 88 );

    defaultChar = ByteAccessUtilities.readUShort( data, 90 );
    breakChar = ByteAccessUtilities.readUShort( data, 92 );
    maxContext = ByteAccessUtilities.readUShort( data, 94 );
  }

  public int getVersion() {
    return version;
  }

  public short getxAvgCharWidth() {
    return xAvgCharWidth;
  }

  public int getWeightClass() {
    return weightClass;
  }

  public int getWidthClass() {
    return widthClass;
  }

  public int getFsType() {
    return fsType;
  }

  public short getySubscriptXSize() {
    return ySubscriptXSize;
  }

  public short getySubscriptYSize() {
    return ySubscriptYSize;
  }

  public short getySubscriptXOffset() {
    return ySubscriptXOffset;
  }

  public short getySubscriptYOffset() {
    return ySubscriptYOffset;
  }

  public short getySuperscriptXSize() {
    return ySuperscriptXSize;
  }

  public short getySuperscriptYSize() {
    return ySuperscriptYSize;
  }

  public short getySuperscriptXOffset() {
    return ySuperscriptXOffset;
  }

  public short getySuperscriptYOffset() {
    return ySuperscriptYOffset;
  }

  public short getyStrikeoutSize() {
    return yStrikeoutSize;
  }

  public short getyStrikeoutPosition() {
    return yStrikeoutPosition;
  }

  public short getsFamilyClass() {
    return familyClass;
  }

  public byte[] getPanose() {
    return panose.clone();
  }

  public boolean isUnicodeRangeSupported( final int pos ) {
    // todo
    return false;
  }

  public byte[] getVendorId() {
    return vendorId.clone();
  }

  public int getFsSelection() {
    return fsSelection;
  }

  public boolean isBold() {
    return ( fsSelection & 0x20 ) == 0x20;
  }

  public boolean isItalic() {
    return ( fsSelection & 0x1 ) == 0x01;
  }

  public int getFirstCharIndex() {
    return firstCharIndex;
  }

  public int getLastCharIndex() {
    return lastCharIndex;
  }

  public int getTypoAscender() {
    return typoAscender;
  }

  public int getTypoDescender() {
    return typoDescender;
  }

  public int getTypoLineGap() {
    return typoLineGap;
  }

  public int getWinAscent() {
    return winAscent;
  }

  public int getWinDescent() {
    return winDescent;
  }

  public boolean isCodepageSupported( final int codepage ) {
    return false; // todo;
  }

  public short getxHeight() {
    return xHeight;
  }

  public short getCapHeight() {
    return capHeight;
  }

  public int getDefaultChar() {
    return defaultChar;
  }

  public int getBreakChar() {
    return breakChar;
  }

  public int getMaxContext() {
    return maxContext;
  }

  public boolean isRestricted() {
    return fsType == TYPE_RESTRICTED_LICENSE;
  }

  public long getName() {
    return TABLE_ID;
  }
}

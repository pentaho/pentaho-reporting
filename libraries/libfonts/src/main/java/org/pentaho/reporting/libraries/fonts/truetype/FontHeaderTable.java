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

import java.io.IOException;

/**
 * Creation-Date: 06.11.2005, 20:24:42
 *
 * @author Thomas Morgner
 */
public class FontHeaderTable implements FontTable {
  public static final long TABLE_ID =
    ( 'h' << 24 | 'e' << 16 | 'a' << 8 | 'd' );

  private static final long MAGIC = 0x5F0F3CF5;

  public static final int STYLE_BOLD = 0x01;
  public static final int STYLE_ITALIC = 0x02;
  public static final int STYLE_UNDERLINE = 0x04;
  public static final int STYLE_OUTLINE = 0x08;
  public static final int STYLE_SHADOW = 0x10;
  public static final int STYLE_CONDENSED = 0x20;
  public static final int STYLE_EXTENDED = 0x40;

  public static final int FEATURE_BASELINE = 0x0001;
  public static final int FEATURE_LSB = 0x0002;
  public static final int FEATURE_EXPLICIT_POINTSIZE = 0x0004;
  public static final int FEATURE_INTEGER_SCALING = 0x0008;
  public static final int FEATURE_NONLINEAR_WIDTH = 0x0010;
  public static final int FEATURE_VERTICAL = 0x0020;
  public static final int FEATURE_LAYOUT_REQUIRED = 0x0080;
  public static final int FEATURE_METAMORPH = 0x0100;
  public static final int FEATURE_RTL_GLYPHS = 0x0200;
  public static final int FEATURE_INDIC_REARRANGE = 0x0400;
  public static final int FEATURE_COMPRESSED = 0x0800;
  public static final int FEATURE_CLEARTYPE = 0x1000;

  private long version;
  private long revision;
  private long checkSumAdjustment;
  private int flags;
  private int unitsPerEm;
  private long createdDate;
  private long modifiedDate;
  private int xMin;
  private int yMin;
  private int xMax;
  private int yMax;

  private int macStyle;
  private int lowestRecPPEM;
  private short fontDirectionHint;
  private short indexToLocFormat;
  private short glyphDataFormat;

  public FontHeaderTable( final byte[] data ) throws IOException {
    version = ByteAccessUtilities.readULong( data, 0 );
    revision = ByteAccessUtilities.readULong( data, 4 );
    checkSumAdjustment = ByteAccessUtilities.readULong( data, 8 );
    if ( ByteAccessUtilities.readULong( data, 12 ) != MAGIC ) {
      throw new IOException( "MagicNumber missing" );
    }
    flags = ByteAccessUtilities.readUShort( data, 16 );
    unitsPerEm = ByteAccessUtilities.readUShort( data, 18 );
    createdDate = ByteAccessUtilities.readLongDateTime( data, 20 );
    modifiedDate = ByteAccessUtilities.readLongDateTime( data, 28 );
    xMin = ByteAccessUtilities.readShort( data, 36 );
    yMin = ByteAccessUtilities.readShort( data, 38 );
    xMax = ByteAccessUtilities.readShort( data, 40 );
    yMax = ByteAccessUtilities.readShort( data, 42 );
    macStyle = ByteAccessUtilities.readUShort( data, 44 );
    lowestRecPPEM = ByteAccessUtilities.readUShort( data, 46 );
    fontDirectionHint = ByteAccessUtilities.readShort( data, 48 );
    indexToLocFormat = ByteAccessUtilities.readShort( data, 50 );
    glyphDataFormat = ByteAccessUtilities.readShort( data, 52 );
  }

  public long getVersion() {
    return version;
  }

  public long getRevision() {
    return revision;
  }

  public long getCheckSumAdjustment() {
    return checkSumAdjustment;
  }

  public int getFlags() {
    return flags;
  }

  public int getUnitsPerEm() {
    return unitsPerEm;
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public long getModifiedDate() {
    return modifiedDate;
  }

  public int getxMin() {
    return xMin;
  }

  public int getyMin() {
    return yMin;
  }

  public int getxMax() {
    return xMax;
  }

  public int getyMax() {
    return yMax;
  }

  public int getMacStyle() {
    return macStyle;
  }

  public int getLowestRecPPEM() {
    return lowestRecPPEM;
  }

  public short getFontDirectionHint() {
    return fontDirectionHint;
  }

  public short getIndexToLocFormat() {
    return indexToLocFormat;
  }

  public short getGlyphDataFormat() {
    return glyphDataFormat;
  }

  public boolean isBold() {
    return ( macStyle & STYLE_BOLD ) == STYLE_BOLD;
  }

  public boolean isItalic() {
    return ( macStyle & STYLE_ITALIC ) == STYLE_ITALIC;
  }

  public long getName() {
    return TABLE_ID;
  }
}

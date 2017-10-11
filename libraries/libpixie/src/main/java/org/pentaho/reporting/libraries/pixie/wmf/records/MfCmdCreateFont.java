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

package org.pentaho.reporting.libraries.pixie.wmf.records;

import org.pentaho.reporting.libraries.pixie.wmf.MfLogFont;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * The CreateFontIndirect function creates a logical font that has the specified characteristics. The font can
 * subsequently be selected as the current font for any device context.
 * <p/>
 * <code> typedef struct tagLOGFONT { LONG lfHeight; LONG lfWidth; LONG lfEscapement; LONG lfOrientation; LONG lfWeight;
 * BYTE lfItalic; BYTE lfUnderline; BYTE lfStrikeOut; BYTE lfCharSet; BYTE lfOutPrecision; BYTE lfClipPrecision; BYTE
 * lfQuality; BYTE lfPitchAndFamily; TCHAR lfFaceName[LF_FACESIZE]; } LOGFONT, *PLOGFONT; </code>
 */
public final class MfCmdCreateFont extends MfCmd {
  public static final int CHARSET_ANSI = 0;
  public static final int CHARSET_DEFAULT = 1;
  public static final int CHARSET_SYMBOL = 2;
  public static final int CHARSET_SHIFTJIS = 128;
  public static final int CHARSET_OEM = 255;

  private static final int FONT_FACE_MAX = 31;
  private static final int FIXED_RECORD_SIZE = 9;
  private static final int POS_HEIGHT = 0;
  private static final int POS_WIDTH = 1;
  private static final int POS_ESCAPEMENT = 2;
  private static final int POS_ORIENTATION = 3;
  private static final int POS_WEIGHT = 4;
  private static final int POS_FLAGS1 = 5;
  private static final int POS_FLAGS2 = 6;
  private static final int POS_PRECISION = 7;
  private static final int POS_QUALITY = 8;
  private static final int POS_FONTFACE = 9;

  private int height;
  private int width;
  private int scaled_height;
  private int scaled_width;

  private int escapement;
  private int orientation;
  private int weight;
  private boolean italic;
  private boolean underline;
  private boolean strikeout;
  private int charset;
  private int outprecision;
  private int clipprecision;
  private int quality;
  private int pitchAndFamily;
  private String facename;

  public MfCmdCreateFont() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final MfLogFont lfont = new MfLogFont();
    lfont.setFace( getFontFace() );
    lfont.setSize( getScaledHeight() );
    int style;
    // should be bold ?
    if ( getWeight() > 650 ) {
      style = Font.BOLD;
    } else {
      style = Font.PLAIN;
    }
    if ( isItalic() ) {
      style += Font.ITALIC;
    }
    lfont.setStyle( style );
    lfont.setUnderline( isUnderline() );
    lfont.setStrikeOut( isStrikeout() );
    lfont.setRotation( getEscapement() / 10 );
    file.getCurrentState().setLogFont( lfont );
    file.storeObject( lfont );
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdCreateFont();
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord() {
    String fontFace = getFontFace();
    if ( fontFace.length() > FONT_FACE_MAX ) {
      fontFace = fontFace.substring( 0, FONT_FACE_MAX );
    }
    final MfRecord record = new MfRecord( FIXED_RECORD_SIZE + fontFace.length() );
    record.setParam( POS_HEIGHT, getHeight() );
    record.setParam( POS_WIDTH, getWidth() );
    record.setParam( POS_ESCAPEMENT, getEscapement() );
    record.setParam( POS_ORIENTATION, getOrientation() );
    record.setParam( POS_WEIGHT, getWeight() );

    record.setParam( POS_FLAGS1, formFlags( isUnderline(), isItalic() ) );
    record.setParam( POS_FLAGS2, formFlags( isStrikeout(), false ) + getCharset() );
    record.setParam( POS_PRECISION, getOutputPrecision() << 8 + getClipPrecision() );
    record.setParam( POS_QUALITY, getQuality() << 8 + getPitchAndFamily() );
    record.setStringParam( POS_FONTFACE, fontFace );
    return record;
  }

  public void setRecord( final MfRecord record ) {
    int height = record.getParam( POS_HEIGHT );
    if ( height == 0 ) {
      // a default height is requested, we use a default height of 10
      height = 10;
    }
    if ( height < 0 ) {
      // windows specifiy font mapper matching, ignored.
      height *= -1;
    }

    final int width = record.getParam( POS_WIDTH );
    final int escape = record.getParam( POS_ESCAPEMENT );
    final int orientation = record.getParam( POS_ORIENTATION );
    final int weight = record.getParam( POS_WEIGHT );
    final int italic = record.getParam( POS_FLAGS1 ) & 0x00FF;
    final int underline = record.getParam( POS_FLAGS1 ) & 0xFF00;
    final int strikeout = record.getParam( POS_FLAGS2 ) & 0x00FF;
    final int charset = record.getParam( POS_FLAGS2 ) & 0xFF00;
    final int outprec = record.getParam( POS_PRECISION ) & 0x00FF;
    final int clipprec = record.getParam( POS_PRECISION ) & 0xFF00;
    final int quality = record.getParam( POS_QUALITY ) & 0x00FF;
    final int pitch = record.getParam( POS_QUALITY ) & 0xFF00;
    // A fontname must not exceed the length of 32 including the null-terminator
    final String facename = record.getStringParam( POS_FONTFACE, 32 );

    setCharset( charset );
    setClipPrecision( clipprec );
    setEscapement( escape );
    setFontFace( facename );
    setHeight( height );
    setItalic( italic != 0 );
    setOrientation( orientation );
    setOutputPrecision( outprec );
    setPitchAndFamily( pitch );
    setQuality( quality );
    setStrikeout( strikeout != 0 );
    setUnderline( underline != 0 );
    setWeight( weight );
    setWidth( width );
  }

  private int formFlags( final boolean f1, final boolean f2 ) {
    int retval = 0;
    if ( f1 ) {
      retval += 0x0100;
    }
    if ( f2 ) {
      retval += 1;
    }
    return ( retval );
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.CREATE_FONT_INDIRECT;
  }

  public void setFontFace( final String facename ) {
    this.facename = facename;
  }

  public String getFontFace() {
    return facename;
  }

  public void setPitchAndFamily( final int pitchAndFamily ) {
    this.pitchAndFamily = pitchAndFamily;
  }

  public int getPitchAndFamily() {
    return pitchAndFamily;
  }

  public void setQuality( final int quality ) {
    this.quality = quality;
  }

  public int getQuality() {
    return quality;
  }

  public void setClipPrecision( final int clipprecision ) {
    this.clipprecision = clipprecision;
  }

  public int getClipPrecision() {
    return clipprecision;
  }

  public void setOutputPrecision( final int outprecision ) {
    this.outprecision = outprecision;
  }

  public int getOutputPrecision() {
    return outprecision;
  }

  public void setCharset( final int charset ) {
    this.charset = charset;
  }

  public int getCharset() {
    return charset;
  }

  public void setHeight( final int height ) {
    this.height = height;
    scaleYChanged();
  }

  public int getHeight() {
    return height;
  }

  public int getScaledHeight() {
    return scaled_height;
  }

  public void setWidth( final int width ) {
    this.width = width;
    scaleXChanged();
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_width = getScaledX( width );
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_height = getScaledY( height );
  }

  public int getWidth() {
    return width;
  }

  public int getScaledWidth() {
    return scaled_width;
  }

  // in 1/10 degrees
  public void setEscapement( final int escapement ) {
    this.escapement = escapement;
  }

  public int getEscapement() {
    return escapement;
  }

  // in 1/10 degrees
  public void setOrientation( final int orientation ) {
    this.orientation = orientation;
  }

  public int getOrientation() {
    return orientation;
  }

  // 200 = narrow
  // 400 = normal
  // 700 = bold
  public void setWeight( final int weight ) {
    this.weight = weight;
  }

  public int getWeight() {
    return weight;
  }

  public void setItalic( final boolean italic ) {
    this.italic = italic;
  }

  public boolean isItalic() {
    return this.italic;
  }

  public void setUnderline( final boolean ul ) {
    this.underline = ul;
  }

  public boolean isUnderline() {
    return this.underline;
  }

  public void setStrikeout( final boolean so ) {
    this.strikeout = so;
  }

  public boolean isStrikeout() {
    return this.strikeout;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[CREATE_FONT] face=" );
    b.append( getFontFace() );
    b.append( " height=" );
    b.append( getHeight() );
    b.append( " width=" );
    b.append( getWidth() );
    b.append( " weight=" );
    b.append( getWeight() );
    b.append( " italic=" );
    b.append( isItalic() );
    b.append( " Strikeout=" );
    b.append( isStrikeout() );
    b.append( " Underline=" );
    b.append( isUnderline() );
    b.append( " outprecision=" );
    b.append( getOutputPrecision() );
    b.append( " escapement=" );
    b.append( getEscapement() );
    return b.toString();
  }
}

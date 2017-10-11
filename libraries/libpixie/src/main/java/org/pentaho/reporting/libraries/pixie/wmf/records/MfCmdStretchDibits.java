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

import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;
import org.pentaho.reporting.libraries.pixie.wmf.bitmap.DIBReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The StretchDIBits function copies the color data for a rectangle of pixels in a DIB to the specified destination
 * rectangle. If the destination rectangle is larger than the source rectangle, this function stretches the rows and
 * columns of color data to fit the destination rectangle. If the destination rectangle is smaller than the source
 * rectangle, this function compresses the rows and columns by using the specified raster operation.
 */
public class MfCmdStretchDibits extends MfCmd {
  //  private static final int RECORD_BASE_SIZE = 11;
  private static final int POS_ROP = 0;
  private static final int POS_USAGE = 2;
  private static final int POS_SRC_H = 3;
  private static final int POS_SRC_W = 4;
  private static final int POS_SRC_Y = 5;
  private static final int POS_SRC_X = 6;
  private static final int POS_DST_H = 7;
  private static final int POS_DST_W = 8;
  private static final int POS_DST_Y = 9;
  private static final int POS_DST_X = 10;
  private static final int POS_DIB = 11;

  private int rop;
  private int srcX;
  private int srcY;
  private int srcW;
  private int srcH;
  private int destX;
  private int destY;
  private int destW;
  private int destH;
  private int usage;

  private int scaled_srcX;
  private int scaled_srcY;
  private int scaled_srcW;
  private int scaled_srcH;
  private int scaled_destX;
  private int scaled_destY;
  private int scaled_destW;
  private int scaled_destH;

  private BufferedImage image;

  public MfCmdStretchDibits() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    // not implemented ...
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdStretchDibits();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.STRETCH_DIBITS;
  }

  public int getROP() {
    return rop;
  }

  public void setROP( final int rop ) {
    this.rop = rop;
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage( final BufferedImage image ) {
    this.image = image;
  }

  public int getUsage() {
    return usage;
  }

  public void setUsage( final int usage ) {
    this.usage = usage;
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int rop = record.getLongParam( POS_ROP );
    final int usage = record.getParam( POS_USAGE ); // This is the only parameter left, so I assume this
    final int srcH = record.getParam( POS_SRC_H );
    final int srcW = record.getParam( POS_SRC_W );
    final int ySrc = record.getParam( POS_SRC_Y );
    final int xSrc = record.getParam( POS_SRC_X );
    final int destH = record.getParam( POS_DST_H );
    final int destW = record.getParam( POS_DST_W );
    final int yDest = record.getParam( POS_DST_Y );
    final int xDest = record.getParam( POS_DST_X );

    try {
      final DIBReader reader = new DIBReader();
      setImage( reader.setRecord( record, POS_DIB ) );
    } catch ( IOException ioe ) {
      // failed to load the bitmap ..
    }

    // DIB ab pos 11
    setROP( rop );
    setUsage( usage );
    setSrcRect( xSrc, ySrc, srcH, srcW );
    setDestRect( xDest, yDest, destH, destW );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    throw new RecordCreationException( "StretchDIBits is not implemented" );
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[STRETCH_DIBITS] rop=" );
    b.append( getROP() );
    b.append( " srcRect=" );
    b.append( getSrcRect() );
    b.append( " destRect=" );
    b.append( getDestRect() );
    b.append( " usage=" );
    b.append( getUsage() );
    return b.toString();
  }

  public void setSrcRect( final int x, final int y, final int w, final int h ) {
    this.srcX = x;
    this.srcY = y;
    this.srcW = w;
    this.srcH = h;
    scaleXChanged();
    scaleYChanged();
  }

  public void setDestRect( final int x, final int y, final int w, final int h ) {
    this.destX = x;
    this.destY = y;
    this.destW = w;
    this.destH = h;
    scaleXChanged();
    scaleYChanged();
  }

  public Rectangle getSrcRect() {
    return new Rectangle( srcX, srcY, srcW, srcH );
  }

  public Rectangle getDestRect() {
    return new Rectangle( destX, destY, destW, destH );
  }

  public Rectangle getScaledSrcRect() {
    return new Rectangle( scaled_srcX, scaled_srcY, scaled_srcW, scaled_srcH );
  }

  public Rectangle getScaledDestRect() {
    return new Rectangle( scaled_destX, scaled_destY, scaled_destW, scaled_destH );
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_srcX = getScaledX( srcX );
    scaled_srcW = getScaledX( srcW );
    scaled_destX = getScaledX( destX );
    scaled_destW = getScaledX( destW );
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_srcY = getScaledY( srcY );
    scaled_srcH = getScaledY( srcH );
    scaled_destY = getScaledY( destY );
    scaled_destH = getScaledY( destH );
  }

}

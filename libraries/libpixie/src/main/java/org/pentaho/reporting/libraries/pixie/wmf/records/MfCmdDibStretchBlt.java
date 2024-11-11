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


package org.pentaho.reporting.libraries.pixie.wmf.records;

import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;
import org.pentaho.reporting.libraries.pixie.wmf.bitmap.DIBReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The StretchBlt function copies a bitmap from a source rectangle into a destination rectangle, stretching or
 * compressing the bitmap to fit the dimensions of the destination rectangle, if necessary. The system stretches or
 * compresses the bitmap according to the stretching mode currently set in the destination device context.
 * <p/>
 * RasterOperations are ignored ..
 */
public class MfCmdDibStretchBlt extends MfCmd {
  //  private static final int RECORD_BASE_SIZE = 10;
  private static final int POS_DIB = 10;
  private static final int POS_OPERATION = 0;
  private static final int POS_SRC_HEIGHT = 2;
  private static final int POS_SRC_WIDTH = 3;
  private static final int POS_SRC_Y = 4;
  private static final int POS_SRC_X = 5;
  private static final int POS_DST_HEIGHT = 6;
  private static final int POS_DST_WIDTH = 7;
  private static final int POS_DST_Y = 8;
  private static final int POS_DST_X = 9;

  private BufferedImage image;

  private int rop;
  private int srcX;
  private int srcY;
  private int srcW;
  private int srcH;
  private int destX;
  private int destY;
  private int destW;
  private int destH;

  private int scaled_destX;
  private int scaled_destY;
  private int scaled_destW;
  private int scaled_destH;

  public MfCmdDibStretchBlt() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    file.getGraphics2D().drawImage( image, srcX, srcY, srcW, srcH,
      scaled_destX, scaled_destY, scaled_destW, scaled_destH, null );
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdDibStretchBlt();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.STRETCH_BLT;
  }

  public int getROP() {
    return rop;
  }

  public void setROP( final int rop ) {
    this.rop = rop;
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int rop = record.getLongParam( POS_OPERATION );
    final int ySrc = record.getParam( POS_SRC_Y );
    final int xSrc = record.getParam( POS_SRC_X );
    final int srcH = record.getParam( POS_SRC_HEIGHT );
    final int srcW = record.getParam( POS_SRC_WIDTH );
    final int destH = record.getParam( POS_DST_HEIGHT );
    final int destW = record.getParam( POS_DST_WIDTH );
    final int yDest = record.getParam( POS_DST_Y );
    final int xDest = record.getParam( POS_DST_X );

    try {
      final DIBReader reader = new DIBReader();
      setImage( reader.setRecord( record, POS_DIB ) );
    } catch ( IOException ioe ) {
      // failed to load the bitmap ..
    }

    setROP( rop );
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
    throw new RecordCreationException( "StretchBlt is not supported" );
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage( final BufferedImage image ) {
    this.image = image;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[STRETCH_BLT] rop=" );
    b.append( getROP() );
    b.append( " srcRect=" );
    b.append( getSrcRect() );
    b.append( " destRect=" );
    b.append( getDestRect() );
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

  public Rectangle getScaledDestRect() {
    return new Rectangle( scaled_destX, scaled_destY, scaled_destW, scaled_destH );
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_destX = getScaledX( destX );
    scaled_destW = getScaledX( destW );
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_destY = getScaledY( destY );
    scaled_destH = getScaledY( destH );
  }
}

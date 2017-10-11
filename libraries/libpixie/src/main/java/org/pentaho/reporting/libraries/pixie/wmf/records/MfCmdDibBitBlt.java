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
 * BitBlockTransfer - Copies PixelData of a rectangle to another position
 * <p/>
 * <pre>
 * BOOL BitBlt(
 * HDC hdcDest, // handle to destination DC
 * int nXDest,  // x-coord of destination upper-left corner
 * int nYDest,  // y-coord of destination upper-left corner
 * int nWidth,  // width of destination rectangle
 * int nHeight, // height of destination rectangle
 * HDC hdcSrc,  // handle to source DC
 * int nXSrc,   // x-coordinate of source upper-left corner
 * int nYSrc,   // y-coordinate of source upper-left corner
 * DWORD dwRop  // raster operation code
 * );
 * </pre>
 */
public class MfCmdDibBitBlt extends MfCmd {
  private static final int RECORD_SIZE_SIMPLE = 9;
  private static final int RECORD_BASE_SIZE_EXT = 8;
  private static final int POS_OPERATION = 0;
  private static final int POS_SRC_Y = 2;
  private static final int POS_SRC_X = 3;
  private static final int SIMPLE_POS_HEIGHT = 5;
  private static final int SIMPLE_POS_WIDTH = 6;
  private static final int SIMPLE_POS_DST_Y = 7;
  private static final int SIMPLE_POS_DST_X = 8;

  private static final int EXT_POS_HEIGHT = 4;
  private static final int EXT_POS_WIDTH = 5;
  private static final int EXT_POS_DST_Y = 6;
  private static final int EXT_POS_DST_X = 7;

  private BufferedImage image;

  private int scaled_destX;
  private int scaled_destY;
  private int scaled_destWidth;
  private int scaled_destHeight;
  private int scaled_sourceX;
  private int scaled_sourceY;

  private int destX;
  private int destY;
  private int destWidth;
  private int destHeight;
  private int sourceX;
  private int sourceY;
  private int operation;

  public MfCmdDibBitBlt() {
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage( final BufferedImage image ) {
    this.image = image;
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    // is not implemented, as we don't have access to the raster data.
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[DIB_BIT_BLT] records=" );
    b.append( getOperation() );
    b.append( " source=" );
    b.append( getOrigin() );
    b.append( " destination=" );
    b.append( getDestination() );
    return b.toString();

  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_sourceX = getScaledX( sourceX );
    scaled_destX = getScaledX( destX );
    scaled_destWidth = getScaledX( destWidth );
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_sourceY = getScaledY( sourceY );
    scaled_destY = getScaledY( destY );
    scaled_destHeight = getScaledY( destHeight );
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdDibBitBlt();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.BIT_BLT;
  }

  public void setOrigin( final int x, final int y ) {
    sourceX = x;
    sourceY = y;
    scaleXChanged();
    scaleYChanged();
  }

  public Point getOrigin() {
    return new Point( sourceX, sourceY );
  }

  public Rectangle getSource() {
    return new Rectangle( sourceX, sourceY, destWidth, destHeight );
  }

  public Point getScaledOrigin() {
    return new Point( scaled_sourceX, scaled_sourceY );
  }

  public Rectangle getScaledSource() {
    return new Rectangle( scaled_sourceX, scaled_sourceY, scaled_destWidth, scaled_destHeight );
  }

  public void setDestination( final int x, final int y, final int w, final int h ) {
    destX = x;
    destY = y;
    destWidth = w;
    destHeight = h;
    scaleXChanged();
    scaleYChanged();
  }

  public Rectangle getDestination() {
    return new Rectangle( destX, destY, destWidth, destHeight );
  }

  public Rectangle getScaledDestination() {
    return new Rectangle( scaled_destX, scaled_destY, scaled_destWidth, scaled_destHeight );
  }

  public void setOperation( final int op ) {
    operation = op;
  }

  public int getOperation() {
    return operation;
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    if ( image == null ) {
      // the simple form, the complex form would need a DIB implementation.
      final MfRecord record = new MfRecord( RECORD_SIZE_SIMPLE );
      record.setLongParam( POS_OPERATION, getOperation() );
      final Rectangle source = getSource();
      record.setParam( POS_SRC_Y, (int) source.getY() );
      record.setParam( POS_SRC_X, (int) source.getX() );

      // Ignore the handle to the device context
      final Rectangle dest = getDestination();
      record.setParam( 4, 0 ); // the handle to the device context ... a stored DIB?.
      record.setParam( SIMPLE_POS_HEIGHT, (int) dest.getHeight() );
      record.setParam( SIMPLE_POS_WIDTH, (int) dest.getWidth() );
      record.setParam( SIMPLE_POS_DST_Y, (int) dest.getY() );
      record.setParam( SIMPLE_POS_DST_X, (int) dest.getX() );
      return record;
    }

    // todo implement the complex form 
    throw new RecordCreationException( "The extended Format of DibBitBlt is not supported" );
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
    final int sy = record.getParam( POS_SRC_Y );
    final int sx = record.getParam( POS_SRC_X );
    setOperation( rop );
    setOrigin( sx, sy );

    if ( record.getLength() == ( MfRecord.RECORD_HEADER_SIZE + 8 * 2 ) ) {
      // Simple form
      final int dh = record.getParam( SIMPLE_POS_HEIGHT );
      final int dw = record.getParam( SIMPLE_POS_WIDTH );
      final int dy = record.getParam( SIMPLE_POS_DST_Y );
      final int dx = record.getParam( SIMPLE_POS_DST_X );
      setDestination( dx, dy, dw, dh );
    } else {
      // Complex form
      final int dh = record.getParam( EXT_POS_HEIGHT );
      final int dw = record.getParam( EXT_POS_WIDTH );
      final int dy = record.getParam( EXT_POS_DST_Y );
      final int dx = record.getParam( EXT_POS_DST_X );
      setDestination( dx, dy, dw, dh );
      try {
        // The sourceDib follows on Position 8 til the end if this is not the simple
        // form.
        final DIBReader reader = new DIBReader();
        setImage( reader.setRecord( record, RECORD_BASE_SIZE_EXT ) );
      } catch ( IOException ioe ) {
        // failed to load the bitmap ..
      }
    }
  }
}

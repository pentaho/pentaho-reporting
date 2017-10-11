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

/**
 * This function is not in the validity list of Microsofts WindowsMetafile Records.
 * <p/>
 */
public class MfCmdBitBlt extends MfCmd {
  private static final int POS_ROP = 0;
  private static final int POS_Y_SOURCE_ORIGIN = 1;
  private static final int POS_X_SOURCE_ORIGIN = 2;
  private static final int POS_DESTINATION_Y_EXT = 3;
  private static final int POS_DESTINATION_X_EXT = 4;
  private static final int POS_Y_DESTINATION_ORIGIN = 5;
  private static final int POS_X_DESTINATION_ORIGIN = 6;
  private static final int POS_BITMAP_WIDTH = 7;
  private static final int POS_BITMAP_HEIGHT = 8;
  private static final int POS_BYTES_PER_RASTER_LINE = 9;
  private static final int POS_COLOR_PLANES_BITMAP = 10;
  private static final int POS_ADJACENT_COLOR_BITS = 11;
  private static final int POS_DEVICE_DEPENDENT_BITMAP_BITS = 12;

  private int rop;
  private int sourceY;
  private int sourceX;
  private int destXExt;
  private int destYExt;
  private int destXOrigin;
  private int destYOrigin;
  private int bitmapWidth;
  private int bitmapHeight;
  private int bytesPerRasterLine;
  private int colorPlanesBitmap;
  private int adjacentColorBits;
  private byte[] deviceDependentBitmap;

  public MfCmdBitBlt() {
  }

  public void replay( final WmfFile file ) {
    // this command is not implemented ...
  }

  public MfCmd getInstance() {
    return new MfCmdBitBlt();
  }

  public void setRecord( final MfRecord record ) {
    setRop( record.getParam( POS_ROP ) );
    setSourceX( record.getParam( POS_Y_SOURCE_ORIGIN ) );
    setSourceY( record.getParam( POS_X_SOURCE_ORIGIN ) );
    setDestXExt( record.getParam( POS_DESTINATION_X_EXT ) );
    setDestYExt( record.getParam( POS_DESTINATION_Y_EXT ) );
    setDestXOrigin( record.getParam( POS_X_DESTINATION_ORIGIN ) );
    setDestYOrigin( record.getParam( POS_Y_DESTINATION_ORIGIN ) );

    setBitmapWidth( record.getParam( POS_BITMAP_WIDTH ) );
    setBitmapHeight( record.getParam( POS_BITMAP_HEIGHT ) );
    setBytesPerRasterLine( record.getParam( POS_BYTES_PER_RASTER_LINE ) );
    setColorPlanesBitmap( record.getParam( POS_COLOR_PLANES_BITMAP ) );
    setAdjacentColorBits( record.getParam( POS_ADJACENT_COLOR_BITS ) );
    // todo read the bitmap data from the record ...
  }


  public String toString() {
    final StringBuffer b = new StringBuffer( 100 );
    b.append( "[OLD_BIT_BLT]" );
    return b.toString();
  }

  public int getFunction() {
    return MfType.OLD_BIT_BLT;
  }

  protected void scaleXChanged() {
  }

  protected void scaleYChanged() {
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    // todo this is not yet correctly implemented ...
    final MfRecord record = new MfRecord( 12 + ( deviceDependentBitmap.length / 4 ) );
    record.setParam( POS_ROP, getRop() );
    record.setParam( POS_Y_SOURCE_ORIGIN, getSourceX() );
    record.setParam( POS_X_SOURCE_ORIGIN, getSourceY() );
    record.setParam( POS_DESTINATION_X_EXT, getDestXExt() );
    record.setParam( POS_DESTINATION_Y_EXT, getDestYExt() );
    record.setParam( POS_X_DESTINATION_ORIGIN, getDestXOrigin() );
    record.setParam( POS_Y_DESTINATION_ORIGIN, getDestYOrigin() );

    record.setParam( POS_BITMAP_WIDTH, getBitmapWidth() );
    record.setParam( POS_BITMAP_HEIGHT, getBitmapHeight() );
    record.setParam( POS_BYTES_PER_RASTER_LINE, getBytesPerRasterLine() );
    record.setParam( POS_COLOR_PLANES_BITMAP, getColorPlanesBitmap() );
    record.setParam( POS_ADJACENT_COLOR_BITS, getAdjacentColorBits() );

    // todo: Write the bitmap data ...
    return record;
  }

  public int getAdjacentColorBits() {
    return adjacentColorBits;
  }

  public void setAdjacentColorBits( final int adjacentColorBits ) {
    this.adjacentColorBits = adjacentColorBits;
  }

  public int getBitmapHeight() {
    return bitmapHeight;
  }

  public void setBitmapHeight( final int bitmapHeight ) {
    this.bitmapHeight = bitmapHeight;
  }

  public int getBitmapWidth() {
    return bitmapWidth;
  }

  public void setBitmapWidth( final int bitmapWidth ) {
    this.bitmapWidth = bitmapWidth;
  }

  public int getBytesPerRasterLine() {
    return bytesPerRasterLine;
  }

  public void setBytesPerRasterLine( final int bytesPerRasterLine ) {
    this.bytesPerRasterLine = bytesPerRasterLine;
  }

  public int getColorPlanesBitmap() {
    return colorPlanesBitmap;
  }

  public void setColorPlanesBitmap( final int colorPlanesBitmap ) {
    this.colorPlanesBitmap = colorPlanesBitmap;
  }

  public int getDestXExt() {
    return destXExt;
  }

  public void setDestXExt( final int destXExt ) {
    this.destXExt = destXExt;
  }

  public int getDestXOrigin() {
    return destXOrigin;
  }

  public void setDestXOrigin( final int destXOrigin ) {
    this.destXOrigin = destXOrigin;
  }

  public int getDestYExt() {
    return destYExt;
  }

  public void setDestYExt( final int destYExt ) {
    this.destYExt = destYExt;
  }

  public int getDestYOrigin() {
    return destYOrigin;
  }

  public void setDestYOrigin( final int destYOrigin ) {
    this.destYOrigin = destYOrigin;
  }

  public byte[] getDeviceDependentBitmap() {
    return deviceDependentBitmap;
  }

  public void setDeviceDependentBitmap( final byte[] deviceDependentBitmap ) {
    this.deviceDependentBitmap = deviceDependentBitmap;
  }

  public int getRop() {
    return rop;
  }

  public void setRop( final int rop ) {
    this.rop = rop;
  }

  public int getSourceX() {
    return sourceX;
  }

  public void setSourceX( final int sourceX ) {
    this.sourceX = sourceX;
  }

  public int getSourceY() {
    return sourceY;
  }

  public void setSourceY( final int sourceY ) {
    this.sourceY = sourceY;
  }
}

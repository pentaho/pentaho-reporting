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


package org.pentaho.reporting.libraries.pixie.wmf.records;

import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

// This structure should include a bitmap. This implementation does
// not know of any bitmaps right now, so this records is ignored.

public class MfCmdSetDibitsToDevice extends MfCmd {
  private static final int POS_FLAG_COLOR_PALETTE = 0;
  private static final int POS_SCANLINE_COUNT = 1;
  private static final int POS_FIRST_SCANLINE = 2;
  private static final int POS_SOURCE_Y_DIB = 3;
  private static final int POS_SOURCE_X_DIB = 4;
  private static final int POS_DIB_HEIGHT = 5;
  private static final int POS_DIB_WIDTH = 6;
  private static final int POS_ORIGIN_DEST_RECT_Y = 7;
  private static final int POS_ORIGIN_DEST_RECT_X = 8;
  private static final int POS_BITMAP_HEADER = 9;
  // the bit map data follows the header.
  // the record has a variable size ...

  public MfCmdSetDibitsToDevice() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdSetDibitsToDevice();
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    // System.out.println("SetDibitsToDevice is not implemented.");

  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[SET_DIBITS_TO_DEVICE] <<windows specific, will not be implemented>>" );
    return b.toString();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.SET_DIBITS_TO_DEVICE;
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord() {
    throw new UnsupportedOperationException( "Native functions are not supported" );
  }
}

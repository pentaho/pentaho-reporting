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

public class MfCmdScaleWindowExt extends MfCmd {
  private static final int RECORD_SIZE = 4;
  private static final int POS_Y_DENOM = 0;
  private static final int POS_X_DENOM = 2;
  private static final int POS_Y_NUM = 1;
  private static final int POS_X_NUM = 3;

  private int yNum;
  private int yDenom;
  private int xNum;
  private int xDenom;

  public MfCmdScaleWindowExt() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    file.getGraphics2D().scale
      ( (double) getXNum() / (double) getXDenom(),
        (double) getYNum() / (double) getYDenom() );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setParam( POS_X_DENOM, getXDenom() );
    record.setParam( POS_X_NUM, getXNum() );
    record.setParam( POS_Y_DENOM, getYDenom() );
    record.setParam( POS_Y_NUM, getYNum() );
    return record;
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdScaleWindowExt();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.SCALE_WINDOW_EXT;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[SCALE_WINDOW] scaleX=" );
    b.append( getXNum() );
    b.append( '/' );
    b.append( getXDenom() );
    b.append( " scaley=" );
    b.append( getYNum() );
    b.append( '/' );
    b.append( getYDenom() );
    return b.toString();
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int yD = record.getParam( 0 );
    final int yN = record.getParam( 1 );
    final int xD = record.getParam( 2 );
    final int xN = record.getParam( 3 );
    setXScale( xN, xD );
    setYScale( yN, yD );
  }

  public void setXScale( final int xNum, final int xDenom ) {
    this.xNum = xNum;
    this.xDenom = xDenom;
  }

  public void setYScale( final int yNum, final int yDenom ) {
    this.yNum = yNum;
    this.yDenom = yDenom;
  }

  public double getXScale() {
    return (double) xNum / xDenom;
  }

  public int getXNum() {
    return xNum;
  }

  public int getXDenom() {
    return xDenom;
  }

  public double getYScale() {
    return (double) yNum / yDenom;
  }

  public int getYNum() {
    return yNum;
  }

  public int getYDenom() {
    return yDenom;
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
}

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

import org.pentaho.reporting.libraries.pixie.wmf.MfLogRegion;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * Inverts the colors in the specified region
 */
public class MfCmdInvertRegion extends MfCmd {
  private int region;

  public MfCmdInvertRegion() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    // by filling with XOR?
    final Graphics2D g2 = (Graphics2D) file.getGraphics2D().create();
    g2.setXORMode( Color.white );
    final MfLogRegion reg = file.getRegionObject( getRegion() );
    g2.fill( reg.getBounds() );
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdInvertRegion();
  }

  private static final int RECORD_SIZE = 1;
  private static final int POS_REGION = 0;

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    region = record.getParam( POS_REGION );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord() {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setParam( POS_REGION, getRegion() );
    return record;
  }

  public void setRegion( final int region ) {
    this.region = region;
  }

  public int getRegion() {
    return region;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[INVERT_REGION] region=" );
    b.append( getRegion() );
    return b.toString();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.INVERT_REGION;
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted. Not used.
   */
  protected void scaleXChanged() {
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted. Not used.
   */
  protected void scaleYChanged() {
  }
}

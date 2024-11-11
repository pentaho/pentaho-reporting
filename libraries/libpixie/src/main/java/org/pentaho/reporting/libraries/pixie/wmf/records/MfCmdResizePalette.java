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

/**
 * The ResizePalette function increases or decreases the size of a logical palette based on the specified value.
 */
public class MfCmdResizePalette extends MfCmd {
  private static final int RECORD_SIZE = 1;
  private static final int POS_NEW_PALETTE_SIZE = 0;
  private int paletteSize;

  public MfCmdResizePalette() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    // Not implemented!
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdResizePalette();
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    setPaletteSize( record.getParam( POS_NEW_PALETTE_SIZE ) );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setType( getFunction() );
    record.setParam( POS_NEW_PALETTE_SIZE, getPaletteSize() );
    return record;
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.RESIZE_PALETTE;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[RESIZE_PALETTE] is not implemented" );
    return b.toString();
  }

  /**
   * Not implemented as no scaling needed for this operation.
   */
  protected void scaleXChanged() {
  }

  /**
   * Not implemented as no scaling needed for this operation.
   */
  protected void scaleYChanged() {
  }

  public int getPaletteSize() {
    return paletteSize;
  }

  public void setPaletteSize( final int paletteSize ) {
    this.paletteSize = paletteSize;
  }
}

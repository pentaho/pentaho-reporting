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

import org.pentaho.reporting.libraries.pixie.wmf.MfDcState;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

/**
 * This records is ignored, as it is used to map fonts into the given aspect ratio. This affects only BitMap-fonts, as
 * TrueTypeFonts are always able to scale to any aspect ratio.
 * <p/>
 * In java all fonts are considered true-type.
 */
public class MfCmdSetMapperFlags extends MfCmd {
  private static final int RECORD_SIZE = 2;
  private static final int POS_MAPFLAGS = 0;

  private int mapperflags;

  public MfCmdSetMapperFlags() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final MfDcState state = file.getCurrentState();
    state.setMapperFlag( mapperflags );
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdSetMapperFlags();
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int id = record.getLongParam( POS_MAPFLAGS );
    setMapperFlags( id );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setLongParam( POS_MAPFLAGS, getMapperFlags() );
    return record;
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.SET_MAPPER_FLAGS;
  }

  public int getMapperFlags() {
    return mapperflags;
  }

  public void setMapperFlags( final int id ) {
    this.mapperflags = id;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[MAPPERFLAGS] mapperflags=" );
    b.append( getMapperFlags() );
    return b.toString();
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

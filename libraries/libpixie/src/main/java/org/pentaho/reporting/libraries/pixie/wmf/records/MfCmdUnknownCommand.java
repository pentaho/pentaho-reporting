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
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

/**
 * This function is not in the validity list of Microsofts WindowsMetafile Records.
 */
public class MfCmdUnknownCommand extends MfCmd {
  private int function;

  public MfCmdUnknownCommand() {
  }

  public void replay( final WmfFile file ) {
  }

  public MfCmd getInstance() {
    return new MfCmdUnknownCommand();
  }

  public void setRecord( final MfRecord record ) {
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    throw new RecordCreationException( "The {Unknown Command} is not writeable" );
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[UNKNOWN COMMAND] " );
    b.append( Integer.toHexString( getFunction() ) );
    return b.toString();
  }

  public void setFunction( final int function ) {
    this.function = function;
  }

  public int getFunction() {
    return function;
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

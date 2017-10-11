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

import org.pentaho.reporting.libraries.pixie.wmf.MfLogBrush;
import org.pentaho.reporting.libraries.pixie.wmf.MfLogFont;
import org.pentaho.reporting.libraries.pixie.wmf.MfLogPalette;
import org.pentaho.reporting.libraries.pixie.wmf.MfLogPen;
import org.pentaho.reporting.libraries.pixie.wmf.MfLogRegion;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;
import org.pentaho.reporting.libraries.pixie.wmf.WmfObject;

/**
 * Activates the specified Object. The object must be previously defined for the device context by using the correct
 * create*() method.
 */
public class MfCmdSelectObject extends MfCmd {
  private static final int RECORD_SIZE = 1;
  private static final int POS_OBJECT_ID = 0;

  private int objectId;

  public MfCmdSelectObject() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final WmfObject object = file.getObject( objectId );
    if ( object == null ) {
      throw new NullPointerException( "Object " + objectId + " is not defined" );
    }
    switch( object.getType() ) {
      case WmfObject.OBJ_BRUSH:
        file.getCurrentState().setLogBrush( (MfLogBrush) object );
        break;
      case WmfObject.OBJ_FONT:
        file.getCurrentState().setLogFont( (MfLogFont) object );
        break;
      case WmfObject.OBJ_PALETTE:
        file.getCurrentState().setLogPalette( (MfLogPalette) object );
        break;
      case WmfObject.OBJ_PEN:
        file.getCurrentState().setLogPen( (MfLogPen) object );
        break;
      case WmfObject.OBJ_REGION:
        file.getCurrentState().setLogRegion( (MfLogRegion) object );
        break;
    }
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdSelectObject();
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int id = record.getParam( POS_OBJECT_ID );
    setObjectId( id );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setParam( POS_OBJECT_ID, getObjectId() );
    return record;
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.SELECT_OBJECT;
  }

  public int getObjectId() {
    return objectId;
  }

  public void setObjectId( final int id ) {
    this.objectId = id;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[SELECT_OBJECT] object=" );
    b.append( getObjectId() );
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

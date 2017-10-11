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

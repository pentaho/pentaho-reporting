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

import org.pentaho.reporting.libraries.pixie.wmf.MfDcState;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * Prints the given string. That record is as weird as everything in windows. First parameter is the string length, then
 * follows the string and finally the x and y coordinates (in that order) where to print the string.
 */
public class MfCmdTextOut extends MfCmd {
  private int x;
  private int y;
  private String text;
  private int count;
  private int scaled_x;
  private int scaled_y;

  public MfCmdTextOut() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final Point p = getScaledDestination();
    final int x = p.x;
    final int y = p.y;

    final Graphics2D graphics = file.getGraphics2D();
    final MfDcState state = file.getCurrentState();

    state.prepareDrawText();
    graphics.drawString( text, x, y );
    state.postDrawText();
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdTextOut();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.TEXT_OUT;
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int count = record.getParam( 0 );
    final byte[] text = new byte[ count ];
    for ( int i = 0; i < count; i++ ) {
      text[ i ] = (byte) record.getByte( MfRecord.RECORD_HEADER_SIZE + 2 + i );
    }
    final String sText = new String( text );
    final int y = record.getParam( (int) ( Math.ceil( count / 2 ) + 1 ) );
    final int x = record.getParam( (int) ( Math.ceil( count / 2 ) + 2 ) );

    setCount( count );
    setDestination( x, y );
    setText( sText );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final String text = getText();
    final int parCntText = (int) Math.ceil( text.length() / 2 );
    final MfRecord record = new MfRecord( parCntText + 3 );
    record.setParam( 0, text.length() );

    final byte[] textRaw = text.getBytes();
    for ( int i = 0; i < count; i++ ) {
      record.setByte( MfRecord.RECORD_HEADER_SIZE + 2 + i, textRaw[ i ] );
    }

    final Point dest = getDestination();
    record.setParam( (int) ( Math.ceil( count / 2 ) + 1 ), dest.y );
    record.setParam( (int) ( Math.ceil( count / 2 ) + 2 ), dest.x );
    return record;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[TEXT_OUT] text=" );
    b.append( getText() );
    b.append( " destination=" );
    b.append( getDestination() );
    b.append( " count=" );
    b.append( getCount() );
    return b.toString();
  }

  public void setDestination( final int x, final int y ) {
    this.x = x;
    this.y = y;
    scaleXChanged();
    scaleYChanged();

  }

  public Point getDestination() {
    return new Point( x, y );
  }

  public void setText( final String text ) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public int getCount() {
    return count;
  }

  public void setCount( final int count ) {
    this.count = count;
  }

  public Point getScaledDestination() {
    return new Point( scaled_x, scaled_y );
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_x = getScaledX( x );
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_y = getScaledY( y );
  }
}

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

import org.pentaho.reporting.libraries.pixie.wmf.GDIColor;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * The FloodFill function fills an area of the display surface with the current brush. The area is assumed to be bounded
 * as specified by the crFill parameter.
 */
public class MfCmdFloodFill extends MfCmd {
  private static final int RECORD_SIZE = 4;
  private static final int POS_COLOR = 0;
  private static final int POS_Y = 2;
  private static final int POS_X = 3;

  private int x;
  private int y;
  private int scaled_x;
  private int scaled_y;
  private Color color;

  public MfCmdFloodFill() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    // there is no way of implementing a flood fill operation for Graphics2D.
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdFloodFill();
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int c = record.getLongParam( POS_COLOR );
    final Color color = new GDIColor( c );
    final int y = record.getParam( POS_Y );
    final int x = record.getParam( POS_X );
    setTarget( x, y );
    setColor( color );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord() {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setLongParam( POS_COLOR, GDIColor.translateColor( getColor() ) );
    final Point target = getTarget();
    record.setParam( POS_Y, (int) target.getY() );
    record.setParam( POS_X, (int) target.getX() );
    return record;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[FLOOD_FILL] color=" );
    b.append( getColor() );
    b.append( " target=" );
    b.append( getTarget() );
    return b.toString();
  }

  public Point getTarget() {
    return new Point( x, y );
  }

  public Point getScaledTarget() {
    return new Point( scaled_x, scaled_y );
  }

  public void setTarget( final Point point ) {
    setTarget( point.x, point.y );
  }

  public void setTarget( final int x, final int y ) {
    this.x = x;
    this.y = y;
    scaleXChanged();
    scaleYChanged();
  }

  public void setColor( final Color c ) {
    this.color = c;
  }

  public Color getColor() {
    return color;
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.FLOOD_FILL;
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

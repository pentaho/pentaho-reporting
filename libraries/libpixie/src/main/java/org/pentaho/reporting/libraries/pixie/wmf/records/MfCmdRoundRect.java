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
import java.awt.geom.RoundRectangle2D;

/**
 * The RoundRect function draws a rectangle with rounded corners. The rectangle is outlined by using the current pen and
 * filled by using the current brush.
 */
public class MfCmdRoundRect extends MfCmd {
  private static final int RECORD_SIZE = 6;
  private static final int POS_TOP = 4;
  private static final int POS_LEFT = 5;
  private static final int POS_RIGHT = 3;
  private static final int POS_BOTTOM = 2;
  private static final int POS_ROUND_WIDTH = 1;
  private static final int POS_ROUND_HEIGHT = 0;

  private int x;
  private int y;
  private int width;
  private int height;
  private int roundWidth;
  private int roundHeight;

  private int scaled_x;
  private int scaled_y;
  private int scaled_width;
  private int scaled_height;
  private int scaled_roundWidth;
  private int scaled_roundHeight;

  public MfCmdRoundRect() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final Graphics2D graph = file.getGraphics2D();
    final Rectangle rec = getScaledBounds();
    final Dimension dim = getScaledRoundingDim();
    final RoundRectangle2D shape = new RoundRectangle2D.Double();
    shape.setRoundRect( rec.x, rec.y, rec.width, rec.height, dim.width, dim.height );
    final MfDcState state = file.getCurrentState();

    if ( state.getLogBrush().isVisible() ) {
      state.preparePaint();
      graph.fill( shape );
      state.postPaint();
    }
    if ( state.getLogPen().isVisible() ) {
      state.prepareDraw();
      graph.draw( shape );
      state.postDraw();
    }
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdRoundRect();
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[ROUND_RECTANGLE] bounds=" );
    b.append( getBounds() );
    b.append( " roundingDim=" );
    b.append( getRoundingDim() );
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
    final int rHeight = record.getParam( POS_ROUND_HEIGHT );
    final int rWidth = record.getParam( POS_ROUND_WIDTH );
    final int bottom = record.getParam( POS_BOTTOM );
    final int right = record.getParam( POS_RIGHT );
    final int top = record.getParam( POS_TOP );
    final int left = record.getParam( POS_LEFT );
    setBounds( left, top, right - left, bottom - top );
    setRoundingDim( rWidth, rHeight );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    final Dimension rDim = getRoundingDim();
    record.setParam( POS_ROUND_HEIGHT, rDim.height );
    record.setParam( POS_ROUND_WIDTH, rDim.width );
    final Rectangle bounds = getBounds();
    record.setParam( POS_BOTTOM, bounds.height + bounds.y );
    record.setParam( POS_RIGHT, bounds.width + bounds.x );
    record.setParam( POS_TOP, bounds.y );
    record.setParam( POS_LEFT, bounds.x );
    return record;
  }

  public Rectangle getBounds() {
    return new Rectangle( x, y, width, height );
  }

  public Rectangle getScaledBounds() {
    return new Rectangle( scaled_x, scaled_y, scaled_width, scaled_height );
  }

  public void setBounds( final int x, final int y, final int width, final int height ) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    scaleXChanged();
    scaleYChanged();

  }

  public void setRoundingDim( final int w, final int h ) {
    this.roundWidth = w;
    this.roundHeight = h;
    scaleXChanged();
    scaleYChanged();
  }

  public Dimension getRoundingDim() {
    return new Dimension( roundWidth, roundHeight );
  }

  public Dimension getScaledRoundingDim() {
    return new Dimension( scaled_roundWidth, scaled_roundHeight );
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_x = getScaledX( x );
    scaled_width = getScaledX( width );
    scaled_roundWidth = getScaledX( roundWidth );
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_y = getScaledY( y );
    scaled_height = getScaledY( height );
    scaled_roundHeight = getScaledY( roundHeight );
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.ROUND_RECT;
  }
}

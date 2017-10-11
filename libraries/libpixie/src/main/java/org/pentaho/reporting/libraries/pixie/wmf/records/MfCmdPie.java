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
import java.awt.geom.Arc2D;

/**
 * The Pie function draws a pie-shaped wedge bounded by the intersection of an ellipse and two radials. The pie is
 * outlined by using the current pen and filled by using the current brush.
 */
public class MfCmdPie extends MfCmd {
  private static final int PARAM_X_END_POS = 0;
  private static final int PARAM_Y_END_POS = 1;
  private static final int PARAM_X_START_POS = 2;
  private static final int PARAM_Y_START_POS = 3;
  private static final int PARAM_BOTTOM_POS = 4;
  private static final int PARAM_RIGHT_POS = 5;
  private static final int PARAM_TOP_POS = 6;
  private static final int PARAM_LEFT_POS = 7;

  private static final int RECORD_SIZE = 8;

  private int x;
  private int y;
  private int width;
  private int height;

  private int xstart;
  private int ystart;
  private int xend;
  private int yend;

  private int scaled_x;
  private int scaled_y;
  private int scaled_width;
  private int scaled_height;

  private int scaled_xstart;
  private int scaled_ystart;
  private int scaled_xend;
  private int scaled_yend;

  public MfCmdPie() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final Graphics2D graph = file.getGraphics2D();
    final Rectangle rec = getScaledBounds();
    final Point start = getScaledStartingIntersection();
    final Point end = getScaledEndingIntersection();

    final Arc2D arc = new Arc2D.Double();
    arc.setArcType( Arc2D.PIE );
    arc.setFrame( rec.x, rec.y, rec.width, rec.height );
    arc.setAngles( start.x, start.y, end.x, end.y );

    final MfDcState state = file.getCurrentState();

    if ( state.getLogBrush().isVisible() ) {
      state.preparePaint();
      graph.fill( arc );
      state.postPaint();
    }
    if ( state.getLogPen().isVisible() ) {
      state.prepareDraw();
      graph.draw( arc );
      state.postDraw();
    }
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_x = getScaledX( x );
    scaled_width = getScaledX( width );
    scaled_xstart = getScaledX( xstart );
    scaled_xend = getScaledX( xend );
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_y = getScaledY( y );
    scaled_height = getScaledY( height );
    scaled_ystart = getScaledY( xstart );
    scaled_yend = getScaledY( xend );
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdPie();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.PIE;
  }

  public Rectangle getBounds() {
    return new Rectangle( x, y, width, height );
  }

  public Rectangle getScaledBounds() {
    return new Rectangle( scaled_x, scaled_y, scaled_width, scaled_height );
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[PIE] bounds=" );
    b.append( getBounds() );
    b.append( " startIntersection=" );
    b.append( getStartingIntersection() );
    b.append( " endingIntersection=" );
    b.append( getEndingIntersection() );
    return b.toString();
  }

  public void setBounds( final int x, final int y, final int width, final int height ) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    scaleXChanged();
    scaleYChanged();

  }

  public void setStartingIntersection( final int x, final int y ) {
    xstart = x;
    ystart = y;
    scaleXChanged();
    scaleYChanged();
  }

  public Point getStartingIntersection() {
    return new Point( xstart, ystart );
  }

  public Point getScaledStartingIntersection() {
    return new Point( scaled_xstart, scaled_ystart );
  }

  public void setEndingIntersection( final int x, final int y ) {
    xend = x;
    yend = y;
    scaleXChanged();
    scaleYChanged();

  }

  public Point getEndingIntersection() {
    return new Point( xend, yend );
  }

  public Point getScaledEndingIntersection() {
    return new Point( scaled_xend, scaled_yend );
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int xend = record.getParam( PARAM_X_END_POS );
    final int yend = record.getParam( PARAM_Y_END_POS );
    final int xstart = record.getParam( PARAM_X_START_POS );
    final int ystart = record.getParam( PARAM_Y_START_POS );
    final int bottom = record.getParam( PARAM_BOTTOM_POS );
    final int right = record.getParam( PARAM_RIGHT_POS );
    final int top = record.getParam( PARAM_TOP_POS );
    final int left = record.getParam( PARAM_LEFT_POS );
    setBounds( left, top, right - left, bottom - top );
    setStartingIntersection( xstart, ystart );
    setEndingIntersection( xend, yend );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord() {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    final Rectangle bounds = getBounds();
    final Point start = getStartingIntersection();
    final Point end = getEndingIntersection();

    record.setParam( PARAM_LEFT_POS, (int) bounds.getX() );
    record.setParam( PARAM_TOP_POS, (int) bounds.getY() );
    record.setParam( PARAM_RIGHT_POS, (int) ( bounds.getX() + bounds.getWidth() ) );
    record.setParam( PARAM_BOTTOM_POS, (int) ( bounds.getY() + bounds.getHeight() ) );
    record.setParam( PARAM_Y_START_POS, (int) ( start.getY() ) );
    record.setParam( PARAM_X_START_POS, (int) ( start.getX() ) );
    record.setParam( PARAM_Y_END_POS, (int) ( end.getY() ) );
    record.setParam( PARAM_X_END_POS, (int) ( end.getX() ) );
    return record;
  }

}

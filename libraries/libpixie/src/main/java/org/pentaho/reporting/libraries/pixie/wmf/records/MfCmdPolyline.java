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
import java.awt.geom.Line2D;

/**
 * The Polyline function draws a series of line segments by connecting the points in the specified array.
 * <p/>
 * The polyline does not use the current cursor position as starting point of the first line. The starting point is
 * defined by the first coordinate of the point-array.
 */
public class MfCmdPolyline extends MfCmd {
  private int[] points_x;
  private int[] points_y;
  private int[] scaled_points_x;
  private int[] scaled_points_y;
  private int count;

  public MfCmdPolyline() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final Graphics2D graph = file.getGraphics2D();
    final MfDcState state = file.getCurrentState();
    int cx = state.getCurPosX();
    int cy = state.getCurPosY();
    final int[] points_x = getScaledPointsX();
    final int[] points_y = getScaledPointsY();

    if ( state.getLogPen().isVisible() ) {
      state.prepareDraw();
      cx = points_x[ 0 ];
      cy = points_y[ 0 ];
      final Line2D.Double line = new Line2D.Double();
      for ( int i = 1; i < count; i++ ) {
        final int destX = points_x[ i ];
        final int destY = points_y[ i ];
        line.setLine( cx, cy, destX, destY );
        graph.draw( line );
        cx = destX;
        cy = destY;
      }
      state.postDraw();
    }
    state.setCurPos( cx, cy );
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdPolyline();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.POLYLINE;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[POLYLINE] count=" );
    b.append( getPointCount() );
    final int l = getPointCount();
    final int[] points_x = getPointsX();
    final int[] points_y = getPointsY();

    for ( int i = 0; i < l; i++ ) {
      if ( i != 0 ) {
        b.append( ',' );
      }

      b.append( " (" );
      b.append( points_x[ i ] );
      b.append( ',' );
      b.append( points_y[ i ] );
      b.append( ") " );
    }
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
    final int count = record.getParam( 0 );
    final int[] points_x = new int[ count ];
    final int[] points_y = new int[ count ];

    for ( int i = 0; i < count; i++ ) {
      points_x[ i ] = record.getParam( 1 + 2 * i );
      points_y[ i ] = record.getParam( 2 + 2 * i );
    }
    setPointCount( count );
    setPoints( points_x, points_y );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final MfRecord record = new MfRecord( getPointCount() * 2 + 1 );
    final int count = getPointCount();
    final int[] points_x = getPointsX();
    final int[] points_y = getPointsY();

    record.setParam( 0, count );

    for ( int i = 0; i < count; i++ ) {
      record.setParam( 1 + 2 * i, points_x[ i ] );
      record.setParam( 2 + 2 * i, points_y[ i ] );
    }
    return record;
  }

  public void setPointCount( final int count ) {
    this.count = count;
  }

  public void setPoints( final int[] points_x, final int[] points_y ) {
    this.points_x = points_x;
    this.points_y = points_y;
    scaleXChanged();
    scaleYChanged();

  }

  public int[] getPointsX() {
    return points_x;
  }

  public int[] getPointsY() {
    return points_y;
  }

  public int getPointCount() {
    return count;
  }

  public int[] getScaledPointsX() {
    return scaled_points_x;
  }

  public int[] getScaledPointsY() {
    return scaled_points_y;
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_points_x = applyScaleX( points_x, scaled_points_x );
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_points_y = applyScaleY( points_y, scaled_points_y );
  }
}

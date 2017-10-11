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
import java.awt.geom.GeneralPath;

/**
 * PolyPolygon, is a list of polygons, for filled polygons SetPolyFillMode affects how the polygon is filled. the number
 * of polygons is recorded, followed by the number of points in each polygon, and then a long sequence of all the points
 * of all the polygons.
 */
public class MfCmdPolyPolygon extends MfCmd {
  private Object[] points_x; // contains int[]
  private Object[] points_y; // contains int[]
  private Object[] scaled_points_x; // contains int[]
  private Object[] scaled_points_y; // contains int[]
  private int polycount;

  public MfCmdPolyPolygon() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final Graphics2D graph = file.getGraphics2D();

    final MfDcState state = file.getCurrentState();

    final GeneralPath genPath = new GeneralPath();
    for ( int i = 0; i < polycount; i++ ) {
      final int[] pointsX = getScaledPointsX( i );
      final int[] pointsY = getScaledPointsY( i );
      final Polygon polygon = new Polygon( pointsX, pointsY, pointsX.length );

      genPath.append( polygon, false );
    }

    if ( state.getLogBrush().isVisible() ) {
      state.preparePaint();
      graph.fill( genPath );
      state.postPaint();
    }
    if ( state.getLogPen().isVisible() ) {
      state.prepareDraw();
      graph.draw( genPath );
      state.postDraw();
    }
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdPolyPolygon();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.POLY_POLYGON;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[POLYPOLYGON] polycount=" );
    b.append( getPolygonCount() );
    b.append( '\n' );
    for ( int p = 0; p < getPolygonCount(); p++ ) {
      b.append( "  Polygon " );
      b.append( p );

      final int[] points_x = getPointsX( p );
      final int[] points_y = getPointsY( p );
      final int l = points_x.length;

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
      b.append( '\n' );
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
    final int numberOfPolygons = record.getParam( 0 );
    //Log.debug("Number Of Polygons: " + numberOfPolygons);
    final int[] count = new int[ numberOfPolygons ];
    final Object[] poly_points_x = new Object[ numberOfPolygons ];
    final Object[] poly_points_y = new Object[ numberOfPolygons ];

    // read the length of each polygon
    for ( int i = 0; i < numberOfPolygons; i++ ) {
      final int numberOfPointsInPolygon = record.getParam( 1 + i );
      //Log.debug("Number Of points in polygon: " + numberOfPointsInPolygon);
      count[ i ] = numberOfPointsInPolygon;
    }

    // getLength?
    //Log.debug("record.getLength(): " + (record.getLength() / 2));
    // for each polygon, read the points ...
    int readPosition = 1 + numberOfPolygons;
    for ( int i = 0; i < numberOfPolygons; i++ ) {
      // Position of the points depends on the number of points
      // of the previous polygons
      final int numberOfPoints = count[ i ];
      final int[] points_x = new int[ numberOfPoints ];
      final int[] points_y = new int[ numberOfPoints ];
      // read position is after numPolygonPointsRead + noOfPolygons + 1 (for the first parameter)
      for ( int point = 0; point < numberOfPoints; point += 1 ) {
        points_x[ point ] = record.getParam( readPosition );
        readPosition += 1;
        points_y[ point ] = record.getParam( readPosition );
        readPosition += 1;
      }
      poly_points_x[ i ] = points_x;
      poly_points_y[ i ] = points_y;
    }
    setPolygonCount( numberOfPolygons );
    setPoints( poly_points_x, poly_points_y );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final int numberOfPolygons = getPolygonCount();
    int pointsTotal = 0;
    for ( int i = 0; i < numberOfPolygons; i++ ) {
      pointsTotal += getPointsX( i ).length;
    }
    final MfRecord record = new MfRecord( 1 + numberOfPolygons + pointsTotal * 2 );
    record.setParam( 0, numberOfPolygons );

    int numberOfPointsRead = 0;
    for ( int i = 0; i < numberOfPolygons; i++ ) {
      final int[] x_points = getPointsX( i );
      final int[] y_points = getPointsY( i );
      final int numberOfPointsInPolygon = x_points.length;
      record.setParam( 1 + i, numberOfPointsInPolygon );

      final int readPos = numberOfPointsRead * 2 + numberOfPolygons + 1;
      for ( int j = 0; j < numberOfPointsInPolygon; j++ ) {
        record.setParam( ( readPos + 1 ) + j * 2, x_points[ i ] );
        record.setParam( ( readPos + 2 ) + j * 2, y_points[ i ] );
      }
      numberOfPointsRead += numberOfPointsInPolygon;
    }
    return record;
  }

  public void setPoints( final Object[] points_x, final Object[] points_y ) {
    this.points_x = points_x;
    this.points_y = points_y;
    scaleXChanged();
    scaleYChanged();
  }

  public int[] getPointsX( final int polygon ) {
    return (int[]) points_x[ polygon ];
  }

  public int[] getPointsY( final int polygon ) {
    return (int[]) points_y[ polygon ];
  }

  public int[] getScaledPointsX( final int polygon ) {
    return (int[]) scaled_points_x[ polygon ];
  }

  public int[] getScaledPointsY( final int polygon ) {
    return (int[]) scaled_points_y[ polygon ];
  }

  public void setPolygonCount( final int count ) {
    this.polycount = count;
  }

  public int getPolygonCount() {
    return polycount;
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    if ( scaled_points_x == null ) {
      scaled_points_x = new Object[ points_x.length ];
    }
    if ( scaled_points_x.length < points_x.length ) {
      scaled_points_x = new Object[ points_x.length ];
    }

    for ( int i = 0; i < polycount; i++ ) {
      scaled_points_x[ i ] = applyScaleX( (int[]) points_x[ i ], (int[]) scaled_points_x[ i ] );
    }
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    if ( scaled_points_y == null ) {
      scaled_points_y = new Object[ points_y.length ];
    }
    if ( scaled_points_y.length < points_y.length ) {
      scaled_points_y = new Object[ points_y.length ];
    }

    for ( int i = 0; i < polycount; i++ ) {
      scaled_points_y[ i ] = applyScaleY( (int[]) points_y[ i ], (int[]) scaled_points_y[ i ] );
    }
  }

}

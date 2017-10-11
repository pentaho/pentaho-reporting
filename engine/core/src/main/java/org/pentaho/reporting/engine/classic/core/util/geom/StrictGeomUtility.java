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

package org.pentaho.reporting.engine.classic.core.util.geom;

import org.pentaho.reporting.libraries.base.util.FloatDimension;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * This class is the heart of the alternative geometrics toolkit. It performs the neccessary conversions from and to the
 * AWT classes to the Strict-classes.
 *
 * @author Thomas Morgner
 */
public strictfp class StrictGeomUtility {
  public static final long MAX_AUTO = StrictGeomUtility.toInternalValue( 0x80000000000L );
  /**
   * This is the correction factor used to convert points into 'Micro-Points'.
   */
  private static final double CORRECTION_FACTOR = 100000.0;

  /**
   * Hidden, non usable constructor.
   */
  private StrictGeomUtility() {
  }

  /**
   * Creates a StrictDimension from the given AWT sizes.
   *
   * @param w
   *          the width in points (1/72th inch).
   * @param h
   *          the height in points (1/72th inch).
   * @return the created dimension object.
   */
  public static StrictDimension createDimension( final double w, final double h ) {
    return new StrictDimension( toInternalValue( w ), toInternalValue( h ) );
  }

  /**
   * Creates a StrictPoint from the given AWT coordinates.
   *
   * @param x
   *          the x coordinate in points (1/72th inch).
   * @param y
   *          the y coordinate in points (1/72th inch).
   * @return the created point object.
   */
  public static StrictPoint createPoint( final double x, final double y ) {
    return new StrictPoint( toInternalValue( x ), toInternalValue( y ) );
  }

  /**
   * Creates a StrictBounds object from the given AWT sizes.
   *
   * @param x
   *          the x coordinate in points (1/72th inch).
   * @param y
   *          the y coordinate in points (1/72th inch).
   * @param width
   *          the width in points (1/72th inch).
   * @param height
   *          the height in points (1/72th inch).
   * @return the created dimension object.
   */
  public static StrictBounds createBounds( final double x, final double y, final double width, final double height ) {
    return new StrictBounds( toInternalValue( x ), toInternalValue( y ), toInternalValue( width ),
        toInternalValue( height ) );
  }

  /**
   * Creates an AWT-Dimension2D object from the given strict sizes.
   *
   * @param width
   *          the width in micro points.
   * @param height
   *          the height in micro points.
   * @return the created dimension object.
   */
  public static Dimension2D createAWTDimension( final long width, final long height ) {
    return new FloatDimension( (float) ( width / CORRECTION_FACTOR ), (float) ( height / CORRECTION_FACTOR ) );
  }

  /**
   * Creates an AWT rectangle object from the given strict sizes.
   *
   * @param x
   *          the x coordinate in micro points.
   * @param y
   *          the y coordinate in micro points.
   * @param width
   *          the width in micro points.
   * @param height
   *          the height in micro points.
   * @return the created dimension object.
   */
  public static Rectangle2D createAWTRectangle( final long x, final long y, final long width, final long height ) {
    return new Rectangle2D.Double( x / CORRECTION_FACTOR, y / CORRECTION_FACTOR, width / CORRECTION_FACTOR, height
        / CORRECTION_FACTOR );
  }

  public static Rectangle2D createAWTRectangle( final StrictBounds bounds ) {
    return createAWTRectangle( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
  }

  /**
   * Converts the given AWT value into a strict value.
   *
   * @param value
   *          the AWT point value.
   * @return the internal micro point value.
   */
  public static long toInternalValue( final double value ) {
    final long rounded = StrictMath.round( value * 10000f );
    return StrictMath.round( rounded * CORRECTION_FACTOR / 10000.0 );
  }

  /**
   * Converts the given micro point value into an AWT value.
   *
   * @param value
   *          the micro point point value.
   * @return the AWT point value.
   */
  public static double toExternalValue( final long value ) {
    return ( value / CORRECTION_FACTOR );
  }

  public static double toFontMetricsValue( final long value ) {
    return ( value * 1000l / CORRECTION_FACTOR );
  }

  public static long fromFontMetricsValue( final long value ) {
    final double rawValue = value / 1000.0;
    return toInternalValue( rawValue );
  }

  public static long multiply( final long x, final long y ) {
    if ( x < y ) {
      return (long) ( x * ( y / CORRECTION_FACTOR ) );
    }

    return (long) ( y * ( x / CORRECTION_FACTOR ) );
  }
}

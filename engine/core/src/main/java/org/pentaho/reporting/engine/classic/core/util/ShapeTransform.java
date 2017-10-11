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

package org.pentaho.reporting.engine.classic.core.util;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import org.pentaho.reporting.libraries.base.util.FloatDimension;

/**
 * Utility class, which resizes or translates a Shape. The class contains special handlers for Rectangles and Lines.
 *
 * @author Thomas Morgner
 */
public final strictfp class ShapeTransform {
  // some constants for the cohenen-algorithmus
  /**
   * Flag for point lying left of clipping area.
   */
  public static final int LEFT = 0x01;
  /**
   * Flag for point lying between horizontal bounds of area.
   */
  public static final int H_CENTER = 0x02;
  /**
   * Flag for point lying right of clipping area.
   */
  public static final int RIGHT = 0x04;

  /**
   * Flag for point lying &quot;below&quot; clipping area.
   */
  public static final int BELOW = 0x10;
  /**
   * Flag for point lying between vertical bounds of clipping area.
   */
  public static final int V_CENTER = 0x20;
  /**
   * Flag for point lying &quot;above&quot; clipping area.
   */
  public static final int ABOVE = 0x40;

  /**
   * A simple way to handle rounding errors.
   */
  private static final double DELTA = 0.000001;
  /**
   * Mask for points which are inside.
   */
  public static final int INSIDE = H_CENTER | V_CENTER;
  /**
   * Mask for points which are outside.
   */
  public static final int OUTSIDE = LEFT | RIGHT | BELOW | ABOVE;

  /**
   * Default constructor.
   */
  private ShapeTransform() {
  }

  /**
   * Resizes a line. Instead of creating a GeneralPath (as AffineTransform's scale would do) we modify the line itself.
   *
   * @param line
   *          the line that should be scaled
   * @param width
   *          the new width of the line bounds
   * @param height
   *          the new height of the line bounds
   * @return the scale Line2D object.
   */
  private static Line2D resizeLine( final Line2D line, final double width, final double height ) {
    final Line2D newLine = getNormalizedLine( line );
    final Point2D p1 = newLine.getP1();
    final Point2D p2 = newLine.getP2();
    final double normPointX = ( p1.getX() - p2.getX() );
    final double normPointY = ( p1.getY() - p2.getY() );
    final double scaleX = ( normPointX == 0 ) ? 1 : width / Math.abs( normPointX );
    final double scaleY = ( normPointY == 0 ) ? 1 : height / Math.abs( normPointY );
    p2.setLocation( ( p2.getX() - p1.getX() ) * scaleX + p1.getX(), ( p2.getY() - p1.getY() ) * scaleY + p1.getY() );
    newLine.setLine( p1, p2 );
    return newLine;
  }

  /**
   * Normalize the line; the point with the lowest X is the primary point, if both points have the same X, that point
   * with the lowest Y value wins.
   *
   * @param line
   *          the original line
   * @return the normalized line
   */
  private static Line2D getNormalizedLine( final Line2D line ) {
    final Line2D lineClone = (Line2D) line.clone();

    final Point2D p1 = line.getP1();
    final Point2D p2 = line.getP2();
    if ( p1.getX() < p2.getX() ) {
      return lineClone;
    }
    if ( p1.getX() > p2.getX() ) {
      lineClone.setLine( p2, p1 );
      return lineClone;
    }
    if ( p1.getY() < p2.getY() ) {
      return lineClone;
    }
    lineClone.setLine( p2, p1 );
    return lineClone;
  }

  /**
   * Resizes a shape, so that the shape has the given width and height, but the origin of the shape does not change.
   * <p/>
   * Unlike the AffineTransform, this method tries to preserve the Shape's Type.
   *
   * @param s
   *          the shape
   * @param width
   *          the new width
   * @param height
   *          the new height
   * @return the resized shape.
   */
  public static Shape resizeShape( final Shape s, final float width, final float height ) {
    if ( s instanceof Line2D ) {
      return resizeLine( (Line2D) s, width, height );
    }
    if ( s instanceof RectangularShape ) {
      return resizeRect( (RectangularShape) s, width, height );
    }
    return transformShape( s, true, false, new FloatDimension( width, height ) );
  }

  /**
   * Resizes a rectangle. This works for real rectangles and produces funny results for RoundRects etc ..
   *
   * @param rectangularShape
   *          the rectangle
   * @param width
   *          the new width of the rectangle
   * @param height
   *          the new height of the rectangle.
   * @return the resized rectangle.
   */
  public static Shape resizeRect( final RectangularShape rectangularShape, final double width, final double height ) {
    final RectangularShape retval = (RectangularShape) rectangularShape.clone();
    retval.setFrame( retval.getX(), retval.getY(), width, height );
    return retval;
  }

  /**
   * Translates the given shape. The shape is translated to the origin supplied in <code>point</code>. If scaling is
   * requested, the shape will also be scaled using an AffineTransform.
   *
   * @param s
   *          the shape that should be transformed
   * @param scale
   *          true, if the shape should be scaled, false otherwise
   * @param keepAR
   *          true, if the scaled shape should keep the aspect ratio
   * @param width
   *          the target width.
   * @param height
   *          the target height.
   * @return the transformed shape
   */
  public static Shape transformShape( final Shape s, final boolean scale, final boolean keepAR, final double width,
      final double height ) {
    /**
     * Always scale to the maximum bounds ...
     */
    if ( scale ) {

      final Rectangle2D boundsShape = s.getBounds2D();
      final double w = boundsShape.getWidth();
      final double h = boundsShape.getHeight();
      double scaleX = 1;

      if ( w != 0 ) {
        scaleX = width / w;
      }

      double scaleY = 1;
      if ( h != 0 ) {
        scaleY = height / h;
      }

      if ( scaleX != 1 || scaleY != 1 ) {
        if ( s instanceof Line2D ) {
          return ShapeTransform.resizeLine( (Line2D) s, w * scaleX, h * scaleY );
        }

        if ( keepAR ) {
          final double scaleFact = Math.min( scaleX, scaleY );
          if ( s instanceof RectangularShape ) {
            return ShapeTransform.resizeRect( (RectangularShape) s, w * scaleFact, h * scaleFact );
          }
          return performDefaultTransformation( s, scaleFact, scaleFact );
        } else {
          if ( s instanceof RectangularShape ) {
            return ShapeTransform.resizeRect( (RectangularShape) s, w * scaleX, h * scaleY );
          }
          return performDefaultTransformation( s, scaleX, scaleY );
        }
      }
    }
    return s;
  }

  /**
   * Translates the given shape. The shape is translated to the origin supplied in <code>point</code>. If scaling is
   * requested, the shape will also be scaled using an AffineTransform.
   *
   * @param s
   *          the shape that should be transformed
   * @param scale
   *          true, if the shape should be scaled, false otherwise
   * @param keepAR
   *          true, if the scaled shape should keep the aspect ratio
   * @param dim
   *          the target dimension.
   * @return the transformed shape
   */
  public static Shape transformShape( final Shape s, final boolean scale, final boolean keepAR, final Dimension2D dim ) {
    return transformShape( s, scale, keepAR, dim.getWidth(), dim.getHeight() );
  }

  /**
   * Clips the given shape to the given bounds. If the shape is a Line2D, manual clipping is performed, as the built in
   * Area does not handle lines.
   *
   * @param s
   *          the shape to be clipped
   * @param bounds
   *          the bounds to which the shape should be clipped
   * @return the clipped shape.
   */
  public static Shape performCliping( final Shape s, final Rectangle2D bounds ) {
    if ( s instanceof Line2D ) {
      final Line2D line = (Line2D) s;
      final Point2D[] clipped =
          getClipped( line.getX1(), line.getY1(), line.getX2(), line.getY2(), -DELTA, DELTA + bounds.getWidth(),
              -DELTA, DELTA + bounds.getHeight() );
      if ( clipped == null ) {
        return new GeneralPath();
      }
      return new Line2D.Float( clipped[0], clipped[1] );
    }

    final Rectangle2D boundsCorrected = bounds.getBounds2D();
    boundsCorrected.setRect( -DELTA, -DELTA, DELTA + boundsCorrected.getWidth(), DELTA + boundsCorrected.getHeight() );
    final Area a = new Area( boundsCorrected );
    if ( a.isEmpty() ) {
      // don't clip ... Area does not like lines
      // operations with lines always result in an empty Bounds:(0,0,0,0) area
      return new GeneralPath();
    }

    final Area clipArea = new Area( s );
    a.intersect( clipArea );
    return a;

  }

  /**
   * Scales a given shape. The shape is first normalized, then scaled and finally brought back into its original
   * position.
   *
   * @param shape
   *          the shape to be scaled
   * @param scaleX
   *          the horizontal scaling factor
   * @param scaleY
   *          the vertical scaling factor
   * @return the scaled shape
   */
  private static Shape performDefaultTransformation( final Shape shape, final double scaleX, final double scaleY ) {
    /**
     * Apply the normalisation shape transform ... bring the shape to pos (0,0)
     */
    final Rectangle2D bounds = shape.getBounds2D();
    final AffineTransform translateTransform =
        AffineTransform.getTranslateInstance( 0 - bounds.getX(), 0 - bounds.getY() );
    // apply normalisation translation ...
    final Shape translatedShape = translateTransform.createTransformedShape( shape );

    final AffineTransform scaleTransform = AffineTransform.getScaleInstance( scaleX, scaleY );
    // apply scaling ...
    final Shape scaledShape = scaleTransform.createTransformedShape( translatedShape );

    // now retranslate the shape to its original position ...
    final AffineTransform translateBackTransform = AffineTransform.getTranslateInstance( bounds.getX(), bounds.getY() );
    return translateBackTransform.createTransformedShape( scaledShape );
  }

  /**
   * Translates a se. Special care is taken to preserve the shape's original class, if the shape is a rectangle or a
   * line.
   *
   * @param s
   *          the shape
   * @param x
   *          the x coordinate where the shape is translated to
   * @param y
   *          the y coordinate where the shape is translated to
   * @return the translated shape
   */
  public static Shape translateShape( final Shape s, final double x, final double y ) {
    if ( s instanceof RectangularShape ) {
      final RectangularShape rect = (RectangularShape) s;
      final RectangularShape retval = (RectangularShape) rect.clone();
      retval.setFrame( retval.getX() + x, retval.getY() + y, retval.getWidth(), retval.getHeight() );
      return retval;
    }
    if ( s instanceof Line2D ) {
      final Line2D line = (Line2D) s;
      final Line2D retval = (Line2D) line.clone();
      retval.setLine( retval.getX1() + x, retval.getY1() + y, retval.getX2() + x, retval.getY2() + y );
      return retval;
    }

    final AffineTransform af = AffineTransform.getTranslateInstance( x, y );
    return af.createTransformedShape( s );
  }

  /**
   * Calculate the clipping points of a line with a rectangle.
   *
   * @param x1
   *          starting x of line
   * @param y1
   *          starting y of line
   * @param x2
   *          ending x of line
   * @param y2
   *          ending y of line
   * @param xmin
   *          lower left x of rectangle
   * @param xmax
   *          upper right x of rectangle
   * @param ymin
   *          lower left y of rectangle
   * @param ymax
   *          upper right y of rectangle
   * @return <code>null</code> (does not clip) or array of two points
   */
  public static Point2D[] getClipped( final double x1, final double y1, final double x2, final double y2,
      final double xmin, final double xmax, final double ymin, final double ymax ) {
    int mask1 = 0; // position mask for first point
    if ( x1 < xmin ) {
      mask1 |= LEFT;
    } else if ( x1 > xmax ) {
      mask1 |= RIGHT;
    } else {
      mask1 |= H_CENTER;
    }
    if ( y1 < ymin ) {
      // btw: I know that in AWT y runs from down but I more used to
      // y pointing up and it makes no difference for the algorithms
      mask1 |= BELOW;
    } else if ( y1 > ymax ) {
      mask1 |= ABOVE;
    } else {
      mask1 |= V_CENTER;
    }

    int mask2 = 0; // position mask for second point
    if ( x2 < xmin ) {
      mask2 |= LEFT;
    } else if ( x2 > xmax ) {
      mask2 |= RIGHT;
    } else {
      mask2 |= H_CENTER;
    }
    if ( y2 < ymin ) {
      mask2 |= BELOW;
    } else if ( y2 > ymax ) {
      mask2 |= ABOVE;
    } else {
      mask2 |= V_CENTER;
    }

    final int mask = mask1 | mask2;

    if ( ( mask & OUTSIDE ) == 0 ) {
      // fine. everything's internal
      final Point2D[] ret = new Point2D[2];
      ret[0] = new Point2D.Double( x1, y1 );
      ret[1] = new Point2D.Double( x2, y2 );
      return ret;
    } else if ( ( mask & ( H_CENTER | LEFT ) ) == 0 || // everything's right
        ( mask & ( H_CENTER | RIGHT ) ) == 0 || // everything's left
        ( mask & ( V_CENTER | BELOW ) ) == 0 || // everything's above
        ( mask & ( V_CENTER | ABOVE ) ) == 0 ) { // everything's below
      // nothing to do
      return null;
    } else {
      // need clipping
      return getClipped( x1, y1, mask1, x2, y2, mask2, xmin, xmax, ymin, ymax );
    }
  }

  /**
   * Calculate the clipping points of a line with a rectangle.
   *
   * @param x1
   *          starting x of line
   * @param y1
   *          starting y of line
   * @param mask1
   *          clipping info mask for starting point
   * @param x2
   *          ending x of line
   * @param y2
   *          ending y of line
   * @param mask2
   *          clipping info mask for ending point
   * @param xmin
   *          lower left x of rectangle
   * @param ymin
   *          lower left y of rectangle
   * @param xmax
   *          upper right x of rectangle
   * @param ymax
   *          upper right y of rectangle
   * @return <code>null</code> (does not clip) or array of two points
   */
  private static Point2D[] getClipped( final double x1, final double y1, final int mask1, final double x2,
      final double y2, final int mask2, final double xmin, final double xmax, final double ymin, final double ymax ) {
    final int mask = mask1 ^ mask2;
    Point2D p1 = null;

    if ( mask1 == INSIDE ) {
      // point 1 is internal
      p1 = new Point2D.Double( x1, y1 );
      if ( mask == 0 ) {
        // both masks are the same, so the second point is inside, too
        final Point2D[] ret = new Point2D[2];
        ret[0] = p1;
        ret[1] = new Point2D.Double( x2, y2 );
        return ret;
      }
    } else if ( mask2 == INSIDE ) {
      // point 2 is internal
      p1 = new Point2D.Double( x2, y2 );
    }

    if ( ( mask & LEFT ) != 0 ) {
      // System.out.println("Trying left");
      // try to calculate intersection with left line
      final Point2D p = intersect( x1, y1, x2, y2, xmin, ymin, xmin, ymax );
      if ( p != null ) {
        if ( p1 == null ) {
          p1 = p;
        } else {
          final Point2D[] ret = new Point2D[2];
          ret[0] = p1;
          ret[1] = p;
          return ret;
        }
      }
    }
    if ( ( mask & RIGHT ) != 0 ) {
      // System.out.println("Trying right");
      // try to calculate intersection with left line
      final Point2D p = intersect( x1, y1, x2, y2, xmax, ymin, xmax, ymax );
      if ( p != null ) {
        if ( p1 == null ) {
          p1 = p;
        } else {
          final Point2D[] ret = new Point2D[2];
          ret[0] = p1;
          ret[1] = p;
          return ret;
        }
      }
    }
    if ( mask1 == ( LEFT | BELOW ) || mask1 == ( RIGHT | BELOW ) ) {
      // for exactly these two special cases use different sequence!

      if ( ( mask & ABOVE ) != 0 ) {
        // System.out.println("Trying top");
        // try to calculate intersection with lower line
        final Point2D p = intersect( x1, y1, x2, y2, xmin, ymax, xmax, ymax );
        if ( p != null ) {
          if ( p1 == null ) {
            p1 = p;
          } else {
            final Point2D[] ret = new Point2D[2];
            ret[0] = p1;
            ret[1] = p;
            return ret;
          }
        }
      }
      if ( ( mask & BELOW ) != 0 ) {
        // System.out.println("Trying bottom");
        // try to calculate intersection with lower line
        final Point2D p = intersect( x1, y1, x2, y2, xmin, ymin, xmax, ymin );
        if ( p != null && p1 != null ) {
          final Point2D[] ret = new Point2D[2];
          ret[0] = p1;
          ret[1] = p;
          return ret;
        }
      }
    } else {
      if ( ( mask & BELOW ) != 0 ) {
        // System.out.println("Trying bottom");
        // try to calculate intersection with lower line
        final Point2D p = intersect( x1, y1, x2, y2, xmin, ymin, xmax, ymin );
        if ( p != null ) {
          if ( p1 == null ) {
            p1 = p;
          } else {
            final Point2D[] ret = new Point2D[2];
            ret[0] = p1;
            ret[1] = p;
            return ret;
          }
        }
      }
      if ( ( mask & ABOVE ) != 0 ) {
        // System.out.println("Trying top");
        // try to calculate intersection with lower line
        final Point2D p = intersect( x1, y1, x2, y2, xmin, ymax, xmax, ymax );
        if ( p != null && p1 != null ) {
          final Point2D[] ret = new Point2D[2];
          ret[0] = p1;
          ret[1] = p;
          return ret;
        }
      }
    }

    // no (or not enough) intersections found
    return null;
  }

  /**
   * Intersect two lines.
   *
   * @param x11
   *          starting x of 1st line
   * @param y11
   *          starting y of 1st line
   * @param x12
   *          ending x of 1st line
   * @param y12
   *          ending y of 1st line
   * @param x21
   *          starting x of 2nd line
   * @param y21
   *          starting y of 2nd line
   * @param x22
   *          ending x of 2nd line
   * @param y22
   *          ending y of 2nd line
   * @return intersection point or <code>null</code>
   */
  private static Point2D intersect( final double x11, final double y11, final double x12, final double y12,
      final double x21, final double y21, final double x22, final double y22 ) {
    final double dx1 = x12 - x11;
    final double dy1 = y12 - y11;
    final double dx2 = x22 - x21;
    final double dy2 = y22 - y21;
    final double det = ( dx2 * dy1 - dy2 * dx1 );

    if ( det != 0.0 ) {
      final double mu = ( ( x11 - x21 ) * dy1 - ( y11 - y21 ) * dx1 ) / det;
      if ( mu >= 0.0 && mu <= 1.0 ) {
        return new Point2D.Double( x21 + mu * dx2, y21 + mu * dy2 );
      }
    }

    return null;
  }

}

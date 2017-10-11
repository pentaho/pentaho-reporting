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

import java.io.Serializable;

/**
 * The StrictBounds class is a replacement for the Rectangle2D classes. This class uses integer mathematics instead of
 * floating point values to achive a higher degree of stability.
 *
 * @author Thomas Morgner
 */
public class StrictBounds implements Serializable, Cloneable {
  /**
   * The x-coordinate of the upper left corner.
   */
  private long x;
  /**
   * The y-coordinate of the upper left corner.
   */
  private long y;
  /**
   * The width of this rectangle.
   */
  private long width;
  /**
   * The height of this rectangle.
   */
  private long height;
  /**
   * A flag indicating whether attempts to change this rectangle should trigger Exceptions.
   */
  private boolean locked;

  /**
   * DefaultConstructor.
   */
  public StrictBounds() {
  }

  /**
   * Creates a StrictBounds object with the given coordinates, width and height.
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width of the rectangle
   * @param height
   *          the height of the rectangle
   */
  public StrictBounds( final long x, final long y, final long width, final long height ) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * Checks whether this bounds object is locked.
   *
   * @return true, if the bounds are locked and therefore immutable, false otherwise.
   */
  public boolean isLocked() {
    return locked;
  }

  /**
   * Returns a copy of this bounds object which cannot be modified anymore.
   *
   * @return a locked copy.
   */
  public StrictBounds getLockedInstance() {
    if ( locked ) {
      return this;
    }

    final StrictBounds retval = (StrictBounds) clone();
    retval.locked = true;
    return retval;
  }

  /**
   * Returns a copy of this bounds object which can be modified later.
   *
   * @return an unlocked copy.
   */
  public StrictBounds getUnlockedInstance() {
    final StrictBounds retval = (StrictBounds) clone();
    retval.locked = false;
    return retval;
  }

  /**
   * Sets the location and size of this <code>StrictBounds</code> to the specified double values.
   *
   * @param bounds
   *          the bounds from where to copy all properties <code>StrictBounds</code>
   */
  public void setRect( final StrictBounds bounds ) {
    if ( locked ) {
      throw new IllegalStateException( "This object is locked" );
    }
    this.x = bounds.x;
    this.y = bounds.y;
    this.width = bounds.width;
    this.height = bounds.height;
  }

  /**
   * Sets the location and size of this <code>StrictBounds</code> to the specified double values.
   *
   * @param x
   *          the coordinates to which to set the location of the upper left corner of this <code>StrictBounds</code>
   * @param y
   *          the coordinates to which to set the location of the upper left corner of this <code>StrictBounds</code>
   * @param w
   *          the value to use to set the width of this <code>StrictBounds</code>
   * @param h
   *          the value to use to set the height of this <code>StrictBounds</code>
   */
  public void setRect( final long x, final long y, final long w, final long h ) {
    if ( locked ) {
      throw new IllegalStateException( "This object is locked" );
    }
    this.x = x;
    this.y = y;
    this.width = w;
    this.height = h;
  }

  /**
   * Returns the height of the framing rectangle in micro points.
   *
   * @return the height of the framing rectangle.
   */
  public long getHeight() {
    return height;
  }

  /**
   * Returns the width of the framing rectangle in micro points.
   *
   * @return the width of the framing rectangle.
   */
  public long getWidth() {
    return width;
  }

  /**
   * Returns the X coordinate of the upper left corner of the framing rectangle in micro points.
   *
   * @return the x coordinate of the upper left corner of the framing rectangle.
   */
  public long getX() {
    return x;
  }

  /**
   * Returns the Y coordinate of the upper left corner of the framing rectangle in micro points.
   *
   * @return the y coordinate of the upper left corner of the framing rectangle.
   */
  public long getY() {
    return y;
  }

  /**
   * Determines whether the <code>RectangularShape</code> is empty. When the <code>RectangularShape</code> is empty, it
   * encloses no area.
   *
   * @return <code>true</code> if the <code>RectangularShape</code> is empty; <code>false</code> otherwise.
   */
  public boolean isEmpty() {
    return width == 0 || height == 0;
  }

  /**
   * Returns a copy of this bounds object. This method will never throw a 'CloneNotSupportedException'.
   *
   * @return the cloned instance.
   */
  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new InternalError( "Clone must always be supported." );
    }
  }

  /**
   * Checks whether this rectangle contains the given point.
   *
   * @param x
   *          the x-coordinate of the point.
   * @param y
   *          the y-coordinate of the point.
   * @return true, if the point is inside or directly on the border of this rectangle, false otherwise.
   */
  public boolean contains( final long x, final long y ) {
    if ( x < this.x ) {
      return false;
    }
    if ( y < this.y ) {
      return false;
    }
    if ( x > ( this.x + this.width ) ) {
      return false;
    }
    return y <= ( this.y + this.height );
  }

  /**
   * Checks whether the given rectangle1 fully contains rectangle 2 (even if rectangle 2 has a height or width of
   * zero!).
   *
   * @param rect1
   *          the first rectangle.
   * @param rect2
   *          the second rectangle.
   * @return true, if the rectangles intersect each other, false otherwise.
   */
  public static boolean intersects( final StrictBounds rect1, final StrictBounds rect2 ) {

    final double x0 = rect1.getX();
    final double y0 = rect1.getY();

    final double x = rect2.getX();
    final double width = rect2.getWidth();
    final double y = rect2.getY();
    final double height = rect2.getHeight();
    return ( x + width >= x0 && y + height >= y0 && x <= x0 + rect1.getWidth() && y <= y0 + rect1.getHeight() );
  }

  /**
   * Adds the given bounds to this bounds instance. The resulting rectangle will fully contain both rectangles.
   *
   * @param bounds
   *          the rectangle that should be added.
   */
  public void add( final StrictBounds bounds ) {
    if ( locked ) {
      throw new IllegalStateException( "This object is locked" );
    }

    final long x1 = Math.min( getX(), bounds.getX() );
    final long y1 = Math.min( getY(), bounds.getY() );
    final long x2 = Math.max( getX() + getWidth(), bounds.getX() + bounds.getWidth() );
    final long y2 = Math.max( getY() + getHeight(), bounds.getY() + bounds.getHeight() );
    setRect( x1, y1, Math.max( 0, x2 - x1 ), Math.max( 0, y2 - y1 ) );
  }

  /**
   * Adds the given bounds to this bounds instance. The resulting rectangle will fully contain both rectangles.
   */
  public void add( final long x, final long y, final long width, final long height ) {
    if ( locked ) {
      throw new IllegalStateException( "This object is locked" );
    }

    final long x1 = Math.min( getX(), x );
    final long y1 = Math.min( getY(), y );
    final long x2 = Math.max( getX() + getWidth(), x + width );
    final long y2 = Math.max( getY() + getHeight(), y + height );
    setRect( x1, y1, Math.max( 0, x2 - x1 ), Math.max( 0, y2 - y1 ) );
  }

  /**
   * Intersects this rectangle with the given bounds. The resulting rectangle will cover the space, that is occupied by
   * both rectangles at the same time.
   *
   * @param bounds
   *          the other rectangle.
   * @return the resulting intersection.
   */
  public StrictBounds createIntersection( final StrictBounds bounds ) {
    final long x1 = Math.max( getX(), bounds.getX() );
    final long y1 = Math.max( getY(), bounds.getY() );
    final long x2 = Math.min( getX() + getWidth(), bounds.getX() + bounds.getWidth() );
    final long y2 = Math.min( getY() + getHeight(), bounds.getY() + bounds.getHeight() );

    return new StrictBounds( x1, y1, Math.max( 0, x2 - x1 ), Math.max( 0, y2 - y1 ) );
  }

  /**
   * Checks whether the given rectangle1 fully contains rectangle 2 (even if rectangle 2 has a height or width of
   * zero!).
   *
   * @param rect1
   *          the first rectangle.
   * @param rect2
   *          the second rectangle.
   * @return A boolean.
   */
  public static boolean contains( final StrictBounds rect1, final StrictBounds rect2 ) {

    final long x0 = rect1.getX();
    final long y0 = rect1.getY();
    final long x = rect2.getX();
    final long y = rect2.getY();
    final long w = rect2.getWidth();
    final long h = rect2.getHeight();

    return ( ( x >= x0 ) && ( y >= y0 ) && ( ( x + w ) <= ( x0 + rect1.getWidth() ) ) && ( ( y + h ) <= ( y0 + rect1
        .getHeight() ) ) );

  }

  /**
   * Checks whether the given object is a StrictBounds instance convering the same area as these bounds.
   *
   * @param o
   *          the other object.
   * @return true, if the other object is equal to this object, false otherwise.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof StrictBounds ) ) {
      return false;
    }

    final StrictBounds strictBounds = (StrictBounds) o;

    if ( height != strictBounds.height ) {
      return false;
    }
    if ( width != strictBounds.width ) {
      return false;
    }
    if ( x != strictBounds.x ) {
      return false;
    }
    return y == strictBounds.y;

  }

  /**
   * Computes the hashcode for this rectangle.
   *
   * @return the computed hashcode.
   */
  public int hashCode() {
    int result = (int) ( x ^ ( x >>> 32 ) );
    result = 29 * result + (int) ( y ^ ( y >>> 32 ) );
    result = 29 * result + (int) ( width ^ ( width >>> 32 ) );
    result = 29 * result + (int) ( height ^ ( height >>> 32 ) );
    return result;
  }

  /**
   * Returns a string representation of these bounds.
   *
   * @return the string representing this object.
   */
  public String toString() {
    return new StringBuffer( 100 ).append( "org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds{" )
        .append( "x=" ).append( x ).append( ", y=" ).append( y ).append( ", width=" ).append( width ).append(
            ", height=" ).append( height ).append( '}' ).toString();
  }

  /**
   * Creates a union from this and the given rectangle. This is similiar to calling 'add'. Calling this method does not
   * modify the original and there are no guarantees, that the resulting rectangle has a positive width or height.
   *
   * @param bg
   *          the other rectangle.
   * @return the resulting union rectangle.
   */
  public StrictBounds createUnion( final StrictBounds bg ) {
    final long x = Math.min( getX(), bg.getX() );
    final long y = Math.min( getY(), bg.getY() );
    final long w = Math.max( getX() + getWidth(), bg.getX() + bg.getWidth() ) - x;
    final long h = Math.max( getY() + getHeight(), bg.getY() + bg.getHeight() ) - y;
    return new StrictBounds( x, y, w, h );
  }
}

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
 * A StrictPoint class represents a coordinate in the report layout. It is similiar to the {@link java.awt.geom.Point2D}
 * class, but uses micro-points instead of doubles.
 *
 * @author Thomas Morgner
 */
public class StrictPoint implements Serializable, Cloneable {
  /**
   * The x-Coordinate.
   */
  private long x;
  /**
   * The y-coordinate.
   */
  private long y;
  /**
   * A flag indicating whether this object is mutable.
   */
  private boolean locked;

  /**
   * DefaultConstructor.
   */
  public StrictPoint() {
  }

  /**
   * Creates a StrictBounds object with the given coordinates, width and height.
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  public StrictPoint( final long x, final long y ) {
    this.x = x;
    this.y = y;
  }

  /**
   * Returns the X coordinate of this <code>StrictPoint</code> in micro points.
   *
   * @return the X coordinate of this <code>StrictPoint</code>.
   */
  public long getX() {
    return x;
  }

  /**
   * Returns the Y coordinate of this <code>StrictPoint</code> in micro points.
   *
   * @return the Y coordinate of this <code>StrictPoint</code>.
   */
  public long getY() {
    return y;
  }

  /**
   * Sets the location of this <code>StrictPoint</code> to the specified coordinates.
   *
   * @param x
   *          the coordinates of this <code>StrictPoint</code>
   * @param y
   *          the coordinates of this <code>StrictPoint</code>
   */
  public void setLocation( final long x, final long y ) {
    if ( locked ) {
      throw new IllegalStateException();
    }

    this.x = x;
    this.y = y;
  }

  /**
   * Checks whether this point object is locked.
   *
   * @return true, if the point is locked and therefore immutable, false otherwise.
   */
  public boolean isLocked() {
    return locked;
  }

  /**
   * Returns a copy of this bounds object which cannot be modified anymore.
   *
   * @return a locked copy.
   */
  public StrictPoint getLockedInstance() {
    if ( locked ) {
      return this;
    }

    final StrictPoint retval = (StrictPoint) clone();
    retval.locked = true;
    return retval;
  }

  /**
   * Returns a copy of this bounds object which can be modified later.
   *
   * @return an unlocked copy.
   */
  public StrictPoint getUnlockedInstance() {
    final StrictPoint retval = (StrictPoint) clone();
    retval.locked = false;
    return retval;
  }

  /**
   * Returns a copy of this Point object. This method will never throw a 'CloneNotSupportedException'.
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
   * Returns a string representation of these bounds.
   *
   * @return the string representing this object.
   */
  public String toString() {
    return "org.pentaho.reporting.engine.classic.core.util.geom.StrictPoint{" + "x=" + x + ", y=" + y + '}';
  }

  /**
   * Checks whether the given object is a StrictPoint instance sharing the same coordinates as this point.
   *
   * @param o
   *          the other object.
   * @return true, if the other object is equal to this object, false otherwise.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final StrictPoint that = (StrictPoint) o;

    if ( x != that.x ) {
      return false;
    }
    if ( y != that.y ) {
      return false;
    }

    return true;
  }

  /**
   * Computes the hashcode for this point.
   *
   * @return the computed hashcode.
   */
  public int hashCode() {
    int result = (int) ( x ^ ( x >>> 32 ) );
    result = 29 * result + (int) ( y ^ ( y >>> 32 ) );
    return result;
  }
}

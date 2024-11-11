/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.util.geom;

import java.io.Serializable;

/**
 * A dimension object that uses micro-points as units of measurement.
 *
 * @author Thomas Morgner
 */
public class StrictDimension implements Serializable, Cloneable {
  /**
   * The width.
   */
  private long width;
  /**
   * The height.
   */
  private long height;
  /**
   * A flag indicating whether this object is mutable.
   */
  private boolean locked;

  /**
   * Default-Constructor.
   */
  public StrictDimension() {
  }

  /**
   * Creates a new dimension object with the given width and height. The object is not locked.
   *
   * @param width
   *          the width.
   * @param height
   *          the height.
   */
  public StrictDimension( final long width, final long height ) {
    this.width = width;
    this.height = height;
  }

  /**
   * Checks whether this instance is locked.
   *
   * @return true, if the dimension object is locked, false otherwise.
   */
  public boolean isLocked() {
    return locked;
  }

  /**
   * Returns a copy of this dimension which cannot be modified anymore.
   *
   * @return a locked copy.
   */
  public StrictDimension getLockedInstance() {
    if ( locked ) {
      return this;
    }

    final StrictDimension retval = (StrictDimension) clone();
    retval.locked = true;
    return retval;
  }

  /**
   * Returns a copy of this dimension which can be modified later.
   *
   * @return an unlocked copy.
   */
  public StrictDimension getUnlockedInstance() {
    final StrictDimension retval = (StrictDimension) clone();
    retval.locked = false;
    return retval;
  }

  /**
   * Sets the size of this <code>Dimension</code> object to the specified width and height.
   *
   * @param width
   *          the new width for the <code>Dimension</code> object
   * @param height
   *          the new height for the <code>Dimension</code> object
   * @throws IllegalStateException
   *           if the dimension object is locked
   */
  public void setSize( final long width, final long height ) {
    if ( locked ) {
      throw new IllegalStateException( "This object is locked" );
    }

    this.width = width;
    this.height = height;
  }

  /**
   * Returns the height of this dimension object.
   *
   * @return the height.
   */
  public long getHeight() {
    return height;
  }

  /**
   * Updates the height of this dimension object.
   *
   * @param height
   *          the new height, given in micro-points.
   */
  public void setHeight( final long height ) {
    if ( locked ) {
      throw new IllegalStateException( "This object is locked" );
    }
    this.height = height;
  }

  /**
   * Returns the width of this dimension object.
   *
   * @return the width.
   */
  public long getWidth() {
    return width;
  }

  /**
   * Updates the width of this dimension object.
   *
   * @param width
   *          the new width, given in micro-points.
   */
  public void setWidth( final long width ) {
    if ( locked ) {
      throw new IllegalStateException( "This object is locked" );
    }
    this.width = width;
  }

  /**
   * Creates a copy of this object. This method is guaranteed to never throw a CloneNotSupportedException.
   *
   * @return the cloned copy.
   */
  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new InternalError( "Clone must always be supported." );
    }
  }

  /**
   * Returns a String representation of this dimension object.
   *
   * @return a string describing the object.
   */
  public String toString() {
    return "org.pentaho.reporting.engine.classic.core.util.geom.StrictDimension{" + "width=" + width + ", height="
        + height + '}';
  }

  /**
   * Checks whether the given object is a StrictDimension instance convering the same area as this dimension.
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

    final StrictDimension that = (StrictDimension) o;

    if ( height != that.height ) {
      return false;
    }
    if ( width != that.width ) {
      return false;
    }

    return true;
  }

  /**
   * Computes the hashcode for this dimension.
   *
   * @return the computed hashcode.
   */
  public int hashCode() {
    int result = (int) ( width ^ ( width >>> 32 ) );
    result = 29 * result + (int) ( height ^ ( height >>> 32 ) );
    return result;
  }
}

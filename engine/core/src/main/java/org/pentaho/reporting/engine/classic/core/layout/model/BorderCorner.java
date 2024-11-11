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


package org.pentaho.reporting.engine.classic.core.layout.model;

/**
 * Creation-Date: 03.04.2007, 13:57:52
 *
 * @author Thomas Morgner
 */
public final class BorderCorner {
  public static final BorderCorner EMPTY = new BorderCorner( 0, 0 );

  private long width;
  private long height;

  public BorderCorner( final long width, final long height ) {
    this.width = width;
    this.height = height;
  }

  public long getWidth() {
    return width;
  }

  public long getHeight() {
    return height;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final BorderCorner that = (BorderCorner) o;

    if ( height != that.height ) {
      return false;
    }
    if ( width != that.width ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = (int) ( width ^ ( width >>> 32 ) );
    result = 29 * result + (int) ( height ^ ( height >>> 32 ) );
    return result;
  }

  public String toString() {
    return "BorderCorner{" + "width=" + width + ", height=" + height + '}';
  }
}

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

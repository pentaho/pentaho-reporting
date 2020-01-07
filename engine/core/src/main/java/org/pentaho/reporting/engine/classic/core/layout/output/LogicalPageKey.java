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
 * Copyright (c) 2001 - 2019 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.output;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Creation-Date: 10.11.2006, 13:04:36
 *
 * @author Thomas Morgner
 */
public final class LogicalPageKey implements Serializable {
  private int position;
  private int width;
  private int height;
  private PhysicalPageKey[] physicalPageKeys;

  public LogicalPageKey( final int position, final int width, final int height ) {
    this.position = position;
    this.width = width;
    this.height = height;
    this.physicalPageKeys = new PhysicalPageKey[width * height];

    final int pageKeyCount = physicalPageKeys.length;
    for ( int i = 0; i < pageKeyCount; i++ ) {
      physicalPageKeys[i] = new PhysicalPageKey( this, i % width, i / width );
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getPosition() {
    return position;
  }

  public PhysicalPageKey getPage( final int x, final int y ) {
    return physicalPageKeys[x + y * width];
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final LogicalPageKey that = (LogicalPageKey) o;

    if ( height != that.height ) {
      return false;
    }
    if ( position != that.position ) {
      return false;
    }
    if ( width != that.width ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = position;
    result = 29 * result + width;
    result = 29 * result + height;
    return result;
  }

  @Override
  public String toString() {
    return "LogicalPageKey{ position="
      + "position=" + position
      + ", width=" + width
      + ", height=" + height
      + ", physicalPageKeys=" + Arrays.toString( physicalPageKeys )
      + '}';
  }
}

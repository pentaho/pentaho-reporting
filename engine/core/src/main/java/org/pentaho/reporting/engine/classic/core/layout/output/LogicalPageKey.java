/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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

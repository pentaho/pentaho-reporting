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

/**
 * A physical page-key identifies a generated page.
 *
 * @author Thomas Morgner
 */
public final class PhysicalPageKey implements Serializable {
  private LogicalPageKey logicalPage;
  private int x;
  private int y;

  public PhysicalPageKey( final LogicalPageKey logicalPage, final int x, final int y ) {
    if ( logicalPage == null ) {
      throw new NullPointerException();
    }
    this.x = x;
    this.y = y;
    this.logicalPage = logicalPage;
  }

  public LogicalPageKey getLogicalPage() {
    return logicalPage;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getSequentialPageNumber() {
    final int logPosition = logicalPage.getPosition();
    return logPosition * logicalPage.getWidth() * logicalPage.getHeight() + x + y * logicalPage.getWidth();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final PhysicalPageKey that = (PhysicalPageKey) o;

    if ( x != that.x ) {
      return false;
    }
    if ( y != that.y ) {
      return false;
    }
    if ( !logicalPage.equals( that.logicalPage ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = logicalPage.hashCode();
    result = 29 * result + x;
    result = 29 * result + y;
    return result;
  }

  @Override
  public String toString() {
    return "PhysicalPageKey{"
      + "logicalPage=[" + logicalPage.getPosition()
      + "], x=" + x
      + ", y=" + y
      + '}';
  }
}

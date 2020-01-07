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

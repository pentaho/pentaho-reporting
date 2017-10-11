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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

/**
 * The TableRectangle contains GridCoordinates for the tables. The rectangle contains x- and y-cuts, so as long as the
 * cell is not empty (width==0, height==0), x1 and x2 will not have the same value.
 */
public class TableRectangle {
  private int x1;
  private int y1;
  private int x2;
  private int y2;

  public TableRectangle() {
  }

  @Deprecated
  public TableRectangle( final int x1, final int x2, final int y1, final int y2 ) {
    setRect( x1, y1, x2, y2 );
  }

  public int getX1() {
    return x1;
  }

  public int getX2() {
    return x2;
  }

  public int getY1() {
    return y1;
  }

  public int getY2() {
    return y2;
  }

  public void setRect( final int x1, final int y1, final int x2, final int y2 ) {
    if ( x1 > x2 ) {
      throw new IllegalArgumentException( "x1 is greater than x2 - the rectangle would have negative content." );
    }
    if ( y1 > y2 ) {
      throw new IllegalArgumentException( "y1 is greater than y2 - the rectangle would have negative content." );
    }
    this.x1 = x1;
    this.x2 = x2;
    this.y1 = y1;
    this.y2 = y2;
  }

  public boolean isOrigin( final int x, final int y ) {
    return ( x == x1 && y == y1 );
  }

  public int getRowSpan() {
    return y2 - y1;
  }

  public int getColumnSpan() {
    return x2 - x1;
  }

  public String toString() {
    return "org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle{" + "x1=" + x1 + ", y1="
        + y1 + ", x2=" + x2 + ", y2=" + y2 + '}';
  }
}

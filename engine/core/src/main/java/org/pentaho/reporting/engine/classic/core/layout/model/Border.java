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
 * Creation-Date: 03.04.2007, 13:54:17
 *
 * @author Thomas Morgner
 */
public final class Border implements Cloneable {
  public static final Border EMPTY_BORDER = new Border( BorderEdge.EMPTY, BorderEdge.EMPTY, BorderEdge.EMPTY,
      BorderEdge.EMPTY, BorderEdge.EMPTY, BorderCorner.EMPTY, BorderCorner.EMPTY, BorderCorner.EMPTY,
      BorderCorner.EMPTY );

  private BorderEdge top;
  private BorderEdge left;
  private BorderEdge bottom;
  private BorderEdge right;
  private BorderEdge splittingEdge;

  private BorderCorner topLeft;
  private BorderCorner topRight;
  private BorderCorner bottomLeft;
  private BorderCorner bottomRight;
  private Boolean empty;
  private Boolean sameForAllSides;

  public Border( final BorderEdge top, final BorderEdge left, final BorderEdge bottom, final BorderEdge right,
      final BorderEdge splittingEdge, final BorderCorner topLeft, final BorderCorner topRight,
      final BorderCorner bottomLeft, final BorderCorner bottomRight ) {
    this.top = top;
    this.left = left;
    this.bottom = bottom;
    this.right = right;
    this.splittingEdge = splittingEdge;
    this.topLeft = topLeft;
    this.topRight = topRight;
    this.bottomLeft = bottomLeft;
    this.bottomRight = bottomRight;
  }

  public BorderEdge getTop() {
    return top;
  }

  public BorderEdge getLeft() {
    return left;
  }

  public BorderEdge getBottom() {
    return bottom;
  }

  public BorderEdge getRight() {
    return right;
  }

  public BorderEdge getSplittingEdge() {
    return splittingEdge;
  }

  public BorderCorner getTopLeft() {
    return topLeft;
  }

  public BorderCorner getTopRight() {
    return topRight;
  }

  public BorderCorner getBottomLeft() {
    return bottomLeft;
  }

  public BorderCorner getBottomRight() {
    return bottomRight;
  }

  public Border[] splitVertically( Border[] borders ) {
    if ( borders == null || borders.length < 2 ) {
      borders = new Border[2];
    }
    final Boolean empty;
    if ( this.empty != null && Boolean.TRUE.equals( this.empty ) ) {
      if ( splittingEdge.isEmpty() ) {
        empty = Boolean.TRUE;
      } else {
        empty = Boolean.FALSE;
      }
    } else {
      empty = null;
    }

    borders[0] = (Border) clone();
    borders[0].empty = empty;
    borders[0].right = borders[0].splittingEdge;
    borders[0].topRight = BorderCorner.EMPTY;
    borders[0].bottomRight = BorderCorner.EMPTY;

    borders[1] = (Border) clone();
    borders[1].empty = empty;
    borders[1].left = borders[1].splittingEdge;
    borders[1].topLeft = BorderCorner.EMPTY;
    borders[1].bottomLeft = BorderCorner.EMPTY;
    return borders;
  }

  public Border[] splitHorizontally( Border[] borders ) {
    if ( borders == null || borders.length < 2 ) {
      borders = new Border[2];
    }
    final Boolean empty;
    if ( this.empty != null && Boolean.TRUE.equals( this.empty ) ) {
      if ( splittingEdge.isEmpty() ) {
        empty = Boolean.TRUE;
      } else {
        empty = Boolean.FALSE;
      }
    } else {
      empty = null;
    }

    borders[0] = (Border) clone();
    borders[0].empty = empty;
    borders[0].sameForAllSides = null;
    borders[0].bottom = borders[0].splittingEdge;
    borders[0].bottomLeft = BorderCorner.EMPTY;
    borders[0].bottomRight = BorderCorner.EMPTY;

    borders[1] = (Border) clone();
    borders[1].empty = empty;
    borders[1].top = borders[1].splittingEdge;
    borders[1].topLeft = BorderCorner.EMPTY;
    borders[1].topRight = BorderCorner.EMPTY;
    return borders;
  }

  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( "Borders not supporting clone is evil!" );
    }
  }

  public boolean isSameForAllSides() {
    if ( isEmpty() ) {
      return true;
    }
    if ( sameForAllSides == null ) {
      sameForAllSides = computeSameForAllSides();
    }
    return sameForAllSides.booleanValue();
  }

  private Boolean computeSameForAllSides() {
    final BorderEdge borderEdge = top;
    if ( borderEdge.equals( bottom ) == false ) {
      return Boolean.FALSE;
    }
    if ( borderEdge.equals( left ) == false ) {
      return Boolean.FALSE;
    }
    if ( borderEdge.equals( right ) == false ) {
      return Boolean.FALSE;
    }
    final BorderCorner corner = topLeft;
    if ( corner.equals( topRight ) == false ) {
      return Boolean.FALSE;
    }
    if ( corner.equals( bottomLeft ) == false ) {
      return Boolean.FALSE;
    }
    if ( corner.equals( bottomRight ) == false ) {
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  public boolean isEmpty() {
    if ( empty != null ) {
      return empty.booleanValue();
    }

    if ( top.getWidth() != 0 ) {
      empty = Boolean.FALSE;
      return false;
    }
    if ( left.getWidth() != 0 ) {
      empty = Boolean.FALSE;
      return false;
    }
    if ( bottom.getWidth() != 0 ) {
      empty = Boolean.FALSE;
      return false;
    }
    if ( right.getWidth() != 0 ) {
      empty = Boolean.FALSE;
      return false;
    }

    empty = Boolean.TRUE;
    return true;
  }

  public String toString() {
    return "Border{" + "top=" + top + ", left=" + left + ", bottom=" + bottom + ", right=" + right + ", splittingEdge="
        + splittingEdge + ", topLeft=" + topLeft + ", topRight=" + topRight + ", bottomLeft=" + bottomLeft
        + ", bottomRight=" + bottomRight + ", empty=" + empty + '}';
  }
}

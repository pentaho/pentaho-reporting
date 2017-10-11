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

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.style.BorderStyle;

public final class BorderEdge {
  public static final BorderEdge EMPTY = new BorderEdge( BorderStyle.NONE, Color.black, 0 );

  /**
   * One of the styles defined in org.pentaho.reporting.libraries.css.input.style.keys.border.BorderStyle.
   */
  private BorderStyle borderStyle;
  private Color color;
  private long width;

  public BorderEdge( final BorderStyle borderStyle, final Color color, final long width ) {
    if ( borderStyle == null ) {
      throw new NullPointerException();
    }

    this.borderStyle = borderStyle;
    this.color = color;
    this.width = width;

    if ( BorderStyle.NONE.equals( borderStyle ) ) {
      this.width = 0;
    }
  }

  public BorderStyle getBorderStyle() {
    return borderStyle;
  }

  public Color getColor() {
    return color;
  }

  public long getWidth() {
    return width;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof BorderEdge ) ) {
      return false;
    }

    final BorderEdge that = (BorderEdge) o;

    if ( width != that.width ) {
      return false;
    }
    if ( !borderStyle.equals( that.borderStyle ) ) {
      return false;
    }
    if ( color != null ? !color.equals( that.color ) : that.color != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = borderStyle.hashCode();
    result = 31 * result + ( color != null ? color.hashCode() : 0 );
    result = 31 * result + (int) ( width ^ ( width >>> 32 ) );
    return result;
  }

  public String toString() {
    return "BorderEdge{" + "borderStyle='" + borderStyle + '\'' + ", color=" + color + ", width=" + width + '}';
  }

  public boolean isEmpty() {
    return width == 0 || BorderStyle.NONE.equals( borderStyle );
  }
}

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

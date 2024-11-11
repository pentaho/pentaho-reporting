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


package org.pentaho.reporting.engine.classic.core.style;

import java.awt.Stroke;
import java.io.ObjectStreamException;
import java.io.Serializable;

import org.pentaho.reporting.engine.classic.core.util.ObjectStreamResolveException;
import org.pentaho.reporting.engine.classic.core.util.StrokeUtility;

/**
 * Creation-Date: 30.10.2005, 19:37:35
 *
 * @author Thomas Morgner
 */
public class BorderStyle implements Serializable {
  public static final BorderStyle NONE = new BorderStyle( "none", StrokeUtility.STROKE_NONE );
  public static final BorderStyle HIDDEN = new BorderStyle( "hidden", StrokeUtility.STROKE_NONE );
  public static final BorderStyle DOTTED = new BorderStyle( "dotted", StrokeUtility.STROKE_DOTTED );
  public static final BorderStyle DASHED = new BorderStyle( "dashed", StrokeUtility.STROKE_DASHED );
  public static final BorderStyle SOLID = new BorderStyle( "solid", StrokeUtility.STROKE_SOLID );
  public static final BorderStyle DOUBLE = new BorderStyle( "double", -1 );
  public static final BorderStyle DOT_DASH = new BorderStyle( "dot-dash", StrokeUtility.STROKE_DOT_DASH );
  public static final BorderStyle DOT_DOT_DASH = new BorderStyle( "dot-dot-dash", StrokeUtility.STROKE_DOT_DOT_DASH );
  public static final BorderStyle WAVE = new BorderStyle( "wave", -1 );
  public static final BorderStyle GROOVE = new BorderStyle( "groove", -1 );
  public static final BorderStyle RIDGE = new BorderStyle( "ridge", -1 );
  public static final BorderStyle INSET = new BorderStyle( "inset", -1 );
  public static final BorderStyle OUTSET = new BorderStyle( "outset", -1 );
  private String type;
  private int strokeType;

  private BorderStyle( final String type, final int strokeType ) {
    this.type = type;
    this.strokeType = strokeType;
  }

  public int getStrokeType() {
    return strokeType;
  }

  public Stroke createStroke( float width ) {
    return StrokeUtility.createStroke( strokeType, width );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final BorderStyle that = (BorderStyle) o;

    if ( type != null ? !type.equals( that.type ) : that.type != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return ( type != null ? type.hashCode() : 0 );
  }

  public String toString() {
    return type;
  }

  public static final BorderStyle getBorderStyle( String type ) {
    if ( type == null ) {
      return null;
    }
    if ( type.equals( BorderStyle.NONE.type ) ) {
      return BorderStyle.NONE;
    }
    if ( type.equals( BorderStyle.DASHED.type ) ) {
      return BorderStyle.DASHED;
    }
    if ( type.equals( BorderStyle.DOT_DASH.type ) ) {
      return BorderStyle.DOT_DASH;
    }
    if ( type.equals( BorderStyle.DOT_DOT_DASH.type ) ) {
      return BorderStyle.DOT_DOT_DASH;
    }
    if ( type.equals( BorderStyle.DOTTED.type ) ) {
      return BorderStyle.DOTTED;
    }
    if ( type.equals( BorderStyle.DOUBLE.type ) ) {
      return BorderStyle.DOUBLE;
    }
    if ( type.equals( BorderStyle.GROOVE.type ) ) {
      return BorderStyle.GROOVE;
    }
    if ( type.equals( BorderStyle.HIDDEN.type ) ) {
      return BorderStyle.HIDDEN;
    }
    if ( type.equals( BorderStyle.INSET.type ) ) {
      return BorderStyle.INSET;
    }
    if ( type.equals( BorderStyle.NONE.type ) ) {
      return BorderStyle.NONE;
    }
    if ( type.equals( BorderStyle.OUTSET.type ) ) {
      return BorderStyle.OUTSET;
    }
    if ( type.equals( BorderStyle.RIDGE.type ) ) {
      return BorderStyle.RIDGE;
    }
    if ( type.equals( BorderStyle.SOLID.type ) ) {
      return BorderStyle.SOLID;
    }
    if ( type.equals( BorderStyle.WAVE.type ) ) {
      return BorderStyle.WAVE;
    }
    return null;
  }

  /**
   * Replaces the automatically generated instance with one of the enumeration instances.
   *
   * @return the resolved element
   * @throws java.io.ObjectStreamException
   *           if the element could not be resolved.
   * @noinspection UNUSED_SYMBOL
   */
  protected Object readResolve() throws ObjectStreamException {
    final BorderStyle style = getBorderStyle( this.type );
    if ( style != null ) {
      return style;
    }
    // unknown element alignment...
    throw new ObjectStreamResolveException();
  }

}

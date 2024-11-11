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


package org.pentaho.reporting.libraries.pixie.wmf;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A Windows metafile logical font object.
 */
public class MfLogFont implements WmfObject {
  private String face;
  private int size;
  private int style;
  private boolean strikeout;
  private boolean underline;
  private double rotation;

  /**
   * Construct from a metafile record.
   */
  public MfLogFont() {
  }

  public void setFace( final String face ) {
    this.face = face;
  }

  /**
   * The name of the font face.
   */
  public String getFace() {
    return face;
  }

  public void setSize( final int size ) {
    this.size = size;
  }


  /**
   * The size, in logical units.
   */
  public int getSize() {
    return size;
  }

  /**
   * The font style.
   */
  public int getStyle() {
    return style;
  }

  public void setStyle( final int style ) {
    this.style = style;
  }

  /**
   * True if this is an underlined font.
   */
  public boolean isUnderline() {
    return underline;
  }

  public void setUnderline( final boolean underline ) {
    this.underline = underline;
  }

  public boolean isStrikeOut() {
    return strikeout;
  }

  public void setStrikeOut( final boolean b ) {
    this.strikeout = b;
  }

  public Font createFont() {
    final Font retfont = new Font( getFace(), getStyle(), getSize() );
    final double rot = StrictMath.sin( Math.toRadians( -rotation ) );
    return retfont.deriveFont( AffineTransform.getRotateInstance( rot ) );
  }

  public int getType() {
    return OBJ_FONT;
  }

  public double getRotation() {
    return rotation;
  }

  public void setRotation( final double d ) {
    this.rotation = d;
  }
}

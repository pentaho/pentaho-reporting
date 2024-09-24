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

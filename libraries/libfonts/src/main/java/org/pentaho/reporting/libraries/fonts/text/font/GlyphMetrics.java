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


package org.pentaho.reporting.libraries.fonts.text.font;

/**
 * A return value for glyph metrics. It is used as return value by the font size producer.
 *
 * @author Thomas Morgner
 */
public class GlyphMetrics {
  private int width;
  private int height;
  private int baselinePosition;

  public GlyphMetrics() {
  }

  public int getWidth() {
    return width;
  }

  public void setWidth( final int width ) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight( final int height ) {
    this.height = height;
  }

  public int getBaselinePosition() {
    return baselinePosition;
  }

  public void setBaselinePosition( final int baselinePosition ) {
    this.baselinePosition = baselinePosition;
  }
}

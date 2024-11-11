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


package org.pentaho.reporting.libraries.fonts.text;

/**
 * Creation-Date: 04.04.2007, 14:03:19
 *
 * @author Thomas Morgner
 */
public class DefaultFontSpecification implements FontSpecification {
  private String fontFamily;
  private String encoding;
  private boolean embedFontData;
  private boolean antiAliasing;
  private boolean smallCaps;
  private boolean italic;
  private boolean oblique;
  private int fontWeight;
  private double fontSize;

  public DefaultFontSpecification() {
  }

  public DefaultFontSpecification( final String fontFamily,
                                   final double fontSize,
                                   final int fontWeight,
                                   final boolean italic,
                                   final boolean oblique,
                                   final boolean smallCaps,
                                   final boolean antiAliasing,
                                   final String encoding,
                                   final boolean embedFontData ) {
    this.fontFamily = fontFamily;
    this.fontSize = fontSize;
    this.fontWeight = fontWeight;
    this.italic = italic;
    this.oblique = oblique;
    this.smallCaps = smallCaps;
    this.antiAliasing = antiAliasing;
    this.encoding = encoding;
    this.embedFontData = embedFontData;
  }

  public String getFontFamily() {
    return fontFamily;
  }

  public void setFontFamily( final String fontFamily ) {
    this.fontFamily = fontFamily;
  }

  public boolean isAntiAliasing() {
    return antiAliasing;
  }

  public void setAntiAliasing( final boolean antiAliasing ) {
    this.antiAliasing = antiAliasing;
  }

  public boolean isSmallCaps() {
    return smallCaps;
  }

  public void setSmallCaps( final boolean smallCaps ) {
    this.smallCaps = smallCaps;
  }

  public boolean isItalic() {
    return italic;
  }

  public void setItalic( final boolean italic ) {
    this.italic = italic;
  }

  public boolean isOblique() {
    return oblique;
  }

  public void setOblique( final boolean oblique ) {
    this.oblique = oblique;
  }

  public int getFontWeight() {
    return fontWeight;
  }

  public void setFontWeight( final int fontWeight ) {
    this.fontWeight = fontWeight;
  }

  public double getFontSize() {
    return fontSize;
  }

  public void setFontSize( final double fontSize ) {
    this.fontSize = fontSize;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding( final String encoding ) {
    this.encoding = encoding;
  }

  public boolean isEmbedFontData() {
    return embedFontData;
  }

  public void setEmbedFontData( final boolean embedFontData ) {
    this.embedFontData = embedFontData;
  }
}

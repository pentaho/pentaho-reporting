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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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

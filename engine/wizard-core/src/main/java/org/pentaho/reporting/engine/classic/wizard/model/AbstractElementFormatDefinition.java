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

package org.pentaho.reporting.engine.classic.wizard.model;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;

import java.awt.*;

public abstract class AbstractElementFormatDefinition implements ElementFormatDefinition {
  private ElementAlignment horizontalAlignment;
  private ElementAlignment verticalAlignment;
  private String fontName;
  private Boolean fontBold;
  private Boolean fontItalic;
  private Boolean fontUnderline;
  private Boolean fontStrikethrough;
  private Integer fontSize;
  private Color fontColor;
  private Color backgroundColor;

  protected AbstractElementFormatDefinition() {
  }

  public ElementAlignment getHorizontalAlignment() {
    return horizontalAlignment;
  }

  public void setHorizontalAlignment( final ElementAlignment horizontalAlignment ) {
    this.horizontalAlignment = horizontalAlignment;
  }

  public ElementAlignment getVerticalAlignment() {
    return verticalAlignment;
  }

  public void setVerticalAlignment( final ElementAlignment verticalAlignment ) {
    this.verticalAlignment = verticalAlignment;
  }

  public String getFontName() {
    return fontName;
  }

  public void setFontName( final String fontName ) {
    this.fontName = fontName;
  }

  public Boolean getFontBold() {
    return fontBold;
  }

  public void setFontBold( final Boolean fontBold ) {
    this.fontBold = fontBold;
  }

  public Boolean getFontItalic() {
    return fontItalic;
  }

  public void setFontItalic( final Boolean fontItalic ) {
    this.fontItalic = fontItalic;
  }

  public Boolean getFontUnderline() {
    return fontUnderline;
  }

  public void setFontUnderline( final Boolean fontUnderline ) {
    this.fontUnderline = fontUnderline;
  }

  public Boolean getFontStrikethrough() {
    return fontStrikethrough;
  }

  public void setFontStrikethrough( final Boolean fontStrikethrough ) {
    this.fontStrikethrough = fontStrikethrough;
  }

  public Integer getFontSize() {
    return fontSize;
  }

  public void setFontSize( final Integer fontSize ) {
    this.fontSize = fontSize;
  }

  public Color getFontColor() {
    return fontColor;
  }

  public void setFontColor( final Color fontColor ) {
    this.fontColor = fontColor;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor( final Color backgroundColor ) {
    this.backgroundColor = backgroundColor;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}

/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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

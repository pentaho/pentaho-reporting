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


package org.pentaho.reporting.engine.classic.wizard.model;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;

import java.awt.*;
import java.io.Serializable;

public interface ElementFormatDefinition extends Cloneable, Serializable {
  public ElementAlignment getHorizontalAlignment();

  public void setHorizontalAlignment( ElementAlignment horizontalAlignment );

  public ElementAlignment getVerticalAlignment();

  public void setVerticalAlignment( ElementAlignment verticalAlignment );

  public String getFontName();

  public void setFontName( String fontName );

  public Boolean getFontBold();

  public void setFontBold( Boolean bold );

  public Boolean getFontItalic();

  public void setFontItalic( Boolean italic );

  public Boolean getFontUnderline();

  public void setFontUnderline( Boolean italic );

  public Boolean getFontStrikethrough();

  public void setFontStrikethrough( Boolean italic );

  public Integer getFontSize();

  public void setFontSize( Integer fontSize );

  public Color getFontColor();

  public void setFontColor( Color fontColor );

  public Color getBackgroundColor();

  public void setBackgroundColor( Color backgroundColor );

  public Object clone() throws CloneNotSupportedException;
}

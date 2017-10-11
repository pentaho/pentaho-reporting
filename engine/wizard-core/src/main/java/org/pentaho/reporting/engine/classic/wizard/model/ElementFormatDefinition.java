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

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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.util;

import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class ReportDrawableRotatedText implements ReportDrawable
{
  private final Float rotationDegree;
  private final Double rotationRadian;
  private final String text;
  private final ReportElement element;

  public ReportDrawableRotatedText(final String someText, final Float someRotation, final ReportElement someElement)
  {
    this.text = someText;
    this.rotationDegree = someRotation;
    this.rotationRadian = Math.toRadians(someRotation.doubleValue());
    this.element = someElement;
  }

  public void draw(final Graphics2D graphics2D, final Rectangle2D bounds)
  {
    final int textWidth = graphics2D.getFontMetrics().stringWidth(this.text);
    final LineMetrics lm = graphics2D.getFontMetrics().getLineMetrics(this.text,graphics2D);
    final float textHeight = lm.getHeight();
    
    String vAlign = String.valueOf(element.getStyle().getStyleProperty(ElementStyleKeys.VALIGNMENT));
    String hAlign = String.valueOf(element.getStyle().getStyleProperty(ElementStyleKeys.ALIGNMENT));    
    
    // half dimension
    final float centerX = (float)bounds.getMaxX()/2f,
        centerY = (float)bounds.getMaxY()/2f;
    
    // coordinates to draw text centered
    final float drawX = ((float)bounds.getMaxX() - textWidth) / 2,
        drawY = (((float)bounds.getMaxY() + lm.getAscent()) / 2);
    
    // translate coordinates
    float translateX = 0f, translateY = 0f;
    if ( hAlign.equals("null") || hAlign.equals(String.valueOf(ElementAlignment.LEFT)) || hAlign.equals(String.valueOf(ElementAlignment.JUSTIFY)) )
    {
      if( (this.rotationDegree > 0 && this.rotationDegree <= 180) || (this.rotationDegree > -360 && this.rotationDegree <= -180) ){
        translateX = -centerX + (textWidth/2f)*(float)Math.abs(Math.cos(this.rotationRadian)) + (lm.getAscent()/2+lm.getDescent())*(float)Math.abs(Math.sin(this.rotationRadian));
      }else{
        translateX = -centerX + (textWidth/2f)*(float)Math.abs(Math.cos(this.rotationRadian)) + (textHeight/2f)*(float)Math.abs(Math.sin(this.rotationRadian));
      }
    }
    else if (hAlign.equals(String.valueOf(ElementAlignment.RIGHT)))
    {
      if( (this.rotationDegree > 0 && this.rotationDegree <= 180) || (this.rotationDegree > -360 && this.rotationDegree <= -180) ){
        translateX = centerX - (textWidth/2f)*(float)Math.abs(Math.cos(this.rotationRadian)) - (textHeight/2f)*(float)Math.abs(Math.sin(this.rotationRadian));
      }else{
        translateX = centerX - (textWidth/2f)*(float)Math.abs(Math.cos(this.rotationRadian)) - (lm.getAscent()/2+lm.getDescent())*(float)Math.abs(Math.sin(this.rotationRadian));
      }
    }
    
    if (vAlign.equals("null") || vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.TOP)))
    {
      translateY = -centerY + (textWidth/2f)*(float)Math.abs(Math.sin(this.rotationRadian)) + (lm.getAscent()/2+lm.getDescent())*(float)Math.abs(Math.cos(this.rotationRadian));
    }
    else if (vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.BOTTOM)))
    {
      translateY = centerY - (textWidth/2f)*(float)Math.abs(Math.sin(this.rotationRadian)) - (textHeight/2f)*(float)Math.abs(Math.cos(this.rotationRadian));
    }

    graphics2D.translate(translateX,translateY);
    graphics2D.rotate(-this.rotationRadian, centerX, centerY);
    graphics2D.drawString(this.text, drawX, drawY-(lm.getDescent()/2));
  }

  public boolean isKeepAspectRatio()
  {
    return true;
  }

  public Dimension getPreferredSize()
  {
    if (element.getStyle().getStyleProperty(ElementStyleKeys.MIN_WIDTH).getClass().isAssignableFrom( Float.class ) &&
        element.getStyle().getStyleProperty(ElementStyleKeys.MIN_HEIGHT).getClass().isAssignableFrom( Float.class ) )
    {
      return new Dimension(((Float)element.getStyle().getStyleProperty(ElementStyleKeys.MIN_WIDTH)).intValue(), ((Float)element.getStyle().getStyleProperty(ElementStyleKeys.MIN_HEIGHT)).intValue());
    }
    else
    {
      return null;
    }
  }

  public ImageMap getImageMap(final Rectangle2D bounds)
  {
    return null;
  }

  public void setConfiguration(final Configuration config)
  {
  }

  public void setResourceBundleFactory(final ResourceBundleFactory bundleFactory)
  {
  }

  public void setStyleSheet(final StyleSheet style)
  {
  }
  
  public String getText(){
    return this.text;
  }
  
  public Double getRotationRadian(){
    return this.rotationRadian;
  }
  
  public Float getRotationDegree(){
    return this.rotationDegree;
  }
  
  public ReportElement getElement(){
	  return this.element;
  }

}

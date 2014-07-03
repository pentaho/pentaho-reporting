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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultStyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleBuilder;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

public class ReportDrawableRotatedComponent implements IReportDrawableRotated
{
  private final Float rotationDegree;
  private final Double rotationRadian;
  private final String text;
  private final ReportElement element;

  public ReportDrawableRotatedComponent(final String someText,
                                        final Float someRotation,
                                        final ReportElement someElement)
  {
    this.text = someText;
    this.rotationDegree = someRotation;
    this.rotationRadian = Math.toRadians(someRotation.doubleValue());
    this.element = someElement;
  }

  public void draw(final Graphics2D graphics2D, final Rectangle2D bounds)
  {
    final String vAlign = String.valueOf(element.getStyle().getStyleProperty(ElementStyleKeys.VALIGNMENT));
    final String hAlign = String.valueOf(element.getStyle().getStyleProperty(ElementStyleKeys.ALIGNMENT));
    /* Process multiple lines if needed */
    AttributedString aSText = new AttributedString(this.text, graphics2D.getFontMetrics().getFont().getAttributes());
    AttributedCharacterIterator aCIText = aSText.getIterator();
    final LineBreakMeasurer lBMText = new LineBreakMeasurer(aCIText, graphics2D.getFontRenderContext());
    lBMText.setPosition(aCIText.getBeginIndex());
    
    /* some spacing to improve visualization */
    final float s = 1f;
    
    /* calculate max break width allowed (value is hypotenuse or bounds width or bounds height) */
    float breakWidth;
    if (this.rotationDegree == 0f || this.rotationDegree == 360f || this.rotationDegree == 180f || this.rotationDegree == -180f ){
      breakWidth = (float)bounds.getWidth() - 2f*s;
    }else if (this.rotationDegree == 90f || this.rotationDegree == -270f || this.rotationDegree == 270f || this.rotationDegree == -90f ){
      breakWidth = (float)bounds.getHeight() - 2f*s;
    }else{
      breakWidth = (float)( bounds.getHeight() / Math.abs(Math.sin(this.rotationRadian)) ) - 2f*s;
    }
    
    /* max width of any line, to help translations */
    float maxTextWidth = 0f;
    /* Break text into several lines and calculate maxTextHeight */
    ArrayList<TextLayout> lines = new ArrayList<TextLayout>(5);
    TextLayout layout = null;
    if ( String.valueOf(element.getStyle().getStyleProperty(TextStyleKeys.TEXT_WRAP)).equals( "null" ) ||
        String.valueOf(element.getStyle().getStyleProperty(TextStyleKeys.TEXT_WRAP)).equals( TextWrap.WRAP.toString() ) ) {
      while (lBMText.getPosition() < aCIText.getEndIndex()) {
        layout = lBMText.nextLayout(breakWidth);
        lines.add( layout );
        if( layout.getBounds().getWidth() > maxTextWidth){
          maxTextWidth = (float)layout.getBounds().getWidth();
        }
      }
    }else if ( String.valueOf(element.getStyle().getStyleProperty(TextStyleKeys.TEXT_WRAP)).equals( TextWrap.NONE.toString() ) ) {
      layout = new TextLayout( this.text, graphics2D.getFont(), graphics2D.getFontRenderContext() );
      lines.add( layout );
      maxTextWidth = (float)layout.getBounds().getWidth();
    }

    /* save original coordinates */
    AffineTransform originalAT = graphics2D.getTransform();
    
    // translate coordinates
    float translateX = 0f, translateY = 0f, textWidth, textHeight, drawX, drawY;
    // half dimension
    final float centerX = (float) bounds.getMaxX() / 2f,
        centerY = (float) bounds.getMaxY() / 2f;

    /* Draw line(s) */
    for (int i = 0; i < lines.size(); i++) {
      /* get line according to rotation degree */
      if ( (this.rotationDegree >= 180) || (this.rotationDegree >= -180 && this.rotationDegree < 0) )
      {
        // get line in LIFO order
        layout = lines.get( lines.size() - 1 - i );
      }else
      {
        // get line in FIFO order
        layout = lines.get( i );
      }

      /* text dimensions according to line attributes */
      textWidth = (float)layout.getBounds().getWidth();
      textHeight = layout.getAscent() + layout.getDescent() + layout.getLeading();//(float)layout.getBounds().getHeight();
      /* coordinates to draw text centered */
      drawX = ((float)bounds.getMaxX() - textWidth) / 2f;
      drawY = ((float)bounds.getMaxY() + layout.getAscent() - layout.getDescent()) / 2f;

      /* Process different types of rotations */
      if ( this.rotationDegree == 90 || this.rotationDegree == -270 || this.rotationDegree == -90 || this.rotationDegree == 270 ){
        /* vertical translation (assuming it is the same for all lines) */
        if (vAlign.equals("null") || vAlign.equals(ElementAlignment.TOP.toString())){
          translateY = -centerY + s + (textWidth / 2f) * (float) Math.abs(Math.sin(this.rotationRadian)) + (layout.getAscent() / 2f + layout.getDescent()) * (float) Math.abs(Math.cos(this.rotationRadian));
        }
        //} else if (vAlign.equals(String.valueOf(ElementAlignment.MIDDLE))) { Already done
        else if (vAlign.equals(ElementAlignment.BOTTOM.toString())){
          translateY = centerY - s - (textWidth / 2f) * (float) Math.abs(Math.sin(this.rotationRadian)) - (textHeight / 2f) * (float) Math.abs(Math.cos(this.rotationRadian));
        }
        /* horizontal translation (assuming it is the same for all lines) */
        if (hAlign.equals("null") || hAlign.equals(ElementAlignment.LEFT.toString()) || hAlign.equals(ElementAlignment.JUSTIFY.toString())){
          translateX = -centerX + (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) + (textHeight / 2f) * (float) Math.abs(Math.sin(this.rotationRadian));
          translateX += i * (textHeight+s);
        }else if (hAlign.equals(ElementAlignment.CENTER.toString())){
          if ( lines.size() % 2 == 0 ){
            if ( ( (lines.size() / 2) - i ) > 0 ){
              /* 1st half */
              translateX = -((textHeight+s)/2f);
              translateX -= ( ( lines.size() / 2 ) - (i+1) ) * (textHeight+s);
            }else{
              /* 2nd half */
              translateX = (textHeight+s)/2f;
              translateX += ( i - ( lines.size() / 2 ) ) * (textHeight+s);
            }
          } else {
            if ( ( ( ( lines.size() - 1 ) / 2) - i ) > 0 ){
              /* 1st half */
              translateX = -( ( ( lines.size() - 1 ) / 2 ) - i ) * (textHeight+s);
            }else if ( ( ( ( lines.size() - 1 ) / 2) - i ) < 0 ){
              /* 2nd half */
              translateX = ( i - ( ( lines.size() - 1 ) / 2 ) ) * (textHeight+s);
            }else if( ( ( ( lines.size() - 1 ) / 2) - i ) == 0 ){
              /* 1 line in the middle */
              translateX = 0f; // already centered
            }
          }
        }else if (hAlign.equals(ElementAlignment.RIGHT.toString())){
          translateX = centerX - (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) - (textHeight / 2f) * (float) Math.abs(Math.sin(this.rotationRadian));
          translateX -= (lines.size() - 1 - i) * (textHeight+s);
        }
      }
      else if ( this.rotationDegree == 180 || this.rotationDegree == -180 )
      {
        /* vertical translation (assuming it is the same for all lines) */
        if (vAlign.equals("null") || vAlign.equals(ElementAlignment.TOP.toString()))
        {
          translateY = -centerY + (textWidth / 2f) * (float) Math.abs(Math.sin(this.rotationRadian)) + (textHeight / 2f) * (float) Math.abs(Math.cos(this.rotationRadian));
          translateY += i * (textHeight+s);
        }else if (vAlign.equals(ElementAlignment.MIDDLE.toString())) {
          if ( lines.size() % 2 == 0 ){
            if ( ( (lines.size() / 2) - i ) < 0 ){
              /* top half */
              translateY = -((textHeight+s)/2f);
              translateY -= ( ( lines.size() / 2 ) - (i+1) ) * (textHeight+s);
            }else{
              /* bottom half */
              translateY = (textHeight+s)/2f;
              translateY += ( i - ( lines.size() / 2 ) ) * (textHeight+s);
            }
          } else {
            if ( ( ( ( lines.size() - 1 ) / 2) - i ) < 0 ){
              /* top half */
              translateY = -( ( ( lines.size() - 1 ) / 2 ) - i ) * (textHeight+s);
            }else if ( ( ( ( lines.size() - 1 ) / 2) - i ) > 0 ){
              /* bottom half */
              translateY = ( i - ( ( lines.size() - 1 ) / 2 ) ) * (textHeight+s);
            }else if( ( ( ( lines.size() - 1 ) / 2) - i ) == 0 ){
              /* 1 line in the middle */
              translateY = 0f; // already centered
            }
          }
        }else if (vAlign.equals(ElementAlignment.BOTTOM.toString()))
        {
          translateY = centerY - (textWidth / 2f) * (float) Math.abs(Math.sin(this.rotationRadian)) - (textHeight / 2f) * (float) Math.abs(Math.cos(this.rotationRadian));
          translateY -= ( lines.size() - 1 - i ) * (textHeight+s);
        }
        /* horizontal translation */
        if (hAlign.equals("null") || hAlign.equals(ElementAlignment.LEFT.toString()) || hAlign.equals(ElementAlignment.JUSTIFY.toString()))
        {
          translateX = -centerX + 2*s + (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) + (layout.getAscent() / 2f + layout.getDescent()) * (float) Math.abs(Math.sin(this.rotationRadian));
        //}else if (hAlign.equals(String.valueOf(ElementAlignment.CENTER))){ // Already done
        }else if (hAlign.equals(String.valueOf(ElementAlignment.RIGHT))){
          translateX = centerX - s - (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) - (textHeight / 2f) * (float) Math.abs(Math.sin(this.rotationRadian));
        }
      }
      else{
        textWidth = maxTextWidth;
        /* vertical translation */
        if (vAlign.equals("null") || vAlign.equals(ElementAlignment.TOP.toString()) )
        {
          translateY = -centerY + (textWidth / 2f) * (float) Math.abs(Math.sin(this.rotationRadian)) + (textHeight / 2f) * (float) Math.abs(Math.cos(this.rotationRadian));
        }
        else if (vAlign.equals(ElementAlignment.BOTTOM.toString()) )
        {
          translateY = centerY - (textWidth / 2f) * (float) Math.abs(Math.sin(this.rotationRadian)) - (textHeight / 2f) * (float) Math.abs(Math.cos(this.rotationRadian));
        }
        /* horizontal translation */
        if (hAlign.equals("null") || hAlign.equals(ElementAlignment.LEFT.toString()) || hAlign.equals(ElementAlignment.JUSTIFY.toString()))
        {
          drawX -= (maxTextWidth-layout.getBounds().getWidth())/2f;
          if ((this.rotationDegree > 0 && this.rotationDegree <= 180) || (this.rotationDegree > -360 && this.rotationDegree <= -180))
          {
            translateX = -centerX + (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) + (layout.getAscent() / 2f + layout.getDescent()) * (float) Math.abs(Math.sin(this.rotationRadian));
          }
          else
          {
            translateX = -centerX + (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) + (textHeight / 2f) * (float) Math.abs(Math.sin(this.rotationRadian));
          }
          translateX += i * ((textHeight+s)/(float) Math.abs(Math.sin(this.rotationRadian)));
        }
        else if (hAlign.equals(ElementAlignment.CENTER.toString()))
        {
          if ( lines.size() % 2 == 0 ){
            if ( ( (lines.size() / 2) - i ) > 0 ){
              /* 1st half */
              translateX = -(((textHeight+s)/(float) Math.abs(Math.sin(this.rotationRadian)))/2f);
              translateX -= ( ( lines.size() / 2 ) - (i+1) ) * ((textHeight+s)/(float) Math.abs(Math.sin(this.rotationRadian)));
            }else{
              /* 2nd half */
              translateX = ((textHeight+s)/(float) Math.abs(Math.sin(this.rotationRadian)))/2f;
              translateX += ( i - ( lines.size() / 2 ) ) * ((textHeight+s)/(float) Math.abs(Math.sin(this.rotationRadian)));
            }
          } else {
            if ( ( ( ( lines.size() - 1 ) / 2) - i ) > 0 ){
              /* 1st half */
              translateX = -( ( ( lines.size() - 1 ) / 2 ) - i ) * ((textHeight+s)/(float) Math.abs(Math.sin(this.rotationRadian)));
            }else if ( ( ( ( lines.size() - 1 ) / 2) - i ) < 0 ){
              /* 2nd half */
              translateX = ( i - ( ( lines.size() - 1 ) / 2 ) ) * ((textHeight+s)/(float) Math.abs(Math.sin(this.rotationRadian)));
            }else if( ( ( ( lines.size() - 1 ) / 2) - i ) == 0 ){
              /* 1 line in the middle */
              translateX = 0f;
            }
          }
        }
        else if (hAlign.equals(ElementAlignment.RIGHT.toString()))
        {
          drawX +=  (maxTextWidth-layout.getBounds().getWidth())/2f;
          if ((this.rotationDegree > 0 && this.rotationDegree <= 180) || (this.rotationDegree > -360 && this.rotationDegree <= -180))
          {
            translateX = centerX - (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) - (textHeight / 2f) * (float) Math.abs(Math.sin(this.rotationRadian));
          }
          else
          {
            translateX = centerX - (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) - (layout.getAscent() / 2f + layout.getDescent()) * (float) Math.abs(Math.sin(this.rotationRadian));
          }
          translateX -=  (lines.size() - 1 - i) * ((textHeight+s)/(float) Math.abs(Math.sin(this.rotationRadian)));
        }
      }
      graphics2D.translate(translateX, translateY);
      graphics2D.rotate(-this.rotationRadian, centerX, centerY);
      layout.draw( graphics2D, drawX, drawY );
      graphics2D.setTransform( originalAT );
    }
  }

  public boolean isKeepAspectRatio()
  {
    return true;
  }

  public Dimension getPreferredSize()
  {
    if (element.getStyle().getStyleProperty(ElementStyleKeys.MIN_WIDTH).getClass().isAssignableFrom(Float.class) &&
        element.getStyle().getStyleProperty(ElementStyleKeys.MIN_HEIGHT).getClass().isAssignableFrom(Float.class))
    {
      return new Dimension(((Float) element.getStyle().getStyleProperty(ElementStyleKeys.MIN_WIDTH)).intValue(), ((Float) element.getStyle().getStyleProperty(ElementStyleKeys.MIN_HEIGHT)).intValue());
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

  public String getText()
  {
    return this.text;
  }

  public Double getRotationRadian()
  {
    return this.rotationRadian;
  }

  public Float getRotationDegree()
  {
    return this.rotationDegree;
  }

  public ReportElement getElement()
  {
    return this.element;
  }

  public boolean drawRotatedComponent(Object componentWriter, Type componentType) throws IOException
  {

    if (componentWriter == null)
    {
      return false;
    }

    switch (componentType)
    {

      case HTML:
        return drawHtml(componentWriter);

      case XLS_XLSX:
        return drawExcel(componentWriter);

      case PDF:
        // already been done at this.draw()

      default:
        //do nothing

    }
    return true;
  }


  public boolean drawExcel(Object writer) throws IOException
  {

    if (!(writer instanceof Cell))
    {
      return false;
    }

    int rotate = getRotationDegree().intValue();
    // transform angle values [270,360] => [-90,0] , [-360,-270] => [0,90]
    if (rotate >= 270 && rotate <= 360)
    {
      rotate = rotate - 360;
    }
    else if (rotate >= -360 && rotate <= -270)
    {
      rotate = rotate + 360;
    }
    // if in range [-90,90] export as cell with rotated text, else export as image
    if (((Cell) writer) instanceof HSSFCell)
    {
      // XLS
      if (rotate >= -90 && rotate <= 90)
      {
        ((Cell) writer).getCellStyle().setRotation((short) rotate);
        ((Cell) writer).setCellValue(getText());
        return true;
      }
    }
    else if (((Cell) writer) instanceof XSSFCell)
    {
      //XLSX
      if (rotate >= 0 && rotate <= 90)
      {
        XSSFCellStyle style = ((XSSFCell) writer).getCellStyle();
        style.setRotation((short) (rotate));
        ((XSSFCell) writer).setCellStyle(style);
        ((XSSFCell) writer).setCellValue(getText());
        return true;
      }
      else if (rotate >= -90 && rotate < 0)
      {
        // XLSX works funny [-90,0[  => ]90,180]
        XSSFCellStyle style = ((XSSFCell) writer).getCellStyle();
        style.setRotation((short) (90 + -1 * rotate));
        ((XSSFCell) writer).setCellStyle(style);
        ((XSSFCell) writer).setCellValue(getText());
        return true;
      }
    }
    return false;
  }

  public boolean drawHtml(Object writer) throws IOException
  {

    if (!(writer instanceof XmlWriter))
    {
      return false;
    }
    
    // using PLAIN as reference
    final Font font = new Font( 
        String.valueOf(element.getStyle().getStyleProperty(TextStyleKeys.FONT)).equals( "null") ? Font.SERIF : String.valueOf(element.getStyle().getStyleProperty(TextStyleKeys.FONT)),
            Font.PLAIN, element.getStyle().getIntStyleProperty(TextStyleKeys.FONTSIZE, 10) );
    AffineTransform at = new AffineTransform();
    final FontRenderContext frc = new FontRenderContext(at, true, true);
    /* Process multiple lines if needed */
    AttributedString aSText = new AttributedString(this.text, font.getAttributes());
    AttributedCharacterIterator aCIText = aSText.getIterator();
    final LineBreakMeasurer lBMText = new LineBreakMeasurer(aCIText, frc);
    lBMText.setPosition(aCIText.getBeginIndex());
    
    /* some spacing to improve visualization */
    final float s = 1f;
    
    /* calculate max break width allowed (value is hypotenuse or bounds width or bounds height) */
    float breakWidth, breakWidthIE10;
    if (this.rotationDegree == 0f || this.rotationDegree == 360f || this.rotationDegree == 180f || this.rotationDegree == -180f ){
      breakWidth = ((Float) element.getStyle().getStyleProperty(ElementStyleKeys.MIN_WIDTH)).floatValue() - 2f*s;
      breakWidthIE10 = ((Float) element.getStyle().getStyleProperty(ElementStyleKeys.MIN_WIDTH)).floatValue() - 2f*s - 2.5f*element.getStyle().getIntStyleProperty(TextStyleKeys.FONTSIZE, 10);
    }else if (this.rotationDegree == 90f || this.rotationDegree == -270f || this.rotationDegree == 270f || this.rotationDegree == -90f ){
      breakWidth = ((Float) element.getStyle().getStyleProperty(ElementStyleKeys.MIN_HEIGHT)).floatValue() - 2f*s;
      breakWidthIE10 = ((Float) element.getStyle().getStyleProperty(ElementStyleKeys.MIN_HEIGHT)).floatValue() - 2f*s - 2.5f*element.getStyle().getIntStyleProperty(TextStyleKeys.FONTSIZE, 10);
    }else{
      breakWidth = (float)( ((Float) element.getStyle().getStyleProperty(ElementStyleKeys.MIN_HEIGHT)).floatValue() / Math.abs(Math.sin(this.rotationRadian)) ) - 2f*s;
      breakWidthIE10 = (float)( ((Float) element.getStyle().getStyleProperty(ElementStyleKeys.MIN_HEIGHT)).floatValue() / Math.abs(Math.sin(this.rotationRadian)) ) - 2f*s - 4f*element.getStyle().getIntStyleProperty(TextStyleKeys.FONTSIZE, 10);
    }
    
    /* Break text into several lines and calculate maxTextHeight */
    ArrayList<String> lines = new ArrayList<String>(5), linesIE10 = new ArrayList<String>(5);
    int firstCharIdx=0, lastCharIdx = 0;
    if ( String.valueOf(element.getStyle().getStyleProperty(TextStyleKeys.TEXT_WRAP)).equals( "null" ) ||
        String.valueOf(element.getStyle().getStyleProperty(TextStyleKeys.TEXT_WRAP)).equals( TextWrap.WRAP.toString() ) ) {
      while (lBMText.getPosition() < aCIText.getEndIndex()) {
        firstCharIdx = lastCharIdx;
        lastCharIdx += lBMText.nextLayout(breakWidth).getCharacterCount();
        lines.add( this.text.substring( firstCharIdx, lastCharIdx ).trim() );
      }
      lBMText.setPosition(aCIText.getBeginIndex());
      firstCharIdx=0;
      lastCharIdx = 0;
      while (lBMText.getPosition() < aCIText.getEndIndex()) {
        firstCharIdx = lastCharIdx;
        lastCharIdx += lBMText.nextLayout(breakWidthIE10).getCharacterCount();
        linesIE10.add( this.text.substring( firstCharIdx, lastCharIdx ).trim() );
      }
    }else if ( String.valueOf(element.getStyle().getStyleProperty(TextStyleKeys.TEXT_WRAP)).equals( TextWrap.NONE.toString() ) ) {
      lines.add( this.text );
      linesIE10.add( this.text );
    }

    DecimalFormat dc = new DecimalFormat("#.000000");
    /* IE8 rotates clockwise */
    String cos = dc.format(Math.cos(-1d*getRotationDegree().doubleValue() * Math.PI / 180d)),
        M12 = dc.format(Math.sin(getRotationDegree().doubleValue() * Math.PI / 180d)),
        sin = dc.format(-1d*Math.sin(getRotationDegree().doubleValue() * Math.PI / 180d));

    // force style class name to update
    final String rotationClassName = "r" + (getText() + getPreferredSize().getHeight() + getPreferredSize().getWidth() + sin + System.currentTimeMillis()).hashCode();

    // avoid IE8 CSS rotation code in IE9+ browsers
    ((XmlWriter) writer).writeText("<style> " +
        "\n@media all\\0 { " +
        "." + rotationClassName + " { " +
        DefaultStyleBuilder.CSSKeys.MS_FILTER.getCssName() + ":" + "progid:DXImageTransform.Microsoft.Matrix(" +
        "M11=" + cos + ",M12=" + M12 + ",M21=" + sin + ",M22=" + cos + ",sizingMethod='auto expand'); } } " +
        "\n@media all and (monochrome:0) { " +
        "." + rotationClassName + " { filter: none; } } </style>\n");
    /* non-IE8 rotate counter clockwise */
    cos = dc.format(Math.cos(getRotationDegree().doubleValue() * Math.PI / 180d));
    M12 = dc.format(-1d * Math.sin(getRotationDegree().doubleValue() * Math.PI / 180d));
    sin = dc.format(Math.sin(getRotationDegree().doubleValue() * Math.PI / 180d));
    /* client-side transformations */
    /* get directions */
    final String vAlign = String.valueOf(getElement().getStyle().getStyleProperty(ElementStyleKeys.VALIGNMENT));
    final String hAlign = String.valueOf(getElement().getStyle().getStyleProperty(ElementStyleKeys.ALIGNMENT));
    ((XmlWriter) writer).writeText("<div class='"+rotationClassName+"' style='display: inline-block; overflow: hidden; white-space: nowrap;'></div>\n");
    ((XmlWriter) writer).writeText("<script>\n");
    ((XmlWriter) writer).writeText("var lines = ['"+lines.get( 0 )+"'");
    for (int i=1; i<lines.size(); i++){
      ((XmlWriter) writer).writeText(",'"+lines.get( i )+"'");
    }
    ((XmlWriter) writer).writeText("];\n");
    ((XmlWriter) writer).writeText("var linesIE10 = ['"+linesIE10.get( 0 )+"'");
    for (int i=1; i<linesIE10.size(); i++){
      ((XmlWriter) writer).writeText(",'"+linesIE10.get( i )+"'");
    }
    ((XmlWriter) writer).writeText("];\n");
    ((XmlWriter) writer).writeText(" var container;\n");
    /* start IE 8 JS */
    ((XmlWriter) writer).writeText("if( (navigator.userAgent.toLowerCase().indexOf('msie') != -1) && parseInt(navigator.userAgent.toLowerCase().split('msie')[1]) == 8){\n");
      ((XmlWriter) writer).writeText(" var elems = document.getElementsByTagName('div');\n"
        + " container = undefined;\n"
        + " for (i in elems) {\n"
        + "  if(typeof container != 'undefined'){ break; }\n"
        + "  if( (''+elems[i].className).indexOf('" + rotationClassName + "') > -1) {\n"
        + "   container = elems[i];\n"
        + "  }\n }\n"
        + " container.innerHTML = lines[0]+'<br>'; for (i = 1; i < lines.length; ++i) { container.innerHTML += lines[i]+'<br>'; }\n"
        + " totalLines = "+linesIE10.size()+";\n"
        + " var h=0, v=0;\n"
        + " var parentStyle = 'white-space: normal;';\n"
        + " var containerStyle = '.'+container.className+'{';\n"
        + " var elem = document.createElement('style'); elem.setAttribute('type','text/css');\n");
      /* Fix positioning for rotation and translation */
      ((XmlWriter) writer).writeText(" parentStyle += ' text-align: left;';\n");
      ((XmlWriter) writer).writeText(" parentStyle += 'vertical-align: top;';\n");
      /* Control wrapping */
      ((XmlWriter) writer).writeText(" containerStyle += 'white-space: normal;';\n");
      /* horizontal translation IE8 */
      if (hAlign.equals("null") || hAlign.equals(ElementAlignment.LEFT.toString()) || hAlign.equals(ElementAlignment.JUSTIFY.toString()))
      {
        if (this.rotationDegree == 180 || this.rotationDegree == -180){
          ((XmlWriter) writer).writeText(" containerStyle += 'text-align: right;';\n");
        }
        //((XmlWriter) writer).writeText(" h = 0;\n"); // h is initialized to 0, Already aligned left
      }
      else if (hAlign.equals(ElementAlignment.RIGHT.toString()))
      {
        if (this.rotationDegree == 180 || this.rotationDegree == -180){
          ((XmlWriter) writer).writeText(" containerStyle += 'text-align: left;';\n");
        }// ToDo diagonal
        ((XmlWriter) writer).writeText(" h = container.parentNode.offsetWidth - container.offsetWidth;\n");
      }
      else if (hAlign.equals(ElementAlignment.CENTER.toString()))
      {
        if (this.rotationDegree == 180 || this.rotationDegree == -180){
          ((XmlWriter) writer).writeText(" containerStyle += 'text-align: center;';\n");
        }// ToDo diagonal
        ((XmlWriter) writer).writeText(" h = (container.parentNode.offsetWidth - container.offsetWidth) / 2;\n");
      }
      
      /* vertical translation IE8 */
      if (vAlign.equals("null") || vAlign.equals(ElementAlignment.TOP.toString()))
      {
        if (this.rotationDegree == 90 || this.rotationDegree == -270){
          ((XmlWriter) writer).writeText(" containerStyle += 'text-align: right;';\n");
        }else if (this.rotationDegree == -90 || this.rotationDegree == 270){
          ((XmlWriter) writer).writeText(" containerStyle += 'text-align: left;';\n");
        }
        //((XmlWriter) writer).writeText(" v = 0;\n"); // v is initialized to 0, already aligned with top
      }
      else if (vAlign.equals(ElementAlignment.BOTTOM.toString()))
      {
        if (this.rotationDegree == 90 || this.rotationDegree == -270){
          ((XmlWriter) writer).writeText(" containerStyle += 'text-align: left;';\n");
        }else if (this.rotationDegree == -90 || this.rotationDegree == 270){
          ((XmlWriter) writer).writeText(" containerStyle += 'text-align: right;';\n");
        }
        ((XmlWriter) writer).writeText(" v = ( container.parentElement.offsetHeight-container.offsetHeight );\n");
      }
      else if (vAlign.equals(ElementAlignment.MIDDLE.toString()))
      {
        if (this.rotationDegree == 90 || this.rotationDegree == -270 || this.rotationDegree == -90 || this.rotationDegree == 270){
          ((XmlWriter) writer).writeText(" containerStyle += 'text-align: center;';\n");
        }
        ((XmlWriter) writer).writeText(" v = ( container.parentElement.offsetHeight-container.offsetHeight )/2;\n");
      }
      ((XmlWriter) writer).writeText(" container.parentNode.setAttribute('style',parentStyle);\n");
      ((XmlWriter) writer).writeText(" containerStyle += 'margin-left:'+h+'px; margin-top:'+v+'px; }';\n");
      ((XmlWriter) writer).writeText(" elem.styleSheet.cssText = containerStyle\n");
      ((XmlWriter) writer).writeText(" document.getElementsByTagName('head')[0].appendChild(elem);\n");
      /* end IE8 JS */
    ((XmlWriter) writer).writeText("}else{\n");
    
      /* start non-IE8 JS */
      ((XmlWriter) writer).writeText(" container = document.getElementsByClassName('"+rotationClassName+"')[0];\n"
          + " var totalLines, s = "+s+";\n"
          + "if( (navigator.userAgent.toLowerCase().indexOf('msie') != -1) && parseInt(navigator.userAgent.toLowerCase().split('msie')[1]) <= 10){\n"
          + "  container.innerHTML = linesIE10[0]+'<br>'; for (i = 1; i < linesIE10.length; ++i) { container.innerHTML += linesIE10[i]+'<br>'; }"
          + "  totalLines = "+linesIE10.size()+";\n"
          + "}else{\n"
          + "  container.innerHTML = lines[0]+'<br>'; for (i = 1; i < lines.length; ++i) { container.innerHTML += lines[i]+'<br>'; }"
          + "  totalLines = "+lines.size()+";\n"
          + "}\n"
          /* transformation matrix */
          + "var mStr= 'matrix(" + cos + "," + M12 + "," + sin + "," + cos + ",';\n"
          + "var elem = document.createElement('style');\n"
          + "elem.setAttribute('type','text/css');\n"
          + "elem.innerHTML = '." + rotationClassName + "{ ';\n");
      /* horizontal translation */
      if (hAlign.equals("null") || hAlign.equals(String.valueOf(ElementAlignment.LEFT)) || hAlign.equals(String.valueOf(ElementAlignment.JUSTIFY)))
      {
        if(this.rotationDegree==180f || this.rotationDegree==-180f){
          ((XmlWriter) writer).writeText(" elem.innerHTML = '." + rotationClassName + "{ text-align: right;';\n");
        }
        ((XmlWriter) writer).writeText("  mStr += ( -(container.offsetWidth/2) +(container.offsetWidth/2)*Math.abs(" + cos + ") +(container.offsetHeight/2)*Math.abs(" + sin + ") )+',';\n");
      }
      else if (hAlign.equals(String.valueOf(ElementAlignment.RIGHT)))
      {
        if(this.rotationDegree==180f || this.rotationDegree==-180f){
          ((XmlWriter) writer).writeText(" elem.innerHTML = '." + rotationClassName + "{ text-align: left;';\n");
        }
        ((XmlWriter) writer).writeText("  if (container.parentNode.offsetWidth >= container.offsetWidth){\n"
          + "     mStr += ( container.offsetWidth/2 -(container.offsetWidth/2)*Math.abs(" + cos + ") -(container.offsetHeight/2)*Math.abs(" + sin + ") )+',';\n"
          + "  }else{\n"
          + "     mStr += ( container.parentNode.offsetWidth-(container.offsetWidth/2) -(container.offsetWidth/2)*Math.abs(" + cos + ") -(container.offsetHeight/2)*Math.abs(" + sin + ") )+',';\n"
          + "  }\n");
      }
      else if (hAlign.equals(String.valueOf(ElementAlignment.CENTER)))
      {
        if(this.rotationDegree != 90f && this.rotationDegree!=-270f && this.rotationDegree!=-90f && this.rotationDegree!=270f && this.rotationDegree!=180f && this.rotationDegree!=-180f){
          ((XmlWriter) writer).writeText(" elem.innerHTML = '." + rotationClassName + "{ text-align: center;';\n");
        }
        ((XmlWriter) writer).writeText("  if (container.parentNode.offsetWidth < container.offsetWidth){\n"
            + "     mStr += ( container.parentNode.offsetWidth/2-container.offsetWidth/2 )+',';\n"
            + "  }else{\n"
            + "     mStr += '0,';\n"
            + "  }\n");
      }
      /* vertical translation */
      if (vAlign.equals("null") || vAlign.equals(String.valueOf(ElementAlignment.TOP)))
      {
        if (this.rotationDegree==90f || this.rotationDegree==-270f){
          ((XmlWriter) writer).writeText(" elem.innerHTML = '." + rotationClassName + "{ text-align: right;';\n");
        }else if (this.rotationDegree==-90 || this.rotationDegree==270){
          ((XmlWriter) writer).writeText(" elem.innerHTML = '." + rotationClassName + "{ text-align: left;';\n");
        }
        ((XmlWriter) writer).writeText("   mStr += (-container.offsetTop -container.offsetHeight/2 +(container.offsetHeight/2)*Math.abs("+cos+") +(container.offsetWidth/2)*Math.abs("+sin+"))+');';\n");
      }
      else if (vAlign.equals(String.valueOf(ElementAlignment.BOTTOM)))
      {
        if (this.rotationDegree==90f || this.rotationDegree==-270f){
          ((XmlWriter) writer).writeText(" elem.innerHTML = '." + rotationClassName + "{ text-align: left;';\n");
        }else if (this.rotationDegree==-90f || this.rotationDegree==270f){
          ((XmlWriter) writer).writeText(" elem.innerHTML = '." + rotationClassName + "{ text-align: right;';\n");
        }
        if (this.rotationDegree==180f || this.rotationDegree==-180f){
          ((XmlWriter) writer).writeText(" mStr += (container.parentNode.offsetHeight -(container.offsetHeight+container.offsetTop))+');';\n");
        }else{
          ((XmlWriter) writer).writeText(" mStr += (container.parentNode.offsetHeight -(container.offsetHeight/2+container.offsetTop)) -(container.offsetHeight/2)*Math.abs("+cos+") -(container.offsetWidth/2)*Math.abs("+sin+")+');';\n");
        }
      }
      else if (vAlign.equals(String.valueOf(ElementAlignment.MIDDLE)))
      {
        if (this.rotationDegree==90f || this.rotationDegree==-270f || this.rotationDegree==-90f || this.rotationDegree==270f){
          ((XmlWriter) writer).writeText(" elem.innerHTML = '." + rotationClassName + "{ text-align: center;';\n");
        }
        ((XmlWriter) writer).writeText(" mStr += (container.parentNode.offsetHeight/2 -(container.offsetHeight/2+container.offsetTop))+');';\n");
      }
        /* CSS3 IE 10.0, Firefox 16, Opera 12.1 */
      ((XmlWriter) writer).writeText(" elem.innerHTML += '" + StyleBuilder.CSSKeys.TRANSFORM.getCssName() + ":'+mStr\n"
        /* IE 9.0 */
        + " elem.innerHTML += '" + StyleBuilder.CSSKeys.MS_TRANSFORM.getCssName() + ":'+mStr\n"
        /* Chrome 12.0, Opera 15.0, Safari 3.1 */
        + " elem.innerHTML += '" + StyleBuilder.CSSKeys.WEBKIT_TRANSFORM.getCssName() + ":'+mStr\n"
        /* Firefox 3.5 */
        + " elem.innerHTML += '" + StyleBuilder.CSSKeys.MOZ_TRANSFORM.getCssName() + ":'+mStr\n"
        /* Opera 10.5 */
        + " elem.innerHTML += '" + StyleBuilder.CSSKeys.O_TRANSFORM.getCssName() + ":'+mStr+' }';\n"
        + " document.body.appendChild(elem);\n");
    ((XmlWriter) writer).writeText("}\n");// end non-IE8 JS
    ((XmlWriter) writer).writeText("</script>");
    return true;
  }
}

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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.DecimalFormat;

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
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
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
    final int textWidth = graphics2D.getFontMetrics().stringWidth(this.text);
    final LineMetrics lm = graphics2D.getFontMetrics().getLineMetrics(this.text, graphics2D);
    final float textHeight = lm.getHeight();

    String vAlign = String.valueOf(element.getStyle().getStyleProperty(ElementStyleKeys.VALIGNMENT));
    String hAlign = String.valueOf(element.getStyle().getStyleProperty(ElementStyleKeys.ALIGNMENT));

    // half dimension
    final float centerX = (float) bounds.getMaxX() / 2f,
        centerY = (float) bounds.getMaxY() / 2f;

    // coordinates to draw text centered
    final float drawX = ((float) bounds.getMaxX() - textWidth) / 2,
        drawY = (((float) bounds.getMaxY() + lm.getAscent()) / 2);

    // translate coordinates
    float translateX = 0f, translateY = 0f;
    if (hAlign.equals("null") || hAlign.equals(String.valueOf(ElementAlignment.LEFT)) || hAlign.equals(String.valueOf(ElementAlignment.JUSTIFY)))
    {
      if ((this.rotationDegree > 0 && this.rotationDegree <= 180) || (this.rotationDegree > -360 && this.rotationDegree <= -180))
      {
        translateX = -centerX + (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) + (lm.getAscent() / 2 + lm.getDescent()) * (float) Math.abs(Math.sin(this.rotationRadian));
      }
      else
      {
        translateX = -centerX + (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) + (textHeight / 2f) * (float) Math.abs(Math.sin(this.rotationRadian));
      }
    }
    else if (hAlign.equals(String.valueOf(ElementAlignment.RIGHT)))
    {
      if ((this.rotationDegree > 0 && this.rotationDegree <= 180) || (this.rotationDegree > -360 && this.rotationDegree <= -180))
      {
        translateX = centerX - (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) - (textHeight / 2f) * (float) Math.abs(Math.sin(this.rotationRadian));
      }
      else
      {
        translateX = centerX - (textWidth / 2f) * (float) Math.abs(Math.cos(this.rotationRadian)) - (lm.getAscent() / 2 + lm.getDescent()) * (float) Math.abs(Math.sin(this.rotationRadian));
      }
    }

    if (vAlign.equals("null") || vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.TOP)))
    {
      translateY = -centerY + (textWidth / 2f) * (float) Math.abs(Math.sin(this.rotationRadian)) + (lm.getAscent() / 2 + lm.getDescent()) * (float) Math.abs(Math.cos(this.rotationRadian));
    }
    else if (vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.BOTTOM)))
    {
      translateY = centerY - (textWidth / 2f) * (float) Math.abs(Math.sin(this.rotationRadian)) - (textHeight / 2f) * (float) Math.abs(Math.cos(this.rotationRadian));
    }

    graphics2D.translate(translateX, translateY);
    graphics2D.rotate(-this.rotationRadian, centerX, centerY);
    graphics2D.drawString(this.text, drawX, drawY - (lm.getDescent() / 2));
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

    DecimalFormat dc = new DecimalFormat("#.000000");
    final String cos = dc.format(Math.cos(getRotationDegree().doubleValue() * Math.PI / 180d)),
        M12 = dc.format(-1d * Math.sin(getRotationDegree().doubleValue() * Math.PI / 180d)),
        sin = dc.format(Math.sin(getRotationDegree().doubleValue() * Math.PI / 180d));

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

        /* client-side transformations */
        /* get directions */
    String vAlign = String.valueOf(getElement().getStyle().getStyleProperty(ElementStyleKeys.VALIGNMENT));
    String hAlign = String.valueOf(getElement().getStyle().getStyleProperty(ElementStyleKeys.ALIGNMENT));
    ((XmlWriter) writer).writeText("<div style='text-align: inherit; vertical-align: inherit; display: inline-block;'>\n");
    ((XmlWriter) writer).writeText("<div class='" + rotationClassName + "' style='clear: both; display: inline-block; overflow: hidden; white-space: nowrap;'>\n");
    ((XmlWriter) writer).writeText(getText());
    ((XmlWriter) writer).writeText("\n </div></div>\n"
        + "<script>\n"
        + "var elems = document.getElementsByTagName('div');\n"
        + "var currEl = undefined;\n"
        + "for (i in elems) {\n"
        + "  if(typeof currEl != 'undefined'){ break; }\n"
        + "  if( (''+elems[i].className).indexOf('" + rotationClassName + "') > -1) {\n"
        + "    currEl = elems[i];\n"
        + "  } }\n"
        // DEBUG: SEVERE: no matching DIV
        + "if(typeof currEl == 'undefined'){ throw 'error: calculating text width'; }\n"
        /* transformation matrix */
        + "var mStr= 'matrix(" + cos + "," + M12 + "," + sin + "," + cos + ",';\n");
    ((XmlWriter) writer).writeText("var h=0, v=0;\n"
        + "var elem = document.createElement('style');\n"
        + "elem.setAttribute('type','text/css');\n"
        + "if(elem.innerHTML == '' && !elem.styleSheet){\n");
        /* horizontal translation */
    if (hAlign.equals("null") || hAlign.equals(String.valueOf(ElementAlignment.LEFT)) || hAlign.equals(String.valueOf(ElementAlignment.JUSTIFY)))
    {
      ((XmlWriter) writer).writeText("  mStr += ( -(currEl.offsetWidth/2) +(currEl.offsetWidth/2)*Math.abs(" + cos + ") +(currEl.offsetHeight/2)*Math.abs(" + sin + ") )+',';\n");
    }
    else if (hAlign.equals(String.valueOf(ElementAlignment.RIGHT)))
    {
      ((XmlWriter) writer).writeText("  if (currEl.parentNode.parentNode.offsetWidth >= currEl.offsetWidth){\n"
          + "     mStr += ( currEl.offsetWidth/2 -((currEl.offsetWidth/2)*Math.abs(" + cos + ")) -((currEl.offsetHeight/2)*Math.abs(" + sin + ")) )+',';\n"
          + "  }else{\n"
          + "     mStr += ( currEl.parentNode.parentNode.offsetWidth-(currEl.offsetWidth/2+currEl.offsetHeight/8) -(currEl.offsetWidth/2)*Math.abs(" + cos + ") -(currEl.offsetHeight/2)*Math.abs(" + sin + ") )+',';\n"
          + "  }\n");
    }
    else if (hAlign.equals(String.valueOf(ElementAlignment.CENTER)))
    {
      ((XmlWriter) writer).writeText("  if (currEl.parentNode.parentNode.offsetWidth >= currEl.offsetWidth){\n"
          + "     mStr += '0,';\n"
          + "  }else{\n"
          + "     mStr += ( currEl.parentNode.parentNode.offsetWidth/2+currEl.offsetHeight/2-currEl.offsetWidth/2 -(currEl.offsetWidth/2)*Math.abs(" + cos + ") -(currEl.offsetHeight/2)*Math.abs(" + sin + ") )+',';\n"
          + "  }\n");
    }
        /* vertical translation */
    if (vAlign.equals("null") || vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.TOP)))
    {
      ((XmlWriter) writer).writeText("   mStr += ( -(currEl.offsetTop+currEl.offsetParent.offsetParent.offsetTop) +(currEl.offsetHeight/2)*Math.abs(" + cos + ") +(currEl.offsetWidth/2)*Math.abs(" + sin + ") )+');';\n");
    }
    else if (vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.BOTTOM)))
    {
      ((XmlWriter) writer).writeText("   mStr += ( (currEl.parentNode.parentNode.offsetHeight+currEl.offsetParent.offsetParent.offsetTop-(currEl.offsetTop+currEl.offsetHeight)) -(currEl.offsetHeight/2)*Math.abs(" + cos + ") -(currEl.offsetWidth/2)*Math.abs(" + sin + ") )+');';\n");
    }
    else if (vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.MIDDLE)))
    {
      ((XmlWriter) writer).writeText("   mStr += ( (currEl.parentNode.parentNode.offsetHeight/2+currEl.offsetParent.offsetParent.offsetTop)-(currEl.offsetHeight+currEl.offsetTop) )+');';\n");
    }
    ((XmlWriter) writer).writeText(" elem.innerHTML = '." + rotationClassName + "{ ';\n"
        /* CSS3 IE 10.0, Firefox 16, Opera 12.1 */
        + " elem.innerHTML += '" + StyleBuilder.CSSKeys.TRANSFORM.getCssName() + ":'+mStr;\n"
        /* IE 9.0 */
        + " elem.innerHTML += '" + StyleBuilder.CSSKeys.MS_TRANSFORM.getCssName() + ":'+mStr;\n"
        /* Chrome 12.0, Opera 15.0, Safari 3.1 */
        + " elem.innerHTML += '" + StyleBuilder.CSSKeys.WEBKIT_TRANSFORM.getCssName() + ":'+mStr;\n"
        /* Firefox 3.5 */
        + " elem.innerHTML += '" + StyleBuilder.CSSKeys.MOZ_TRANSFORM.getCssName() + ":'+mStr;\n"
        /* Opera 10.5 */
        + " elem.innerHTML += '" + StyleBuilder.CSSKeys.O_TRANSFORM.getCssName() + ":'+mStr+' }';\n"
        + " document.body.appendChild(elem);\n"
        + "}else{\n"
        + " var pCurr = currEl.parentElement;\n"
        + " var ppCurr = currEl.parentElement.parentElement;\n"
        + " ppCurr.removeChild(pCurr);\n"
        + " ppCurr.appendChild(currEl);\n"
        + " currEl.parentNode.setAttribute('style','vertical-align: top; white-space: normal;');\n");
        /* horizontal translation IE8 */
    if (hAlign.equals("null") || hAlign.equals(String.valueOf(ElementAlignment.LEFT)) || hAlign.equals(String.valueOf(ElementAlignment.JUSTIFY)))
    {
      ((XmlWriter) writer).writeText("  h = 0;\n");
    }
    else if (hAlign.equals(String.valueOf(ElementAlignment.RIGHT)))
    {
      ((XmlWriter) writer).writeText("  h = currEl.parentNode.offsetWidth - currEl.offsetWidth;\n");
    }
    else if (hAlign.equals(String.valueOf(ElementAlignment.CENTER)))
    {
      ((XmlWriter) writer).writeText("  h = (currEl.parentNode.offsetWidth - currEl.offsetWidth)/2;\n");
    }
    //+ " v = ( -(currEl.offsetTop+currEl.parentNode.offsetTop) +(currEl.offsetHeight/2)*Math.abs("+cos+") +(currEl.offsetWidth/2)*Math.abs("+sin+") );\n"
        /* vertical translation IE8 */
    if (vAlign.equals("null") || vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.TOP)))
    {
      ((XmlWriter) writer).writeText(" v = 0;\n"); // already forced to top
    }
    else if (vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.BOTTOM)))
    {
      ((XmlWriter) writer).writeText(" v = ( currEl.parentElement.offsetHeight-currEl.offsetHeight );\n");
    }
    else if (vAlign.equalsIgnoreCase(String.valueOf(VerticalTextAlign.MIDDLE)))
    {
      ((XmlWriter) writer).writeText(" v = ( currEl.parentElement.offsetHeight-currEl.offsetHeight )/2;\n");
    }

    ((XmlWriter) writer).writeText(" elem.styleSheet.cssText = '.'+currEl.className+'{ margin-left:'+h+'px; margin-top:'+v+'px;}';\n"
        + " document.getElementsByTagName('head')[0].appendChild(elem);\n}\n"
        + "</script>");

    return true;
  }
}

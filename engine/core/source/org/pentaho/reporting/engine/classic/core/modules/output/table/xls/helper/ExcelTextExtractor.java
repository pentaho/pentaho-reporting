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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.RichTextString;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.util.RichTextSpec;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.DefaultTextExtractor;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

/**
 * Creation-Date: 10.05.2007, 19:49:46
 *
 * @author Thomas Morgner
 */
public class ExcelTextExtractor extends DefaultTextExtractor
{
  private static final Log logger = LogFactory.getLog(ExcelTextExtractor.class);

  private static class RichTextFormat
  {
    private int position;
    private HSSFFontWrapper font;

    protected RichTextFormat(final int position, final HSSFFontWrapper font)
    {
      if (font == null)
      {
        throw new NullPointerException();
      }
      this.position = position;
      this.font = font;
    }

    public int getPosition()
    {
      return position;
    }

    public HSSFFontWrapper getFont()
    {
      return font;
    }


  }

  private ArrayList buffer;
  private ExcelColorProducer colorProducer;

  public ExcelTextExtractor(final OutputProcessorMetaData metaData,
                            final ExcelColorProducer colorProducer)
  {
    super(metaData);
    if (colorProducer == null)
    {
      throw new NullPointerException();
    }
    this.colorProducer = colorProducer;
    buffer = new ArrayList();
  }

  public Object compute(final RenderBox paraBox,
                        final ExcelFontFactory fontFactory,
                        final CreationHelper creationHelper)
  {
    buffer.clear();
    super.compute(paraBox);

    final String text = getText();
    if (buffer.size() <= 1)
    {
      // A simple result. So there's no need to create a rich-text string.
      final Object rawResult = getRawResult();
      if (rawResult != null && rawResult instanceof String == false)
      {

        return rawResult;
      }
      if (text.length() > 32767)
      {
        ExcelTextExtractor.logger.warn(
            "Excel-Cells cannot contain text larger than 32.737 characters. Text will be clipped.");
        return text.substring(0, 32767);
      }
      else if (text.length() > 0)
      {
        return text;
      }
      return null;
    }
    else if (text.length() > 0)
    {
      if (text.length() < 32768)
      {
        // There's rich text.
        final RichTextString rtStr = creationHelper.createRichTextString(text);
        for (int i = 0; i < buffer.size(); i++)
        {
          final RichTextFormat o = (RichTextFormat) buffer.get(i);
          final int position = o.getPosition();
          final HSSFFontWrapper font = o.getFont();
          if (i == (buffer.size() - 1))
          {
            // Last element ..
            rtStr.applyFont(position, text.length(), fontFactory.getExcelFont(font));
          }
          else
          {
            final RichTextFormat next = (RichTextFormat) buffer.get(i + 1);
            rtStr.applyFont(position, next.getPosition(), fontFactory.getExcelFont(font));
          }
        }
        return rtStr;
      }
      else
      {
        ExcelTextExtractor.logger.warn(
            "Excel-Cells cannot contain text larger than 32.737 characters. Text will be clipped.");

        final String realText = text.substring(0, 32767);
        final RichTextString rtStr = creationHelper.createRichTextString(realText);
        for (int i = 0; i < buffer.size(); i++)
        {
          final RichTextFormat o = (RichTextFormat) buffer.get(i);
          final int position = o.getPosition();
          if (position >= 32767)
          {
            break;
          }
          final HSSFFontWrapper font = o.getFont();
          if (i == (buffer.size() - 1))
          {
            // Last element ..
            final int endPosition = Math.min(32767, text.length());
            rtStr.applyFont(position, endPosition, fontFactory.getExcelFont(font));
          }
          else
          {
            final RichTextFormat next = (RichTextFormat) buffer.get(i + 1);
            final int endPosition = Math.min(32767, next.getPosition());
            rtStr.applyFont(position, endPosition, fontFactory.getExcelFont(font));
          }
        }
        return rtStr;
      }
    }
    return null;
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return false;
    }

    final StyleSheet styleSheet = box.getStyleSheet();
    final Color textColor = (Color) styleSheet.getStyleProperty(ElementStyleKeys.PAINT);
    final String fontName = (String) styleSheet.getStyleProperty(TextStyleKeys.FONT);
    final short fontSize = (short) styleSheet.getIntStyleProperty(TextStyleKeys.FONTSIZE, 0);
    final boolean bold = styleSheet.getBooleanStyleProperty(TextStyleKeys.BOLD);
    final boolean italic = styleSheet.getBooleanStyleProperty(TextStyleKeys.ITALIC);
    final boolean underline = styleSheet.getBooleanStyleProperty(TextStyleKeys.UNDERLINED);
    final boolean strikethrough = styleSheet.getBooleanStyleProperty(TextStyleKeys.STRIKETHROUGH);
    final HSSFFontWrapper wrapper = new HSSFFontWrapper
        (fontName, fontSize, bold, italic, underline, strikethrough, colorProducer.getNearestColor(textColor));
    final RichTextFormat rtf = new RichTextFormat(getTextLength(), wrapper);

    // Check the style.
    if (buffer.isEmpty())
    {
      buffer.add(rtf);
    }
    else
    {
      final RichTextFormat lastRtf = (RichTextFormat) buffer.get(buffer.size() - 1);
      if (lastRtf.getFont().equals(rtf.getFont()) == false)
      {
        buffer.add(rtf);
      }
    }

    return true;
  }

  protected void drawComplexText(final RenderableComplexText renderableComplexText)
  {
    if (renderableComplexText.getRawText().length() == 0)
    {
      // This text is empty.
      return;
    }
    if (renderableComplexText.isNodeVisible(getParagraphBounds(), isOverflowX(), isOverflowY()) == false)
    {
      return;
    }

    // check if we have to process inline text elements
    if(renderableComplexText.getRichText().getStyleChunks().size() > 1) {
      int relativeLength = 0;
      // iterate through all inline elements
      for(RichTextSpec.StyledChunk styledChunk: renderableComplexText.getRichText().getStyleChunks()) {
        // Add style for current styled chunk
        final StyleSheet styleSheet = styledChunk.getStyleSheet();
        final Color textColor = (Color) styleSheet.getStyleProperty(ElementStyleKeys.PAINT);
        final String fontName = (String) styleSheet.getStyleProperty(TextStyleKeys.FONT);
        final short fontSize = (short) styleSheet.getIntStyleProperty(TextStyleKeys.FONTSIZE, 0);
        final boolean bold = styleSheet.getBooleanStyleProperty(TextStyleKeys.BOLD);
        final boolean italic = styleSheet.getBooleanStyleProperty(TextStyleKeys.ITALIC);
        final boolean underline = styleSheet.getBooleanStyleProperty(TextStyleKeys.UNDERLINED);
        final boolean strikethrough = styleSheet.getBooleanStyleProperty(TextStyleKeys.STRIKETHROUGH);
        final HSSFFontWrapper wrapper = new HSSFFontWrapper
            (fontName, fontSize, bold, italic, underline, strikethrough, colorProducer.getNearestColor(textColor));

        final RichTextFormat rtf = new RichTextFormat(relativeLength, wrapper);
        relativeLength += styledChunk.getText().length();

        buffer.add(rtf);
      }
    }

    super.drawComplexText(renderableComplexText);
  }

}

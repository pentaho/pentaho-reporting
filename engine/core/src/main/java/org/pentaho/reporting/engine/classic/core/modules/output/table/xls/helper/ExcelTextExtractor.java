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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.RichTextString;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.text.RichTextSpec;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.DefaultTextExtractor;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.FastStack;

public class ExcelTextExtractor extends DefaultTextExtractor {
  private static final Log logger = LogFactory.getLog( ExcelTextExtractor.class );

  private ArrayList<RichTextFormat> formatBuffer;
  private FastStack<RichTextFormat> formatBufferStack;
  private ExcelColorProducer colorProducer;
  private CreationHelper creationHelper;
  private ExcelFontFactory fontFactory;

  public ExcelTextExtractor( final OutputProcessorMetaData metaData, final ExcelColorProducer colorProducer,
      final CreationHelper creationHelper, final ExcelFontFactory fontFactory ) {
    super( metaData );
    this.creationHelper = creationHelper;
    this.fontFactory = fontFactory;
    if ( colorProducer == null ) {
      throw new NullPointerException();
    }
    this.colorProducer = colorProducer;
    this.formatBuffer = new ArrayList<RichTextFormat>();
    this.formatBufferStack = new FastStack<RichTextFormat>();
  }

  public Object compute( final RenderBox paraBox ) {
    formatBuffer.clear();
    super.compute( paraBox );

    if ( formatBuffer.size() <= 1 ) {
      // A simple result. So there's no need to create a rich-text string.
      final Object rawResult = getRawResult();
      if ( rawResult != null && rawResult instanceof String == false ) {
        return rawResult;
      }
      final String text = getText();
      if ( text.length() > 32767 ) {
        ExcelTextExtractor.logger
            .warn( "Excel-Cells cannot contain text larger than 32.737 characters. Text will be clipped." );
        return text.substring( 0, 32767 );
      } else if ( text.length() > 0 ) {
        return text;
      }
      return null;
    }

    final String text = getText();
    return computeRichText( fontFactory, creationHelper, text, formatBuffer );
  }

  public static RichTextString computeRichText( final ExcelFontFactory fontFactory,
      final CreationHelper creationHelper, final String text, final ArrayList<RichTextFormat> buffer ) {
    if ( text.length() > 0 ) {
      if ( text.length() < 32768 ) {
        // There's rich text.
        final RichTextString rtStr = creationHelper.createRichTextString( text );
        for ( int i = 0; i < buffer.size(); i++ ) {
          final RichTextFormat o = buffer.get( i );
          final int position = o.getPosition();
          final HSSFFontWrapper font = o.getFont();
          if ( i == ( buffer.size() - 1 ) ) {
            // Last element ..
            rtStr.applyFont( position, text.length(), fontFactory.getExcelFont( font ) );
          } else {
            final RichTextFormat next = buffer.get( i + 1 );
            rtStr.applyFont( position, next.getPosition(), fontFactory.getExcelFont( font ) );
          }
        }
        return rtStr;
      } else {
        ExcelTextExtractor.logger
            .warn( "Excel-Cells cannot contain text larger than 32.737 characters. Text will be clipped." );

        final String realText = text.substring( 0, 32767 );
        final RichTextString rtStr = creationHelper.createRichTextString( realText );
        for ( int i = 0; i < buffer.size(); i++ ) {
          final RichTextFormat o = buffer.get( i );
          final int position = o.getPosition();
          if ( position >= 32767 ) {
            break;
          }
          final HSSFFontWrapper font = o.getFont();
          if ( i == ( buffer.size() - 1 ) ) {
            // Last element ..
            final int endPosition = Math.min( 32767, text.length() );
            rtStr.applyFont( position, endPosition, fontFactory.getExcelFont( font ) );
          } else {
            final RichTextFormat next = buffer.get( i + 1 );
            final int endPosition = Math.min( 32767, next.getPosition() );
            rtStr.applyFont( position, endPosition, fontFactory.getExcelFont( font ) );
          }
        }
        return rtStr;
      }
    }
    return null;
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    final StyleSheet styleSheet = box.getStyleSheet();
    final Color textColor = (Color) styleSheet.getStyleProperty( ElementStyleKeys.PAINT );
    final HSSFFontWrapper wrapper = new HSSFFontWrapper( styleSheet, colorProducer.getNearestColor( textColor ) );
    final RichTextFormat rtf = new RichTextFormat( getTextLength(), wrapper );

    // Check the style.
    if ( formatBuffer.isEmpty() ) {
      formatBuffer.add( rtf );
    } else {
      int lastIndex = formatBuffer.size() - 1;
      final RichTextFormat lastRtf = formatBuffer.get( lastIndex );
      if ( lastRtf.getPosition() == rtf.getPosition() ) {
        formatBuffer.set( lastIndex, rtf );
      } else if ( lastRtf.getFont().equals( rtf.getFont() ) == false ) {
        formatBuffer.add( rtf );
      }
    }

    formatBufferStack.push( rtf );

    return true;
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
    formatBufferStack.pop();
    if ( formatBufferStack.isEmpty() ) {
      return;
    }

    RichTextFormat rtf = formatBufferStack.peek();
    final RichTextFormat lastRtf = formatBuffer.get( formatBuffer.size() - 1 );
    if ( lastRtf.getFont().equals( rtf.getFont() ) == false ) {
      formatBuffer.add( new RichTextFormat( getTextLength(), rtf.getFont() ) );
    }
  }

  protected void drawComplexText( final RenderableComplexText renderableComplexText ) {
    if ( renderableComplexText.getRawText().length() == 0 ) {
      // This text is empty.
      return;
    }
    if ( renderableComplexText.isNodeVisible( getParagraphBounds(), isOverflowX(), isOverflowY() ) == false ) {
      return;
    }

    // check if we have to process inline text elements
    if ( renderableComplexText.getRichText().getStyleChunks().size() > 1 ) {
      int relativeLength = 0;
      // iterate through all inline elements
      for ( RichTextSpec.StyledChunk styledChunk : renderableComplexText.getRichText().getStyleChunks() ) {
        // Add style for current styled chunk
        final StyleSheet styleSheet = styledChunk.getStyleSheet();
        final Color textColor = (Color) styleSheet.getStyleProperty( ElementStyleKeys.PAINT );
        final String fontName = (String) styleSheet.getStyleProperty( TextStyleKeys.FONT );
        final short fontSize = (short) styleSheet.getIntStyleProperty( TextStyleKeys.FONTSIZE, 0 );
        final boolean bold = styleSheet.getBooleanStyleProperty( TextStyleKeys.BOLD );
        final boolean italic = styleSheet.getBooleanStyleProperty( TextStyleKeys.ITALIC );
        final boolean underline = styleSheet.getBooleanStyleProperty( TextStyleKeys.UNDERLINED );
        final boolean strikethrough = styleSheet.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH );
        final HSSFFontWrapper wrapper =
            new HSSFFontWrapper( fontName, fontSize, bold, italic, underline, strikethrough, colorProducer
                .getNearestColor( textColor ) );

        if ( styledChunk.getOriginatingTextNode() instanceof RenderableComplexText ) {
          final RichTextFormat rtf = new RichTextFormat( relativeLength, wrapper );
          relativeLength += styledChunk.getText().length();
          formatBuffer.add( rtf );
        }
      }
    }

    super.drawComplexText( renderableComplexText );
  }
}

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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelColorProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelFontFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.HSSFFontWrapper;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.RichTextFormat;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.FastStack;

public class FastExcelTextExtractor extends FastTextExtractor {
  private static final Log logger = LogFactory.getLog( FastExcelTextExtractor.class );

  private final ExcelFontFactory fontFactory;
  private final CreationHelper creationHelper;
  private final ArrayList<RichTextFormat> formatBuffer;
  private final ExcelColorProducer colorProducer;
  private final FastStack<RichTextFormat> formatBufferStack;

  public FastExcelTextExtractor( final ExcelColorProducer colorProducer, final ExcelFontFactory fontFactory,
      final CreationHelper creationHelper ) {
    this.colorProducer = colorProducer;
    this.formatBuffer = new ArrayList<RichTextFormat>();
    this.fontFactory = fontFactory;
    this.creationHelper = creationHelper;
    this.formatBufferStack = new FastStack<RichTextFormat>();
  }

  public Object compute( final ReportElement content, final ExpressionRuntime runtime )
    throws ContentProcessingException {
    this.formatBuffer.clear();
    super.compute( content, runtime );

    if ( formatBuffer.size() <= 1 ) {
      // A simple result. So there's no need to create a rich-text string.
      final Object rawResult = getRawResult();
      if ( rawResult != null && rawResult instanceof String == false ) {
        return rawResult;
      }
      final String text = getText();
      if ( text.length() > 32767 ) {
        logger.warn( "Excel-Cells cannot contain text larger than 32.737 characters. Text will be clipped." );
        return text.substring( 0, 32767 );
      } else if ( text.length() > 0 ) {
        return text;
      }
      return null;
    }

    final String text = getText();
    return ExcelTextExtractor.computeRichText( fontFactory, creationHelper, text, formatBuffer );
  }

  protected boolean inspectStartSection( final ReportElement box, final boolean inlineSection ) {
    SimpleStyleSheet styleSheet = box.getComputedStyle();
    if ( styleSheet.getBooleanStyleProperty( ElementStyleKeys.VISIBLE ) == false ) {
      return false;
    }

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

  @Override
  protected void handleValueContent( final ReportElement element, final Object value, final boolean inlineSection )
    throws ContentProcessingException {
    super.handleValueContent( element, value, inlineSection );
  }

  protected void inspectEndSection( final ReportElement box, final boolean inlineSection ) {
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
}

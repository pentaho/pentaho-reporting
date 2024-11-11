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


package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.PlainTextPage;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.TextFilePrinterDriver;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

import java.awt.print.Paper;
import java.io.IOException;

public class PageSizeTest extends TestCase {
  public PageSizeTest() {
  }

  public PageSizeTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testFontSize() {
    final OutputProcessorMetaData metaData = new TextOutputProcessorMetaData( 12, 12 );
    final FontMetrics fontMetrics = metaData.getFontMetrics( ElementDefaultStyleSheet.getDefaultStyle() );
    final long charWidth = fontMetrics.getCharWidth( 'A' );
    final long height = fontMetrics.getMaxHeight();
    assertEquals( 6000, height );
    assertEquals( 6000, charWidth );
  }

  public void testPageSize() throws IOException {
    final TextFilePrinterDriver pc = new TextFilePrinterDriver( new NullOutputStream(), 12, 12 );
    final Paper paper = PageFormatFactory.getInstance().createPaper( PageSize.A4 );
    pc.startPage( paper, "ASCII" );

    final PlainTextPage page = new PlainTextPage( paper, pc, "ASCII" );
    final int width = page.getWidth();
    final int height = page.getHeight();
    assertEquals( 99, width );
    assertEquals( 140, height );
  }
}

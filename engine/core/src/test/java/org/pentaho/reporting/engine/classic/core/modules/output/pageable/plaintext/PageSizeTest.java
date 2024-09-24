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

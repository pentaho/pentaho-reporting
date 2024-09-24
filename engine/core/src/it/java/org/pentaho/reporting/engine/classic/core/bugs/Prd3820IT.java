/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.SinglePageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfGraphics2D;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfLogicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.internal.XmlPageOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.support.itext.BaseFontModule;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.fonts.itext.ITextFontStorage;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;

public class Prd3820IT {

  private static class TestPdfLogicalPageDrawable extends PdfLogicalPageDrawable {
    private final Graphics2D g;
    private ArrayList<Long> textRendering;

    private TestPdfLogicalPageDrawable( final PdfWriter writer, final LFUMap<ResourceKey, Image> imageCache,
        final char version, final Graphics2D g ) {
      super( writer, imageCache, version );
      this.g = g;
      this.textRendering = new ArrayList<Long>();
    }

    public ArrayList<Long> getTextRendering() {
      return textRendering;
    }

    public void draw() {
      draw( g, new Rectangle2D.Double( 0, 0, 700, 500 ) );
    }

    protected void drawText( final RenderableText renderableText, final long contentX2 ) {
      super.drawText( renderableText, contentX2 );
      final PdfGraphics2D g2 = (PdfGraphics2D) getGraphics();
      PdfContentByte rawContentByte = g2.getRawContentByte();
      float ytlm = getGlobalHeight() - rawContentByte.getYTLM();
      textRendering.add( StrictGeomUtility.toInternalValue( ytlm ) );
    }
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testTextRenderingComplex() throws Exception {
    URL resource = getClass().getResource( "Prd-3820.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    LogicalPageBox logicalPageBox = layoutPage( report, 0 );
    ParagraphRenderBox first = (ParagraphRenderBox) MatchFactory.findElementByName( logicalPageBox, "first" );
    ParagraphRenderBox second = (ParagraphRenderBox) MatchFactory.findElementByName( logicalPageBox, "second" );
    // font size and height = 28, and 1pt for border.
    Assert.assertEquals( StrictGeomUtility.toInternalValue( 29 ), first.getFirstChild().getY2() );
    Assert.assertEquals( StrictGeomUtility.toInternalValue( 29 ), second.getFirstChild().getY2() );

    TestPdfLogicalPageDrawable pdf = createDrawableForTest( report, logicalPageBox );
    pdf.draw();
    Assert.assertEquals( 2, pdf.textRendering.size() );
    Assert.assertEquals( pdf.textRendering.get( 0 ), pdf.textRendering.get( 1 ) );
    Assert.assertTrue( pdf.textRendering.get( 0 ) < StrictGeomUtility.toInternalValue( 29 ) );
  }

  private LogicalPageBox layoutPage( final MasterReport report, final int page ) throws Exception {

    final DebugReportRunner.InterceptingXmlPageOutputProcessor outputProcessor =
        new DebugReportRunner.InterceptingXmlPageOutputProcessor( new NullOutputStream(),
            new XmlPageOutputProcessorMetaData( BaseFontModule.getFontRegistry() ) );
    outputProcessor.setFlowSelector( new SinglePageFlowSelector( page, false ) );
    final PageableReportProcessor proc = new PageableReportProcessor( report, outputProcessor );
    proc.processReport();

    if ( outputProcessor.getLogicalPageBox() == null ) {
      junit.framework.Assert.fail( "Did not find the requested page" );
    }

    return outputProcessor.getLogicalPageBox();
  }

  protected TestPdfLogicalPageDrawable createDrawableForTest( final MasterReport report,
      final LogicalPageBox logicalPageBox ) throws DocumentException {
    Document document = new Document();
    PdfWriter writer = PdfWriter.getInstance( document, new NullOutputStream() );
    writer.setLinearPageMode();
    writer.open();

    document.setPageSize( new com.lowagie.text.Rectangle( 700, 500 ) );
    document.setMargins( 10, 10, 10, 10 );
    document.open();

    PdfOutputProcessorMetaData metaData =
        new PdfOutputProcessorMetaData( new ITextFontStorage( BaseFontModule.getFontRegistry() ) );
    metaData.initialize( report.getConfiguration() );
    final Graphics2D graphics = new PdfGraphics2D( writer.getDirectContent(), 700, 500, metaData );

    TestPdfLogicalPageDrawable pdf =
        new TestPdfLogicalPageDrawable( writer, new LFUMap<ResourceKey, Image>( 10 ), '5', graphics );
    pdf.init( logicalPageBox, metaData, report.getResourceManager(), logicalPageBox.getPageGrid().getPage( 0, 0 ) );
    return pdf;
  }
}

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
import com.lowagie.text.pdf.PdfWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfGraphics2D;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfLogicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.support.itext.BaseFontModule;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.fonts.itext.ITextFontStorage;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;

public class Prd5321IT {

  private static class TestPdfLogicalPageDrawable extends PdfLogicalPageDrawable {
    private final Graphics2D g;
    private int textRendering;

    private TestPdfLogicalPageDrawable( final PdfWriter writer, final LFUMap<ResourceKey, Image> imageCache,
        final char version, final Graphics2D g ) {
      super( writer, imageCache, version );
      this.g = g;
    }

    public int getTextRendering() {
      return textRendering;
    }

    public void draw() {
      draw( g, new Rectangle2D.Double( 0, 0, 700, 500 ) );
    }

    protected void drawText( final RenderableText renderableText, final long contentX2 ) {
      textRendering += 1;
      super.drawText( renderableText, contentX2 );
    }

    protected void drawComplexText( final RenderableComplexText node, final Graphics2D g2 ) {
      textRendering += 1;
      super.drawComplexText( node, g2 );
    }
  }

  public Prd5321IT() {
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  /**
   * In 5.0 and later, elements are dynamic by default - if they only define a minimum height, and no max-height then
   * that element can grow as if dynamic=true has been set. To limit the element growth, use the max-height property.
   * <p/>
   * This test validates that growth happens unless explicitly restricted, and if restricted, that the first line is
   * printed.
   *
   * @throws Exception
   */
  @Test
  public void testTextLimitedInHeight() throws Exception {
    Element l = new Element();
    l.setElementType( LabelType.INSTANCE );
    l.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 10f );
    l.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    l.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 50 );
    l.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Label" );
    l.setName( "Label" );

    Element l2 = new Element();
    l2.setElementType( LabelType.INSTANCE );
    l2.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 100f );
    l.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    l2.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 10f );
    l2.getStyle().setStyleProperty( ElementStyleKeys.MAX_HEIGHT, 10f );
    l2.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 50 );
    l2.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Label" );
    l2.setName( "Label2" );

    MasterReport r = new MasterReport();
    r.getReportHeader().addElement( l );
    r.getReportFooter().addElement( l2 );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( r, 0 );
    ModelPrinter.INSTANCE.print( logicalPageBox );
    RenderNode label = MatchFactory.findElementByName( logicalPageBox, "Label" );
    // PRD-2736 note: the label is split into two parts now, since it does not fit the line width anymore:
    // Lab
    // el_
    Assert.assertEquals( StrictGeomUtility.toInternalValue( 100 ), label.getCachedHeight() );
    RenderNode label2 = MatchFactory.findElementByName( logicalPageBox, "Label2" );
    Assert.assertEquals( StrictGeomUtility.toInternalValue( 10 ), label2.getCachedHeight() );
  }

  /**
   * Tests the sample report in modern mode by setting the compatibility flag to "5.0". As none of the elements defines
   * a max-height, the elements will expect into multi-line elements.
   *
   * @throws Exception
   */
  @Test
  public void testModernMode() throws Exception {
    URL resource = getClass().getResource( "Prd-5321-2.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 5, 0, 0 ) );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    ModelPrinter.INSTANCE.print( logicalPageBox );

    RenderNode[] paragraphs = MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_PARAGRAPH );
    Assert.assertEquals( 4, paragraphs.length );
    for ( RenderNode paragraph : paragraphs ) {
      ParagraphRenderBox pb = (ParagraphRenderBox) paragraph;
      Assert.assertNotNull( pb.getFirstChild() );
    }
  }

  /**
   * Tests the rendering of text in compatibility mode. In this mode elements that do not define an explicit height can
   * be limited in growth to their defined minimum height. This auto-limit only takes place outside of crosstabs (which
   * were not available in pre-5.0 releases) and if the element is contained in a parent band that uses an canvas-layout
   * for its child elements.
   *
   * @throws Exception
   */
  @Test
  public void testCompatibilityMode() throws Exception {
    URL resource = getClass().getResource( "Prd-5321-2.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    report.setCompatibilityLevel( ClassicEngineBoot.VERSION_3_8 );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    RenderNode[] paragraphs = MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_PARAGRAPH );
    Assert.assertEquals( 4, paragraphs.length );
    for ( RenderNode paragraph : paragraphs ) {
      ParagraphRenderBox pb = (ParagraphRenderBox) paragraph;
      Assert.assertNotNull( pb.getFirstChild() );
      Assert.assertSame( pb.getFirstChild(), pb.getLastChild() );
    }
  }

  @Test
  public void testTextRendering() throws Exception {
    URL resource = getClass().getResource( "Prd-5321.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    ModelPrinter.INSTANCE.print( logicalPageBox );

    Assert.assertEquals( 6,
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_PARAGRAPH ).length );
    Assert.assertEquals( 13,
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_NODE_TEXT ).length );

    TestPdfLogicalPageDrawable pdf = createDrawableForTest( report, logicalPageBox );
    pdf.draw();
    Assert.assertEquals( 13, pdf.textRendering );
  }

  @Test
  public void testTextRenderingComplex() throws Exception {
    if ( !DebugReportRunner.isSafeToTestComplexText() ) {
      return;
    }
    Assert.assertTrue( DebugReportRunner.isSafeToTestComplexText() );

    URL resource = getClass().getResource( "Prd-5321.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    Assert.assertEquals( 6,
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_PARAGRAPH ).length );
    Assert.assertEquals( 8, MatchFactory
        .findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ).length );

    TestPdfLogicalPageDrawable pdf = createDrawableForTest( report, logicalPageBox );
    pdf.draw();
    Assert.assertEquals( 8, pdf.textRendering );
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

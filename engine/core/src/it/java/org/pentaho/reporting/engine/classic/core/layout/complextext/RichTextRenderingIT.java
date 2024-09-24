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
 *  Copyright (c) 2006 - 2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.complextext;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.build.RichTextStyleResolver;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.ParagraphLineBreakStep;
import org.pentaho.reporting.engine.classic.core.layout.process.text.RichTextSpec;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.html.FastHtmlImageBounds;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.html.FastHtmlTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriteException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.ContentUrlReWriteService;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultHtmlContentGenerator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultStyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultStyleBuilderFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlTagHelper;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.InlineStyleManager;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelColorProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelFontFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.StaticExcelColorSupport;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextDirection;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.AttributedString;
import java.util.HashMap;

import static org.junit.Assert.*;

public class RichTextRenderingIT {
  public RichTextRenderingIT() {
  }

  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  // @Test
  public void testPdf() throws ResourceException, ReportProcessingException, IOException {
    URL resource = getClass().getResource( "rich-text-sample1.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.LTR );
    report.getReportHeader().getElement( 1 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.RTL );
    report.getReportHeader().removeElement( 0 );
    report.getReportHeader().getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.YELLOW );
    report.getReportFooter().clear();
    // DebugReportRunner.showDialog(report);
    // PdfReportUtil.createPDF(report, new File(DebugReportRunner.createTestOutputFile(), "rich-text-sample1.pdf"));
  }

  // @Test
  public void testPdfRendering() throws Exception {
    MasterReport report = new MasterReport();
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    report
        .getReportHeader()
        .addElement(
            createDataItem(
                "A longer text asda askdjalejqi halfhlajdfh askdjfha ksdfjhLKFDH ASKDJFHAS asda askdjalejqi halfhlajdfh "
                    + "askdjfha ksdfjhLKFDH ASKDJFHAS asda askdjalejqi halfhlajdfh askdjfha ksdfjhLKFDH ASKDJFHAS DKFHSDKF",
                400, 60 ) );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.RTL );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.FONT, "Arial Unicode MS" );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 15 );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR,
        Color.YELLOW );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT,
        ElementAlignment.RIGHT );
    // DebugReportRunner.showDialog(report);
    // PdfReportUtil.createPDF(report, new File(DebugReportRunner.createTestOutputFile(), "rich-text-sample1.pdf"));
  }

  @Test
  public void testExcelRendering() throws Exception {
    URL resource = getClass().getResource( "rich-text-sample1.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.LTR );
    report.getReportHeader().getElement( 1 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.RTL );
    report.getReportHeader().removeElement( 0 );
    report.getReportHeader().getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.YELLOW );
    report.getReportFooter().clear();

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    RenderNode second = MatchFactory.findElementByName( logicalPageBox, "second" );
    assertTrue( second instanceof RenderBox );

    ExcelOutputProcessorMetaData metaData =
        new ExcelOutputProcessorMetaData( ExcelOutputProcessorMetaData.PAGINATION_FULL );
    metaData.initialize( report.getConfiguration() );

    XSSFWorkbook hssfWorkbook = new XSSFWorkbook();
    ExcelColorProducer colorProducer = new StaticExcelColorSupport();
    ExcelFontFactory ff = new ExcelFontFactory( hssfWorkbook, colorProducer );
    CreationHelper ch = hssfWorkbook.getCreationHelper();
    ExcelTextExtractor te = new ExcelTextExtractor( metaData, colorProducer, ch, ff );

    Object compute = te.compute( (RenderBox) second );
    assertTrue( compute instanceof RichTextString );
    XSSFRichTextString rt = (XSSFRichTextString) compute;
    assertEquals( 4, rt.numFormattingRuns() );
  }

  @Test
  public void testFastExcelRendering() throws Exception {
    URL resource = getClass().getResource( "rich-text-sample1.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.LTR );
    report.getReportHeader().getElement( 1 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.RTL );
    report.getReportHeader().removeElement( 0 );
    report.getReportHeader().getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.YELLOW );
    report.getReportFooter().clear();

    ExpressionRuntime runtime =
        new GenericExpressionRuntime( new DefaultTableModel(), 0, new DefaultProcessingContext( report ) );

    RichTextStyleResolver resolver = new RichTextStyleResolver( runtime.getProcessingContext(), report );
    resolver.resolveRichTextStyle( report );

    XSSFWorkbook hssfWorkbook = new XSSFWorkbook();
    ExcelColorProducer colorProducer = new StaticExcelColorSupport();
    ExcelFontFactory ff = new ExcelFontFactory( hssfWorkbook, colorProducer );
    CreationHelper ch = hssfWorkbook.getCreationHelper();
    FastExcelTextExtractor te = new FastExcelTextExtractor( colorProducer, ff, ch );

    Element element = report.getReportHeader().getElement( 0 );
    Object compute = te.compute( element, runtime );
    assertTrue( compute instanceof RichTextString );
    XSSFRichTextString rt = (XSSFRichTextString) compute;
    assertEquals( 4, rt.numFormattingRuns() );
  }

  @Test
  public void testHtmlRendering() throws Exception {
    URL resource = getClass().getResource( "rich-text-sample1.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.LTR );
    report.getReportHeader().getElement( 1 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.RTL );
    report.getReportHeader().removeElement( 0 );
    report.getReportHeader().getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.YELLOW );
    report.getReportFooter().clear();

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    RenderNode second = MatchFactory.findElementByName( logicalPageBox, "second" );
    assertTrue( second instanceof RenderBox );

    HtmlRenderingSetup setup = new HtmlRenderingSetup( report );
    HtmlTextExtractor te = setup.createTextExtractor();

    assertTrue( te.performOutput( (RenderBox) second, setup.productImpliedStyles() ) );
    String text = setup.getResult();
    String start = text.substring( 0, FAST_HTML_MATCH.length() );
    assertEquals( FAST_HTML_MATCH, start );
    assertTrue( text.endsWith( "</span>\n" ) );
  }

  @Test
  public void testFastHtmlRendering() throws Exception {
    URL resource = getClass().getResource( "rich-text-sample1.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.LTR );
    report.getReportHeader().getElement( 1 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.RTL );
    report.getReportHeader().removeElement( 0 );
    report.getReportHeader().getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.YELLOW );
    report.getReportFooter().clear();

    RichTextStyleResolver resolver = new RichTextStyleResolver( new DefaultProcessingContext(), report );
    resolver.resolveRichTextStyle( report );

    HtmlRenderingSetup setup = new HtmlRenderingSetup( report );
    FastHtmlTextExtractor te = setup.createFastExtractor();

    Band element = (Band) report.getReportHeader().getElement( 0 );
    HashMap<InstanceID, FastHtmlImageBounds> recordedBounds = new HashMap<InstanceID, FastHtmlImageBounds>();
    FastHtmlImageBounds bounds =
        new FastHtmlImageBounds( StrictGeomUtility.toInternalValue( 20.465 ), StrictGeomUtility
            .toInternalValue( 20.465 ), StrictGeomUtility.toInternalValue( 16 ), StrictGeomUtility.toInternalValue( 16 ) );
    recordedBounds.put( element.getElement( 1 ).getObjectID(), bounds );

    ExpressionRuntime runtime =
        new GenericExpressionRuntime( new DefaultTableModel(), 0, new DefaultProcessingContext( report ) );
    assertTrue( te.performOutput( element, setup.productImpliedStyles(), recordedBounds, runtime ) );
    String text = setup.getResult();
    String start = text.substring( 0, FAST_HTML_MATCH.length() );
    assertEquals( FAST_HTML_MATCH, start );
    assertTrue( text.endsWith( "</span>\n" ) );
  }

  private static final String FAST_HTML_MATCH = "<span>Label</span>\n"
      + "<span><img src=\"image.gif\" border=\"0\" style=\"width: 27px; height: 27px\"/></span>\n"
      + "<span style=\"font-size: 15pt\">Label</span>\n" + "<span style=\"font-size: 20pt\">Label</span>\n" + "<span>";

  public static Element createDataItem( final String text, final float width, final float height ) {
    final Element label = new Element();
    label.setName( "Label" );
    label.setElementType( LabelType.INSTANCE );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, width );
    label.getStyle().setStyleProperty( ElementStyleKeys.MAX_WIDTH, width );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, height );
    label.getStyle().setStyleProperty( ElementStyleKeys.MAX_HEIGHT, height );
    return label;
  }

  // @Test
  public void testGraphics() throws ResourceException, ReportProcessingException, IOException {
    URL resource = getClass().getResource( "rich-text-sample1.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.LTR );
    report.getReportHeader().getElement( 1 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.RTL );
    DebugReportRunner.execGraphics2D( report );

  }

  @Test
  public void testGraphics2D() throws Exception {
    URL resource = getClass().getResource( "rich-text-sample1.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    RenderNode first = MatchFactory.findElementByName( logicalPageBox, "first" );
    assertNotNull( first );
    assertTrue( first.getHeight() > StrictGeomUtility.toInternalValue( 20 ) );

    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( first, LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT );
    assertEquals( 1, elementsByNodeType.length );
    assertTrue( elementsByNodeType[0] instanceof RenderableComplexText );

    RenderableComplexText text = (RenderableComplexText) elementsByNodeType[0];
    RichTextSpec richText = text.getRichText();
    assertEquals( 4, richText.getStyleChunks().size() );
    assertEquals( "Label@LabelLabel", richText.getText() );

    RenderNode second = MatchFactory.findElementByName( logicalPageBox, "second" );
    assertTrue( second instanceof ParagraphRenderBox );
    ParagraphRenderBox p = (ParagraphRenderBox) second;
    assertTrue( p.getPool().getFirstChild().getNext() instanceof RenderableReplacedContentBox );

    assertTrue( second.getHeight() > StrictGeomUtility.toInternalValue( 20 ) );
    RenderNode[] secondText = MatchFactory.findElementsByNodeType( second, LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT );
    assertTrue( secondText.length > 10 ); // this is an approximate value. There is no safe stable value with complex
                                          // text
    assertTrue( secondText[0] instanceof RenderableComplexText );
  }

  @Test
  public void testLineBreaking() throws Exception {
    URL resource = getClass().getResource( "rich-text-sample1.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    report.getReportHeader().removeElement( report.getReportHeader().getElement( 0 ) );
    report.getReportHeader().getElement( 0 ).getStyle().setStyleProperty( TextStyleKeys.DIRECTION, TextDirection.RTL );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    RenderNode second = MatchFactory.findElementByName( logicalPageBox, "second" );
    assertTrue( second instanceof ParagraphRenderBox );
    ParagraphRenderBox p = (ParagraphRenderBox) second;
    assertTrue( p.getPool().getFirstChild().getNext() instanceof RenderableReplacedContentBox );

    ParagraphLineBreakStep step = new ParagraphLineBreakStep();
    step.compute( logicalPageBox );

    RenderNode[] elementsByNodeType = MatchFactory.findElementsByNodeType( p, LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT );
    assertContainsImage( elementsByNodeType );
    RenderableComplexText t = (RenderableComplexText) elementsByNodeType[0];
    AttributedString attributedString = t.getRichText().getAttributedString();
    assertEquals( TextAttribute.RUN_DIRECTION_RTL, attributedString.getIterator().getAttribute(
        TextAttribute.RUN_DIRECTION ) );

  }

  private void assertContainsImage( RenderNode[] elementsByNodeType ) {
    for ( RenderNode renderNode : elementsByNodeType ) {
      RenderableComplexText t = (RenderableComplexText) renderNode;
      RichTextSpec richText = t.getRichText();
      for ( RichTextSpec.StyledChunk styledChunk : richText.getStyleChunks() ) {
        if ( styledChunk.getAttributes().containsKey( TextAttribute.CHAR_REPLACEMENT ) ) {
          return;
        }
      }

    }
    Assert.fail();
  }

  private static class DummyContentUrlReWriteService implements ContentUrlReWriteService {
    public String rewriteContentDataItem( final ContentItem item ) throws URLRewriteException {
      return item.getName();
    }
  }

  private static class HtmlRenderingSetup {
    private final StringWriter sw;
    private final XmlWriter writer;
    private MasterReport report;

    private HtmlRenderingSetup( final MasterReport report ) {
      this.report = report;
      sw = new StringWriter();
      writer = new XmlWriter( sw, new DefaultTagDescription(), "  ", "\n" );
      writer.addImpliedNamespace( "http://www.w3.org/1999/xhtml", "" );
    }

    public String getResult() {
      return sw.toString();
    }

    private XmlWriter getWriter() {
      return writer;
    }

    private StyleBuilder.StyleCarrier[] productImpliedStyles() {
      DefaultStyleBuilder builder = new DefaultStyleBuilder( new DefaultStyleBuilderFactory() );
      builder.append( StyleBuilder.CSSKeys.COLOR, "black" );
      builder.append( StyleBuilder.CSSKeys.FONT_SIZE, "10", "pt" );
      builder.appendRaw( StyleBuilder.CSSKeys.FONT_FAMILY, "\"Arial Unicode MS\"" );
      builder.append( StyleBuilder.CSSKeys.FONT_WEIGHT, "normal" );
      builder.append( StyleBuilder.CSSKeys.FONT_STYLE, "normal" );
      builder.append( StyleBuilder.CSSKeys.TEXT_DECORATION, "none" );
      builder.append( StyleBuilder.CSSKeys.TEXT_ALIGN, "right" );
      builder.append( StyleBuilder.CSSKeys.WORD_SPACING, "0", "pt" );
      builder.append( StyleBuilder.CSSKeys.LETTER_SPACING, "0", "pt" );
      builder.append( StyleBuilder.CSSKeys.WHITE_SPACE, "pre" );
      return builder.toArray();
    }

    public HtmlTextExtractor createTextExtractor() throws ContentIOException {
      OutputProcessorMetaData metaData = createMetaData();
      DefaultHtmlContentGenerator contentGenerator = createContentGenerator();
      HtmlTagHelper tagHelper = createTagHelper();
      return new HtmlTextExtractor( metaData, getWriter(), contentGenerator, tagHelper );
    }

    private HtmlTagHelper createTagHelper() {
      HtmlTagHelper tagHelper = new HtmlTagHelper( report.getConfiguration(), new DefaultStyleBuilderFactory() );
      tagHelper.setStyleManager( new InlineStyleManager() );
      return tagHelper;
    }

    private DefaultHtmlContentGenerator createContentGenerator() throws ContentIOException {
      ZipRepository zr = new ZipRepository( new ByteArrayOutputStream() );
      DefaultHtmlContentGenerator contentGenerator = new DefaultHtmlContentGenerator( report.getResourceManager() );
      contentGenerator.setDataWriter( zr.getRoot(), new DefaultNameGenerator( zr.getRoot() ),
          new RichTextRenderingIT.DummyContentUrlReWriteService() );
      return contentGenerator;
    }

    private OutputProcessorMetaData createMetaData() {
      OutputProcessorMetaData metaData = new HtmlOutputProcessorMetaData( HtmlOutputProcessorMetaData.PAGINATION_NONE );
      metaData.initialize( report.getReportConfiguration() );
      return metaData;
    }

    public FastHtmlTextExtractor createFastExtractor() throws ContentIOException {
      OutputProcessorMetaData metaData = createMetaData();
      DefaultHtmlContentGenerator contentGenerator = createContentGenerator();
      HtmlTagHelper tagHelper = createTagHelper();
      return new FastHtmlTextExtractor( metaData, getWriter(), contentGenerator, tagHelper );
    }
  }
}

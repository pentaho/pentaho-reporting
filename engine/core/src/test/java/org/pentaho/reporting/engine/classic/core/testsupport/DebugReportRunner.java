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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport;

import java.awt.GraphicsEnvironment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.EmptyReportException;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.ReportProcessor;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.modules.output.csv.CSVDataReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.SinglePageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PlainTextReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.XmlPageOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.XmlPageReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.internal.XmlPageOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.PageableHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.SingleRepositoryURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.internal.XmlTableOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.xml.XMLProcessor;
import org.pentaho.reporting.engine.classic.core.states.DefaultPerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;
import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontRegistry;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.fonts.monospace.MonospaceFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontStorage;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * @noinspection HardCodedStringLiteral
 */
public class DebugReportRunner {
  private static class StaticStyleResolver extends AbstractStructureVisitor {
    private SimpleStyleResolver simpleStyleResolver;
    private ResolverStyleSheet resolveStyleSheet;

    private StaticStyleResolver() {
      this.simpleStyleResolver = new SimpleStyleResolver( true );
      this.resolveStyleSheet = new ResolverStyleSheet();
    }

    public void resolve( final Section section ) {
      inspectElement( section );
      traverseSection( section );
    }

    protected void inspectElement( final ReportElement element ) {
      simpleStyleResolver.resolve( element, resolveStyleSheet );
      element.setComputedStyle( new SimpleStyleSheet( resolveStyleSheet ) );
    }
  }

  private static final Log logger = LogFactory.getLog( DebugReportRunner.class );

  private DebugReportRunner() {
  }

  public static boolean createPlainText( final MasterReport report ) {
    try {
      PlainTextReportUtil.createPlainText( report, new NullOutputStream(), 10, 15 );
      return true;
    } catch ( ReportParameterValidationException p ) {
      return true;
    } catch ( Exception rpe ) {
      logger.debug( "Failed to execute plain text: ", rpe );
      Assert.fail();
      return false;
    }
  }

  public static void createRTF( final MasterReport report ) throws Exception {
    try {
      RTFReportUtil.createRTF( report, new NullOutputStream() );
    } catch ( IndexOutOfBoundsException ibe ) {
      // this is a known iText bug that does not get fixed.
    } catch ( ReportParameterValidationException p ) {
      // reports that have mandatory parameters are ok to fail.
      Assert.fail();
    }
  }

  public static byte[] createXmlTablePageable( final MasterReport report ) throws IOException,
    ReportProcessingException {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try {
      final LocalFontRegistry localFontRegistry = new LocalFontRegistry();
      localFontRegistry.initialize();
      final XmlTableOutputProcessor outputProcessor =
          new XmlTableOutputProcessor( outputStream, new XmlTableOutputProcessorMetaData(
              XmlTableOutputProcessorMetaData.PAGINATION_FULL, localFontRegistry ) );
      final ReportProcessor streamReportProcessor = new PageableReportProcessor( report, outputProcessor );
      try {
        streamReportProcessor.processReport();
      } finally {
        streamReportProcessor.close();
      }
    } finally {
      outputStream.close();
    }
    return ( outputStream.toByteArray() );
  }

  public static void createXmlPageable( final MasterReport report ) throws Exception {
    XmlPageReportUtil.createXml( report, new NullOutputStream() );
  }

  public static void createXmlFlow( final MasterReport report ) throws Exception {
    XmlTableReportUtil.createFlowXML( report, new NullOutputStream() );
  }

  public static void createXmlStream( final MasterReport report ) throws Exception {
    XmlTableReportUtil.createStreamXML( report, new NullOutputStream() );
  }

  public static void createCSV( final MasterReport report ) throws Exception {
    try {
      CSVReportUtil.createCSV( report, new NullOutputStream(), null );
    } catch ( ReportParameterValidationException e ) {
      Assert.fail();
    }
  }

  public static void createDataCSV( final MasterReport report ) throws Exception {
    try {
      CSVDataReportUtil.createCSV( report, new NullOutputStream(), "UTF-8" );
    } catch ( ReportParameterValidationException e ) {
      Assert.fail();
    }
  }

  public static void createDataXML( final MasterReport report ) throws Exception {
    try {
      final XMLProcessor pr = new XMLProcessor( report );
      final Writer fout = new BufferedWriter( new OutputStreamWriter( new NullOutputStream(), "UTF-8" ) );
      pr.setWriter( fout );
      pr.processReport();
      fout.flush();
    } catch ( ReportParameterValidationException e ) {
      Assert.fail();
    }
  }

  public static void createXLS( final MasterReport report ) throws Exception {
    try {
      ExcelReportUtil.createXLS( report, new NullOutputStream() );
    } catch ( ReportParameterValidationException e ) {
      Assert.fail();
    }
  }

  public static void createStreamHTML( final MasterReport report ) throws Exception {
    try {
      HtmlReportUtil.createStreamHTML( report, new NullOutputStream() );
    } catch ( ReportParameterValidationException e ) {
      Assert.fail();
    }
  }

  public static void createZIPHTML( final MasterReport report ) throws Exception {
    try {
      HtmlReportUtil.createZIPHTML( report, new NullOutputStream(), "report.html" );
    } catch ( ReportParameterValidationException e ) {
      Assert.fail();
    }
  }

  public static void createPageableHTML( final MasterReport report ) throws Exception {
    try {
      if ( report == null ) {
        throw new NullPointerException();
      }

      try {
        final ZipRepository zipRepository = new ZipRepository( new NullOutputStream() );
        final ContentLocation root = zipRepository.getRoot();
        final ContentLocation data =
            RepositoryUtilities.createLocation( zipRepository, RepositoryUtilities.splitPath( "data", "/" ) );

        final PageableHtmlOutputProcessor outputProcessor = new PageableHtmlOutputProcessor( report.getConfiguration() );

        final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
        printer.setContentWriter( root, new DefaultNameGenerator( root, "report.html" ) );
        printer.setDataWriter( data, new DefaultNameGenerator( data, "content" ) );
        printer.setUrlRewriter( new SingleRepositoryURLRewriter() );
        outputProcessor.setPrinter( printer );

        final PageableReportProcessor sp = new PageableReportProcessor( report, outputProcessor );
        sp.processReport();
        sp.close();
        zipRepository.close();
      } catch ( IOException ioe ) {
        throw ioe;
      } catch ( ReportProcessingException re ) {
        throw re;
      } catch ( Exception re ) {
        throw new ReportProcessingException( "Failed to process the report", re );
      }
    } catch ( ReportParameterValidationException e ) {
      Assert.fail();
    }
  }

  public static int execGraphics2D( final MasterReport report ) {
    try {
      final PrintReportProcessor proc = new PrintReportProcessor( report );
      final int nop = proc.getNumberOfPages();
      if ( proc.isError() ) {
        if ( proc.getErrorReason() instanceof ReportParameterValidationException ) {
          return 0;
        }
        Assert.fail();
        return -1;
      }
      if ( nop == 0 ) {
        return 0;
      }
      for ( int i = 0; i < nop; i++ ) {
        if ( proc.getPageDrawable( i ) == null ) {
          Assert.fail();
          return -1;
        }
      }
      proc.close();
      return nop;
    } catch ( ReportParameterValidationException p ) {
      // reports that have mandatory parameters are ok to fail.
      return 0;
    } catch ( EmptyReportException ere ) {
      return 0;
    } catch ( Exception e ) {
      logger.error( "Generating Graphics2D failed.", e );
      Assert.fail();
      return -1;
    }
  }

  public static class InterceptingXmlPageOutputProcessor extends XmlPageOutputProcessor {
    private List<LogicalPageBox> logicalPageBox;

    public InterceptingXmlPageOutputProcessor( final OutputStream outputStream, final OutputProcessorMetaData metaData ) {
      super( outputStream, metaData );
      this.logicalPageBox = new ArrayList<LogicalPageBox>();
    }

    protected void processPhysicalPage( final PageGrid pageGrid, final LogicalPageBox logicalPage, final int row,
        final int col, final PhysicalPageKey pageKey ) throws ContentProcessingException {
      logicalPageBox.add( logicalPage.derive( true ) );
    }

    protected void processLogicalPage( final LogicalPageKey key, final LogicalPageBox logicalPage )
      throws ContentProcessingException {
      logicalPageBox.add( logicalPage.derive( true ) );
    }

    public LogicalPageBox getLogicalPageBox() {
      if ( logicalPageBox.size() == 1 ) {
        return logicalPageBox.get( 0 );
      } else {
        throw new IllegalStateException();
      }
    }

    public List<LogicalPageBox> getPages() {
      return Collections.unmodifiableList( logicalPageBox );
    }
  }

  private static class InterceptingXmlTableOutputProcessor extends XmlTableOutputProcessor {
    private LogicalPageBox logicalPageBox;
    private FlowSelector flowSelector;

    private InterceptingXmlTableOutputProcessor( final OutputStream outputStream, final OutputProcessorMetaData metaData ) {
      super( outputStream, metaData );
    }

    public void setFlowSelector( final FlowSelector flowSelector ) {
      this.flowSelector = flowSelector;
    }

    protected FlowSelector getFlowSelector() {
      return flowSelector;
    }

    protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
        final TableContentProducer contentProducer ) throws ContentProcessingException {
      logicalPageBox = logicalPage.derive( true );
    }

    public LogicalPageBox getLogicalPageBox() {
      return logicalPageBox;
    }
  }

  public static LogicalPageBox layoutPage( final MasterReport report, final int page ) throws Exception {
    final LocalFontRegistry localFontRegistry = new LocalFontRegistry();
    localFontRegistry.initialize();

    final InterceptingXmlPageOutputProcessor outputProcessor =
        new InterceptingXmlPageOutputProcessor( new NullOutputStream(), new XmlPageOutputProcessorMetaData(
            localFontRegistry ) );
    outputProcessor.setFlowSelector( new SinglePageFlowSelector( page, false ) );
    final PageableReportProcessor proc = new PageableReportProcessor( report, outputProcessor );
    proc.processReport();

    if ( outputProcessor.getLogicalPageBox() == null ) {
      Assert.fail( "Did not find the requested page" );
    }

    return outputProcessor.getLogicalPageBox();
  }

  public static LogicalPageBox layoutPageStrict( final MasterReport report, final int maxPage, final int page )
    throws Exception {
    final LocalFontRegistry localFontRegistry = new LocalFontRegistry();
    localFontRegistry.initialize();

    final InterceptingXmlPageOutputProcessor outputProcessor =
        new InterceptingXmlPageOutputProcessor( new NullOutputStream(), new XmlPageOutputProcessorMetaData(
            localFontRegistry ) );
    outputProcessor.setFlowSelector( new StrictMultiPageFlowSelector( false, maxPage, page ) );
    final PageableReportProcessor proc = new PageableReportProcessor( report, outputProcessor );
    proc.processReport();

    if ( outputProcessor.getLogicalPageBox() == null ) {
      Assert.fail( "Did not find the requested page" );
    }

    return outputProcessor.getLogicalPageBox();
  }

  public static List<LogicalPageBox> layoutPages( final MasterReport report, final int... page ) throws Exception {
    final LocalFontRegistry localFontRegistry = new LocalFontRegistry();
    localFontRegistry.initialize();

    final InterceptingXmlPageOutputProcessor outputProcessor =
        new InterceptingXmlPageOutputProcessor( new NullOutputStream(), new XmlPageOutputProcessorMetaData(
            localFontRegistry ) );
    outputProcessor.setFlowSelector( new MultiPageFlowSelector( false, page ) );
    final PageableReportProcessor proc = new PageableReportProcessor( report, outputProcessor );
    proc.processReport();

    List<LogicalPageBox> pages = outputProcessor.getPages();
    Assert.assertEquals( "Pages have been generated", page.length, pages.size() );
    return pages;
  }

  public static List<LogicalPageBox>
    layoutPagesStrict( final MasterReport report, final int maxPage, final int... page ) throws Exception {
    final LocalFontRegistry localFontRegistry = new LocalFontRegistry();
    localFontRegistry.initialize();

    final InterceptingXmlPageOutputProcessor outputProcessor =
        new InterceptingXmlPageOutputProcessor( new NullOutputStream(), new XmlPageOutputProcessorMetaData(
            localFontRegistry ) );
    outputProcessor.setFlowSelector( new StrictMultiPageFlowSelector( false, maxPage, page ) );
    final PageableReportProcessor proc = new PageableReportProcessor( report, outputProcessor );
    proc.processReport();

    List<LogicalPageBox> pages = outputProcessor.getPages();
    Assert.assertEquals( "Pages have been generated", page.length, pages.size() );
    return pages;
  }

  public static LogicalPageBox layoutTablePage( final MasterReport report, final int page ) throws Exception {
    final LocalFontRegistry localFontRegistry = new LocalFontRegistry();
    localFontRegistry.initialize();

    final InterceptingXmlTableOutputProcessor outputProcessor =
        new InterceptingXmlTableOutputProcessor( new NullOutputStream(), new XmlTableOutputProcessorMetaData(
            XmlTableOutputProcessorMetaData.PAGINATION_MANUAL, localFontRegistry ) );
    outputProcessor.setFlowSelector( new SinglePageFlowSelector( page, true ) );
    final ReportProcessor proc = new FlowReportProcessor( report, outputProcessor );
    proc.processReport();

    if ( outputProcessor.getLogicalPageBox() == null ) {
      Assert.fail( "Did not find the requested page" );
    }

    return outputProcessor.getLogicalPageBox();
  }

  /**
   * Saves a report to PDF format.
   *
   * @param report
   *          the report.
   * @return true or false.
   */
  public static boolean createPDF( final MasterReport report ) throws ReportProcessingException {
    final OutputStream out = new NullOutputStream();
    try {
      final PdfOutputProcessor outputProcessor =
          new PdfOutputProcessor( report.getConfiguration(), out, report.getResourceManager() );
      final PageableReportProcessor proc = new PageableReportProcessor( report, outputProcessor );
      proc.processReport();
      return true;
    } catch ( ReportParameterValidationException e ) {
      return true;
    } catch ( Exception e ) {
      logger.error( "Writing PDF failed.", e );
      throw new ReportProcessingException( "Failed to create PDF", e );
    }
  }

  public static void executeAll( final MasterReport report ) throws Exception {
    logger.debug( "   GRAPHICS2D .." );
    TestCase.assertTrue( DebugReportRunner.execGraphics2D( report ) >= 0 );
    logger.debug( "   PDF .." );
    TestCase.assertTrue( DebugReportRunner.createPDF( report ) );
    logger.debug( "   CSV .." );
    DebugReportRunner.createCSV( report );
    logger.debug( "   PLAIN_TEXT .." );
    TestCase.assertTrue( DebugReportRunner.createPlainText( report ) );
    logger.debug( "   RTF .." );
    DebugReportRunner.createRTF( report );
    logger.debug( "   STREAM_HTML .." );
    DebugReportRunner.createStreamHTML( report );
    logger.debug( "   EXCEL .." );
    DebugReportRunner.createXLS( report );
    logger.debug( "   ZIP_HTML .." );
    DebugReportRunner.createZIPHTML( report );
  }

  public static LogicalPageBox layoutSingleBand( final MasterReport report, final Band reportHeader )
    throws ReportProcessingException, ContentProcessingException {
    return layoutSingleBand( report, reportHeader, true, false );
  }

  public static LogicalPageBox layoutSingleBandInDesignTime( final MasterReport report, final Band reportHeader )
    throws ReportProcessingException, ContentProcessingException {
    return layoutSingleBand( report, reportHeader, true, false, true );
  }

  public static LogicalPageBox layoutSingleBand( final MasterReport originalReport, final Band reportHeader,
      final boolean monospaced, final boolean expectPageBreak ) throws ReportProcessingException,
    ContentProcessingException {
    return layoutSingleBand( originalReport, reportHeader, monospaced, expectPageBreak, false );
  }

  public static LogicalPageBox layoutSingleBand( final MasterReport originalReport, final Band reportHeader,
      final boolean monospaced, final boolean expectPageBreak, final boolean designTime )
    throws ReportProcessingException, ContentProcessingException {
    final FontStorage fontRegistry;
    if ( monospaced ) {
      fontRegistry = new DefaultFontStorage( new MonospaceFontRegistry( 9, 18 ) );
    } else {
      fontRegistry = DebugOutputProcessorMetaData.getLocalFontStorage();
    }
    return layoutSingleBand( originalReport, reportHeader, fontRegistry, expectPageBreak, designTime );
  }

  public static LogicalPageBox layoutSingleBand( final MasterReport originalReport, final Band reportHeader,
      final FontStorage fontRegistry, final boolean expectPageBreak ) throws ReportProcessingException,
    ContentProcessingException {
    return layoutSingleBand( originalReport, reportHeader, fontRegistry, expectPageBreak, false );
  }

  public static LogicalPageBox layoutSingleBand( final MasterReport originalReport, final Band reportHeader,
      final FontStorage fontRegistry, final boolean expectPageBreak, final boolean designTime )
    throws ReportProcessingException, ContentProcessingException {
    final ReportStateKey stateKey = new ReportStateKey();

    final DebugOutputProcessorMetaData metaData = new DebugOutputProcessorMetaData( fontRegistry );
    metaData.setDesignTime( designTime );

    final MasterReport report = originalReport.derive( true );
    resolveStyle( report );
    resolveStyle( reportHeader );

    metaData.initialize( wrapForCompatibility( report ) );

    final ProcessingContext processingContext = new DefaultProcessingContext( report, metaData );
    final DebugExpressionRuntime runtime = new DebugExpressionRuntime( new DefaultTableModel(), 0, processingContext );

    final DebugRenderer debugLayoutSystem = new DebugRenderer( metaData );
    debugLayoutSystem.setStateKey( stateKey );
    debugLayoutSystem.startReport( report, processingContext, new DefaultPerformanceMonitorContext() );
    debugLayoutSystem.startSection( Renderer.SectionType.NORMALFLOW );
    debugLayoutSystem.add( reportHeader, runtime );
    debugLayoutSystem.endSection();
    if ( expectPageBreak ) {
      debugLayoutSystem.endReport();
      final Renderer.LayoutResult result = debugLayoutSystem.validatePages();
      Assert.assertEquals( Renderer.LayoutResult.LAYOUT_PAGEBREAK, result );
    } else {
      debugLayoutSystem.validatePages();
    }
    return debugLayoutSystem.getPageBox();
  }

  private static Configuration wrapForCompatibility( final MasterReport processingContext ) {
    final Integer compatibilityLevel = processingContext.getCompatibilityLevel();
    if ( compatibilityLevel == null || compatibilityLevel < 0 ) {
      return processingContext.getConfiguration();
    }

    if ( compatibilityLevel < ClassicEngineBoot.computeVersionId( 3, 999, 999 ) ) {
      // enable strict compatibility mode for reports older than 4.0.
      final HierarchicalConfiguration config = new HierarchicalConfiguration( processingContext.getConfiguration() );
      config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.legacy.WrapProgressMarkerInSection", "true" );
      config.setConfigProperty( "org.pentaho.reporting.engine.classic.core.legacy.StrictCompatibility", "true" );
      return config;
    }

    // this is a trunk or 4.0 or newer report.
    return processingContext.getConfiguration();
  }

  public static void resolveStyle( final Section band ) {
    final DebugReportRunner.StaticStyleResolver resolver = new DebugReportRunner.StaticStyleResolver();
    resolver.resolve( band );
  }

  public static MasterReport parseGoldenSampleReport( final String name ) throws ResourceException {
    final File file = GoldTestBase.locateGoldenSampleReport( name );
    if ( file == null ) {
      throw new ResourceException( "Unable to locate report '" + name + "' in the golden samples." );
    }

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    return (MasterReport) mgr.createDirectly( file, MasterReport.class ).getResource();
  }

  public static MasterReport parseLocalReport( final String name, Class<?> context ) throws ResourceException {
    final URL file = context.getResource( name );
    if ( file == null ) {
      throw new ResourceException( "Unable to locate report '" + name + "' near class " + context );
    }

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    return (MasterReport) mgr.createDirectly( file, MasterReport.class ).getResource();
  }

  public static void showDialog( final MasterReport report ) {
    if ( GraphicsEnvironment.isHeadless() ) {
      return;
    }

    final PreviewDialog dialog = new PreviewDialog( report );
    dialog.setModal( true );
    dialog.pack();
    LibSwingUtil.centerFrameOnScreen( dialog );
    dialog.setVisible( true );
  }

  public static boolean isSkipLongRunTest() {
    if ( "false".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
        "org.pentaho.reporting.engine.classic.test.ExecuteLongRunningTest" ) ) ) {
      return true;
    }
    return false;
  }

  private static class MultiPageFlowSelector implements PageFlowSelector {
    private Set<Integer> acceptedPage;
    private boolean logicalPage;

    public MultiPageFlowSelector( final boolean logicalPage, final int... acceptedPage ) {
      this.acceptedPage = new HashSet<Integer>();
      for ( int page : acceptedPage ) {
        this.acceptedPage.add( page );
      }
      this.logicalPage = logicalPage;
    }

    public boolean isLogicalPage() {
      return logicalPage;
    }

    public MultiPageFlowSelector( final int acceptedPage ) {
      this( true, acceptedPage );
    }

    public boolean isPhysicalPageAccepted( final PhysicalPageKey key ) {
      if ( key == null ) {
        return false;
      }
      return logicalPage == false && acceptedPage.contains( key.getSequentialPageNumber() );
    }

    public boolean isLogicalPageAccepted( final LogicalPageKey key ) {
      if ( key == null ) {
        return false;
      }
      return logicalPage && acceptedPage.contains( key.getPosition() );
    }
  }

  private static class StrictMultiPageFlowSelector extends MultiPageFlowSelector {
    private int maxPage;

    private StrictMultiPageFlowSelector( final boolean logicalPage, final int maxPage, final int... acceptedPage ) {
      super( logicalPage, acceptedPage );
      this.maxPage = maxPage;
    }

    public boolean isPhysicalPageAccepted( final PhysicalPageKey key ) {
      if ( !isLogicalPage() ) {
        if ( key.getSequentialPageNumber() > maxPage ) {
          Assert.fail( "Maximum expected page number exceeded: " + key.getSequentialPageNumber() );
        }
      }
      return super.isPhysicalPageAccepted( key );
    }

    public boolean isLogicalPageAccepted( final LogicalPageKey key ) {
      if ( isLogicalPage() ) {
        if ( key.getPosition() > maxPage ) {
          Assert.fail( "Maximum expected page number exceeded: " + key.getPosition() );
        }
      }
      return super.isLogicalPageAccepted( key );
    }
  }

  public static File createTestOutputFile() {
    return createTestOutputFile( null );
  }

  public static boolean isSafeToTestComplexText() {
    // this property is undefined by default. This is safer than testing for existing Ant properties
    // or other hacks. You will have to explicitly enable this property in the system properties or
    // system configuration to test complex text code on your machine.
    //
    // Note that the results of the layout is machine dependent, so a failure may not be an error, but
    // the result of a difference in fonts or settings.
    return "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
        "junit.enable-platform-dependent-tests" ) );
  }

  public static File createTestOutputFile( String name ) {
    final File file = new File( "test-output" );
    // noinspection ResultOfMethodCallIgnored
    file.mkdir();
    if ( StringUtils.isEmpty( name, true ) ) {
      return file;
    }
    return new File( file, name );
  }
}

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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout;

import java.awt.print.PageFormat;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ResultTable;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.SourceChunk;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ValidationSequence;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.parser.TableTestSpecXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.DefaultPerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 17.08.2007, 17:34:13
 *
 * @author Thomas Morgner
 */
public class TableLayoutTest extends TestCase
{
  private static final Log logger = LogFactory.getLog(TableLayoutTest.class);

  public TableLayoutTest()
  {
  }

  public TableLayoutTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBorderComputation() throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    runTest("border-computation.xml");
  }

  public void testBackgroundColor() throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    runTest("background-color.xml");
  }

  public void testBorderSplittingSimple() throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    runTest("border-splitting-simple.xml");
  }

  public void testBorderSplittingOverlapping() throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    runTest("border-splitting-overlapping.xml");
  }

  public void testLegacyLinesBorder() throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    runTest("legacy-lines-border.xml");
  }

  public void testLegacyLinesBug() throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    runTest("legacy-lines-bug.xml");
  }

  public void testLegacyLinesBug2() throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    runTest("legacy-lines-bug2.xml");
  }

  public void testBorderOverlaySimple() throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    runTest("border-overlay-simple.xml");
  }

  public void testRoundRectLayout() throws ReportProcessingException, ResourceException, ContentProcessingException
  {
    runTest("legacy-round-rectangles.xml"); // Excel does not support round-rects, so this test will fail
  }

  private static void runTest(final String filename)
      throws ResourceException, ContentProcessingException, ReportProcessingException
  {
    logger.debug("Processing " + filename);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    resourceManager.registerFactory(new TableTestSpecXmlResourceFactory());


    final URL url = TableLayoutTest.class.getResource(filename);
    assertNotNull(url);
    final Resource resource = resourceManager.createDirectly(url, ValidationSequence.class);
    final ValidationSequence sequence = (ValidationSequence) resource.getResource();

    final TableLayoutTest runtime = new TableLayoutTest();

    final HierarchicalConfiguration config = new HierarchicalConfiguration(
        ClassicEngineBoot.getInstance().getGlobalConfig());
    config.setConfigProperty("org.pentaho.reporting.engine.classic.core.modules.output.table.base.StrictLayout",
        String.valueOf(sequence.isStrict()));
    config.setConfigProperty("org.pentaho.reporting.engine.classic.core.modules.output.table.base.VerboseCellMarkers",
        "true");
    final HtmlOutputProcessorMetaData metaData =
        new HtmlOutputProcessorMetaData(HtmlOutputProcessorMetaData.PAGINATION_NONE);
    metaData.initialize(config);

    runtime.run(sequence, metaData);
  }


  public void run(final ValidationSequence sequence,
                  final OutputProcessorMetaData metaData) throws ResourceKeyCreationException, ContentProcessingException, ReportProcessingException
  {
    // Set up the process ..
    final PageFormatFactory fmFactory = PageFormatFactory.getInstance();
    final PageFormat pageFormat = new PageFormat();
    pageFormat.setPaper(fmFactory.createPaper((double) sequence.getPageWidth(), 1000));

    final SimplePageDefinition pageDefinition = new SimplePageDefinition(pageFormat);
    final ProcessingContext processingContext = new DefaultProcessingContext();
    final DebugExpressionRuntime runtime = new DebugExpressionRuntime(new DefaultTableModel(), 0, processingContext);

    final TableDebugOutputProcessor outputProcessor = new TableDebugOutputProcessor(metaData);
    final TableDebugRenderer flowRenderer = new TableDebugRenderer(outputProcessor);
    final MasterReport report = new MasterReport();
    report.setPageDefinition(pageDefinition);
    DebugReportRunner.resolveStyle(report);
    flowRenderer.startReport(report, processingContext, new DefaultPerformanceMonitorContext());
    // execute .. (maybe it is not pretty, "... but it works")
    final ArrayList list = sequence.getContents();
    for (int i = 0; i < list.size(); i++)
    {
      final Object o = list.get(i);
      if (o instanceof SourceChunk)
      {
        final SourceChunk chunk = (SourceChunk) o;
        flowRenderer.startSection(Renderer.SectionType.NORMALFLOW);
        final Band band = chunk.getRootBand();
        DebugReportRunner.resolveStyle(band);
        flowRenderer.add(band, runtime);
        flowRenderer.endSection();
        flowRenderer.applyAutoCommit();
        if (Renderer.LayoutResult.LAYOUT_PAGEBREAK == flowRenderer.validatePages())
        {
          flowRenderer.processPage(null, new Object(), true);
        }
        else
        {
          flowRenderer.processIncrementalUpdate(true);
        }
        flowRenderer.processIncrementalUpdate(true);
      }
    }
    flowRenderer.endReport();
    if (Renderer.LayoutResult.LAYOUT_PAGEBREAK == flowRenderer.validatePages())
    {
      assertTrue(flowRenderer.processPage(null, new Object(), true));
    }
    else
    {
      fail();
    }

    flowRenderer.startReport(report, processingContext, new DefaultPerformanceMonitorContext());
    for (int i = 0; i < list.size(); i++)
    {
      final Object o = list.get(i);
      if (o instanceof SourceChunk)
      {
        final SourceChunk chunk = (SourceChunk) o;
        flowRenderer.startSection(Renderer.SectionType.NORMALFLOW);
        final Band band = chunk.getRootBand();
        DebugReportRunner.resolveStyle(band);
        flowRenderer.add(band, runtime);
        flowRenderer.endSection();
        flowRenderer.applyAutoCommit();
        if (Renderer.LayoutResult.LAYOUT_PAGEBREAK == flowRenderer.validatePages())
        {
          flowRenderer.processPage(null, new Object(), true);
        }
        else
        {
          flowRenderer.processIncrementalUpdate(true);
        }
      }
      else if (o instanceof ResultTable)
      {
        // perform the layouting first.
        final ResultTable chunk = (ResultTable) o;
        outputProcessor.validate(chunk);
      }
    }
    flowRenderer.endReport();
    if (Renderer.LayoutResult.LAYOUT_PAGEBREAK == flowRenderer.validatePages())
    {
      flowRenderer.processPage(null, new Object(), false);
    }
    else
    {
      fail();
    }

    logger.debug("All ok");
  }
}

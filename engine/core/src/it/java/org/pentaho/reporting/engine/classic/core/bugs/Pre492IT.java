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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FlowHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.SingleRepositoryURLRewriter;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Ignore
public class Pre492IT extends TestCase {
  private static class DebugFlowOutputProcessor extends FlowHtmlOutputProcessor {
    private DebugFlowOutputProcessor() {
    }

    protected TableContentProducer createTableContentProducer( final SheetLayout layout ) {
      return new TestTableContentProducer( layout, getMetaData() );
    }
  }

  private static class TestTableContentProducer extends TableContentProducer {
    private TestTableContentProducer( final SheetLayout sheetLayout, final OutputProcessorMetaData metaData ) {
      super( sheetLayout, metaData );
    }

    protected void handleContentConflict( final RenderBox box ) {
      Assert.fail();
    }
  }

  public Pre492IT() {
  }

  public Pre492IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRun() throws Exception {
    final URL url = getClass().getResource( "Pre-492.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    try {
      final ZipRepository zipRepository = new ZipRepository( new NullOutputStream() );
      final ContentLocation root = zipRepository.getRoot();
      final ContentLocation data =
          RepositoryUtilities.createLocation( zipRepository, RepositoryUtilities.splitPath( "data", "/" ) );

      final DebugFlowOutputProcessor outputProcessor = new DebugFlowOutputProcessor();

      final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
      printer.setContentWriter( root, new DefaultNameGenerator( root, "report" ) );
      printer.setDataWriter( data, new DefaultNameGenerator( data, "content" ) );
      printer.setUrlRewriter( new SingleRepositoryURLRewriter() );
      outputProcessor.setPrinter( printer );

      final FlowReportProcessor sp = new FlowReportProcessor( report, outputProcessor );
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
  }

  public void testPagebreakHonoredOnFirstPage() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Pre-492.prpt" );
    masterReport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "true" );
    List<LogicalPageBox> logicalPageBoxes = DebugReportRunner.layoutPages( masterReport, 0, 1 );
    final LogicalPageBox page0 = logicalPageBoxes.get( 0 );
    // ModelPrinter.INSTANCE.print(page0);

    final RenderNode[] elementsByElementType =
        MatchFactory.findElementsByElementType( page0.getContentArea(), AutoLayoutBoxType.INSTANCE );
    assertEquals( 28, elementsByElementType.length );
    assertEquals( StrictGeomUtility.toInternalValue( 199 ), elementsByElementType[27].getY() );

    final LogicalPageBox page1 = logicalPageBoxes.get( 1 );
    final RenderNode[] elementsPage1 =
        MatchFactory.findElementsByElementType( page1.getContentArea(), AutoLayoutBoxType.INSTANCE );
    assertEquals( 31, elementsPage1.length );
    assertEquals( StrictGeomUtility.toInternalValue( 211 ), elementsPage1[30].getY() );
    // ModelPrinter.INSTANCE.print(page1);
  }

  public void testPagebreakHonoredOnFirstPageSimple() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Pre-492.prpt" );
    masterReport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    List<LogicalPageBox> logicalPageBoxes = DebugReportRunner.layoutPages( masterReport, 0, 1 );
    final LogicalPageBox page0 = logicalPageBoxes.get( 0 );
    // ModelPrinter.INSTANCE.print(page0);

    final RenderNode[] elementsByElementType =
        MatchFactory.findElementsByElementType( page0.getContentArea(), AutoLayoutBoxType.INSTANCE );
    assertEquals( 31, elementsByElementType.length );
    assertEquals( StrictGeomUtility.toInternalValue( 199 ), elementsByElementType[elementsByElementType.length - 1]
        .getY() );

    final LogicalPageBox page1 = logicalPageBoxes.get( 1 );
    final RenderNode[] elementsPage1 =
        MatchFactory.findElementsByElementType( page1.getContentArea(), AutoLayoutBoxType.INSTANCE );
    assertEquals( 34, elementsPage1.length );
    assertEquals( StrictGeomUtility.toInternalValue( 211 ), elementsPage1[elementsPage1.length - 1].getY() );
    // ModelPrinter.INSTANCE.print(page1);
  }
}

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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.CountBoxesStep;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.PerformanceTestSequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FileSystemURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StreamHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.FlowExcelOutputProcessor;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.RelationalReportBuilder;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.OutputStream;

public class Prd4069IT extends TestCase {
  public Prd4069IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testFlowTableExport() throws ReportProcessingException {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final MasterReport report = createTestReport();
    final FlowExcelOutputProcessor target =
        new ValidatingFlowExcelOutputProcessor( report.getConfiguration(), new NullOutputStream(), report
            .getResourceManager() );
    target.setUseXlsxFormat( true );
    final FlowReportProcessor reportProcessor = new FlowReportProcessor( report, target );
    reportProcessor.processReport();
    reportProcessor.close();

  }

  private static class ValidatingFlowExcelOutputProcessor extends FlowExcelOutputProcessor {
    private CountBoxesStep countBoxesStep;

    private ValidatingFlowExcelOutputProcessor( final Configuration config, final OutputStream outputStream,
        final ResourceManager resourceManager ) {
      super( config, outputStream, resourceManager );
      countBoxesStep = new CountBoxesStep();
    }

    protected void processTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
        final TableContentProducer contentProducer ) throws ContentProcessingException {
      final int count = countBoxesStep.countChildren( logicalPage );
      // Count the maximum number of boxes that are active at any given point in time in the model.
      // The model should work on a revolving basis, removing boxes that have been processed, so that
      // we should see a relatively stable, low number of boxes, even though we process 20K of rows.
      if ( count > 10000 ) {
        Assert.fail();
      }
      super.processTableContent( logicalPageKey, logicalPage, contentProducer );
    }

    protected void updateTableContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPageBox,
        final TableContentProducer tableContentProducer, final boolean performOutput )
      throws ContentProcessingException {
      final int count = countBoxesStep.countChildren( logicalPageBox );
      // Count the maximum number of boxes that are active at any given point in time in the model.
      // The model should work on a revolving basis, removing boxes that have been processed, so that
      // we should see a relatively stable, low number of boxes, even though we process 20K of rows.
      if ( count > 10000 ) {
        Assert.fail();
      }
      super.updateTableContent( logicalPageKey, logicalPageBox, tableContentProducer, performOutput );
    }
  }

  public void testStreamTableExport() throws ReportProcessingException {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final MasterReport report = createTestReport();
    final StreamRepository targetRepository = new StreamRepository( new NullOutputStream() );
    final ContentLocation targetRoot = targetRepository.getRoot();

    final HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor( report.getConfiguration() );
    final HtmlPrinter printer = new ValidatingHtmlPrinter( report.getResourceManager() );
    printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, "index", "html" ) );
    printer.setDataWriter( null, null );
    printer.setUrlRewriter( new FileSystemURLRewriter() );
    outputProcessor.setPrinter( printer );

    final StreamReportProcessor sp = new StreamReportProcessor( report, outputProcessor );
    sp.processReport();
    sp.close();
  }

  private static class ValidatingHtmlPrinter extends AllItemsHtmlPrinter {
    private CountBoxesStep countBoxesStep;

    private ValidatingHtmlPrinter( final ResourceManager resourceManager ) {
      super( resourceManager );
      countBoxesStep = new CountBoxesStep();
    }

    public void print( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage,
        final TableContentProducer contentProducer, final OutputProcessorMetaData metaData, final boolean incremental )
      throws ContentProcessingException {
      final int count = countBoxesStep.countChildren( logicalPage );
      // Count the maximum number of boxes that are active at any given point in time in the model.
      // The model should work on a revolving basis, removing boxes that have been processed, so that
      // we should see a relatively stable, low number of boxes, even though we process 20K of rows.
      if ( count > 2500 ) {
        Assert.fail();
      }
      super.print( logicalPageKey, logicalPage, contentProducer, metaData, incremental );
    }
  }

  private MasterReport createTestReport() {
    final PerformanceTestSequence sequence = new PerformanceTestSequence();
    sequence.setParameter( "seed", 999L );
    sequence.setParameter( "limit", 20000 );

    final SequenceDataFactory sdf = new SequenceDataFactory();
    sdf.addSequence( "query", sequence );

    // use HTML export for test
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageSize( 800, 300 ) ) );
    report.setDataFactory( sdf );
    report.setQuery( "query" );

    final DesignTimeDataSchemaModel dataSchemaModel = new DesignTimeDataSchemaModel( report );
    final RelationalReportBuilder builder = new RelationalReportBuilder( dataSchemaModel );
    builder.addDetails( "text", null, null );

    report.setRootGroup( builder.create() );
    return report;
  }

}

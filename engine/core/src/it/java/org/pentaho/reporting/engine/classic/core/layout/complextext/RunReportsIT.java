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

package org.pentaho.reporting.engine.classic.core.layout.complextext;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.filter.types.MessageType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.NoCloseOutputStream;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.net.URL;

public class RunReportsIT extends TestCase {
  public RunReportsIT() {
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  /**
   * This test shows how to validate the layout code easily, without having to start a full report processing run or
   * having to worry about the actual output target implementations.
   *
   * @throws ResourceException
   * @throws ReportProcessingException
   * @throws ContentProcessingException
   */
  public void testLayoutSingleBand() throws ResourceException, ReportProcessingException, ContentProcessingException {
    // parse an existing report. You can create reports either via PRD or you can
    // produce them via the API.
    //
    // When you use PRD, only the "SampleData" datasource is available, or you can hardcode values via
    // the table-datasource

    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3529.prpt" );
    // to enable the complex-processing mode, set this configuration option to true
    report.getReportConfiguration()
      .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );

    ReportHeader reportHeader = report.getReportHeader();
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, reportHeader );

    // this creates a print-out of the layout. This is great to quickly see what the layouter produces.
    ModelPrinter.INSTANCE.print( logicalPageBox );

    // use the MatchFactory to quickly locate elements inside the layout model
    RenderNode[] elementsByElementType = MatchFactory.findElementsByElementType( logicalPageBox, MessageType.INSTANCE );
    for ( int i = 0; i < elementsByElementType.length; i++ ) {
      RenderNode renderNode = elementsByElementType[ i ];
      ModelPrinter.INSTANCE.print( renderNode );
    }
  }

  public void testHTMLExport() throws ReportProcessingException, ResourceException {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3529.prpt" );
    // produce HTML output ..
    HtmlReportUtil.createStreamHTML( report, new NoCloseOutputStream( System.out ) );
  }

  public void testSwingPrintPreview() throws ReportProcessingException, ResourceException {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3529.prpt" );
    // produce a print preview. This produces a Graphics2D output which is also used for printing.
    DebugReportRunner.showDialog( report );
  }

  @Test
  //Report has 6 physical pages and 2 logical, check that we work with logical
  public void testPageSpanning() throws Exception {
    URL url = getClass().getResource( "page-spanning.prpt" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();
    org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner.createTestOutputFile();
    try ( ByteArrayOutputStream stream = new ByteArrayOutputStream() ) {
      PageableReportProcessor proc = null;
      final ReportProgressEvent[] ev = new ReportProgressEvent[ 1 ];
      try {

        final PdfOutputProcessor outputProcessor =
          new PdfOutputProcessor( report.getConfiguration(), stream, report.getResourceManager() );
        proc = new PageableReportProcessor( report, outputProcessor );
        proc
          .addReportProgressListener( new ReportProgressListener() {
            @Override public void reportProcessingStarted( ReportProgressEvent event ) {

            }

            @Override public void reportProcessingUpdate( ReportProgressEvent event ) {

            }

            @Override public void reportProcessingFinished( ReportProgressEvent event ) {
              ev[ 0 ] = event;
            }
          } );
        proc.processReport();
      } catch ( final Error e ) {
        throw new ReportProcessingException( "Writing PDF failed", e );
      } finally {
        if ( proc != null ) {
          proc.close();
        }
      }
      final byte[] bytes = stream.toByteArray();
      Assert.assertNotNull( bytes );
      Assert.assertTrue( bytes.length > 0 );
      Assert.assertNotNull( ev[ 0 ] );
      Assert.assertEquals( 2, ev[ 0 ].getPage() );
      Assert.assertEquals( 2, ev[ 0 ].getTotalPages() );
    }
  }
}

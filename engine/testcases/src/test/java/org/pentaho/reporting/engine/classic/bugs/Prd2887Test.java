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

package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableLayoutProducer;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd2887Test extends TestCase {
  private static class TestTableContentProducer extends TableContentProducer {
    private TestTableContentProducer( final SheetLayout sheetLayout, final OutputProcessorMetaData metaData ) {
      super( sheetLayout, metaData );
    }

    protected void handleContentConflict( final RenderBox box ) {
      fail( box.toString() );
    }
  }

  public Prd2887Test() {
  }

  public Prd2887Test( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testOverlapWarning() throws Exception {
    final URL url = getClass().getResource( "Prd-2887.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    final Band b1 = (Band) report.getPageHeader().getElement( 0 );
    b1.getElement( 0 ).setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE, "Null Field1" );
    b1.getElement( 1 ).setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE, "Null Field2" );
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getPageHeader() );
    //ModelPrinter.print(logicalPageBox);

    final DebugOutputProcessorMetaData metaData = new DebugOutputProcessorMetaData();
    metaData.initialize( report.getConfiguration() );

    final TableLayoutProducer tlb = new TableLayoutProducer( metaData );
    tlb.update( logicalPageBox, false );
    final TableContentProducer tcp = new TestTableContentProducer( tlb.getLayout(), metaData );
    tcp.compute( logicalPageBox, false );
    //    final SheetLayout layout = tlb.getLayout();
  }

  public void testRun() throws ResourceException, ReportProcessingException {
    final URL url = getClass().getResource( "Prd-2887.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    DebugReportRunner.createPDF( report );
  }
}

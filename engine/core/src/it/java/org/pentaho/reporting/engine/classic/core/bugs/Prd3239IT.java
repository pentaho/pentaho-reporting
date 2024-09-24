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

import junit.framework.TestCase;
import org.junit.Ignore;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Prd3239IT extends TestCase {
  public Prd3239IT() {
  }

  public Prd3239IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport() throws ResourceException {
    final URL url = getClass().getResource( "Prd-3239.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    final ReportHeader rh = report.getReportHeader();
    // rh.removeSubreport(rh.getSubReport(1));
    rh.removeSubreport( rh.getSubReport( 1 ) );
    rh.removeSubreport( rh.getSubReport( 1 ) );
    rh.removeSubreport( rh.getSubReport( 1 ) );
    rh.removeSubreport( rh.getSubReport( 1 ) );
    DebugReportRunner.execGraphics2D( report );

  }

//  @Ignore
//  public void testFullReport() throws ResourceException {
//    final URL url = getClass().getResource( "Prd-3239.prpt" );
//    assertNotNull( url );
//    final ResourceManager resourceManager = new ResourceManager();
//    resourceManager.registerDefaults();
//    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
//    final MasterReport report = (MasterReport) directly.getResource();
//
//    DebugReportRunner.execGraphics2D( report );
//  }

//  @Ignore
//  public void testFlowPageReport() throws ResourceException, IOException, ReportProcessingException {
//    final URL url = getClass().getResource( "Prd-3239.prpt" );
//    assertNotNull( url );
//    final ResourceManager resourceManager = new ResourceManager();
//    resourceManager.registerDefaults();
//    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
//    final MasterReport report = (MasterReport) directly.getResource();
//
//    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
//    XmlTableReportUtil.createFlowXML( report, bout );
//  }

//  @Ignore
//  public void testGoldenSample() throws Exception {
//    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-3239.prpt" );
//    List<LogicalPageBox> logicalPageBoxes = DebugReportRunner.layoutPages( masterReport, 0, 1 );
//
//    final LogicalPageBox page1 = logicalPageBoxes.get( 0 );
//    assertNull( MatchFactory.findElementByName( page1, "Element@3459142" ) );
//    assertNotNull( MatchFactory.findElementByName( page1, "TextField@18032083" ) );
//
//    final LogicalPageBox page2 = logicalPageBoxes.get( 1 );
//    assertNotNull( MatchFactory.findElementByName( page2, "Element@3459142" ) );
//    assertNull( MatchFactory.findElementByName( page2, "TextField@18032083" ) );
//
//  }
}

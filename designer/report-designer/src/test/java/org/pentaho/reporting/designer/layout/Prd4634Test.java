/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd4634Test extends TestCase {
  public Prd4634Test() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReportHeader() throws Exception {
    final URL resource = getClass().getResource( "Prd-4634.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext reportContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( report.getReportHeader(), reportContext );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    graphics2D.expect( "Interactive", "Report" );
    assertTrue( graphics2D.hitClip( 10, 10, 1, 1 ) );
    r.draw( graphics2D );
  }

  public void testReportFooter() throws Exception {
    final URL resource = getClass().getResource( "Prd-4634.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext reportContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final ReportFooter reportFooter = report.getReportFooter();
    final TestRootBandRenderer r = new TestRootBandRenderer( reportFooter, reportContext );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    graphics2D.expectSentence( (String) reportFooter.getElement( 0 ).getAttribute
      ( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE ) );
    assertTrue( graphics2D.hitClip( 10, 10, 1, 1 ) );
    r.draw( graphics2D );
  }

  public void testPageFooter() throws Exception {
    final URL resource = getClass().getResource( "Prd-4634.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext reportContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( report.getPageFooter(), reportContext );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    assertTrue( graphics2D.hitClip( 10, 10, 1, 1 ) );
    r.draw( graphics2D );
  }

}

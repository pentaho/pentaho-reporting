/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.designer.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.graphics.TestGraphics2D;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.geom.Rectangle2D;
import java.net.URL;

import javax.naming.spi.NamingManager;

public class Prd4642Test extends TestCase {
  public Prd4642Test() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  public void testBug() throws Exception {
    final URL resource = getClass().getResource( "Prd-4642.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext masterContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final SubReport subReport = (SubReport) report.getReportHeader().getElement( 1 );
    final ReportRenderContext subContext =
      new ReportRenderContext( report, subReport, masterContext, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( subReport.getReportHeader(), subContext );
    final Rectangle2D bounds = r.getBounds();
    assertEquals( new Rectangle2D.Double( 0, 21, 468, 108 ), bounds );

    final TestGraphics2D graphics2D = new ValidateTextGraphics( 468, 108 );
    assertTrue( graphics2D.hitClip( 10, 10, 1, 1 ) );
    r.draw( graphics2D );
  }


}

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
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

import javax.naming.spi.NamingManager;

public class Prd4637Test extends TestCase {
  public Prd4637Test() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  /**
   * Validate that the master-report page-footer content shows up in the layout-editor
   *
   * @throws Exception
   */
  public void testOutsidePageFooter() throws Exception {
    final URL resource = getClass().getResource( "Prd-4637.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext reportContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( report.getPageFooter(), reportContext );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    graphics2D.expect( "Outside", "Page", "Footer" );
    assertTrue( graphics2D.hitClip( 10, 10, 1, 1 ) );
    r.draw( graphics2D );
  }

  /**
   * Validate that the banded page-footer content shows up in the layout-editor
   *
   * @throws Exception
   */
  public void testBandedSubReport() throws Exception {
    final URL resource = getClass().getResource( "Prd-4637.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext masterContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final SubReport subReport = report.getItemBand().getSubReport( 0 );
    final ReportRenderContext subContext =
      new ReportRenderContext( report, subReport, masterContext, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( subReport.getPageFooter(), subContext );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    graphics2D.expect( "Banded", "SubReport", "Footer" );
    assertTrue( graphics2D.hitClip( 10, 10, 1, 1 ) );
    r.draw( graphics2D );
  }

  /**
   * Validate that the inline page-footer content DOES NOT show up in the layout-editor
   *
   * @throws Exception
   */
  public void testInlineSubReport() throws Exception {
    final URL resource = getClass().getResource( "Prd-4637.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext masterContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final SubReport subReport = (SubReport) report.getReportHeader().getElement( 0 );
    final ReportRenderContext subContext =
      new ReportRenderContext( report, subReport, masterContext, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( subReport.getPageFooter(), subContext );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    graphics2D.expect( "Any Text Printed Is An Error!" );
    assertTrue( graphics2D.hitClip( 10, 10, 1, 1 ) );
    r.draw( graphics2D );
  }

  /**
   * Validate that the banded subreport contained in an inline subreport page-footer content DOES NOT show up in the
   * layout-editor
   *
   * @throws Exception
   */
  public void testInlineBandedSubReport() throws Exception {
    final URL resource = getClass().getResource( "Prd-4637.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext masterContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final SubReport subReport = (SubReport) report.getReportHeader().getElement( 0 );
    final ReportRenderContext subContext =
      new ReportRenderContext( report, subReport, masterContext, globalAuthenticationStore );

    final SubReport subReport2 = subReport.getReportHeader().getSubReport( 0 );
    final ReportRenderContext subSubContext =
      new ReportRenderContext( report, subReport2, subContext, globalAuthenticationStore );

    final TestRootBandRenderer r = new TestRootBandRenderer( subReport.getPageFooter(), subSubContext );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    graphics2D.expect( "Any Text Printed Is An Error!" );
    assertTrue( graphics2D.hitClip( 10, 10, 1, 1 ) );
    r.draw( graphics2D );
  }

}

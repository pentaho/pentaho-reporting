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

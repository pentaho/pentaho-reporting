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

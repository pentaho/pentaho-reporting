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

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
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.graphics.TestGraphics2D;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.naming.spi.NamingManager;

public class Prd4638Test extends TestCase {
  public Prd4638Test() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  public void testWatermarkOverlap() throws Exception {
    final URL resource = getClass().getResource( "Prd-4638.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext reportContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( report.getPageFooter(), reportContext );
    r.draw( new TestGraphics2D() );

    final Map<InstanceID, Set<InstanceID>> conflicts = reportContext.getSharedRenderer().getConflicts();
    assertEquals( 1, conflicts.size() );
    final Watermark watermark = report.getWatermark();
    final InstanceID watermarkE1 = watermark.getElement( 0 ).getObjectID();
    final InstanceID watermarkE2 = watermark.getElement( 1 ).getObjectID();
    assertFalse( conflicts.containsKey( watermarkE1 ) );
    assertTrue( conflicts.containsKey( watermarkE2 ) );
  }

}

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

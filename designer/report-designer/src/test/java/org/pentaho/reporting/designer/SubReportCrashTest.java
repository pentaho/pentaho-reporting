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


package org.pentaho.reporting.designer;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class SubReportCrashTest extends TestCase {
  public SubReportCrashTest() {
  }

  public SubReportCrashTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ReportDesignerBoot.getInstance().start();
  }

  public void testSubreport() throws Exception {
    final URL url = getClass().getResource( "subreport-crash.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();

    DebugReportRunner.executeAll( resource );

  }

  public void testSubreportPageHeader() throws Exception {
    // right now, this ends in a infinite loop. Not funny ..
    final URL url = getClass().getResource( "subreport-pageheader.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();

    DebugReportRunner.executeAll( resource );
    // subreports in the page-header must be handled gracefully.
  }
}

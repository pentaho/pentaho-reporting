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


package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

/**
 * This test only acts as smoke-test, not a real content test. The content test is done via the golden sample test
 * instead.
 */
public class Prd3689IT extends TestCase {
  public Prd3689IT() {
  }

  public Prd3689IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunReport() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final URL url = getClass().getResource( "Prd-3689.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final PrintReportProcessor p = new PrintReportProcessor( report );
    p.paginate();
    assertFalse( p.isError() );
    assertTrue( p.isPaginated() );
    assertTrue( p.getNumberOfPages() > 58 );
  }
}

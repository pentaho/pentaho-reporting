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

package org.pentaho.reporting.engine.classic.core.modules.parser.simple;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class SubBandParsingIT extends TestCase {
  public SubBandParsingIT() {
  }

  public SubBandParsingIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParsing() throws Exception {
    final URL url = getClass().getResource( "subband.xml" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final Band band = report.getReportHeader();
    assertEquals( 2, band.getElementCount() );
    for ( int i = 0; i < 2; i++ ) {
      final Band subband = (Band) band.getElement( i );
      assertEquals( 2, subband.getElementCount() );
      for ( int x = 0; x < 2; x++ ) {
        final Band bandLowest = (Band) subband.getElement( x );
        assertTrue( bandLowest.getElementCount() > 0 );
      }
    }
  }

  public void testPreview() throws Exception {
    final URL url = getClass().getResource( "subband.xml" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    DebugReportRunner.execGraphics2D( report );
  }
}

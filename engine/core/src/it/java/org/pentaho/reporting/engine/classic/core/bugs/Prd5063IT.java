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


package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd5063IT extends TestCase {
  public Prd5063IT() {
  }

  public Prd5063IT( final String s ) {
    super( s );
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testFixedPosition() throws Exception {
    final URL url = getClass().getResource( "Prd-5063.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );
    final RenderNode elementByName = MatchFactory.findElementByName( box, "fixed_footer" );

    // y + fixed position = 1900000 + 60200000 = 62100000
    assertEquals( 62100000, elementByName.getY() );
  }
}

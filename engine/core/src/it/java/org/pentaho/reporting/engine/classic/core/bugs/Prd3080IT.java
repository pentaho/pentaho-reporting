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
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldenSampleGenerator;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;

public class Prd3080IT extends TestCase {
  public Prd3080IT() {
  }

  public Prd3080IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPrd3080() throws Exception {
    final File file = GoldenSampleGenerator.locateGoldenSampleReport( "Prd-3080.prpt" );
    assertNotNull( file );

    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    final RenderNode[] nodes = MatchFactory.matchAll( logicalPageBox, "RowRenderBox" );
    for ( int i = 0; i < nodes.length; i++ ) {
      final RenderNode node = nodes[i];
      assertEquals( 0, node.getX() );
      assertEquals( StrictGeomUtility.toInternalValue( 559 ), node.getWidth() );
    }
  }
}

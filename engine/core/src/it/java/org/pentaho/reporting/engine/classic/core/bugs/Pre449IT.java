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
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Pre449IT extends TestCase {
  public Pre449IT() {
  }

  public Pre449IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testWatermarkCrash() throws Exception {
    final URL url = getClass().getResource( "Pre-449.xml" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    PageDefinition pageDefinition = report.getPageDefinition();
    assertEquals( 500f, pageDefinition.getWidth() );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( report, report.getReportHeader(), false, true );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    new ValidateRunner().startValidation( logicalPageBox );
  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }

    protected boolean startBlockBox( final BlockRenderBox box ) {
      if ( "reportheader".equals( box.getName() ) ) {
        assertEquals( "X=0pt", 0, box.getX() );
        assertEquals( "Y=0pt", 0, box.getY() );
        assertEquals( "Height=150pt", StrictGeomUtility.toInternalValue( 150 ), box.getHeight() );
        assertEquals( "Width=500pt", StrictGeomUtility.toInternalValue( 500 ), box.getWidth() );
      }
      return true;
    }
  }
}

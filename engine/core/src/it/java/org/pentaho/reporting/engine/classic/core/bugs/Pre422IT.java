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
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.net.URL;

/**
 * Creation-Date: 10.08.2007, 13:45:14
 *
 * @author Thomas Morgner
 */
public class Pre422IT extends TestCase {
  public Pre422IT() {
  }

  public Pre422IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSubReportDoesNotCrash() throws Exception {
    final URL target = Pre422IT.class.getResource( "Pre-422.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final MasterReport basereport = new MasterReport();
    basereport.setCompatibilityLevel( null );
    basereport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final Band band = report.getReportHeader();
    band.setName( "ReportHeader1" );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getReportHeader(), false, true );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation( logicalPageBox );

  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }

    protected boolean startCanvasBox( final CanvasRenderBox box ) {
      if ( box.getName().equals( "test" ) ) {
        assertEquals( "Y=10pt", StrictGeomUtility.toInternalValue( 10 ), box.getY() );
        assertEquals( "Height=90pt", StrictGeomUtility.toInternalValue( 90 ), box.getHeight() );
      }
      return super.startCanvasBox( box );
    }
  }
}

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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
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
public class Pre448IT extends TestCase {
  public Pre448IT() {
  }

  public Pre448IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBlockLayoutBox() throws Exception {
    final URL target = Pre448IT.class.getResource( "Pre-448.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, (Band) report.getReportHeader().getElement( 3 ), false, true );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation( logicalPageBox );

  }

  public void testCanvasLayoutBox() throws Exception {
    final URL target = Pre448IT.class.getResource( "Pre-448.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, (Band) report.getReportHeader().getElement( 2 ), false, true );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation( logicalPageBox );
  }

  public void testBlockLayout() throws Exception {
    final URL target = Pre448IT.class.getResource( "Pre-448.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, (Band) report.getReportHeader().getElement( 1 ), false, true );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation( logicalPageBox );
  }

  public void testCanvasLayout() throws Exception {
    final URL target = Pre448IT.class.getResource( "Pre-448.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, (Band) report.getReportHeader().getElement( 0 ), false, true );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation( logicalPageBox );
  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }

    protected boolean startCanvasBox( final CanvasRenderBox box ) {
      if ( box.getName().equals( "reportheader" ) ) {
        assertEquals( "Y=0pt", 0, box.getY() );
        assertEquals( "Height=71pt", StrictGeomUtility.toInternalValue( 71 ), box.getHeight() );
      }
      if ( box.getName().equals( "test-01" ) ) {
        assertEquals( "Y=1pt", StrictGeomUtility.toInternalValue( 1 ), box.getY() );
        assertEquals( "Height=70pt", StrictGeomUtility.toInternalValue( 70 ), box.getHeight() );
      }
      if ( box.getName().equals( "rect" ) ) {
        assertEquals( "Y=1pt", StrictGeomUtility.toInternalValue( 1 ), box.getY() );
        assertEquals( "Height=70pt", StrictGeomUtility.toInternalValue( 70 ), box.getHeight() );
      }
      return super.startCanvasBox( box );
    }

    protected void processRenderableContent( final RenderableReplacedContentBox box ) {
      // the wrapper box applies the padding top
      assertEquals( "Y=1pt", StrictGeomUtility.toInternalValue( 1 ), box.getY() );
      // the padding will reduce the available space we have
      assertEquals( "Height=70pt", StrictGeomUtility.toInternalValue( 70 ), box.getHeight() );
    }
  }
}

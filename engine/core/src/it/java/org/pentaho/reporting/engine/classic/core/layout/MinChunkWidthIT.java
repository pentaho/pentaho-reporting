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

package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.net.URL;

public class MinChunkWidthIT extends TestCase {
  public MinChunkWidthIT() {
  }

  public MinChunkWidthIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testMinChunkWidthLegacyMode() throws Exception {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );
    basereport.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );
    basereport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    final URL target = LayoutIT.class.getResource( "min-chunkwidth.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    // PRD-2736 note: word breaks are implemented now,
    // need to set WORDBREAK to false to keep the previous behaviour
    report.getStyle().setStyleProperty( TextStyleKeys.WORDBREAK, false );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getReportHeader(), true, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    new ValidateRunner( true, false ).startValidation( logicalPageBox );
  }

  public void testMinChunkWidthSimpleText() throws Exception {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );
    basereport.setCompatibilityLevel( null );
    basereport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    final URL target = LayoutIT.class.getResource( "min-chunkwidth.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    // PRD-2736 note: word breaks are implemented now,
    // need to set WORDBREAK to false to keep the previous behaviour
    report.getStyle().setStyleProperty( TextStyleKeys.WORDBREAK, false );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getReportHeader(), true, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    new ValidateRunner( false, false ).startValidation( logicalPageBox );
  }

  public void testMinChunkWidth() throws Exception {
    if ( DebugReportRunner.isSafeToTestComplexText() == false ) {
      return;
    }

    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );
    basereport.setCompatibilityLevel( null );
    basereport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "true" );

    final URL target = LayoutIT.class.getResource( "min-chunkwidth.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.getStyle().setStyleProperty( TextStyleKeys.WORDBREAK, true );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getReportHeader(), true, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485 or 400
    // and that only two lines exist for each
    ModelPrinter.INSTANCE.print( logicalPageBox );
    new ValidateRunner( false, true ).startValidation( logicalPageBox );
  }

  @SuppressWarnings( "HardCodedStringLiteral" )
  private static class ValidateRunner extends IterateStructuralProcessStep {
    private final boolean complexText;
    private boolean legacyMode;

    private ValidateRunner( final boolean legacyMode, final boolean complexText ) {
      this.legacyMode = legacyMode;
      this.complexText = complexText;
    }

    protected boolean startCanvasBox( final CanvasRenderBox box ) {
      return testBox( box );
    }

    protected boolean startBlockBox( final BlockRenderBox box ) {
      return testBox( box );
    }

    protected boolean startOtherBox( final RenderBox box ) {
      return testBox( box );
    }

    protected boolean startRowBox( final RenderBox box ) {
      return testBox( box );
    }

    private boolean testBox( final RenderNode box ) {
      final String s = box.getName();
      if ( s == null ) {
        return true;
      }
      final float expectedHeight = ( s.endsWith( "i" ) || s.contains( "-i" ) ) ? 8 : 10;

      if ( s.startsWith( "test-" ) ) {
        assertEquals( "Width = 468: " + s, StrictGeomUtility.toInternalValue( 468 ), box.getWidth() );
        if ( !complexText ) {
          assertEquals( "Height = '" + expectedHeight + "' (PRD-4255): " + s, StrictGeomUtility
              .toInternalValue( expectedHeight ), box.getHeight() );
        }
      } else if ( s.startsWith( "canvas-" ) ) {
        assertTrue( "Width is not zero!: " + s, box.getWidth() != 0 );
        if ( !complexText ) {
          assertEquals( "Height = 8 (PRD-4255): " + s, StrictGeomUtility.toInternalValue( expectedHeight ), box
              .getHeight() );
        }
      } else if ( s.startsWith( "label-b" ) ) {
        // thats (nearly) random ..
      } else if ( s.startsWith( "label-cb" ) ) {
        if ( legacyMode ) {
          // assert that the element is 468
          assertEquals( "Width = 468 in legacy mode; " + s, StrictGeomUtility.toInternalValue( 468 ), box.getWidth() );
        } else {
          assertEquals( "Width = 100; " + s, StrictGeomUtility.toInternalValue( 100 ), box.getWidth() );
        }
        if ( !complexText ) {
          assertEquals( "Height = 10; " + s, StrictGeomUtility.toInternalValue( 10 ), box.getHeight() );
        }
      } else if ( s.startsWith( "label-" ) ) {
        assertEquals( "Width = 100; " + s, StrictGeomUtility.toInternalValue( 100 ), box.getWidth() );
        if ( !complexText ) {
          assertEquals( "Height = 10; " + s, StrictGeomUtility.toInternalValue( 10 ), box.getHeight() );
        }
      }
      return true;
    }

    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }
  }
}

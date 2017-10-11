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
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.net.URL;

public class WidthComputationIT extends TestCase {
  public WidthComputationIT() {
  }

  public WidthComputationIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLayoutMatrix() throws Exception {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );
    basereport.setCompatibilityLevel( null );
    basereport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    final URL target = LayoutIT.class.getResource( "layout-matrix.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getReportHeader(), true, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    new ValidateRunner().startValidation( logicalPageBox );
  }

  public void testLayoutMatrixLegacy() throws Exception {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );
    basereport.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );
    basereport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    final URL target = LayoutIT.class.getResource( "layout-matrix.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getReportHeader(), true, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    new ValidateRunner().startValidation( logicalPageBox );
  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    protected boolean startBox( final RenderBox box ) {
      final String name = box.getName();
      if ( name == null ) {
        return true;
      }

      if ( name.startsWith( "test-" ) && ( box instanceof InlineRenderBox == false ) ) {
        assertEquals( "Cached-Width is same as page-width [BLOCK->*]", StrictGeomUtility.toInternalValue( 468 ), box
            .getCachedWidth() );
      }

      assertCanvas( box, name );
      assertBlock( box, name );
      assertRow( box, name );
      assertInline( box, name );

      return true;
    }

    private void assertInline( final RenderBox box, final String name ) {
      if ( "canvas-ib".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [INLINE->BLOCK]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->BLOCK]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-ib".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-ii".equals( name ) ) {
        // inline elements do not establish a own width. They take whatever their content requires.
        assertEquals( "Cached-Width is 24 [INLINE->INLINE]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->INLINE]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-ci".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-ic".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [INLINE->CANVAS]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->CANVAS]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-ic".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [CANVAS->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [CANVAS->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-ir".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [INLINE->ROW]", StrictGeomUtility.toInternalValue( 24 ), box.getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->ROW]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-ir".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [ROW->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box.getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [ROW->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
    }

    private void assertRow( final RenderBox box, final String name ) {
      if ( "canvas-rb".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [ROW->BLOCK]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [ROW->BLOCK]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-rb".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [BLOCK->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [BLOCK->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-ri".equals( name ) && box instanceof ParagraphRenderBox ) {
        // inline elements do not establish a own width. They take whatever their content requires.
        assertEquals( "Cached-Width is 24 [CANVAS->INLINE*]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [CANVAS->INLINE*]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "canvas-ri".equals( name ) && box instanceof InlineRenderBox ) {
        // inline elements do not establish a own width. They take whatever their content requires.
        assertEquals( "Cached-Width is 24 [INLINE->INLINE]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->INLINE]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-ri".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-rc".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [CANVAS->CANVAS]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [CANVAS->CANVAS]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-rc".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [CANVAS->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [CANVAS->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-rr".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [CANVAS->ROW]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [CANVAS->ROW]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-rr".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [ROW->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box.getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [ROW->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
    }

    private void assertCanvas( final RenderBox box, final String name ) {
      if ( "canvas-cc".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [CANVAS->CANVAS]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [CANVAS->CANVAS]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-cc".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [CANVAS->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [CANVAS->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-cb".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [CANVAS->BLOCK]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [CANVAS->BLOCK]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-cb".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [BLOCK->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [BLOCK->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-ci".equals( name ) && box instanceof ParagraphRenderBox ) {
        // inline elements do not establish a own width. They take whatever their content requires.
        assertEquals( "Cached-Width is 24 [CANVAS->INLINE*]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [CANVAS->INLINE*]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "canvas-ci".equals( name ) && box instanceof InlineRenderBox ) {
        // inline elements do not establish a own width. They take whatever their content requires.
        assertEquals( "Cached-Width is 24 [INLINE->INLINE]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->INLINE]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-ci".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-cr".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [CANVAS->ROW]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [CANVAS->ROW]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-cr".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [ROW->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box.getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [ROW->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
    }

    private void assertBlock( final RenderBox box, final String name ) {
      if ( "canvas-bb".equals( name ) ) {
        assertEquals( "Cached-Width is 468 [BLOCK->BLOCK]", StrictGeomUtility.toInternalValue( 468 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [BLOCK->BLOCK]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-bb".equals( name ) ) {
        assertEquals( "Cached-Width is 468 [BLOCK->TEXT]", StrictGeomUtility.toInternalValue( 468 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [BLOCK->BLOCK]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-bc".equals( name ) ) {
        assertEquals( "Cached-Width is 468 [BLOCK->CANVAS]", StrictGeomUtility.toInternalValue( 468 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [BLOCK->BLOCK]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-bc".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [CANVAS->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [CANVAS->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-bi".equals( name ) && box instanceof ParagraphRenderBox ) {
        // inline elements do not establish a own width. They take whatever their content requires.
        assertEquals( "Cached-Width is 468 [BLOCK->INLINE]", StrictGeomUtility.toInternalValue( 468 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [BLOCK->INLINE]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "canvas-bi".equals( name ) && box instanceof InlineRenderBox ) {
        // inline elements do not establish a own width. They take whatever their content requires.
        assertEquals( "Cached-Width is 24 [INLINE->INLINE]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->INLINE]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-bi".equals( name ) ) {
        assertEquals( "Cached-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 24 [INLINE->TEXT]", StrictGeomUtility.toInternalValue( 24 ), box
            .getMinimumChunkWidth() );
      }

      if ( "canvas-br".equals( name ) ) {
        assertEquals( "Cached-Width is 468 [BLOCK->ROW]", StrictGeomUtility.toInternalValue( 468 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [BLOCK->ROW]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
      if ( "label-br".equals( name ) ) {
        assertEquals( "Cached-Width is 100 [BLOCK->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getCachedWidth() );
        assertEquals( "Min-Chunk-Width is 100 [BLOCK->TEXT]", StrictGeomUtility.toInternalValue( 100 ), box
            .getMinimumChunkWidth() );
      }
    }

    protected boolean startCanvasBox( final CanvasRenderBox box ) {
      return startBox( box );
    }

    protected boolean startBlockBox( final BlockRenderBox box ) {
      return startBox( box );
    }

    protected boolean startInlineBox( final InlineRenderBox box ) {
      return startBox( box );
    }

    protected boolean startRowBox( final RenderBox box ) {
      return startBox( box );
    }

    protected void processParagraphChilds( final ParagraphRenderBox box ) {
      processBoxChilds( box );
    }

    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }
  }

}

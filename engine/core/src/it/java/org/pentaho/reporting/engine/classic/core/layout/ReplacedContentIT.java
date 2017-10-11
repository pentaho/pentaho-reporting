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
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.net.URL;

public class ReplacedContentIT extends TestCase {
  public ReplacedContentIT() {
    super();
  }

  public ReplacedContentIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReplacedContent() throws Exception {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final URL target = LayoutIT.class.getResource( "replaced-content.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getReportHeader(), true, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.print(logicalPageBox);
    new ValidateRunner().startValidation( logicalPageBox );
  }

  public void testReplacedContentRel() throws Exception {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final URL target = LayoutIT.class.getResource( "replaced-content-relative.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    PageFormat pageFormat = report.getPageDefinition().getPageFormat( 0 );
    DebugLog.log( PageFormatFactory.printPageFormat( pageFormat ) );
    final Band containerBand = report.getReportHeader();

    // Each character (regarless of font or font-size) will be 8pt high and 4pt wide.
    // this makes this test independent of the fonts installed on the system we run on.
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, containerBand, true, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    new ValidateRelativeRunner().startValidation( logicalPageBox );
  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    protected void processRenderableContent( final RenderableReplacedContentBox node ) {
      final String s = node.getName();
      if ( s.endsWith( "i" ) || s.charAt( s.length() - 2 ) == 'i' ) {
        // inline elements take the intrinsinc width/height unless explicitly defined otherwise
        assertEquals( "Rect height=100000; " + node.getName(), StrictGeomUtility.toInternalValue( 100 ), node
            .getCachedHeight() );
      } else {
        assertEquals( "Rect height=10000; " + node.getName(), StrictGeomUtility.toInternalValue( 10 ), node
            .getCachedHeight() );
      }
      assertEquals( "Rect width=100000; " + node.getName(), StrictGeomUtility.toInternalValue( 100 ), node
          .getCachedWidth() );
    }

    protected boolean startBox( final RenderBox box ) {
      final String name = box.getName();
      if ( name == null ) {
        return true;
      }
      if ( box instanceof ParagraphRenderBox ) {
        assertNotNull( "Have at only one child", box.getFirstChild() );
        assertNotNull( "Have at only one child", box.getLastChild() );
        assertSame( "Have at only one child", box.getFirstChild(), box.getLastChild() );
      }

      return true;
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

  private static class ValidateRelativeRunner extends IterateStructuralProcessStep {
    protected void processRenderableContent( final RenderableReplacedContentBox node ) {
      if ( node.getParent() instanceof InlineRenderBox || node.getName().startsWith( "rect-i" ) ) {
        assertEquals( "Rect height=100000; " + node.getName(), StrictGeomUtility.toInternalValue( 100 ), node
            .getCachedHeight() );
        assertEquals( "Rect width=100000; " + node.getName(), StrictGeomUtility.toInternalValue( 100 ), node
            .getCachedWidth() );
      } else if ( node.getName().equals( "rect-cb" ) || node.getName().equals( "rect-rb" ) ) {
        assertEquals( "Rect height=0; " + node.getName(), 0, node.getCachedHeight() );
        assertEquals( "Rect width=100000; " + node.getName(), StrictGeomUtility.toInternalValue( 100 ), node
            .getCachedWidth() );
      } else if ( node.getName().equals( "rect-bb" ) ) {
        assertEquals( "Rect height=0; " + node.getName(), 0, node.getCachedHeight() );
        assertEquals( "Rect width=545000; " + node.getName(), StrictGeomUtility.toInternalValue( 545 ), node
            .getCachedWidth() );
      } else if ( node.getName().equals( "rect-bc" ) || node.getName().equals( "rect-br" ) ) {
        assertEquals( "Rect height=10000; " + node.getName(), StrictGeomUtility.toInternalValue( 10 ), node
            .getCachedHeight() );
        assertEquals( "Rect width=545000; " + node.getName(), StrictGeomUtility.toInternalValue( 545 ), node
            .getCachedWidth() );
      } else if ( node.getParent() instanceof RowRenderBox ) {
        assertEquals( "Rect height=10000; " + node.getName(), StrictGeomUtility.toInternalValue( 10 ), node
            .getCachedHeight() );
        assertEquals( "Rect width=100000; " + node.getName(), StrictGeomUtility.toInternalValue( 100 ), node
            .getCachedWidth() );
      } else {
        assertEquals( "Rect height=10000; " + node.getName(), StrictGeomUtility.toInternalValue( 10 ), node
            .getCachedHeight() );
        assertEquals( "Rect width=100000; " + node.getName(), StrictGeomUtility.toInternalValue( 100 ), node
            .getCachedWidth() );
      }
    }

    protected boolean startBox( final RenderBox box ) {
      final String name = box.getName();
      if ( name == null ) {
        return true;
      }
      if ( box instanceof ParagraphRenderBox ) {
        assertNotNull( "Have at only one child", box.getFirstChild() );
        assertNotNull( "Have at only one child", box.getLastChild() );
        assertSame( "Have at only one child", box.getFirstChild(), box.getLastChild() );
      }

      return true;
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

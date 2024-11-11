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


package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
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
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.net.URL;

public class WatermarkSizeIT extends TestCase {
  public WatermarkSizeIT() {
  }

  public WatermarkSizeIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testNestedRows() throws Exception {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final URL target = LayoutIT.class.getResource( "watermark-size.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getWatermark(), true, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation( logicalPageBox );
  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    protected void processRenderableContent( final RenderableReplacedContentBox box ) {

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

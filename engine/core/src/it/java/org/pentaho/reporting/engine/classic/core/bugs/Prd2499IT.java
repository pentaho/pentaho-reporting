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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateVisualProcessStep;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugRenderer;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportProcessor;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd2499IT extends TestCase {
  private static class MyDebugRenderer extends DebugRenderer {
    private MyDebugRenderer() {
    }

    @Override
    protected void debugPrint( final LogicalPageBox pageBox ) {
      new ValidateRunner().startValidation( pageBox );
    }
  }

  public Prd2499IT() {
  }

  public Prd2499IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunSample() throws ResourceException, ReportProcessingException {
    final URL url = getClass().getResource( "Prd-2499.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final MyDebugRenderer renderer = new MyDebugRenderer();
    final DebugReportProcessor processor = new DebugReportProcessor( report, renderer );
    processor.processReport();

  }

  public void testRunSampleShort() throws ResourceException, ReportProcessingException {
    final URL url = getClass().getResource( "Prd-2499.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    SubReport subReport = (SubReport) report.getItemBand().getElement( 0 );
    subReport.setQueryLimit( 13 );

    final MyDebugRenderer renderer = new MyDebugRenderer();
    final DebugReportProcessor processor = new DebugReportProcessor( report, renderer );
    processor.processReport();

  }

  private static class ValidateRunner extends IterateVisualProcessStep {
    private long lastEnd;

    public void startValidation( final LogicalPageBox logicalPageBox ) {
      lastEnd = 0;
      startProcessing( logicalPageBox );
    }

    protected void processParagraphChilds( final ParagraphRenderBox box ) {
      super.processBoxChilds( box );
    }

    protected boolean startInlineLevelBox( final RenderBox box ) {
      return false;
    }

    protected boolean startCanvasLevelBox( final RenderBox box ) {
      return false;
    }

    protected boolean startRowLevelBox( final RenderBox box ) {
      return false;
    }

    protected boolean startBlockLevelBox( final RenderBox box ) {
      final RenderBox parent = box.getParent();
      if ( parent == null ) {
        assertTrue( "PageBox must start at top", lastEnd == 0 );
        assertTrue( "PageBox must start at top", box.getY() == 0 );
      } else {
        assertTrue( "Box within boundaries of parent", box.getY() >= parent.getY() );
        assertTrue( "Box within boundaries of parent", ( box.getY() + box.getHeight() ) <= ( parent.getY() + parent
            .getHeight() ) );
      }

      if ( box.getPrev() != null ) {
        final RenderNode n = box.getPrev();
        assertTrue( "Box is aligned to previous sibling", box.getY() == ( n.getY() + n.getHeight() ) );
      }
      return true;
    }
  }

  public void testGoldenSample() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-2499.prpt" );
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( masterReport, 0 );
    final RenderNode[] elementsByElementType =
        MatchFactory.findElementsByElementType( logicalPageBox, ItemBandType.INSTANCE );

    assertEquals( 12, elementsByElementType.length );
    assertEquals( StrictGeomUtility.toInternalValue( 726 ), elementsByElementType[11].getY2() );

  }
}

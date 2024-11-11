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


package org.pentaho.reporting.engine.classic.core.layout.table;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class CellSpanIT extends TestCase {
  public CellSpanIT() {
  }

  public CellSpanIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testCellSizeForEmptyCells() throws ReportProcessingException, ContentProcessingException {
    final MasterReport report = new MasterReport();
    final Band table = TableTestUtil.createTable( 1, 1, 1 );
    table.setName( "table" );
    report.getReportHeader().addElement( table );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( report, report.getReportHeader(), false, false );
    // ModelPrinter.print(logicalPageBox);

    final RenderNode table1 = MatchFactory.findElementByName( logicalPageBox, "table" );
    assertEquals( StrictGeomUtility.toInternalValue( 150 ), table1.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 40 ), table1.getHeight() );

    final RenderNode row1 = MatchFactory.findElementByName( logicalPageBox, "r-0" );
    assertEquals( StrictGeomUtility.toInternalValue( 150 ), row1.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), row1.getHeight() );

    final RenderNode row2 = MatchFactory.findElementByName( logicalPageBox, "r-1" );
    assertEquals( StrictGeomUtility.toInternalValue( 150 ), row2.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), row2.getHeight() );

    final RenderNode cell1 = MatchFactory.findElementByName( logicalPageBox, "hr-0-0" );
    assertEquals( StrictGeomUtility.toInternalValue( 150 ), cell1.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), cell1.getHeight() );

    final RenderNode cell2 = MatchFactory.findElementByName( logicalPageBox, "dr-0-0" );
    assertEquals( StrictGeomUtility.toInternalValue( 150 ), cell2.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), cell2.getHeight() );

  }

  public void testCellSizeOffsetInCanvas() throws ReportProcessingException, ContentProcessingException {
    final MasterReport report = new MasterReport();
    final Band table = TableTestUtil.createTable( 2, 1, 1 );
    table.setName( "table" );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 50f );
    report.getReportHeader().addElement( table );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( report, report.getReportHeader(), false, false );

    final RenderNode table1 = MatchFactory.findElementByName( logicalPageBox, "table" );
    assertEquals( StrictGeomUtility.toInternalValue( 300 ), table1.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 40 ), table1.getHeight() );

    final RenderNode row1 = MatchFactory.findElementByName( logicalPageBox, "r-0" );
    assertEquals( StrictGeomUtility.toInternalValue( 300 ), row1.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), row1.getHeight() );

    final RenderNode row2 = MatchFactory.findElementByName( logicalPageBox, "r-1" );
    assertEquals( StrictGeomUtility.toInternalValue( 300 ), row2.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), row2.getHeight() );

    final RenderNode cell1 = MatchFactory.findElementByName( logicalPageBox, "hr-0-0" );
    assertEquals( StrictGeomUtility.toInternalValue( 150 ), cell1.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), cell1.getHeight() );

    final RenderNode cell1a = MatchFactory.findElementByName( logicalPageBox, "hr-0-1" );
    assertEquals( StrictGeomUtility.toInternalValue( 150 ), cell1a.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), cell1a.getHeight() );

    final RenderNode cell2 = MatchFactory.findElementByName( logicalPageBox, "dr-0-0" );
    assertEquals( StrictGeomUtility.toInternalValue( 150 ), cell2.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), cell2.getHeight() );

    final RenderNode cell2a = MatchFactory.findElementByName( logicalPageBox, "dr-0-1" );
    assertEquals( StrictGeomUtility.toInternalValue( 150 ), cell2a.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), cell2a.getHeight() );

  }

  public void testLargeCrosstab() throws Exception {
    final URL url = getClass().getResource( "Prd-3929-2.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 4, 0, 0 ) );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(logicalPageBox);

    final RenderNode[] cell2a = MatchFactory.findElementsByName( logicalPageBox, "header-#1" );
    for ( int i = 0; i < cell2a.length; i += 1 ) {
      final RenderBox cellParent = cell2a[i].getParent();
      assertEquals( cell2a[i].getWidth(), cellParent.getWidth() );
    }
  }
}

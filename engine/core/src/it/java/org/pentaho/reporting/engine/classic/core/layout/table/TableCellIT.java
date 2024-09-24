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

package org.pentaho.reporting.engine.classic.core.layout.table;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.ElementMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public class TableCellIT extends TestCase {
  public TableCellIT() {
  }

  public TableCellIT( final String name ) {
    super( name );
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSimpleTable() throws ReportProcessingException, ContentProcessingException {
    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( BandStyleKeys.TABLE_LAYOUT, TableLayout.fixed );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    table.addElement( tableBody );

    final Band row = new Band();
    row.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_ROW );
    row.setName( "r-0" );
    final Band cell = TableTestUtil.createCell( 1, 1 );
    cell.setName( "dr-0-0" );
    cell.addElement( TableTestUtil.createDataItem( "Data-0-0" ) );
    cell.addElement( TableTestUtil.createDataItem( "Data-0-1" ) );
    cell.addElement( TableTestUtil.createDataItem( "Data-0-2" ) );
    cell.addElement( TableTestUtil.createDataItem( "Data-0-3" ) );

    row.addElement( cell );
    tableBody.addElement( row );

    MasterReport report = new MasterReport();
    report.getReportHeader().addElement( table );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader() );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, new ElementMatcher( ParagraphRenderBox.class ) );
    long positionY = 0;
    for ( int i = 0; i < all.length; i += 1 ) {
      final RenderNode node = all[i];

      assertEquals( positionY, node.getY() );
      positionY += node.getHeight();
    }
  }

  public void testTableCellHeight() throws ReportProcessingException, ContentProcessingException {
    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( BandStyleKeys.TABLE_LAYOUT, TableLayout.fixed );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    table.addElement( tableBody );

    final Band row = new Band();
    row.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_ROW );
    row.setName( "r-0" );

    final Band cell = TableTestUtil.createCell( 1, 1 );
    cell.setName( "dr-0-0" );
    cell.addElement( TableTestUtil.createDataItem( "Data-0-0", 150, -100 ) );

    row.addElement( cell );
    tableBody.addElement( row );

    MasterReport report = new MasterReport();
    report.getReportHeader().addElement( table );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader() );
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, new ElementMatcher( ParagraphRenderBox.class ) );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), all[0].getHeight() );
  }
}

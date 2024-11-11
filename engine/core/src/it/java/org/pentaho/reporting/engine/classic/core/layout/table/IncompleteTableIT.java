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
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.DescendantMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.ElementMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class IncompleteTableIT extends TestCase {
  public IncompleteTableIT() {
  }

  public IncompleteTableIT( final String name ) {
    super( name );
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testMissingTableCell() throws ReportProcessingException, ContentProcessingException {

    final Band tableRow = TableTestUtil.createRow();

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableBody.addElement( TableTestUtil.createAutoBox( tableRow ) );

    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    table.addElement( TableTestUtil.createAutoBox( tableBody ) );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( table );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.print(logicalPageBox);

    final DescendantMatcher matcher = new DescendantMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    assertEquals( 0, all.length );
  }

  public void testMissingTableRow() throws ReportProcessingException, ContentProcessingException {
    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );

    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    table.addElement( TableTestUtil.createAutoBox( tableBody ) );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( table );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.print(logicalPageBox);

    final DescendantMatcher matcher = new DescendantMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    assertEquals( 0, all.length );
  }

  public void testMissingTableBody() throws ReportProcessingException, ContentProcessingException {
    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( table );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.print(logicalPageBox);

    final DescendantMatcher matcher = new DescendantMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    assertEquals( 0, all.length );
  }
}

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

package org.pentaho.reporting.engine.classic.core.layout.table;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.AndMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.AttributeMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.ChildMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.ElementMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.NodeMatcher;

import java.io.IOException;

public class TableIT extends TestCase {
  public TableIT() {
  }

  public TableIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSimpleTable() throws ReportProcessingException, ContentProcessingException {
    final Element label = TableTestUtil.createDataItem( "Cell" );

    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableCell.addElement( label );

    final Band tableRow = TableTestUtil.createRow();
    tableRow.addElement( tableCell );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableBody.addElement( tableRow );

    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    table.addElement( tableBody );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( table );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.print(logicalPageBox);

    final NodeMatcher matcher = new ChildMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    assertEquals( 1, all.length );
    for ( final RenderNode renderNode : all ) {
      assertEquals( 0l, renderNode.getX() );
      assertEquals( 46800000l, renderNode.getWidth() );
      assertEquals( 20000000l, renderNode.getHeight() );
      assertEquals( 0l, renderNode.getY() );
    }
  }

  public void testSimpleTableWithXOffset() throws ReportProcessingException, ContentProcessingException {
    final Element label = TableTestUtil.createDataItem( "Cell" );

    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableCell.addElement( label );

    final Band tableRow = TableTestUtil.createRow();
    tableRow.addElement( tableCell );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableBody.addElement( tableRow );

    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 10f );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 10f );
    table.addElement( tableBody );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( table );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.print(logicalPageBox);

    final NodeMatcher matcher = new ChildMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    assertEquals( 1, all.length );
    for ( final RenderNode renderNode : all ) {
      assertEquals( 1000000l, renderNode.getX() );
      assertEquals( 46800000l, renderNode.getWidth() );
      assertEquals( 20000000l, renderNode.getHeight() );
      assertEquals( 1000000l, renderNode.getY() );
    }
  }

  /**
   * This test should create auto-table elements to have a fully functional table.
   *
   * @throws ReportProcessingException
   * @throws ContentProcessingException
   */
  public void testBrokenTableRow() throws ReportProcessingException, ContentProcessingException {
    final Element label = TableTestUtil.createDataItem( "Cell" );

    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableCell.addElement( label );

    final Band tableRow = TableTestUtil.createRow();
    tableRow.addElement( tableCell );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( tableRow );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.print(logicalPageBox);

    final NodeMatcher matcher = new ChildMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    assertEquals( 1, all.length );
    for ( final RenderNode renderNode : all ) {
      assertEquals( 0l, renderNode.getX() );
      assertEquals( 10000000l, renderNode.getWidth() );
      assertEquals( 20000000l, renderNode.getHeight() );
      assertEquals( 0l, renderNode.getY() );
    }
  }

  /**
   * This test should create auto-table elements to have a fully functional table.
   *
   * @throws ReportProcessingException
   * @throws ContentProcessingException
   */
  public void testBrokenTableBody() throws ReportProcessingException, ContentProcessingException, IOException {
    final Element label = TableTestUtil.createDataItem( "Cell" );

    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableCell.addElement( label );

    final Band tableRow = TableTestUtil.createRow();
    tableRow.addElement( tableCell );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableBody.addElement( tableRow );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( tableBody );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.INSTANCE.print(logicalPageBox);

    final NodeMatcher matcher = new ChildMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    assertEquals( 1, all.length );
    for ( final RenderNode renderNode : all ) {
      assertEquals( 0l, renderNode.getX() );
      assertEquals( 10000000l, renderNode.getWidth() );
      assertEquals( 20000000l, renderNode.getHeight() );
      assertEquals( 0l, renderNode.getY() );
    }
  }

  /**
   * This test should create auto-table elements to have a fully functional table.
   *
   * @throws ReportProcessingException
   * @throws ContentProcessingException
   */
  public void testBrokenTableBody2() throws ReportProcessingException, ContentProcessingException, IOException {
    final Element label = TableTestUtil.createDataItem( "Cell" );

    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableCell.addElement( label );

    final Band tableRow = TableTestUtil.createRow();
    tableRow.addElement( tableCell );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -50f );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableBody.addElement( tableRow );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( tableBody );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.INSTANCE.print(logicalPageBox);

    final NodeMatcher matcher = new ChildMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    assertEquals( 1, all.length );
    for ( final RenderNode renderNode : all ) {
      assertEquals( 0l, renderNode.getX() );
      assertEquals( 10000000l, renderNode.getWidth() );
      assertEquals( 20000000l, renderNode.getHeight() );
      assertEquals( 0l, renderNode.getY() );
    }
  }

  /**
   * This test should create auto-table elements to have a fully functional table.
   *
   * @throws ReportProcessingException
   * @throws ContentProcessingException
   */
  public void testBrokenTableCell() throws ReportProcessingException, ContentProcessingException {
    final Element label = TableTestUtil.createDataItem( "Cell" );

    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableCell.addElement( label );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( tableCell );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.print(logicalPageBox);

    final NodeMatcher matcher = new ChildMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    assertEquals( 1, all.length );
    for ( final RenderNode renderNode : all ) {
      assertEquals( 0l, renderNode.getX() );
      assertEquals( 10000000l, renderNode.getWidth() );
      assertEquals( 20000000l, renderNode.getHeight() );
      assertEquals( 0l, renderNode.getY() );
    }
  }

  public void testSimpleTable2() throws ReportProcessingException, ContentProcessingException {
    final Band tableCellA1 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell A1" ) );
    final Band tableCellA2 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell A2" ) );
    final Band tableCellB1 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell B1" ) );
    final Band tableCellB2 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell B2" ) );
    final Band tableCellC1 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell C1" ) );
    final Band tableCellC2 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell C2" ) );

    final Band tableRowA = TableTestUtil.createRow( tableCellA1, tableCellA2 );
    final Band tableRowB = TableTestUtil.createRow( tableCellB1, tableCellB2 );
    final Band tableRowC = TableTestUtil.createRow( tableCellC1, tableCellC2 );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 150f );
    tableBody.addElement( tableRowA );
    tableBody.addElement( tableRowB );
    tableBody.addElement( tableRowC );

    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    table.addElement( tableBody );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( table );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.print(logicalPageBox);

    final NodeMatcher matcher = new ChildMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    final NodeMatcher childMatcher =
        new AndMatcher( new ElementMatcher( "ParagraphRenderBox" ), new AttributeMatcher( "value" ) );
    assertEquals( 6, all.length );
    for ( final RenderNode renderNode : all ) {
      final RenderNode node = MatchFactory.match( renderNode, childMatcher );
      assertNotNull( node );

      final String name = (String) node.getAttributes().getFirstAttribute( "value" );
      assertNotNull( name );

      if ( name.endsWith( "1" ) ) {
        assertEquals( 0l, renderNode.getX() );
      } else {
        assertEquals( 23400000l, renderNode.getX() );
      }
      assertEquals( 23400000l, renderNode.getWidth() );

      if ( name.startsWith( "Cell A" ) ) {
        assertEquals( 0l, renderNode.getY() );
      } else if ( name.startsWith( "Cell B" ) ) {
        assertEquals( 20000000l, renderNode.getY() );
      } else if ( name.startsWith( "Cell C" ) ) {
        assertEquals( 40000000l, renderNode.getY() );
      }
      assertEquals( 20000000l, renderNode.getHeight() );
    }

  }

}

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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldenSampleGenerator;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.List;

public class PagebreakIT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    new File( "test-output" ).mkdir();
  }

  @Test
  public void testRunSimpleReport() throws Exception {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 10, 1 ) ) );
    report.setQuery( "query" );

    final Band table = TableTestUtil.createTable( 1, 1, 6, true );
    table.setName( "table" );
    report.getReportHeader().addElement( table );
    report.getReportHeader().setLayout( "block" );

    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2 );
    assertPageValid( pages, 0 );
    assertPageValid( pages, 1 );
    assertPageValid( pages, 2 );
  }

  @Test
  public void testRunRowSpanReport() throws Exception {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 10, 1 ) ) );
    report.setQuery( "query" );

    final Band table = TableTestUtil.createTable( 1, 1, 6, true );
    final Band section = (Band) table.getElement( 1 );
    final Band row = (Band) section.getElement( 0 );
    final Band cell = TableTestUtil.createCell( 6, 1 );
    cell.addElement( TableTestUtil.createDataItem( "Text", 100, 20 ) );
    row.addElement( 0, cell );

    table.setName( "table" );
    report.getReportHeader().addElement( table );
    report.getReportHeader().setLayout( "block" );

    PdfReportUtil.createPDF( report, "test-output/PRD-3857-rowspan-output.pdf" );
    /*
     * assertPageValid(report, 0); assertPageValid(report, 1); assertPageValid(report, 2);
     */
  }

  @Test
  public void testBlockWithPrePostPad() throws Exception {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 10, 1 ) ) );
    report.setQuery( "query" );

    final Band table = TableTestUtil.createTable( 1, 1, 6, true );
    table.setName( "table" );
    report.getReportHeader().addElement( TableTestUtil.createDataItem( "Pre-Padding", 100, 10 ) );
    report.getReportHeader().addElement( table );
    report.getReportHeader().addElement( TableTestUtil.createDataItem( "Post-Padding", 100, 10 ) );
    report.getReportHeader().setLayout( "block" );

    PdfReportUtil.createPDF( report, "test-output/PRD-3857-output-block.pdf" );

    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2 );
    assertPageValid( pages, 0, StrictGeomUtility.toInternalValue( 10 ) );
    assertPageValid( pages, 1 );
    assertPageValid( pages, 2 );
    // assertPageValid(report, 3);
    // assertPageValid(report, 4);
  }

  @Test
  public void testRowWithPrePostPad() throws Exception {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 10, 1 ) ) );
    report.setQuery( "query" );

    final Band table = TableTestUtil.createTable( 1, 1, 6, true );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 200f );
    table.setName( "table" );
    report.getReportHeader().addElement( TableTestUtil.createDataItem( "Pre-Padding", 100, 10 ) );
    report.getReportHeader().addElement( table );
    report.getReportHeader().addElement( TableTestUtil.createDataItem( "Post-Padding", 100, 10 ) );
    report.getReportHeader().setLayout( "row" );

    PdfReportUtil.createPDF( report, "test-output/PRD-3857-output-row.pdf" );
    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2 );

    assertPageValid( pages, 0 );
    assertPageValid( pages, 1 );
    assertPageValid( pages, 2 );
    // assertPageValid(report, 3);
    // assertPageValid(report, 4);
  }

  private class CustomProducer extends TableTestUtil.DefaultElementProducer {

    public CustomProducer() {
      super( true );
    }

    public Element createDataItem( final String text, final int row, final int column ) {
      if ( text.startsWith( "Head" ) ) {
        return TableTestUtil.createDataItem( text, 100, 99 );
      }

      Element dataItem = super.createDataItem( text, row, column );
      return dataItem;
    }
  }

  @Test
  public void testCanvasWithPrePostPad() throws Exception {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 10, 1 ) ) );
    report.setQuery( "query" );

    final Band table = TableTestUtil.createTable( 1, 1, 6, new CustomProducer() );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 200f );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 10f );
    table.setName( "table" );
    report.getReportHeader().addElement( TableTestUtil.createDataItem( "Pre-Padding", 100, 10 ) );
    report.getReportHeader().addElement( table );

    Element postPaddingItem = TableTestUtil.createDataItem( "Post-Padding", 100, 10 );
    postPaddingItem.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 300f );
    report.getReportHeader().addElement( postPaddingItem );
    report.getReportHeader().setLayout( "canvas" );

    PdfReportUtil.createPDF( report, "test-output/PRD-3857-output-canvas.pdf" );

    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2 );
    assertPageValid( pages, 0, StrictGeomUtility.toInternalValue( 10 ) );
    assertPageValid( pages, 1 );
    assertPageValid( pages, 2 );
  }

  @Test
  public void testPrd3857Report() throws Exception {
    File file = GoldenSampleGenerator.locateGoldenSampleReport( "Prd-3857-001.prpt" );
    Assert.assertNotNull( file );

    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource res = manager.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) res.getResource();

    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1 );
    assertPageValid( pages, 0 );
    assertPageValid( pages, 1 );
  }

  private void assertPageValid( final List<LogicalPageBox> pages, final int page ) throws Exception {
    assertPageValid( pages, page, 0 );
  }

  private void assertPageValid( final List<LogicalPageBox> pages, final int page, final long offset ) throws Exception {
    final LogicalPageBox pageBox = pages.get( page );
    final long pageOffset = pageBox.getPageOffset();

    // ModelPrinter.INSTANCE.print(pageBox);

    final RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( pageBox, LayoutNodeTypes.TYPE_BOX_TABLE_SECTION );
    Assert.assertEquals( 2, elementsByNodeType.length );
    final TableSectionRenderBox header = (TableSectionRenderBox) elementsByNodeType[0];
    Assert.assertEquals( TableSectionRenderBox.Role.HEADER, header.getDisplayRole() );
    final TableSectionRenderBox body = (TableSectionRenderBox) elementsByNodeType[1];
    Assert.assertEquals( TableSectionRenderBox.Role.BODY, body.getDisplayRole() );
    final RenderNode[] rows = MatchFactory.findElementsByNodeType( body, LayoutNodeTypes.TYPE_BOX_TABLE_ROW );
    Assert.assertTrue( "Have rows on page " + page, rows.length > 0 );

    Assert.assertEquals( "Header starts at top of page " + page, pageOffset + offset, header.getY() );
    Assert.assertEquals( "Row starts after the header on page " + page, header.getY() + header.getHeight(), rows[0]
        .getY() );

    final RenderNode[] table = MatchFactory.findElementsByNodeType( pageBox, LayoutNodeTypes.TYPE_BOX_TABLE );
    Assert.assertEquals( 1, table.length );
    final RenderBox box = (RenderBox) table[0];
    final RenderNode lastChild = box.getLastChild();
    Assert.assertEquals( "Table height extends correctly on page " + page, box.getY() + box.getHeight(), lastChild
        .getY()
        + lastChild.getHeight() );
  }
}

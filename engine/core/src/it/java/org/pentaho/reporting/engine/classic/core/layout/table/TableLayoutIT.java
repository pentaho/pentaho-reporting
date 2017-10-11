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
import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.GenericObjectTable;

@SuppressWarnings( "HardCodedStringLiteral" )
public class TableLayoutIT extends TestCase {

  public TableLayoutIT() {
  }

  public TableLayoutIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLayoutSmallToLarge() throws ReportProcessingException, ContentProcessingException {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final int[][] layout = new int[][] { { 200, 400 }, { 400, 800 } };

    final Band table = createTable( layout, 1 );
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( new MasterReport(), table );

    // TESTBUG: Test disabled for a few days, until we can deal with it properly
    // ModelPrinter.print(logicalPageBox);
    // assertWidth(layout, logicalPageBox);
  }

  public void testLayoutLargeToSmall() throws ReportProcessingException, ContentProcessingException {
    final int[][] layout = new int[][] { { 300, 600 }, { 200, 100 } };

    final Band table = createTable( layout, 1 );
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( new MasterReport(), table );

    // TESTBUG: Test disabled for a few days, until we can deal with it properly
    // ModelPrinter.INSTANCE.print(logicalPageBox);
    // assertWidth(layout, logicalPageBox);
  }

  public void assertWidth( final int[][] layout, final RenderNode logicalPageBox ) {
    final GenericObjectTable<Long> table = new GenericObjectTable<Long>();

    for ( int r = 0; r < layout.length; r++ ) {
      final int[] cells = layout[r];
      for ( int c = 0; c < cells.length; c++ ) {
        final Long object = table.getObject( 0, c );
        final long l = StrictGeomUtility.toInternalValue( cells[c] );
        if ( object == null ) {
          table.setObject( 0, c, Long.valueOf( l ) );
        } else {
          table.setObject( 0, c, Long.valueOf( Math.max( object.longValue(), l ) ) );
        }
      }
    }

    for ( int r = 0; r < layout.length; r++ ) {
      final int[] cells = layout[r];
      for ( int c = 0; c < cells.length; c++ ) {
        final String cellName = "c-" + r + "-" + c;
        final RenderNode[] elementsByName = MatchFactory.findElementsByName( logicalPageBox, cellName );
        assertEquals( "Cell '" + cellName + "' exists", 1, elementsByName.length );
        assertEquals( table.getObject( 0, c ).longValue(), elementsByName[0].getWidth() );
      }
    }

  }

  public void testFixedSizeTableCells() throws Exception {

    final Band tableCell1 = TableTestUtil.createCell( 0, 0, 100, 20, TableTestUtil.createDataItem( "Text", 100, 20 ) );
    tableCell1.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, Integer.valueOf( 2 ) );
    final Band tableCell2 = TableTestUtil.createCell( 0, 1, 100, 20, TableTestUtil.createDataItem( "Text2", 100, 20 ) );

    final Band tableRow = new Band();
    tableRow.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow.addElement( tableCell1 );
    tableRow.addElement( tableCell2 );

    final Band tableCell3 = TableTestUtil.createCell( 1, 1, 100, 20, TableTestUtil.createDataItem( "Text3", 100, 20 ) );
    final Band tableRow2 = new Band();
    tableRow2.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow2.addElement( tableCell3 );

    final Band tableSection = new Band();
    tableSection.setLayout( BandStyleKeys.LAYOUT_TABLE_BODY );
    tableSection.addElement( tableRow );
    tableSection.addElement( tableRow2 );

    final MasterReport report = new MasterReport();
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_TABLE );
    report.getReportHeader().addElement( tableSection );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(logicalPageBox);

    final RenderNode renderedCell1 = MatchFactory.findElementByName( logicalPageBox, "c-0-0" );
    assertNotNull( renderedCell1 );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 40 ), renderedCell1.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell1.getWidth() );

    final RenderNode renderedCell2 = MatchFactory.findElementByName( logicalPageBox, "c-0-1" );
    assertNotNull( renderedCell2 );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell2.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), renderedCell2.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getWidth() );

    final RenderNode renderedCell3 = MatchFactory.findElementByName( logicalPageBox, "c-1-1" );
    assertNotNull( renderedCell3 );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), renderedCell3.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), renderedCell3.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getWidth() );

    // Validate that tableCell1 has a layouted height of 40 (2* 20)

  }

  public void testFixedSizeTableCellsRelativeSizeComplex() throws Exception {
    if ( !DebugReportRunner.isSafeToTestComplexText() ) {
      return;
    }

    final Band tableCell1 =
        TableTestUtil.createCell( 0, 0, 100, 10, TableTestUtil.createDataItem( "Text", -100, -100 ) );
    tableCell1.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, Integer.valueOf( 2 ) );
    final Band tableCell2 =
        TableTestUtil.createCell( 0, 1, 100, 10, TableTestUtil.createDataItem( "Text2", -100, -100 ) );

    final Band tableRow = new Band();
    tableRow.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow.addElement( tableCell1 );
    tableRow.addElement( tableCell2 );

    final Band tableCell3 =
        TableTestUtil.createCell( 1, 1, 100, 10, TableTestUtil.createDataItem( "Text3", -100, -100 ) );
    final Band tableRow2 = new Band();
    tableRow2.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow2.addElement( tableCell3 );

    final Band tableSection = new Band();
    tableSection.setLayout( BandStyleKeys.LAYOUT_TABLE_BODY );
    tableSection.addElement( tableRow );
    tableSection.addElement( tableRow2 );

    final MasterReport report = new MasterReport();
    report.getStyle().setStyleProperty( TextStyleKeys.WORDBREAK, true );
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_TABLE );
    report.getReportHeader().addElement( tableSection );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(logicalPageBox);

    final RenderNode renderedCell1 = MatchFactory.findElementByName( logicalPageBox, "c-0-0" );
    assertNotNull( renderedCell1 );
    long heightCell00 = renderedCell1.getHeight();
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell1.getWidth() );

    final RenderNode renderedCell2 = MatchFactory.findElementByName( logicalPageBox, "c-0-1" );
    assertNotNull( renderedCell2 );
    long heightCell01 = renderedCell2.getHeight();
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell2.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getWidth() );

    final RenderNode renderedCell3 = MatchFactory.findElementByName( logicalPageBox, "c-1-1" );
    assertNotNull( renderedCell3 );
    long heightCell11 = renderedCell3.getHeight();
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getWidth() );

    Assert.assertTrue( heightCell00 > 0 );
    Assert.assertTrue( heightCell01 > 0 );
    Assert.assertTrue( heightCell11 > 0 );

    // Validate that tableCell1 has a layouted height of 40 (2* 20)
    assertEquals( heightCell00, heightCell01 + heightCell11 );

  }

  public void testFixedSizeTableCellsRelativeSize() throws Exception {

    final Band tableCell1 =
        TableTestUtil.createCell( 0, 0, 100, 10, TableTestUtil.createDataItem( "Text", -100, -100 ) );
    tableCell1.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, Integer.valueOf( 2 ) );
    final Band tableCell2 =
        TableTestUtil.createCell( 0, 1, 100, 10, TableTestUtil.createDataItem( "Text2", -100, -100 ) );

    final Band tableRow = new Band();
    tableRow.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow.addElement( tableCell1 );
    tableRow.addElement( tableCell2 );

    final Band tableCell3 =
        TableTestUtil.createCell( 1, 1, 100, 10, TableTestUtil.createDataItem( "Text3", -100, -100 ) );
    final Band tableRow2 = new Band();
    tableRow2.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow2.addElement( tableCell3 );

    final Band tableSection = new Band();
    tableSection.setLayout( BandStyleKeys.LAYOUT_TABLE_BODY );
    tableSection.addElement( tableRow );
    tableSection.addElement( tableRow2 );

    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_TABLE );
    report.getReportHeader().addElement( tableSection );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(logicalPageBox);

    final RenderNode renderedCell1 = MatchFactory.findElementByName( logicalPageBox, "c-0-0" );
    assertNotNull( renderedCell1 );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), renderedCell1.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell1.getWidth() );

    final RenderNode renderedCell2 = MatchFactory.findElementByName( logicalPageBox, "c-0-1" );
    assertNotNull( renderedCell2 );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell2.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 10 ), renderedCell2.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getWidth() );

    final RenderNode renderedCell3 = MatchFactory.findElementByName( logicalPageBox, "c-1-1" );
    assertNotNull( renderedCell3 );
    assertEquals( StrictGeomUtility.toInternalValue( 10 ), renderedCell3.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 10 ), renderedCell3.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getWidth() );

    // Validate that tableCell1 has a layouted height of 40 (2* 20)

  }

  private Band wrapInCanvas( final Element e ) {
    final Band band = new Band();
    band.setLayout( BandStyleKeys.LAYOUT_CANVAS );
    band.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    band.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    band.addElement( e );
    return band;
  }

  public void testFixedSizeTableCellsRelativeSizeCanvas() throws Exception {
    final Band tableCell1 =
        TableTestUtil.createCell( 0, 0, 100, 10, wrapInCanvas( TableTestUtil.createDataItem( "Text", -100, -100 ) ) );
    tableCell1.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, 2 );
    final Band tableCell2 =
        TableTestUtil.createCell( 0, 1, 100, 10, wrapInCanvas( TableTestUtil.createDataItem( "Text2", -100, -100 ) ) );

    final Band tableRow = new Band();
    tableRow.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow.addElement( tableCell1 );
    tableRow.addElement( tableCell2 );

    final Band tableCell3 =
        TableTestUtil.createCell( 1, 1, 100, 10, wrapInCanvas( TableTestUtil.createDataItem( "Text3", -100, -100 ) ) );
    final Band tableRow2 = new Band();
    tableRow2.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow2.addElement( tableCell3 );

    final Band tableSection = new Band();
    tableSection.setLayout( BandStyleKeys.LAYOUT_TABLE_BODY );
    tableSection.addElement( tableRow );
    tableSection.addElement( tableRow2 );

    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_TABLE );
    report.getReportHeader().addElement( tableSection );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(logicalPageBox);

    final RenderBox renderedCell1 = (RenderBox) MatchFactory.findElementByName( logicalPageBox, "c-0-0" );
    assertNotNull( renderedCell1 );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), renderedCell1.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell1.getWidth() );

    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getFirstChild().getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getFirstChild().getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), renderedCell1.getFirstChild().getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell1.getFirstChild().getWidth() );

    final RenderBox renderedCell2 = (RenderBox) MatchFactory.findElementByName( logicalPageBox, "c-0-1" );
    assertNotNull( renderedCell2 );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell2.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 10 ), renderedCell2.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getWidth() );

    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell2.getFirstChild().getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getFirstChild().getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 10 ), renderedCell2.getFirstChild().getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getFirstChild().getWidth() );

    final RenderBox renderedCell3 = (RenderBox) MatchFactory.findElementByName( logicalPageBox, "c-1-1" );
    assertNotNull( renderedCell3 );
    assertEquals( StrictGeomUtility.toInternalValue( 10 ), renderedCell3.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 10 ), renderedCell3.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getWidth() );

    assertEquals( StrictGeomUtility.toInternalValue( 10 ), renderedCell3.getFirstChild().getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getFirstChild().getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 10 ), renderedCell3.getFirstChild().getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getFirstChild().getWidth() );

    // Validate that tableCell1 has a layouted height of 40 (2* 20)

  }

  public void testFixedSizeTableCellsRelativeSizeCanvasComplex() throws Exception {
    if ( !DebugReportRunner.isSafeToTestComplexText() ) {
      return;
    }
    final Band tableCell1 =
        TableTestUtil.createCell( 0, 0, 100, 10, wrapInCanvas( TableTestUtil.createDataItem( "Text", -100, -100 ) ) );
    tableCell1.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, 2 );
    final Band tableCell2 =
        TableTestUtil.createCell( 0, 1, 100, 10, wrapInCanvas( TableTestUtil.createDataItem( "Text2", -100, -100 ) ) );

    final Band tableRow = new Band();
    tableRow.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow.addElement( tableCell1 );
    tableRow.addElement( tableCell2 );

    final Band tableCell3 =
        TableTestUtil.createCell( 1, 1, 100, 10, wrapInCanvas( TableTestUtil.createDataItem( "Text3", -100, -100 ) ) );
    final Band tableRow2 = new Band();
    tableRow2.setLayout( BandStyleKeys.LAYOUT_TABLE_ROW );
    tableRow2.addElement( tableCell3 );

    final Band tableSection = new Band();
    tableSection.setLayout( BandStyleKeys.LAYOUT_TABLE_BODY );
    tableSection.addElement( tableRow );
    tableSection.addElement( tableRow2 );

    final MasterReport report = new MasterReport();
    report.getStyle().setStyleProperty( TextStyleKeys.WORDBREAK, true );
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    report.getReportHeader().setLayout( BandStyleKeys.LAYOUT_TABLE );
    report.getReportHeader().addElement( tableSection );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(logicalPageBox);

    final RenderBox renderedCell1 = (RenderBox) MatchFactory.findElementByName( logicalPageBox, "c-0-0" );
    assertNotNull( renderedCell1 );
    long heightCell00 = renderedCell1.getHeight();
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getX() );
    assertTrue( heightCell00 > StrictGeomUtility.toInternalValue( 20 ) );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell1.getWidth() );

    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getFirstChild().getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell1.getFirstChild().getX() );
    assertEquals( heightCell00, renderedCell1.getFirstChild().getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell1.getFirstChild().getWidth() );

    final RenderBox renderedCell2 = (RenderBox) MatchFactory.findElementByName( logicalPageBox, "c-0-1" );
    assertNotNull( renderedCell2 );
    long heightCell01 = renderedCell2.getHeight();
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell2.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getWidth() );

    assertEquals( StrictGeomUtility.toInternalValue( 0 ), renderedCell2.getFirstChild().getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getFirstChild().getX() );
    assertEquals( heightCell01, renderedCell2.getFirstChild().getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell2.getFirstChild().getWidth() );

    final RenderBox renderedCell3 = (RenderBox) MatchFactory.findElementByName( logicalPageBox, "c-1-1" );
    long heightCell11 = renderedCell3.getHeight();
    assertNotNull( renderedCell3 );
    assertEquals( heightCell01, renderedCell3.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getWidth() );

    assertEquals( heightCell01, renderedCell3.getFirstChild().getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getFirstChild().getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), renderedCell3.getFirstChild().getWidth() );

    // Validate that tableCell1 has a layouted height of 40 (2* 20)
    assertEquals( heightCell00, heightCell01 + heightCell11 );
  }

  public static Band createTable( final int[][] layout, final int headerRows ) {
    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( BandStyleKeys.TABLE_LAYOUT, TableLayout.fixed );

    if ( headerRows > 0 ) {
      final Band tableHeader = new Band();
      tableHeader.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_HEADER );

      for ( int r = 0; r < headerRows; r += 1 ) {
        final Band row = new Band();
        row.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_ROW );
        row.setName( "r-" + r );

        final int[] rowDefinition = layout[r];
        for ( int cellNumber = 0; cellNumber < rowDefinition.length; cellNumber++ ) {
          final int cellWidth = rowDefinition[cellNumber];

          final Band cell = TableTestUtil.createCell( r, cellNumber, cellWidth, 10 );
          cell.setName( "c-" + r + "-" + cellNumber );
          row.addElement( cell );
        }
        tableHeader.addElement( row );
      }
      table.addElement( tableHeader );
    }

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    for ( int r = headerRows; r < layout.length; r += 1 ) {
      final Band row = new Band();
      row.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_ROW );
      row.setName( "r-" + r );

      final int[] rowDefinition = layout[r];
      for ( int cellNumber = 0; cellNumber < rowDefinition.length; cellNumber++ ) {
        final int cellWidth = rowDefinition[cellNumber];

        final Band cell = TableTestUtil.createCell( r, cellNumber, cellWidth, 10 );
        cell.setName( "c-" + r + "-" + cellNumber );
        row.addElement( cell );
      }
      tableBody.addElement( row );
    }
    table.addElement( tableBody );
    return table;
  }
}

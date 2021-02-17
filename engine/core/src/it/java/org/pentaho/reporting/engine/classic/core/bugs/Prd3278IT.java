/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2021 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.util.Units;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SlimSheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelImageHandler;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelPrinterBase;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd3278IT {
  private static class TestSheetLayout extends SheetLayout {
    private TestSheetLayout( final boolean strict, final boolean ellipseAsRectangle ) {
      super( strict, ellipseAsRectangle );
    }

    public void ensureXMapping( final long coordinate, final Boolean aux ) {
      super.ensureXMapping( coordinate, aux );
    }

    public void ensureYMapping( final long coordinate, final Boolean aux ) {
      super.ensureYMapping( coordinate, aux );
    }
  }

  private TestSheetLayout sheetLayout;
  private TestExcelImageHandler imageHandler;
  private TestExcelImageHandler xlsxImageHandler;
  private FastExcelPrinter excelPrinter;
  private FastExcelPrinter xlsxPrinter;

  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();

    sheetLayout = new TestSheetLayout( true, false );
    sheetLayout.ensureXMapping( StrictGeomUtility.toInternalValue( 0 ), Boolean.FALSE );
    sheetLayout.ensureXMapping( StrictGeomUtility.toInternalValue( 100 ), Boolean.FALSE );
    sheetLayout.ensureXMapping( StrictGeomUtility.toInternalValue( 500 ), Boolean.FALSE );
    sheetLayout.ensureXMapping( StrictGeomUtility.toInternalValue( 600 ), Boolean.FALSE );
    sheetLayout.ensureYMapping( StrictGeomUtility.toInternalValue( 0 ), Boolean.FALSE );
    sheetLayout.ensureYMapping( StrictGeomUtility.toInternalValue( 100 ), Boolean.FALSE );
    sheetLayout.ensureYMapping( StrictGeomUtility.toInternalValue( 500 ), Boolean.FALSE );
    sheetLayout.ensureYMapping( StrictGeomUtility.toInternalValue( 600 ), Boolean.FALSE );

    ExcelOutputProcessorMetaData metaData =
        new ExcelOutputProcessorMetaData( ExcelOutputProcessorMetaData.PAGINATION_MANUAL );
    metaData.initialize( ClassicEngineBoot.getInstance().getGlobalConfig() );
    ResourceManager resourceManager = new ResourceManager();

    excelPrinter = new FastExcelPrinter( sheetLayout );
    excelPrinter.setUseXlsxFormat( false );
    excelPrinter.init( metaData, resourceManager, new MasterReport() );
    imageHandler = new TestExcelImageHandler( resourceManager, excelPrinter );

    xlsxPrinter = new FastExcelPrinter( sheetLayout );
    xlsxPrinter.setUseXlsxFormat( true );
    xlsxPrinter.init( metaData, resourceManager, new MasterReport() );
    xlsxImageHandler = new TestExcelImageHandler( resourceManager, xlsxPrinter );

  }

  private class TestExcelImageHandler extends ExcelImageHandler {
    private TestExcelImageHandler( final ResourceManager resourceManager, final ExcelPrinterBase printerBase ) {
      super( resourceManager, printerBase );
    }

    public ClientAnchor computeClientAnchor( final SlimSheetLayout currentLayout, final TableRectangle rectangle,
        final StrictBounds cb ) {
      return super.computeClientAnchor( currentLayout, rectangle, cb );
    }
  }

  @Test
  public void testImageAligningToCells() {
    TableRectangle rect = new TableRectangle();
    rect.setRect( 1, 1, 2, 2 );
    StrictBounds b =
        new StrictBounds( StrictGeomUtility.toInternalValue( 100 ), StrictGeomUtility.toInternalValue( 100 ),
            StrictGeomUtility.toInternalValue( 400 ), StrictGeomUtility.toInternalValue( 400 ) );
    ClientAnchor clientAnchor = imageHandler.computeClientAnchor( sheetLayout, rect, b );
    Assert.assertEquals( 1, clientAnchor.getCol1() );
    Assert.assertEquals( 1, clientAnchor.getCol2() );
    Assert.assertEquals( 1, clientAnchor.getRow1() );
    Assert.assertEquals( 1, clientAnchor.getRow2() );

    Assert.assertEquals( 0, clientAnchor.getDx1() );
    Assert.assertEquals( 1023, clientAnchor.getDx2() );
    Assert.assertEquals( 0, clientAnchor.getDy1() );
    Assert.assertEquals( 255, clientAnchor.getDy2() );
  }

  @Test
  public void testImageAligningLeftAndTop() {
    TableRectangle rect = new TableRectangle();
    rect.setRect( 1, 1, 2, 2 );
    StrictBounds b =
        new StrictBounds( StrictGeomUtility.toInternalValue( 100 ), StrictGeomUtility.toInternalValue( 100 ),
            StrictGeomUtility.toInternalValue( 300 ), StrictGeomUtility.toInternalValue( 300 ) );

    ClientAnchor clientAnchor = imageHandler.computeClientAnchor( sheetLayout, rect, b );
    Assert.assertEquals( 1, clientAnchor.getCol1() );
    Assert.assertEquals( 1, clientAnchor.getCol2() );
    Assert.assertEquals( 1, clientAnchor.getRow1() );
    Assert.assertEquals( 1, clientAnchor.getRow2() );

    Assert.assertEquals( 0, clientAnchor.getDx1() );
    Assert.assertEquals( 1023 * 3 / 4, clientAnchor.getDx2() );
    Assert.assertEquals( 0, clientAnchor.getDy1() );
    Assert.assertEquals( 255 * 3 / 4, clientAnchor.getDy2() );
  }

  @Test
  public void testImageAligningMiddle() {
    TableRectangle rect = new TableRectangle();
    rect.setRect( 1, 1, 2, 2 );
    StrictBounds b =
        new StrictBounds( StrictGeomUtility.toInternalValue( 200 ), StrictGeomUtility.toInternalValue( 200 ),
            StrictGeomUtility.toInternalValue( 200 ), StrictGeomUtility.toInternalValue( 200 ) );

    ClientAnchor clientAnchor = imageHandler.computeClientAnchor( sheetLayout, rect, b );
    Assert.assertEquals( 1, clientAnchor.getCol1() );
    Assert.assertEquals( 1, clientAnchor.getCol2() );
    Assert.assertEquals( 1, clientAnchor.getRow1() );
    Assert.assertEquals( 1, clientAnchor.getRow2() );

    Assert.assertEquals( 255, clientAnchor.getDx1() );
    Assert.assertEquals( 1023 * 3 / 4, clientAnchor.getDx2() );
    Assert.assertEquals( 63, clientAnchor.getDy1() );
    Assert.assertEquals( 255 * 3 / 4, clientAnchor.getDy2() );
  }

  @Test
  public void testImageAligningToCellsXLSX() {
    TableRectangle rect = new TableRectangle();
    rect.setRect( 1, 1, 2, 2 );
    StrictBounds b =
        new StrictBounds( StrictGeomUtility.toInternalValue( 100 ), StrictGeomUtility.toInternalValue( 100 ),
            StrictGeomUtility.toInternalValue( 400 ), StrictGeomUtility.toInternalValue( 400 ) );
    ClientAnchor clientAnchor = xlsxImageHandler.computeClientAnchor( sheetLayout, rect, b );
    Assert.assertEquals( 1, clientAnchor.getCol1() );
    Assert.assertEquals( 1, clientAnchor.getCol2() );
    Assert.assertEquals( 1, clientAnchor.getRow1() );
    Assert.assertEquals( 1, clientAnchor.getRow2() );

    Assert.assertEquals( 0, clientAnchor.getDx1() );
    Assert.assertEquals( 400 * Units.EMU_PER_POINT, clientAnchor.getDx2() );
    Assert.assertEquals( 0, clientAnchor.getDy1() );
    Assert.assertEquals( 400 * Units.EMU_PER_POINT, clientAnchor.getDy2() );
  }

  @Test
  public void testImageAligningLeftAndTopXLSX() {
    TableRectangle rect = new TableRectangle();
    rect.setRect( 1, 1, 2, 2 );
    StrictBounds b =
        new StrictBounds( StrictGeomUtility.toInternalValue( 100 ), StrictGeomUtility.toInternalValue( 100 ),
            StrictGeomUtility.toInternalValue( 300 ), StrictGeomUtility.toInternalValue( 300 ) );

    ClientAnchor clientAnchor = xlsxImageHandler.computeClientAnchor( sheetLayout, rect, b );
    Assert.assertEquals( 1, clientAnchor.getCol1() );
    Assert.assertEquals( 1, clientAnchor.getCol2() );
    Assert.assertEquals( 1, clientAnchor.getRow1() );
    Assert.assertEquals( 1, clientAnchor.getRow2() );

    Assert.assertEquals( 0, clientAnchor.getDx1() );
    Assert.assertEquals( 300 * Units.EMU_PER_POINT, clientAnchor.getDx2() );
    Assert.assertEquals( 0, clientAnchor.getDy1() );
    Assert.assertEquals( 300 * Units.EMU_PER_POINT, clientAnchor.getDy2() );
  }

  @Test
  public void testImageAligningMiddleXLSX() {
    TableRectangle rect = new TableRectangle();
    rect.setRect( 1, 1, 2, 2 );
    StrictBounds b =
        new StrictBounds( StrictGeomUtility.toInternalValue( 200 ), StrictGeomUtility.toInternalValue( 200 ),
            StrictGeomUtility.toInternalValue( 200 ), StrictGeomUtility.toInternalValue( 200 ) );

    ClientAnchor clientAnchor = xlsxImageHandler.computeClientAnchor( sheetLayout, rect, b );
    Assert.assertEquals( 1, clientAnchor.getCol1() );
    Assert.assertEquals( 1, clientAnchor.getCol2() );
    Assert.assertEquals( 1, clientAnchor.getRow1() );
    Assert.assertEquals( 1, clientAnchor.getRow2() );

    Assert.assertEquals( 100 * Units.EMU_PER_POINT, clientAnchor.getDx1() );
    Assert.assertEquals( 300 * Units.EMU_PER_POINT, clientAnchor.getDx2() );
    Assert.assertEquals( 100 * Units.EMU_PER_POINT, clientAnchor.getDy1() );
    Assert.assertEquals( 300 * Units.EMU_PER_POINT, clientAnchor.getDy2() );
  }

}

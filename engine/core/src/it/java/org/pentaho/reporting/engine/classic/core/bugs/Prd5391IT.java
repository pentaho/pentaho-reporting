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


package org.pentaho.reporting.engine.classic.core.bugs;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Prd5391IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testSlowExport() throws ResourceException, ReportProcessingException, IOException {
    // This establishes a baseline for the second test using the slow export.

    final MasterReport report = DebugReportRunner.parseLocalReport( "Prd-5391.prpt", Prd5391IT.class );
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ExcelReportUtil.createXLS( report, bout );

    final HSSFWorkbook wb = new HSSFWorkbook( new ByteArrayInputStream( bout.toByteArray() ) );
    final HSSFSheet sheetAt = wb.getSheetAt( 0 );
    final HSSFRow row = sheetAt.getRow( 0 );
    final HSSFCell cell0 = row.getCell( 0 );

    // assert that we are in the correct export type ..
    final HSSFCellStyle cellStyle = cell0.getCellStyle();
    final HSSFColor fillBackgroundColorColor = cellStyle.getFillBackgroundColorColor();
    final HSSFColor fillForegroundColorColor = cellStyle.getFillForegroundColorColor();
    Assert.assertEquals( "0:0:0", fillBackgroundColorColor.getHexString() );
    Assert.assertEquals( "FFFF:8080:8080", fillForegroundColorColor.getHexString() );

    HSSFFont font = cellStyle.getFont( wb );
    Assert.assertEquals( "Times New Roman", font.getFontName() );
  }

  @Test
  public void testFastExport() throws ResourceException, ReportProcessingException, IOException {
    // This establishes a baseline for the second test using the slow export.

    final MasterReport report = DebugReportRunner.parseLocalReport( "Prd-5391.prpt", Prd5391IT.class );
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    FastExcelReportUtil.processXls( report, bout );

    final HSSFWorkbook wb = new HSSFWorkbook( new ByteArrayInputStream( bout.toByteArray() ) );
    final HSSFSheet sheetAt = wb.getSheetAt( 0 );
    final HSSFRow row = sheetAt.getRow( 0 );
    final HSSFCell cell0 = row.getCell( 0 );

    // assert that we are in the correct export type ..
    final HSSFCellStyle cellStyle = cell0.getCellStyle();
    final HSSFColor fillBackgroundColorColor = cellStyle.getFillBackgroundColorColor();
    final HSSFColor fillForegroundColorColor = cellStyle.getFillForegroundColorColor();
    Assert.assertEquals( "0:0:0", fillBackgroundColorColor.getHexString() );
    Assert.assertEquals( "FFFF:8080:8080", fillForegroundColorColor.getHexString() );

    HSSFFont font = cellStyle.getFont( wb );
    Assert.assertEquals( "Times New Roman", font.getFontName() );
  }
}

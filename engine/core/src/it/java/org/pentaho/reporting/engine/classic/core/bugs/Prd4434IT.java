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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Prd4434IT extends TestCase {
  public Prd4434IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testExcelExport() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3625.prpt" );

    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ExcelReportUtil.createXLS( report, bout );

    final HSSFWorkbook wb = new HSSFWorkbook( new ByteArrayInputStream( bout.toByteArray() ) );
    final HSSFSheet sheetAt = wb.getSheetAt( 0 );
    final HSSFRow row = sheetAt.getRow( 0 );
    final HSSFCell cell0 = row.getCell( 0 );
    assertEquals( CellType.NUMERIC, cell0.getCellType() );
    assertEquals( "yyyy-MM-dd", cell0.getCellStyle().getDataFormatString() );
    final HSSFCell cell1 = row.getCell( 1 );
    assertEquals( CellType.NUMERIC, cell1.getCellType() );
    assertEquals( "#,###.00;(#,###.00)", cell1.getCellStyle().getDataFormatString() );
  }

}

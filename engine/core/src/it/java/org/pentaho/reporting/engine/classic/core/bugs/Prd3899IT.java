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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Prd3899IT extends TestCase {
  public Prd3899IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBug() throws ResourceException, IOException, ReportProcessingException {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3889.prpt" );
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
    assertEquals( "0:0:0", fillBackgroundColorColor.getHexString() );
    assertEquals( "FFFF:FFFF:9999", fillForegroundColorColor.getHexString() );

    // assert that there are no extra columns ..
    final HSSFRow row8 = sheetAt.getRow( 7 );
    assertNull( row8 );

  }
}

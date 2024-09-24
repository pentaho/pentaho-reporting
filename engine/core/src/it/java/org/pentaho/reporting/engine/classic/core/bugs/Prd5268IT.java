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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelReportUtil;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

public class Prd5268IT {
  public Prd5268IT() {
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testSheetNames() throws Exception {
    MasterReport report = createReport();
    Assert.assertFalse( new ReportStructureValidator().isValidForFastProcessing( report ) );

    ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    FastExcelReportUtil.processXlsx( report, boutFast );

    Workbook workbook = WorkbookFactory.create( new ByteArrayInputStream( boutFast.toByteArray() ) );
    Assert.assertEquals( 3, workbook.getNumberOfSheets() );
    Assert.assertEquals( "FIRST REPORT", workbook.getSheetName( 0 ) );
    Assert.assertEquals( "SECOND REPORT", workbook.getSheetName( 1 ) );
    Assert.assertEquals( "SECOND REPORT 2", workbook.getSheetName( 2 ) );
  }

  @Test
  public void testSheetContent() throws Exception {
    MasterReport report = createReport();
    Assert.assertFalse( new ReportStructureValidator().isValidForFastProcessing( report ) );

    ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    FastExcelReportUtil.processXlsx( report, boutFast );

    Workbook workbook = WorkbookFactory.create( new ByteArrayInputStream( boutFast.toByteArray() ) );
    Assert.assertEquals( 3, workbook.getNumberOfSheets() );
    assertSheetNotEmpty( workbook.getSheetAt( 0 ) );
    assertSheetNotEmpty( workbook.getSheetAt( 1 ) );
    assertSheetNotEmpty( workbook.getSheetAt( 2 ) );
  }

  private void assertSheetNotEmpty( final Sheet sheet ) {
    Assert.assertTrue( sheet.getPhysicalNumberOfRows() > 0 );
    for ( int r = 0; r < 10; r += 1 ) {
      Row row = sheet.getRow( r );
      Assert.assertNotNull( row );
      Assert.assertNotNull( row.getCell( 0 ) );
      Assert.assertNotNull( row.getCell( 0 ).getStringCellValue() );
    }
  }

  private MasterReport createReport() throws ResourceException {
    URL resource = getClass().getResource( "Prd-5268-min.prpt" );
    return (MasterReport) new ResourceManager().createDirectly( resource, MasterReport.class ).getResource();
  }
}

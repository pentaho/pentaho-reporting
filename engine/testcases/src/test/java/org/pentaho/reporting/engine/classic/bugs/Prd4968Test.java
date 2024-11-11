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


package org.pentaho.reporting.engine.classic.bugs;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.naming.spi.NamingManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class Prd4968Test {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  @Test
  public void testExcelExport() throws Exception {
    URL resource = getClass().getResource( "Prd-4968.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ExcelReportUtil.createXLS( report, bout );
    Workbook workbook = WorkbookFactory.create( new ByteArrayInputStream( bout.toByteArray() ) );
    assertEquals( 34, workbook.getNumCellStyles() );
    assertEquals( 9, workbook.getNumberOfFonts() );
  }

  @Test
  public void testExcel2007Export() throws Exception {
    URL resource = getClass().getResource( "Prd-4968.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ExcelReportUtil.createXLSX( report, bout );
    Workbook workbook = WorkbookFactory.create( new ByteArrayInputStream( bout.toByteArray() ) );
    assertEquals( 14, workbook.getNumCellStyles() );
    assertEquals( 6, workbook.getNumberOfFonts() );

    //    File testOutputFile = DebugReportRunner.createTestOutputFile();
    //    ExcelReportUtil.createXLSX(report, "test-output/Prd-4988.xlsx");

  }
}

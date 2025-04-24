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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertNotNull;

public class Prd5144IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testExportFormatValid() throws ReportProcessingException, IOException {
    MasterReport report = new MasterReport();
    ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    FastExcelReportUtil.processXlsx( report, boutFast );
    ExcelReportUtil.createXLSX( report, boutSlow );

    assertZip( boutFast );
    assertZip( boutSlow );
  }

  @Test
  public void testExportFormatInValid() throws ReportProcessingException, IOException {
    MasterReport report = new MasterReport();
    report.getReportHeader().addElement( new SubReport() );
    ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    FastExcelReportUtil.processXlsx( report, boutFast );
    ExcelReportUtil.createXLSX( report, boutSlow );

    assertZip( boutFast );
    assertZip( boutSlow );
  }

  private void assertZip( final ByteArrayOutputStream outputStream ) throws IOException {
    try ( ByteArrayInputStream inputStream = new ByteArrayInputStream( outputStream.toByteArray() );
          ZipInputStream zin = new ZipInputStream( inputStream ) ) {
      assertNotNull( "Expected at least 1 entry in the zip file", zin.getNextEntry() );
    }
  }

}

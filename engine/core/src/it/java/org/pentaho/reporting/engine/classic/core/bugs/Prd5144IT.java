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

  private void assertZip( final ByteArrayOutputStream boutSlow ) throws IOException {
    ZipInputStream zin = new ZipInputStream( new ByteArrayInputStream( boutSlow.toByteArray() ) );
    int entries = 0;
    while ( zin.getNextEntry() != null ) {
      entries += 1;
    }
    // IntelliJ bug: Does not detect update within While loop.
    // noinspection ConstantConditions
    Assert.assertTrue( entries > 0 );
  }

}

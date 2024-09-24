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

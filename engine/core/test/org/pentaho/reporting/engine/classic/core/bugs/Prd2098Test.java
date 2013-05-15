package org.pentaho.reporting.engine.classic.core.bugs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class Prd2098Test extends TestCase
{
  public Prd2098Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReportInExcel() throws Exception
  {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport("Prd-2098.prpt");
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ExcelReportUtil.createXLS(report, bout);

    final HSSFWorkbook wb = new HSSFWorkbook(new ByteArrayInputStream(bout.toByteArray()));
    assertNull(wb.getSheetAt(2).getRow(2));
    // this row would exist to have a height if the bug is there.
  }
}

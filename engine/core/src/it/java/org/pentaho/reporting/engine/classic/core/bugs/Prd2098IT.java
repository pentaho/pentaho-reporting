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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Prd2098IT extends TestCase {
  public Prd2098IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReportInExcel() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-2098.prpt" );
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ExcelReportUtil.createXLS( report, bout );

    final HSSFWorkbook wb = new HSSFWorkbook( new ByteArrayInputStream( bout.toByteArray() ) );
    assertNull( wb.getSheetAt( 2 ).getRow( 2 ) );
    // this row would exist to have a height if the bug is there.
  }

  public void testGoldenSampleReport() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-2098.prpt" );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 1 );

    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_PROGRESS_MARKER );
    assertEquals( 3, elementsByNodeType.length );
  }

  public void testRunGoldenSample() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-2098.prpt" );
    DebugReportRunner.createXmlFlow( report );
  }
}

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

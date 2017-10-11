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

package org.pentaho.reporting.engine.classic.core.crosstab;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

import java.util.List;

public class CrosstabPagebreakIT extends TestCase {
  public CrosstabPagebreakIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testStandardReport() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }

    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3857-001.prpt" );
    final Group rootGroup = report.getRootGroup();
    assertTrue( rootGroup instanceof CrosstabGroup );

    final CrosstabGroup ct = (CrosstabGroup) rootGroup;
    ct.setPrintColumnTitleHeader( true );
    ct.setPrintDetailsHeader( false );

    // Prints 4 header rows, and 19 data rows (row 0 to row 18)
    List<LogicalPageBox> logicalPageBoxes = DebugReportRunner.layoutPages( report, 0, 1 );
    final LogicalPageBox boxP1 = logicalPageBoxes.get( 0 );
    // ModelPrinter.INSTANCE.print(boxP1);
    final RenderNode[] rowsPage1 = MatchFactory.findElementsByNodeType( boxP1, LayoutNodeTypes.TYPE_BOX_TABLE_ROW );
    assertEquals( 23, rowsPage1.length );

    // Prints 4 header rows and 9 data rows (row 19 to row 27)
    final LogicalPageBox boxP2 = logicalPageBoxes.get( 1 );
    // ModelPrinter.INSTANCE.print(boxP2);
    final RenderNode[] rowsPage2 = MatchFactory.findElementsByNodeType( boxP2, LayoutNodeTypes.TYPE_BOX_TABLE_ROW );
    assertEquals( 13, rowsPage2.length );

  }

  public void testStandardReport2() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }

    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3857-001.prpt" );
    final Group rootGroup = report.getRootGroup();
    assertTrue( rootGroup instanceof CrosstabGroup );

    final CrosstabGroup ct = (CrosstabGroup) rootGroup;
    ct.setPrintColumnTitleHeader( false );
    ct.setPrintDetailsHeader( false );

    // Prints two header rows, and 21 data rows (row 0 to row 20)
    List<LogicalPageBox> logicalPageBoxes = DebugReportRunner.layoutPages( report, 0, 1 );
    final LogicalPageBox boxP1 = logicalPageBoxes.get( 0 );
    // ModelPrinter.INSTANCE.print(boxP1);
    final RenderNode[] rowsPage1 = MatchFactory.findElementsByNodeType( boxP1, LayoutNodeTypes.TYPE_BOX_TABLE_ROW );
    assertEquals( 23, rowsPage1.length );

    // Prints two header rows and 7 data rows (row 21 to row 27)
    final LogicalPageBox boxP2 = logicalPageBoxes.get( 1 );
    // ModelPrinter.INSTANCE.print(boxP2);
    final RenderNode[] rowsPage2 = MatchFactory.findElementsByNodeType( boxP2, LayoutNodeTypes.TYPE_BOX_TABLE_ROW );
    assertEquals( 9, rowsPage2.length );

  }
}

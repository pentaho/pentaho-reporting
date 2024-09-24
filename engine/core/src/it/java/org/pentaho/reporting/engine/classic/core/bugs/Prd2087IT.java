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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public class Prd2087IT extends TestCase {
  public Prd2087IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testWidow1Crash() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-2087-Widow-1.prpt" );
    // masterReport.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));
    DebugReportRunner.createXmlTablePageable( masterReport );
  }

  public void testWidow1Error() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-2087-Widow-1.prpt" );
    // masterReport.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));
    // DebugReportRunner.createXmlTablePageable(masterReport);
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( masterReport, 3 );
    // ModelPrinter.INSTANCE.print(logicalPageBox);
  }

  public void testOrphan1Crash() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-2087-Orphan-0.prpt" );
    masterReport.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );
    DebugReportRunner.createXmlTablePageable( masterReport );
  }

  public void testWidow1Crash2() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-2087-Widow-1.prpt" );
    // masterReport.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));
    DebugReportRunner.createXmlPageable( masterReport );

    // DebugReportRunner.showDialog(masterReport);

  }

  public void testWidow2Crash2() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-2087-Widow-2.prpt" );
    // masterReport.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));
    DebugReportRunner.createXmlPageable( masterReport );

    // DebugReportRunner.showDialog(masterReport);

  }

  public void testSeq1Crash2() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-2087-small.prpt" );
    DebugReportRunner.createPDF( masterReport );

  }

  /**
   * Canvas elements do not shift content. Therefore the widow definition is not effective.
   *
   * @throws Exception
   */
  public void testOrphan5() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-2087-Orphan-5.prpt" );
    // masterReport.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));
    // DebugReportRunner.createXmlPageable(masterReport);

    final LogicalPageBox box = DebugReportRunner.layoutPage( masterReport, 0 );
    final RenderNode elementByName = MatchFactory.findElementByName( box, "outer-group" );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), elementByName.getY() );
    // ModelPrinter.INSTANCE.print(box);
    // DebugReportRunner.showDialog(masterReport);

  }

  public void testOrphan4() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-2087-Orphan-4.prpt" );

    final LogicalPageBox box = DebugReportRunner.layoutPage( masterReport, 0 );
    final RenderNode[] srs = MatchFactory.findElementsByElementType( box, SubReportType.INSTANCE );
    assertEquals( 1, srs.length );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), srs[0].getY() );
    final RenderNode elementByName = MatchFactory.findElementByName( box, "outer-group" );
    assertEquals( StrictGeomUtility.toInternalValue( 20 ), elementByName.getY() );

  }

}

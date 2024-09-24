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

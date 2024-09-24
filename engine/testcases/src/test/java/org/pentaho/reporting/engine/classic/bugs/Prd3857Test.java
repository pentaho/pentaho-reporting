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

package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.*;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.engine.classic.testcases.FixAllBrokenLogging;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;

@SuppressWarnings( "HardCodedStringLiteral" )
public class Prd3857Test extends TestCase {
  public Prd3857Test() {
  }

  protected void setUp() throws Exception {
    FixAllBrokenLogging.fixBrokenLogging();
    ClassicEngineBoot.getInstance().start();
  }

  public void testGoldRun2() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "2sql-subreport.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );

    DebugReportRunner.createXmlFlow( report );
  }

  public void testGoldRun3() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "trafficlighting.xml" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );

    DebugReportRunner.createXmlFlow( report );
  }

  public void testGoldRun4() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "Income Statement.xml" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel( null );

    final Band element = (Band) report.getReportHeader().getElement( 2 );
    element.setName( "Tester" );
    element.getElement( 0 ).setName( "m1" );
    report.getReportHeader().getElement( 3 ).setName( "image" );

    final LogicalPageBox pageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader(), false, false );
    //ModelPrinter.INSTANCE.print(pageBox);

    final RenderNode m1 = MatchFactory.findElementByName( pageBox, "m1" );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), m1.getX() );
    final RenderNode img = MatchFactory.findElementByName( pageBox, "image" );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), img.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), img.getWidth() );
  }

  public void testGoldRun5() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "Income Statement.xml" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel( null );

    final Band element = (Band) report.getReportHeader().getElement( 2 );
    element.setName( "Tester" );
    element.getElement( 0 ).setName( "m1" );

    DebugReportRunner.layoutSingleBand( report, report.getReportHeader() );

    final LogicalPageBox pageBox = DebugReportRunner.layoutPage( report, 0 );
    final RenderNode m1 = MatchFactory.findElementByName( pageBox, "m1" );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), m1.getX() );
  }

  public void testGoldRun5a() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "Income Statement.xml" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel( null );

    final Band element = (Band) report.getReportHeader().getElement( 2 );
    element.setName( "Tester" );
    element.getElement( 0 ).setName( "m1" );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader() );
    //ModelPrinter.INSTANCE.print(logicalPageBox);
    final RenderNode m1 = MatchFactory.findElementByName( logicalPageBox, "m1" );
    assertEquals( StrictGeomUtility.toInternalValue( 234 ), m1.getX() );
  }

  public void testGoldRun6() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "Prd-3514.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.STRICT_ERROR_HANDLING_KEY, "false" );
    //    report.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    ModelPrinter.INSTANCE.print( logicalPageBox );
  }


  public void testGoldRun7() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "pre111.xml" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    report.setCompatibilityLevel( null );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 2 );
    //ModelPrinter.INSTANCE.print(logicalPageBox);
    final RenderBox autoChild = (RenderBox) logicalPageBox.getHeaderArea().getFirstChild();
    final RenderBox canvasChild = (RenderBox) autoChild.getFirstChild();
    final RenderNode line1 = canvasChild.getFirstChild();
    final RenderNode line2 = line1.getNext();

    assertEquals( 0, line1.getX() );
    assertEquals( 0, line1.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 504 ), line1.getWidth() );
    assertEquals( 0, line1.getHeight() );

    assertEquals( 0, line2.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 3 ), line2.getY() );
    assertEquals( StrictGeomUtility.toInternalValue( 504 ), line2.getWidth() );
    assertEquals( 0, line2.getHeight() );

    DebugReportRunner.createXmlFlow( report );

  }


  public void testGoldRun8() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "OrderDetailReport.xml" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL, null );
    report.getReportHeader().getElement( 3 ).setName( "image" );

    final LogicalPageBox pageBox = DebugReportRunner.layoutPage( report, 0 );
    //ModelPrinter.INSTANCE.print(pageBox);
    final RenderNode img = MatchFactory.findElementByName( pageBox, "image" );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), img.getX() );
    assertEquals( StrictGeomUtility.toInternalValue( 270 ), img.getWidth() );
  }

  public strictfp void testGoldRun9() throws ResourceException, ReportProcessingException, ContentProcessingException {
    final File file = GoldTestBase.locateGoldenSampleReport( "prd-2884-2.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );

    final LogicalPageBox pageBox = DebugReportRunner.layoutSingleBand
      ( report, report.getRelationalGroup( 0 ).getHeader(), false, false );
    // ModelPrinter.INSTANCE.print(pageBox);

    final RenderNode[] activeDaysFields = MatchFactory.findElementsByName( pageBox, "ActiveDays" );
    final RenderBox activeDaysField = (RenderBox) activeDaysFields[ 0 ];
    final long borderTop = activeDaysField.getStaticBoxLayoutProperties().getBorderTop();
    assertEquals( 800, borderTop );


  }

}

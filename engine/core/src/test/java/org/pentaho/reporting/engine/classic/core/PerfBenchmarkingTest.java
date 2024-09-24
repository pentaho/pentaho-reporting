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
* Copyright (c) 2002 - 2024 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core;

import com.github.javatlacati.contiperf.PerfTest;
import com.github.javatlacati.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.states.CascadingDataFactory;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.util.ArrayList;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings( "HardCodedStringLiteral" )
public class PerfBenchmarkingTest {
  @Rule
  public ContiPerfRule i = new ContiPerfRule();

  private boolean isExecutePerformanceTest;

  /**
   * The total number of invocations to perform
   */
  final public static int MAX_INVOCATIONS = 100;


  /**
   * The number of milliseconds to run and repeat the test with the full number of configured threads. When using a
   * rampUp(), the ramp-up times add to the duration.
   */
  final public static int MAX_DURATION = 10000;

  /**
   * The number of threads which concurrently invoke the test.
   */
  final public static int MAX_THREADS = 1;

  /**
   * The number of milliseconds to wait before each thread is added to the currently active threads.
   */
  final public static int MAX_RAMPUP = 0;

  /**
   * The number of milliseconds to wait before the actual measurement and requirements monitoring is activated. Use this
   * to exclude ramp-up times from measurement or wait some minutes before dynamic optimizations are applied (like code
   * optimization or cache population).
   */
  final public static int MAX_WARMUP = 1000;


  public PerfBenchmarkingTest() {
    isExecutePerformanceTest = ( "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.engine.classic.test.ExecutePerformanceTest" ) ) );
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private TableModel createFruitTableModel() {
    final String[] names = new String[] { "Id Number", "Cat", "Fruit" };
    final Object[][] data = new Object[][] {
      { "I1", "A", "Apple" },
      { "I2", "A", "Orange" },
      { "I2", "A", "Orange" },
      { "I2", "A", "Orange" },
      { "I2", "A", "Orange" },
      { "I2", "A", "Orange" },
      { "I2", "A", "Orange" },
      { "I2", "A", "Orange" },
      { "I2", "A", "Orange" },
      { "I3", "B", "Water melon" },
      { "I3", "B", "Water melon" },
      { "I3", "B", "Water melon" },
      { "I3", "B", "Water melon" },
      { "I3", "B", "Water melon" },
      { "I3", "B", "Water melon" },
      { "I3", "B", "Water melon" },
      { "I3", "B", "Water melon" },
      { "I4", "B", "Strawberry" },
    };
    return new DefaultTableModel( data, names );
  }

  public Element createLabelElement( final String label, final Rectangle2D bounds ) {
    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setName( "LabelElement-" + label );
    labelFactory.setText( label );
    labelFactory.setFontName( "Serif" );
    labelFactory.setFontSize( new Integer( 10 ) );
    labelFactory.setBold( Boolean.FALSE );
    labelFactory.setHeight( new Float( bounds.getHeight() ) );
    labelFactory.setWidth( new Float( bounds.getWidth() ) );
    labelFactory.setWrap( TextWrap.WRAP );
    labelFactory.setAbsolutePosition( new Point2D.Double( bounds.getX(), bounds.getY() ) );
    labelFactory.setHorizontalAlignment( ElementAlignment.LEFT );
    labelFactory.setVerticalAlignment( ElementAlignment.TOP );
    final Element labelElement = labelFactory.createElement();

    return labelElement;
  }

  private SubReport createSubReportWithDefaultDatasource( final AbstractRootLevelBand band, final String name ) {
    final SubReport subReport = new SubReport();

    final TableDataFactory tableDataFactory = new TableDataFactory();
    tableDataFactory.addTable( "sub-fruit", createFruitTableModel() );
    subReport.setQuery( "Subreport Query Fruit" );
    subReport.setDataFactory( tableDataFactory );
    subReport.setName( name );

    band.addSubReport( subReport );

    return subReport;
  }

  private ArrayList<Element> buildLabelElementList( final Band band, final int numRows, final int numElement,
                                                    final int height, final int width ) {
    final ArrayList<Element> elementList = new ArrayList<Element>();
    int currentX = 0;
    int currentY = 0;

    for ( int row = 0; row < numRows; row++ ) {
      for ( int elemNum = 0; elemNum < numElement; elemNum++ ) {
        final Rectangle coordinates = new Rectangle( currentX, currentY, width, height );
        final String labelText = "Label-" + currentX + currentY;
        final Element label = createLabelElement( labelText, coordinates );

        currentX += width;

        band.addElement( label );
        elementList.add( label );
      }

      currentY += height;
    }

    return elementList;
  }


  @PerfTest( duration = PerfBenchmarkingTest.MAX_DURATION,
    threads = PerfBenchmarkingTest.MAX_THREADS,
    rampUp = PerfBenchmarkingTest.MAX_RAMPUP,
    warmUp = PerfBenchmarkingTest.MAX_WARMUP )
  //  @Required(max = 130000, average = 15000)
  @Test
  public void perfSubReportsWithManyLabelElements() throws Exception {
    if ( !isExecutePerformanceTest ) {
      return;
    }

    // Create a master report with a default datasource query
    final MasterReport master = new MasterReport();
    final TableDataFactory tableDataFactory = new TableDataFactory();
    tableDataFactory.addTable( "fruit", createFruitTableModel() );
    master.setQuery( "Query Fruit" );
    master.setDataFactory( tableDataFactory );

    // Create  a bunch of label elements in the master report's page header
    final ArrayList<Element> labelList = buildLabelElementList( master.getPageHeader(), 5, 5, 25, 25 );
    assertEquals( labelList.size(), 25 );

    // Create several subreport's in the master's report header
    final SubReport subReport = createSubReportWithDefaultDatasource( master.getReportHeader(), "subReport-1" );
    final SubReport subReport2 = createSubReportWithDefaultDatasource( master.getReportHeader(), "subReport-2" );
    final SubReport subReport3 = createSubReportWithDefaultDatasource( master.getReportHeader(), "subReport-3" );

    // Create a bunch of label elements inside the first subreport
    final ArrayList<Element> subreportLabelList = buildLabelElementList( subReport.getPageHeader(), 6, 6, 25, 25 );
    assertEquals( subreportLabelList.size(), 36 );


    // Layout the master report's page header
    final LogicalPageBox pageBox = DebugReportRunner.layoutSingleBand( master, master.getPageHeader() );

    // Search for the master reports first element
    final RenderBox labelElement = (RenderBox) MatchFactory.findElementByName( pageBox, "LabelElement-Label-00" );
    assertNotNull( labelElement );
    assertEquals( StrictGeomUtility.toInternalValue( 25 ), labelElement.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 0 ), labelElement.getY() );

    // Search for the master reports last element
    final RenderBox lastLabelElement =
      (RenderBox) MatchFactory.findElementByName( pageBox, "LabelElement-Label-600100" );
    assertNotNull( lastLabelElement );
    assertEquals( StrictGeomUtility.toInternalValue( 25 ), lastLabelElement.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 100 ), lastLabelElement.getY() );

    // Search for the master reports for an invalid element
    final RenderBox invalidLabelElement =
      (RenderBox) MatchFactory.findElementByName( pageBox, "LabelElement-Label-XXXXXXX" );
    assertNull( invalidLabelElement );

    // Retrieve all the sub-reports from master's report header and validate.
    final SubReport[] subReports = master.getReportHeader().getSubReports();
    assertTrue( subReports.length == 3 );
    assertSame( subReport, subReports[ 0 ] );
    assertEquals( subReports[ 0 ].getPageHeader().getElementCount(), 36 );

    assertSame( subReport2, subReports[ 1 ] );
    assertEquals( subReport2.getName(), subReports[ 1 ].getName() );
    assertEquals( subReports[ 1 ].getPageHeader().getElementCount(), 0 );

    assertSame( subReport3, subReports[ 2 ] );
    assertEquals( subReport3.getName(), subReports[ 2 ].getName() );
    assertEquals( subReports[ 2 ].getPageHeader().getElementCount(), 0 );

    DebugReportRunner.executeAll( master );
  }

  @PerfTest( duration = PerfBenchmarkingTest.MAX_DURATION,
    threads = PerfBenchmarkingTest.MAX_THREADS,
    rampUp = PerfBenchmarkingTest.MAX_RAMPUP,
    warmUp = PerfBenchmarkingTest.MAX_WARMUP )
  //  @Required(max = 45000, average = 55000)
  @Test
  public void perfMultipleEmbeddedSubReports() throws Exception {
    if ( !isExecutePerformanceTest ) {
      return;
    }

    final SubReport sr = new SubReport();
    sr.getReportHeader().addSubReport( new SubReport() );
    sr.getReportHeader().addSubReport( new SubReport() );

    final MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport( sr );
    report.getReportHeader().addSubReport( new SubReport() );
    report.getReportHeader().addSubReport( new SubReport() );

    DebugReportRunner.executeAll( report );
  }

  @PerfTest( duration = PerfBenchmarkingTest.MAX_DURATION,
    threads = PerfBenchmarkingTest.MAX_THREADS,
    rampUp = PerfBenchmarkingTest.MAX_RAMPUP,
    warmUp = PerfBenchmarkingTest.MAX_WARMUP )
  @Test
  public void perfCascadingBandedProperties() {
    if ( !isExecutePerformanceTest ) {
      return;
    }

    // Create several elements in master report in page header
    final MasterReport master = new MasterReport();
    master.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );
    final ReportHeader header = master.getReportHeader();

    header.setName( "Property-Header" );
    header.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    header.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.BOTTOM );
    header.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 500 ) );
    header.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 100 ) );
    header.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, new Float( 2 ) );
    header.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, new Float( 2 ) );
    header.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, BorderStyle.SOLID );
    header.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, BorderStyle.SOLID );

    // Create  a bunch of label elements in the master report's report header
    final ArrayList<Element> labelList = buildLabelElementList( header, 5, 5, 25, 25 );
    assertEquals( labelList.size(), 25 );

    // Create two levels of sub-reports with elements in each page header.
    final SubReport subReport = createSubReportWithDefaultDatasource( header, "subReport" );
    subReport.getReportHeader().getStyle().addInherited( header.getStyle() );
    buildLabelElementList( subReport.getReportHeader(), 5, 5, 25, 25 );

    final SubReport subReport2 =
      createSubReportWithDefaultDatasource( subReport.getReportHeader(), "subReport-embedded" );
    subReport2.getReportHeader().getStyle().addInherited( header.getStyle() );
    buildLabelElementList( subReport2.getReportHeader(), 5, 5, 25, 25 );
    assertEquals( subReport2.getReportHeader().getElementCount(), 25 );

    Element labelElement = subReport2.getReportHeader().getElement( 0 );
    assertEquals( labelElement.getParent().getStyle().getStyleProperty( ElementStyleKeys.VALIGNMENT ),
      ElementAlignment.BOTTOM );

    DebugReportRunner.resolveStyle( subReport2.getReportHeader() );
  }

  @PerfTest( duration = PerfBenchmarkingTest.MAX_DURATION,
    threads = PerfBenchmarkingTest.MAX_THREADS,
    rampUp = PerfBenchmarkingTest.MAX_RAMPUP,
    warmUp = PerfBenchmarkingTest.MAX_WARMUP )
  @Test
  public void perfDataSource() throws Exception {
    if ( !isExecutePerformanceTest ) {
      return;
    }

    final MasterReport master = new MasterReport();
    final TableDataFactory tableDataFactory = new TableDataFactory();
    tableDataFactory.addTable( "fruit", createFruitTableModel() );
    master.setQuery( "Query Fruit" );
    master.setDataFactory( tableDataFactory );

    final CompoundDataFactory cdf = new CompoundDataFactory();
    cdf.add( tableDataFactory );
    master.setDataFactory( cdf );

    final CachingDataFactory caDf = new CachingDataFactory( cdf, true );
    master.setDataFactory( caDf );

    final CompoundDataFactory ccdf = new CascadingDataFactory();
    ccdf.add( caDf );
    ccdf.add( tableDataFactory );
    master.setDataFactory( ccdf );

    //    assertTrue(ccdf.isQueryExecutable("Query Fruit", new StaticDataRow()));
  }

  @PerfTest( duration = PerfBenchmarkingTest.MAX_DURATION,
    threads = PerfBenchmarkingTest.MAX_THREADS,
    rampUp = PerfBenchmarkingTest.MAX_RAMPUP,
    warmUp = PerfBenchmarkingTest.MAX_WARMUP )
  @Test
  public void perfClassicBootStart() {
    if ( !isExecutePerformanceTest ) {
      return;
    }

    // Mock up isBootDone to return true to ensure we reload libs every time
    ClassicEngineBoot mock = mock( ClassicEngineBoot.class );
    when( mock.isBootDone() ).thenReturn( true );

    mock.start();
  }
}

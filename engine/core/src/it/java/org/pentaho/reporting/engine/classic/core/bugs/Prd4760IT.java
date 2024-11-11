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
import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.table.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public class Prd4760IT extends TestCase {
  public Prd4760IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport() throws ReportProcessingException, ContentProcessingException {
    Band b = new Band();
    b.setLayout( BandStyleKeys.LAYOUT_ROW );
    b.addElement( TableTestUtil.createDataItem( "Test" ) );
    b.setVisible( false );

    MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    ReportHeader reportHeader = report.getReportHeader();
    reportHeader.setLayout( BandStyleKeys.LAYOUT_ROW );
    reportHeader.addElement( b );
    reportHeader.getStyle().setStyleProperty( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, false );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, reportHeader );

    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_NODE_TEXT );
    Assert.assertEquals( 0, elementsByNodeType.length );
  }

  public void testReportDefaults() throws ReportProcessingException, ContentProcessingException {
    Band b = new Band();
    b.setLayout( BandStyleKeys.LAYOUT_ROW );
    b.addElement( TableTestUtil.createDataItem( "Test" ) );
    b.setVisible( false );

    MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    ReportHeader reportHeader = report.getReportHeader();
    reportHeader.setLayout( BandStyleKeys.LAYOUT_ROW );
    reportHeader.addElement( b );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, reportHeader );
    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_NODE_TEXT );
    Assert.assertEquals( 1, elementsByNodeType.length );
  }

  public void testSimpleReport() throws ReportProcessingException, ContentProcessingException {
    MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    ReportHeader reportHeader = report.getReportHeader();
    reportHeader.setLayout( BandStyleKeys.LAYOUT_ROW );
    reportHeader.addElement( TableTestUtil.createDataItem( "Test" ) );
    reportHeader.getStyle().setStyleProperty( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, false );
    reportHeader.setVisible( false );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, reportHeader );
    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_NODE_TEXT );
    Assert.assertEquals( 0, elementsByNodeType.length );
  }

  public void testSimpleReport2() throws ReportProcessingException, ContentProcessingException {
    Element test = TableTestUtil.createDataItem( "Test" );
    test.setVisible( false );

    MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    ReportHeader reportHeader = report.getReportHeader();
    reportHeader.setLayout( BandStyleKeys.LAYOUT_ROW );
    reportHeader.addElement( test );
    reportHeader.getStyle().setStyleProperty( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, false );
    reportHeader.setVisible( false );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, reportHeader );
    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_NODE_TEXT );
    Assert.assertEquals( 0, elementsByNodeType.length );
  }

  public void testGoldenSample() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4760.prpt" );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_PARAGRAPH );
    Assert.assertEquals( 4, elementsByNodeType.length );
    for ( RenderNode renderNode : elementsByNodeType ) {
      if ( renderNode.getX() != 0 && renderNode.getX() != StrictGeomUtility.toInternalValue( 100 ) ) {
        Assert.fail();
      }
    }
  }

  public void testGoldenSampleComplex() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4760.prpt" );
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_PARAGRAPH );
    Assert.assertEquals( 4, elementsByNodeType.length );
    for ( RenderNode renderNode : elementsByNodeType ) {
      if ( renderNode.getX() != 0 && renderNode.getX() != StrictGeomUtility.toInternalValue( 100 ) ) {
        Assert.fail();
      }
    }
  }
}

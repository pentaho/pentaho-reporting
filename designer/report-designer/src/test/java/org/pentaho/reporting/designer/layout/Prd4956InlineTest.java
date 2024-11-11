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


package org.pentaho.reporting.designer.layout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.testsupport.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;

import java.awt.geom.Rectangle2D;

import javax.naming.spi.NamingManager;

public class Prd4956InlineTest {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  private MasterReport createReport() {
    SubReport sr = new SubReport();
    sr.getPageHeader().addElement( create( "SR-Page-Header" ) );
    sr.getPageFooter().addElement( create( "SR-Page-Footer" ) );
    sr.getReportHeader().addElement( create( "SR-Report-Header" ) );

    MasterReport report = new MasterReport();
    report.getReportHeader().addElement( sr );
    report.getPageHeader().addElement( create( "MR-Page-Header" ) );
    report.getPageFooter().addElement( create( "MR-Page-Footer" ) );
    return report;
  }

  private Element create( String name ) {
    Element dataItemSR = TableTestUtil.createDataItem( name, 100, 20 );
    dataItemSR.setName( name );
    return dataItemSR;
  }

  @Test
  public void testBandedPageHeader() {

    final MasterReport report = createReport();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext masterContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final SubReport subReport = (SubReport) report.getReportHeader().getElement( 0 );
    final ReportRenderContext subContext =
      new ReportRenderContext( report, subReport, masterContext, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( subReport.getPageHeader(), subContext );
    final Rectangle2D bounds = r.getBounds();

    Assert.assertEquals( new Rectangle2D.Double( 0, 20, 468, 72 ), bounds );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    graphics2D.expect( "SR-Page-Header" );
    r.draw( graphics2D );
    Assert.assertFalse( graphics2D.isValid() );
  }

  @Test
  public void testBandedPageFooter() {

    final MasterReport report = createReport();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext masterContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final SubReport subReport = (SubReport) report.getReportHeader().getElement( 0 );
    final ReportRenderContext subContext =
      new ReportRenderContext( report, subReport, masterContext, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( subReport.getPageFooter(), subContext );
    final Rectangle2D bounds = r.getBounds();

    Assert.assertEquals( new Rectangle2D.Double( 0, 20, 468, 72 ), bounds );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    graphics2D.expect( "SR-Page-Footer" );
    r.draw( graphics2D );
    Assert.assertFalse( graphics2D.isValid() );
  }

  @Test
  public void testBandedReportHeader() {

    final MasterReport report = createReport();

    final GlobalAuthenticationStore globalAuthenticationStore = new GlobalAuthenticationStore();
    final ReportRenderContext masterContext =
      new ReportRenderContext( report, report, null, globalAuthenticationStore );
    final SubReport subReport = (SubReport) report.getReportHeader().getElement( 0 );
    final ReportRenderContext subContext =
      new ReportRenderContext( report, subReport, masterContext, globalAuthenticationStore );
    final TestRootBandRenderer r = new TestRootBandRenderer( subReport.getReportHeader(), subContext );
    final Rectangle2D bounds = r.getBounds();

    //ModelPrinter.INSTANCE.print(r.getLogicalPageDrawable().getLogicalPageBox());
    Assert.assertEquals( new Rectangle2D.Double( 0, 20, 468, 108 ), bounds );

    final ValidateTextGraphics graphics2D = new ValidateTextGraphics( 468, 108 );
    graphics2D.expect( "SR-Report-Header" );
    r.draw( graphics2D );
    Assert.assertTrue( graphics2D.isValid() );

  }
}

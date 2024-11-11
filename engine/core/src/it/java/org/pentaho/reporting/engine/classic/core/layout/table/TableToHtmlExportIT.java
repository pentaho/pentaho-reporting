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


package org.pentaho.reporting.engine.classic.core.layout.table;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.net.URL;

public class TableToHtmlExportIT extends TestCase {
  public TableToHtmlExportIT() {
  }

  public TableToHtmlExportIT( final String name ) {
    super( name );
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testHtmlExportFull() throws Exception {
    final URL url = getClass().getResource( "Prd-3931.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel( null );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    final Group rootGroup = report.getRootGroup();
    assertTrue( rootGroup instanceof CrosstabGroup );

    final CrosstabGroup ct = (CrosstabGroup) rootGroup;
    ct.setPrintColumnTitleHeader( true );
    ct.setPrintDetailsHeader( false );

    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    HtmlReportUtil.createStreamHTML( report, outputStream );

    final String htmlText = new String( outputStream.toByteArray(), "UTF-8" );
    DebugLog.log( htmlText );
    assertTrue( htmlText.contains( "<td colspan=\"2\" valign=\"top\" class=\"style-1\">2003</td>" ) );
    assertTrue( htmlText.contains( "<td colspan=\"2\" valign=\"top\" class=\"style-1\">2004</td>" ) );
    assertTrue( htmlText.contains( "<td colspan=\"2\" valign=\"top\" class=\"style-1\">2005</td>" ) );
    assertTrue( htmlText.contains( "<td valign=\"top\" class=\"style-3\">Product Line</td>" ) );
    assertTrue( htmlText.contains( "<td valign=\"top\" class=\"style-3\">Market</td>" ) );
  }

  public void testHtmlExportFullComplexText() throws Exception {
    final URL url = getClass().getResource( "Prd-3931.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel( null );
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );

    final Group rootGroup = report.getRootGroup();
    assertTrue( rootGroup instanceof CrosstabGroup );

    final CrosstabGroup ct = (CrosstabGroup) rootGroup;
    ct.setPrintColumnTitleHeader( true );
    ct.setPrintDetailsHeader( false );

    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    HtmlReportUtil.createStreamHTML( report, outputStream );

    final String htmlText = new String( outputStream.toByteArray(), "UTF-8" );
    DebugLog.log( htmlText );
    assertTrue( htmlText.contains( "<td colspan=\"2\" valign=\"top\" class=\"style-1\">2003</td>" ) );
    assertTrue( htmlText.contains( "<td colspan=\"2\" valign=\"top\" class=\"style-1\">2004</td>" ) );
    assertTrue( htmlText.contains( "<td colspan=\"2\" valign=\"top\" class=\"style-1\">2005</td>" ) );
    assertTrue( htmlText.contains( "<td valign=\"top\" class=\"style-3\">Product Line</td>" ) );
    assertTrue( htmlText.contains( "<td valign=\"top\" class=\"style-3\">Market</td>" ) );
  }

  public void testExportToXml() throws Exception {
    final URL url = getClass().getResource( "Prd-3931.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 4, 0, 0 ) );

    final LogicalPageBox pageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.INSTANCE.print(pageBox);
  }

  public void testHtmlExport() throws ReportProcessingException, IOException, ResourceException {
    final Band tableHeader = createBodyBox( "header" );
    tableHeader.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_HEADER );

    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    table.addElement( tableHeader );
    table.addElement( createBodyBox( "body" ) );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.addElement( table );

    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    HtmlReportUtil.createStreamHTML( report, outputStream );

    final String htmlText = new String( outputStream.toByteArray(), "UTF-8" );
    assertTrue( htmlText.contains( "<td valign=\"top\" class=\"style-1\">header</td>" ) );
    assertTrue( htmlText.contains( "<td valign=\"top\" class=\"style-1\">body</td>" ) );
  }

  private Band createBodyBox( final String text ) {
    final Element label = TableTestUtil.createDataItem( text );

    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.addElement( label );

    final Band tableRow = TableTestUtil.createRow();
    tableRow.addElement( tableCell );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.addElement( tableRow );
    return tableBody;
  }
}

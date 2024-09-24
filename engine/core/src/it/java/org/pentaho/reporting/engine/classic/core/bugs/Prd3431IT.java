/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
 * All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableReportUtil;
import org.pentaho.reporting.engine.classic.core.util.NoCloseOutputStream;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

public class Prd3431IT extends TestCase {
  public Prd3431IT() {
  }

  public Prd3431IT( final String name ) {
    super( name );
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testAsXmlOutput() throws ResourceException, ReportProcessingException, IOException, SAXException,
    ParserConfigurationException {
    final URL url = getClass().getResource( "Prd-3431.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    final MemoryByteArrayOutputStream mbos = new MemoryByteArrayOutputStream();
    XmlTableReportUtil.createFlowXML( report, new NoCloseOutputStream( mbos ) );

    final ByteArrayInputStream bin = new ByteArrayInputStream( mbos.getRaw(), 0, mbos.getLength() );
    final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    final Document document = documentBuilder.parse( bin );
    final NodeList table = document.getDocumentElement().getElementsByTagName( "table" );
    assertSheetName( (Element) table.item( 0 ), "Summary" );
    assertSheetName( (Element) table.item( 1 ), "AuthorPublisher A" );
    assertSheetName( (Element) table.item( 2 ), "AuthorPublisher B" );
    assertSheetName( (Element) table.item( 3 ), "AuthorPublisher C" );
  }

  public void testAsExcelOutput() throws ResourceException, ReportProcessingException, IOException, SAXException,
    ParserConfigurationException, InvalidFormatException {
    final URL url = getClass().getResource( "Prd-3431.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    final MemoryByteArrayOutputStream mbos = new MemoryByteArrayOutputStream();
    ExcelReportUtil.createXLS( report, new NoCloseOutputStream( mbos ) );

    final ByteArrayInputStream bin = new ByteArrayInputStream( mbos.getRaw(), 0, mbos.getLength() );
    final Workbook workbook = WorkbookFactory.create( bin );
    assertEquals( 4, workbook.getNumberOfSheets() );
    assertEquals( "Summary", workbook.getSheetAt( 0 ).getSheetName() );
    assertEquals( "AuthorPublisher A", workbook.getSheetAt( 1 ).getSheetName() );
    assertEquals( "AuthorPublisher B", workbook.getSheetAt( 2 ).getSheetName() );
    assertEquals( "AuthorPublisher C", workbook.getSheetAt( 3 ).getSheetName() );
  }

  private void assertSheetName( final Element n, final String sheetName ) {
    assertEquals( sheetName, n.getAttribute( "sheet-name" ) );
  }
}

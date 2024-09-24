/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.filter.types.RotatableText;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PlainTextReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlTableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.RotatedTextDrawable;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RotationTest {

  @Before
  public void setUp() throws IOException {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testXLS() throws ResourceException, IOException {

    URL url = getClass().getResource( "BACKLOG-6818.prpt" );
    final File testOutputFile = File.createTempFile( "test", ".xls" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();

    try ( FileOutputStream stream = new FileOutputStream( testOutputFile ) ) {
      ExcelReportUtil.createXLS( report, stream );
      HSSFWorkbook workbook = new HSSFWorkbook( new FileInputStream( testOutputFile ) );
      assertNotNull( workbook );
      final HSSFSheet sheet = workbook.getSheetAt( 0 );
      assertNotNull( sheet );
      final Iterator<Row> rowIterator = sheet.rowIterator();
      assertNotNull( rowIterator );
      final Row next = rowIterator.next();
      assertNotNull( next );
      int k = 1;
      for ( int i = 0; i < 5; i++, k = -k ) {
        final Cell cell = next.getCell( i );
        assertNotNull( cell );
        assertTrue( cell.getCellStyle().getRotation() == k * 90 );
      }
      for ( int i = 6; i < 9; i++, k = -k ) {
        final Cell cell = next.getCell( i );
        assertNull( cell );
      }
    } catch ( IOException | ReportProcessingException e ) {
      fail();
    } finally {
      assertTrue( testOutputFile.delete() );
    }
  }

  @Test
  public void testXLSX() throws ResourceException, IOException {

    URL url = getClass().getResource( "BACKLOG-6818.prpt" );
    final File testOutputFile = File.createTempFile( "test", ".xlsx" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();

    try ( FileOutputStream stream = new FileOutputStream( testOutputFile ) ) {
      ExcelReportUtil.createXLSX( report, stream );
      XSSFWorkbook workbook = new XSSFWorkbook( new FileInputStream( testOutputFile ) );
      assertNotNull( workbook );
      final XSSFSheet sheet = workbook.getSheetAt( 0 );
      assertNotNull( sheet );
      final Iterator<Row> rowIterator = sheet.rowIterator();
      assertNotNull( rowIterator );
      final Row next = rowIterator.next();
      assertNotNull( next );
      int k = 1;
      for ( int i = 0; i < 5; i++, k = -k ) {
        final Cell cell = next.getCell( i );
        assertNotNull( cell );
        assertTrue( cell.getCellStyle().getRotation() == ( k > 0 ? 90 : 180 ) );
      }
      for ( int i = 6; i < 9; i++, k = -k ) {
        final Cell cell = next.getCell( i );
        assertNull( cell );
      }
    } catch ( IOException | ReportProcessingException e ) {
      fail();
    } finally {
      assertTrue( testOutputFile.delete() );
    }
  }


  @Test
  public void testHTML() throws ResourceException, IOException {

    URL url = getClass().getResource( "BACKLOG-6818.prpt" );

    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();

    try ( ByteArrayOutputStream stream = new ByteArrayOutputStream() ) {
      HtmlReportUtil.createStreamHTML( report, stream );

      final String html = new String( stream.toByteArray(), "UTF-8" );
      int k = 1;
      for ( int i = 1; i < 5; i++, k = -k ) {
        final Pattern pattern = Pattern.compile( "(.*id=\"test" + i + "\".*style=\")(.*)(\".*)" );
        final Matcher matcher = pattern.matcher( html );
        if ( matcher.find() ) {
          final String group = matcher.group( 2 );
          assertTrue( group.contains( k > 0 ? TextRotation.D_90.getCss() : TextRotation.D_270.getCss() ) );
        }
      }
    } catch ( final IOException | ReportProcessingException e ) {
      fail();
    }
  }

  @Test
  public void testGetInstance() {
    assertEquals( TextRotation.getInstance( (short) 90 ), TextRotation.D_90 );
    assertEquals( TextRotation.getInstance( (short) -90 ), TextRotation.D_270 );
  }

  @Test
  public void testCss() {
    assertEquals( TextRotation.D_90.getCss(),
      "transform: rotate(-90deg); -ms-transform: rotate(-90deg); -webkit-transform: rotate(-90deg); white-space: "
        + "nowrap; transform-origin: right bottom;" );
    assertEquals( TextRotation.D_270.getCss(),
      "transform: rotate(90deg); -ms-transform: rotate(90deg); -webkit-transform: rotate(90deg); white-space: nowrap;"
        + " transform-origin: left bottom;" );
  }

  @Test
  public void testTxt() throws ResourceException {
    URL url = getClass().getResource( "BACKLOG-6818.prpt" );

    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();

    try ( ByteArrayOutputStream stream = new ByteArrayOutputStream() ) {
      PlainTextReportUtil.createPlainText( report, stream );
      final byte[] bytes = stream.toByteArray();
      assertNotNull( bytes );
      assertTrue( bytes.length > 0 );
      assertTrue( StringUtils.isNotBlank( new String( bytes, "UTF-8" ) ) );
    } catch ( final IOException | ReportProcessingException e ) {
      fail();
    }
  }

  @Test
  public void testCSV() throws ResourceException {
    URL url = getClass().getResource( "BACKLOG-6818.prpt" );

    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();

    try ( ByteArrayOutputStream stream = new ByteArrayOutputStream() ) {
      CSVReportUtil.createCSV( report, stream, "UTF-8" );
      final byte[] bytes = stream.toByteArray();
      assertNotNull( bytes );
      assertTrue( bytes.length > 0 );
      assertTrue( StringUtils.isNotBlank( new String( bytes, "UTF-8" ).replaceAll( ",", "" ) ) );
    } catch ( final IOException | ReportProcessingException e ) {
      fail();
    }
  }


  @Test
  public void testRotationSupport() {
    assertFalse( RotatableText.isRotationSupported( null ) );
    final ExpressionRuntime runtime = mock( ExpressionRuntime.class );
    assertFalse( RotatableText.isRotationSupported( runtime ) );
    final ProcessingContext processingContext = mock( ProcessingContext.class );
    when( runtime.getProcessingContext() ).thenReturn( processingContext );
    assertFalse( RotatableText.isRotationSupported( runtime ) );
    final OutputProcessorMetaData metaData = mock( OutputProcessorMetaData.class );
    when( processingContext.getOutputProcessorMetaData() ).thenReturn( metaData );
    when( metaData.isFeatureSupported( eq( OutputProcessorFeature.IGNORE_ROTATION ) ) ).thenReturn( true );
    assertFalse( RotatableText.isRotationSupported( runtime ) );
    when( metaData.isFeatureSupported( eq( OutputProcessorFeature.IGNORE_ROTATION ) ) ).thenReturn( false );
    assertTrue( RotatableText.isRotationSupported( runtime ) );
    RotatableText rt = new RotatableText() {
    };
    assertNull( rt.rotate( null, null, null ) );
    final UUID uuid = UUID.randomUUID();
    assertEquals( uuid, rt.rotate( null, uuid, null ) );
    final ReportElement reportElement = mock( ReportElement.class );
    assertEquals( uuid, rt.rotate( reportElement, uuid, null ) );
    assertEquals( uuid, rt.rotate( reportElement, uuid, runtime ) );
    final ElementStyleSheet sheet = mock( ElementStyleSheet.class );
    when( reportElement.getStyle() ).thenReturn( sheet );
    assertEquals( uuid, rt.rotate( reportElement, uuid, runtime ) );
    when( sheet.getStyleProperty( eq( TextStyleKeys.TEXT_ROTATION ), isNull() ) ).thenReturn( "blabla" );
    assertEquals( uuid, rt.rotate( reportElement, uuid, runtime ) );
    when( sheet.getStyleProperty( eq( TextStyleKeys.TEXT_ROTATION ), isNull() ) ).thenReturn( TextRotation.D_90 );
    assertTrue( rt.rotate( reportElement, uuid, runtime ) instanceof RotatedTextDrawable );
  }

  @Test
  public void testHandleRotatedTextHTML() throws Exception {
    URL url = getClass().getResource( "BACKLOG-10064.prpt" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();
    report.getReportConfiguration().setConfigProperty( HtmlTableModule.INLINE_STYLE, "true" );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.STRICT_ERROR_HANDLING_KEY, "false" );

    List<String> elementsIdList = Arrays.asList("topLeft90","topCenter90","topRight90","topJustify90",
                                                "topLeft-90","topCenter-90","topRight-90","topJustify-90",
                                                "middleLeft90","middleCenter90","middleRight90","middleJustify90",
                                                "middleLeft-90","middleCenter-90","middleRight-90","middleJustify-90",
                                                "bottomLeft90","bottomCenter90","bottomRight90","bottomJustify90",
                                                "bottomLeft-90","bottomCenter-90","bottomRight-90","bottomJustify-90");

    XPathFactory xpathFactory = XPathFactory.newInstance();
    try ( ByteArrayOutputStream stream = new ByteArrayOutputStream() ) {
      HtmlReportUtil.createStreamHTML( report, stream );
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware( true );
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse( new ByteArrayInputStream( stream.toByteArray() ) );

      for ( String elementId : elementsIdList ) {
        org.w3c.dom.Element element = document.getElementById( elementId );
        Node rotatedTextStyle = element.getFirstChild().getAttributes().getNamedItem( "style" );
        Node trSryle = element.getParentNode().getParentNode().getAttributes().getNamedItem( "style" );
        assertTrue( isStyleValid( rotatedTextStyle.getNodeValue(), trSryle.getNodeValue(), elementId.contains( "middle" ) ) );
      }
    } catch ( final IOException | ReportProcessingException e ) {
      fail();
    }

  }

  private boolean isStyleValid( String rotatedTextStyle, String trSryle, boolean isMiddleAlign ) {
    String rotatedTextHeight;
    if ( isMiddleAlign ) {
      rotatedTextHeight = rotatedTextStyle.substring( rotatedTextStyle.indexOf( "height: " ) + 8, rotatedTextStyle.indexOf( "pt" ) );
    } else {
      rotatedTextHeight = rotatedTextStyle.substring( rotatedTextStyle.indexOf( "width: " ) + 7, rotatedTextStyle.indexOf( "pt" ) );
    }
    String trHeight = trSryle.substring( trSryle.indexOf( "height: " ) + 8, trSryle.indexOf( "pt" ) );

    return rotatedTextHeight.equals( trHeight );
  }

}

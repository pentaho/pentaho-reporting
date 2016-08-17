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
 *  Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

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
      "transform: rotate(-90deg); -ms-transform: rotate(-90deg); -webkit-transform: rotate(-90deg);" );
    assertEquals( TextRotation.D_270.getCss(),
      "transform: rotate(90deg); -ms-transform: rotate(90deg); -webkit-transform: rotate(90deg);" );
  }


}

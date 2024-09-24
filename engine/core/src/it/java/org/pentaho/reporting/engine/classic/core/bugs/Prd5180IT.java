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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.csv.FastCsvReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.html.FastHtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import javax.swing.table.TableModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Prd5180IT {
  public Prd5180IT() {
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testHtmlExport() throws ReportProcessingException, IOException {
    MasterReport report = createReport();

    ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    FastHtmlReportUtil.processStreamHtml( report, boutFast );
    HtmlReportUtil.createStreamHTML( report, boutSlow );
    String htmlFast = boutFast.toString( "UTF-8" );
    String htmlSlow = boutSlow.toString( "UTF-8" );
    Assert.assertEquals( htmlSlow, htmlFast );
  }

  @Test
  public void testCsvExport() throws ReportProcessingException, IOException {
    MasterReport report = createReport();

    ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    FastCsvReportUtil.process( report, boutFast );
    CSVReportUtil.createCSV( report, boutSlow, "UTF-8" );
    String htmlFast = boutFast.toString( "UTF-8" );
    String htmlSlow = boutSlow.toString( "UTF-8" );
    Assert.assertEquals( htmlSlow, htmlFast );
  }

  @Test
  public void testExcelExport() throws ReportProcessingException, IOException, InvalidFormatException {
    MasterReport report = createReport();

    ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    FastExcelReportUtil.processXls( report, boutFast );
    ExcelReportUtil.createXLS( report, boutSlow );
    // writeToFile("test-output/PRD-5180-fast.xls", boutFast);
    // writeToFile("test-output/PRD-5180-slow.xls", boutSlow);

    // the two streams are not directly comparable, so we have to manually compare contents
    validateExcelSheet( boutSlow, createData() );
    validateExcelSheet( boutFast, createData() );
  }

  private MasterReport createReport() {
    MasterReport report = new MasterReport();
    report.setQuery( "query" );
    report.setDataFactory( new TableDataFactory( report.getQuery(), createData() ) );

    ItemBand itemBand = report.getItemBand();
    itemBand.setLayout( BandStyleKeys.LAYOUT_ROW );
    itemBand.addElement( createField( "f1", 100, 20 ) );
    itemBand.addElement( createField( "f2", 100, 20 ) );
    itemBand.addElement( createField( "f3", 100, 20 ) );
    return report;
  }

  @Test
  public void testExcel2010Export() throws ReportProcessingException, IOException, InvalidFormatException {
    MasterReport report = createReport();

    ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    FastExcelReportUtil.processXlsx( report, boutFast );
    ExcelReportUtil.createXLSX( report, boutSlow );
    // writeToFile("test-output/PRD-5180-fast.xls", boutFast);
    // writeToFile("test-output/PRD-5180-slow.xls", boutSlow);

    // the two streams are not directly comparable, so we have to manually compare contents
    validateExcelSheet( boutSlow, createData() );
    validateExcelSheet( boutFast, createData() );
  }

  private void validateExcelSheet( final ByteArrayOutputStream boutSlow, final TableModel data ) throws IOException,
    InvalidFormatException {
    Workbook workbook = WorkbookFactory.create( new ByteArrayInputStream( boutSlow.toByteArray() ) );
    Sheet sheet = workbook.getSheetAt( 0 );
    Assert.assertEquals( 0, sheet.getFirstRowNum() );
    Assert.assertEquals( data.getRowCount() - 1, sheet.getLastRowNum() );

    for ( int r = 0; r < data.getRowCount(); r += 1 ) {
      Row row = sheet.getRow( r );
      for ( int c = 0; c < data.getColumnCount(); c += 1 ) {
        Cell cell = row.getCell( c );

        Object valueAt = data.getValueAt( r, c );
        if ( valueAt == null ) {
          if ( cell != null ) {
            // excel cells never return null
            Assert.assertEquals( "", cell.getStringCellValue() );
          }
        } else {
          Assert.assertEquals( valueAt, cell.getStringCellValue() );
        }
      }

    }

  }

  private void writeToFile( String file, ByteArrayOutputStream bout ) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream( file );
    try {
      fileOutputStream.write( bout.toByteArray() );
    } finally {
      fileOutputStream.close();
    }
  }

  private TableModel createData() {
    TypedTableModel model = new TypedTableModel();
    model.addColumn( "f1", String.class );
    model.addColumn( "f2", String.class );
    model.addColumn( "f3", String.class );

    model.addRow( null, null, null );
    model.addRow( "F1-0", null, null );
    model.addRow( "F1-1", "F2-1", null );
    model.addRow( null, "F2-2", "F3-2" );
    model.addRow( "F2-3", null, "F3-3" );
    return model;
  }

  public static Element createField( final String field, final float width, final float height ) {
    final Element label = new Element();
    label.setElementType( TextFieldType.INSTANCE );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, field );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, width );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, height );
    return label;
  }

}

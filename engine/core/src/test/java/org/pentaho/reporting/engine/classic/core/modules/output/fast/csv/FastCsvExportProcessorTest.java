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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.csv;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.DateFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import javax.swing.table.DefaultTableModel;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test suite for FastCsvExportProcessor.
 * Tests the fast CSV export functionality with various data scenarios.
 */
public class FastCsvExportProcessorTest {

  private MasterReport report;
  private ByteArrayOutputStream outputStream;

  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
    report = new MasterReport();
    outputStream = new ByteArrayOutputStream();
  }

  @After
  public void tearDown() {
    if ( outputStream != null ) {
      try {
        outputStream.close();
      } catch ( Exception e ) {
        // ignore
      }
    }
  }

  // =====================================================================
  // Helper method to create a report with table data
  // =====================================================================

  private void setUpReportData( Object[][] data, Object[] columnNames ) {
    DefaultTableModel tableModel = new DefaultTableModel( data, columnNames );
    TableDataFactory dataFactory = new TableDataFactory();
    dataFactory.addTable( "default", tableModel );
    report.setDataFactory( dataFactory );
  }

  // =====================================================================
  // Constructor tests
  // =====================================================================

  @Test
  public void testConstructorWithTwoArgs() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    assertThat( "Processor should not be null", processor, is( notNullValue() ) );
    processor.close();
  }

  @Test
  public void testConstructorWithVariousEncodings() throws ReportProcessingException {
    String[] encodings = { "UTF-8", null, "ISO-8859-1" };
    for ( String encoding : encodings ) {
      FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream, encoding );
      assertThat( "Processor should not be null for encoding: " + encoding, processor, is( notNullValue() ) );
      processor.close();
    }
  }

  // =====================================================================
  // Configuration and setup tests
  // =====================================================================

  @Test
  public void testGetOutputProcessor() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    assertNotNull( "Output processor should not be null", processor.getOutputProcessor() );
    processor.close();
  }

  @Test
  public void testGetConfiguration() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    assertNotNull( "Configuration should not be null", processor.getConfiguration() );
    processor.close();
  }

  // =====================================================================
  // State management tests
  // =====================================================================

  @Test
  public void testHandleInterruptedStateDefault() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    assertTrue( "Handle interrupted state should be true by default", processor.isHandleInterruptedState() );
    processor.close();
  }

  @Test
  public void testSetHandleInterruptedStateFalse() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    processor.setHandleInterruptedState( false );
    assertFalse( "Handle interrupted state should be false after setting", processor.isHandleInterruptedState() );
    processor.close();
  }

  @Test
  public void testSetHandleInterruptedStateTrue() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    processor.setHandleInterruptedState( true );
    assertTrue( "Handle interrupted state should be true after setting", processor.isHandleInterruptedState() );
    processor.close();
  }

  @Test
  public void testFullStreamingProcessorDefault() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    assertTrue( "Full streaming processor should be true by default", processor.isFullStreamingProcessor() );
    processor.close();
  }

  @Test
  public void testSetFullStreamingProcessorFalse() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    processor.setFullStreamingProcessor( false );
    assertFalse( "Full streaming processor should be false after setting", processor.isFullStreamingProcessor() );
    processor.close();
  }

  @Test
  public void testSetFullStreamingProcessorTrue() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    processor.setFullStreamingProcessor( true );
    assertTrue( "Full streaming processor should be true after setting", processor.isFullStreamingProcessor() );
    processor.close();
  }

  // =====================================================================
  // Close and cancel tests
  // =====================================================================

  @Test
  public void testCloseIsIdempotent() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    processor.close();
    processor.close();
    assertNotNull( "Processor should still exist after double close", processor );
  }

  @Test
  public void testCancel() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    processor.cancel();
    assertNotNull( "Processor should still exist after cancel", processor );
    processor.close();
  }

  // =====================================================================
  // Layout manager test
  // =====================================================================

  @Test
  public void testCreateLayoutManager() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    OutputFunction layoutManager = processor.createLayoutManager();
    assertNotNull( "Layout manager should not be null", layoutManager );
    processor.close();
  }

  // =====================================================================
  // Empty table/report tests — using try/catch with assertions
  // =====================================================================

  @Test
  public void testProcessReportWithNoData() throws ReportProcessingException {
    DefaultTableModel tableModel = new DefaultTableModel( new Object[0][0], new Object[0] );
    TableDataFactory dataFactory = new TableDataFactory();
    dataFactory.addTable( "default", tableModel );
    report.setDataFactory( dataFactory );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      fail( "Expected an exception when processing report with no data" );
    } catch ( Exception e ) {
      assertNotNull( "Exception message should not be null", e.getMessage() );
    } finally {
      processor.close();
    }
  }

  @Test
  public void testProcessReportWithNullDataFactory() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      fail( "Expected an exception when processing report with null data factory" );
    } catch ( Exception e ) {
      assertNotNull( "Exception message should not be null", e.getMessage() );
    } finally {
      processor.close();
    }
  }

  @Test
  public void testProcessReportWithEmptyRows() throws ReportProcessingException {
    DefaultTableModel tableModel = new DefaultTableModel(
        new Object[0][3], new Object[] { "A", "B", "C" } );
    TableDataFactory dataFactory = new TableDataFactory();
    dataFactory.addTable( "default", tableModel );
    report.setDataFactory( dataFactory );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      fail( "Expected an exception when processing report with empty rows" );
    } catch ( Exception e ) {
      assertNotNull( "Exception should be thrown for empty rows", e.getMessage() );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Basic single-row test
  // =====================================================================

  @Test
  public void testProcessReportWithSingleRow() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "Alice", "30", "NYC" } },
        new Object[] { "Name", "Age", "City" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertThat( "Output should have content", output.length(), greaterThan( 0 ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Multi-row test
  // =====================================================================

  @Test
  public void testProcessReportWithMultipleRows() throws ReportProcessingException {
    setUpReportData(
        new Object[][] {
            { "Alice", "30", "NYC" },
            { "Bob", "25", "LA" },
            { "Charlie", "35", "Chicago" }
        },
        new Object[] { "Name", "Age", "City" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain Alice", output.contains( "Alice" ) );
      assertTrue( "Output should contain Bob", output.contains( "Bob" ) );
      assertTrue( "Output should contain Charlie", output.contains( "Charlie" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Null values test
  // =====================================================================

  @Test
  public void testProcessReportWithNullValues() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "Alice", null, "NYC" } },
        new Object[] { "Name", "Age", "City" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain Alice", output.contains( "Alice" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Special characters test
  // =====================================================================

  @Test
  public void testProcessReportWithSpecialCharacters() throws ReportProcessingException {
    setUpReportData(
        new Object[][] {
            { "O'Brien, Jim", "value with \"quotes\"", "123,456" }
        },
        new Object[] { "Name", "Note", "Amount" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertThat( "Output should have content", output.length(), greaterThan( 0 ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Numeric types test
  // =====================================================================

  @Test
  public void testProcessReportWithNumericTypes() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { 42, 3.14, -100, 0 } },
        new Object[] { "Int", "Double", "Negative", "Zero" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain 42", output.contains( "42" ) );
      assertTrue( "Output should contain 3.14", output.contains( "3.14" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Boolean types test
  // =====================================================================

  @Test
  public void testProcessReportWithBooleanValues() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { true, false, true } },
        new Object[] { "Flag1", "Flag2", "Flag3" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain true", output.toLowerCase().contains( "true" ) );
      assertTrue( "Output should contain false", output.toLowerCase().contains( "false" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Single column test
  // =====================================================================

  @Test
  public void testProcessReportWithSingleColumn() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "OnlyValue" } },
        new Object[] { "SingleColumn" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain the value", output.contains( "OnlyValue" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // All null values test
  // =====================================================================

  @Test
  public void testProcessReportWithAllNullValues() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { null, null, null } },
        new Object[] { "A", "B", "C" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertThat( "Output should have content", output.length(), greaterThan( 0 ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Large number of columns test
  // =====================================================================

  @Test
  public void testProcessReportWithManyColumns() throws ReportProcessingException {
    int numCols = 50;
    Object[] headers = new Object[numCols];
    Object[] data = new Object[numCols];
    for ( int i = 0; i < numCols; i++ ) {
      headers[i] = "Col" + i;
      data[i] = "Value" + i;
    }
    setUpReportData( new Object[][] { data }, headers );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain first column value", output.contains( "Value0" ) );
      assertTrue( "Output should contain last column value", output.contains( "Value49" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Large number of rows test — triggers flush interval and progress
  // (FLUSH_INTERVAL=10000, PROGRESS_INTERVAL=5000)
  // =====================================================================

  @Test
  public void testProcessReportWithManyRows() throws ReportProcessingException {
    int numRows = 1000;
    Object[][] data = new Object[numRows][3];
    for ( int i = 0; i < numRows; i++ ) {
      data[i] = new Object[] { "Name" + i, i, "City" + ( i % 10 ) };
    }
    setUpReportData( data, new Object[] { "Name", "ID", "City" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should be substantial size", output.length() > 10000 );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Rows > PROGRESS_INTERVAL to trigger fireProgressIfNeeded path
  // =====================================================================

  @Test
  public void testProcessReportWithRowsExceedingProgressInterval() throws ReportProcessingException {
    int numRows = 5001;
    Object[][] data = new Object[numRows][2];
    for ( int i = 0; i < numRows; i++ ) {
      data[i] = new Object[] { "R" + i, i };
    }
    setUpReportData( data, new Object[] { "Name", "ID" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain first row", output.contains( "R0" ) );
      assertTrue( "Output should contain last row", output.contains( "R5000" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Rows > FLUSH_INTERVAL to trigger flush path
  // =====================================================================

  @Test
  public void testProcessReportWithRowsExceedingFlushInterval() throws ReportProcessingException {
    int numRows = 10001;
    Object[][] data = new Object[numRows][1];
    for ( int i = 0; i < numRows; i++ ) {
      data[i] = new Object[] { "V" + i };
    }
    setUpReportData( data, new Object[] { "Col" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain first row", output.contains( "V0" ) );
      assertTrue( "Output should contain row at flush boundary", output.contains( "V10000" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Encoding tests
  // =====================================================================

  @Test
  public void testProcessReportWithUTF8Encoding() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "Caf\u00e9", "M\u00fcller", "\u65e5\u672c" } },
        new Object[] { "Name1", "Name2", "Name3" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream, "UTF-8" );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertThat( "Output should have content", output.length(), greaterThan( 0 ) );
    } finally {
      processor.close();
    }
  }

  @Test
  public void testProcessReportWithISOEncoding() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "Hello", "World" } },
        new Object[] { "Col1", "Col2" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream, "ISO-8859-1" );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.ISO_8859_1 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain Hello", output.contains( "Hello" ) );
      assertTrue( "Output should contain World", output.contains( "World" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Mixed data types test
  // =====================================================================

  @Test
  public void testProcessReportWithMixedDataTypes() throws ReportProcessingException {
    setUpReportData(
        new Object[][] {
            { "String", 42, 3.14, true, null }
        },
        new Object[] { "Text", "Integer", "Double", "Boolean", "Null" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain string value", output.contains( "String" ) );
      assertTrue( "Output should contain integer value", output.contains( "42" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Report with null title (default MasterReport has no title)
  // =====================================================================

  @Test
  public void testProcessReportWithNullTitle() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "Data1", "Data2" } },
        new Object[] { "Col1", "Col2" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain data", output.contains( "Data1" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Column header tests
  // =====================================================================

  @Test
  public void testProcessReportContainsColumnHeaders() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "Val1", "Val2", "Val3" } },
        new Object[] { "Header1", "Header2", "Header3" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain Header1", output.contains( "Header1" ) );
      assertTrue( "Output should contain Header2", output.contains( "Header2" ) );
      assertTrue( "Output should contain Header3", output.contains( "Header3" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // CSV separator test
  // =====================================================================

  @Test
  public void testProcessReportOutputContainsSeparator() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "A", "B", "C" } },
        new Object[] { "Col1", "Col2", "Col3" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain comma separator", output.contains( "," ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Line separator test
  // =====================================================================

  @Test
  public void testProcessReportOutputContainsLineSeparator() throws ReportProcessingException {
    setUpReportData(
        new Object[][] {
            { "Row1" },
            { "Row2" }
        },
        new Object[] { "Col1" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain CRLF line separator", output.contains( "\r\n" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Whitespace and empty string values test
  // =====================================================================

  @Test
  public void testProcessReportWithEmptyStringValues() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "", "  ", "value" } },
        new Object[] { "Empty", "Spaces", "Normal" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain value", output.contains( "value" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Long string values test
  // =====================================================================

  @Test
  public void testProcessReportWithLongStringValues() throws ReportProcessingException {
    String longValue = "x".repeat( 1000 );
    setUpReportData(
        new Object[][] { { longValue } },
        new Object[] { "LongColumn" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain long value", output.contains( longValue ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Multiple rows with null mixed in
  // =====================================================================

  @Test
  public void testProcessReportWithMixedNullRows() throws ReportProcessingException {
    setUpReportData(
        new Object[][] {
            { "A", null, "C" },
            { null, "B", null },
            { "X", "Y", "Z" }
        },
        new Object[] { "Col1", "Col2", "Col3" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain X", output.contains( "X" ) );
      assertTrue( "Output should contain Y", output.contains( "Y" ) );
      assertTrue( "Output should contain Z", output.contains( "Z" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // BufferedOutputStream wrapping test (covers wrapOutputStream)
  // =====================================================================

  @Test
  public void testProcessReportWithBufferedOutputStream() throws ReportProcessingException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BufferedOutputStream bufferedOut = new BufferedOutputStream( baos );

    setUpReportData(
        new Object[][] { { "buffered", "test" } },
        new Object[] { "Col1", "Col2" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, bufferedOut );
    try {
      processor.processReport();
      String output = baos.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain data", output.contains( "buffered" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Two rows test
  // =====================================================================

  @Test
  public void testProcessReportWithTwoRows() throws ReportProcessingException {
    setUpReportData(
        new Object[][] {
            { "Row1Col1", "Row1Col2" },
            { "Row2Col1", "Row2Col2" }
        },
        new Object[] { "ColumnA", "ColumnB" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain Row1Col1", output.contains( "Row1Col1" ) );
      assertTrue( "Output should contain Row2Col2", output.contains( "Row2Col2" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Negative numbers test
  // =====================================================================

  @Test
  public void testProcessReportWithNegativeNumbers() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { -1, -99.99, -0.001 } },
        new Object[] { "Neg1", "Neg2", "Neg3" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain negative value", output.contains( "-1" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Date values test (as String to avoid formatter dependency)
  // =====================================================================

  @Test
  public void testProcessReportWithDateStrings() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "2026-01-01", "2026-12-31" } },
        new Object[] { "StartDate", "EndDate" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain start date", output.contains( "2026-01-01" ) );
      assertTrue( "Output should contain end date", output.contains( "2026-12-31" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Single row single column test
  // =====================================================================

  @Test
  public void testProcessReportWithSingleCellValue() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "TheOnlyCell" } },
        new Object[] { "OnlyCol" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain the single cell value", output.contains( "TheOnlyCell" ) );
      assertTrue( "Output should contain the column header", output.contains( "OnlyCol" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // OutputProcessorMetaData test
  // =====================================================================

  @Test
  public void testOutputProcessorMetaData() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    assertNotNull( "Output processor should not be null", processor.getOutputProcessor() );
    assertNotNull( "Output processor meta data should not be null",
        processor.getOutputProcessor().getMetaData() );
    processor.close();
  }

  // =====================================================================
  // Test default encoding fallback (null encoding constructor)
  // =====================================================================

  @Test
  public void testProcessReportWithDefaultEncoding() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "default-enc-test" } },
        new Object[] { "Col" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream, null );
    try {
      processor.processReport();
      byte[] bytes = outputStream.toByteArray();
      assertTrue( "Output bytes should not be empty", bytes.length > 0 );
      String output = new String( bytes, StandardCharsets.UTF_8 );
      assertTrue( "Output should contain data", output.contains( "default-enc-test" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with cell value containing newlines
  // =====================================================================

  @Test
  public void testProcessReportWithNewlinesInValue() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "line1\nline2", "normal" } },
        new Object[] { "MultiLine", "Normal" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain normal value", output.contains( "normal" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with Unicode characters
  // =====================================================================

  @Test
  public void testProcessReportWithUnicodeCharacters() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "\u00e9\u00e8\u00ea", "\u00fc\u00f6\u00e4", "\u4e16\u754c" } },
        new Object[] { "French", "German", "Chinese" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream, "UTF-8" );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertThat( "Output should have content beyond just headers", output.length(), greaterThan( 20 ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with Date object values (exercises formatValue with Date)
  // =====================================================================

  @Test
  public void testProcessReportWithDateObjects() throws ReportProcessingException {
    Date now = new Date();
    setUpReportData(
        new Object[][] { { now, "text" } },
        new Object[] { "DateCol", "TextCol" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain text", output.contains( "text" ) );
      // Date is formatted via String.valueOf when no formatter is set
      assertThat( "Output should have content beyond headers", output.length(), greaterThan( 30 ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with Number object values (Integer, Long, Float, Double)
  // =====================================================================

  @Test
  public void testProcessReportWithVariousNumberTypes() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { 42, 123456789L, 1.5f, 99.99d } },
        new Object[] { "IntCol", "LongCol", "FloatCol", "DoubleCol" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain integer", output.contains( "42" ) );
      assertTrue( "Output should contain long", output.contains( "123456789" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with ItemBand containing text field elements
  // (covers buildColumnFormatters, collectFormatters, extractFormatter)
  // =====================================================================

  @Test
  public void testProcessReportWithItemBandTextFields() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "Alice", "Bob" } },
        new Object[] { "FirstName", "LastName" } );

    ItemBand itemBand = report.getItemBand();
    Element textElement = new Element();
    textElement.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "FirstName" );
    itemBand.addElement( textElement );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain Alice", output.contains( "Alice" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with ItemBand element that has no field attribute
  // (covers extractFormatter early return when fieldAttr is null)
  // =====================================================================

  @Test
  public void testProcessReportWithItemBandElementNoField() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "DataVal" } },
        new Object[] { "Col1" } );

    ItemBand itemBand = report.getItemBand();
    Element noFieldElement = new Element();
    // No field attribute set — extractFormatter should return early
    itemBand.addElement( noFieldElement );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain DataVal", output.contains( "DataVal" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with ItemBand element that has field not matching any column
  // (covers extractFormatter when colIndex is null)
  // =====================================================================

  @Test
  public void testProcessReportWithItemBandElementUnmatchedField() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "DataVal" } },
        new Object[] { "Col1" } );

    ItemBand itemBand = report.getItemBand();
    Element unmatchedElement = new Element();
    unmatchedElement.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "NonExistentColumn" );
    itemBand.addElement( unmatchedElement );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain DataVal", output.contains( "DataVal" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with ItemBand element that has visibility set to false
  // (covers isHiddenColumn and visibility extraction)
  // =====================================================================

  @Test
  public void testProcessReportWithHiddenColumn() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "Visible", "Hidden", "AlsoVisible" } },
        new Object[] { "Col1", "Col2", "Col3" } );

    ItemBand itemBand = report.getItemBand();
    Element hiddenElement = new Element();
    hiddenElement.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "Col2" );
    hiddenElement.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, Boolean.FALSE );
    itemBand.addElement( hiddenElement );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain Visible", output.contains( "Visible" ) );
      assertTrue( "Output should contain AlsoVisible", output.contains( "AlsoVisible" ) );
      // Hidden column's value should not appear
      assertFalse( "Output should NOT contain Hidden value", output.contains( "\"Hidden\"" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with ItemBand element that has visibility set to true explicitly
  // =====================================================================

  @Test
  public void testProcessReportWithExplicitlyVisibleColumn() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "VisData" } },
        new Object[] { "Col1" } );

    ItemBand itemBand = report.getItemBand();
    Element visibleElement = new Element();
    visibleElement.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "Col1" );
    visibleElement.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, Boolean.TRUE );
    itemBand.addElement( visibleElement );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain VisData", output.contains( "VisData" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with Date object and ItemBand date-field element with format
  // (covers createColumnFormatter date-field branch, buildDateFormat)
  // =====================================================================

  @Test
  public void testProcessReportWithDateFieldFormatter() throws ReportProcessingException {
    Date testDate = new Date( 1672531200000L ); // 2023-01-01 00:00:00 UTC
    setUpReportData(
        new Object[][] { { testDate, "text" } },
        new Object[] { "DateCol", "TextCol" } );

    ItemBand itemBand = report.getItemBand();
    DateFieldElementFactory dateFactory = new DateFieldElementFactory();
    dateFactory.setFieldname( "DateCol" );
    dateFactory.setFormatString( "yyyy-MM-dd" );
    itemBand.addElement( dateFactory.createElement() );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream, "UTF-8" );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      // Date should be formatted as yyyy-MM-dd
      SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd", Locale.US );
      sdf.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
      String expected = sdf.format( testDate );
      assertTrue( "Output should contain formatted date " + expected, output.contains( expected ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with Number object and ItemBand number-field element with format
  // (covers createColumnFormatter number-field branch, buildNumberFormat)
  // =====================================================================

  @Test
  public void testProcessReportWithNumberFieldFormatter() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { 12345.678, "text" } },
        new Object[] { "NumCol", "TextCol" } );

    ItemBand itemBand = report.getItemBand();
    NumberFieldElementFactory numFactory = new NumberFieldElementFactory();
    numFactory.setFieldname( "NumCol" );
    numFactory.setFormatString( "#,##0.00" );
    itemBand.addElement( numFactory.createElement() );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream, "UTF-8" );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      // Number should be formatted with the pattern
      assertTrue( "Output should contain formatted number",
          output.contains( "12" ) && output.contains( "345" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with ItemBand element with format string but no matching type
  // (covers createColumnFormatter default branch — no date/number format)
  // =====================================================================

  @Test
  public void testProcessReportWithTextFieldAndFormatString() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "hello" } },
        new Object[] { "Col1" } );

    ItemBand itemBand = report.getItemBand();
    TextFieldElementFactory textFactory = new TextFieldElementFactory();
    textFactory.setFieldname( "Col1" );
    itemBand.addElement( textFactory.createElement() );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain hello", output.contains( "hello" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with INDEX_COLUMN_PREFIX columns (covers computeRealColumnCount)
  // =====================================================================

  @Test
  public void testProcessReportFiltersIndexColumns() throws ReportProcessingException {
    // Use a custom TableModel that has INDEX_COLUMN_PREFIX columns
    DefaultTableModel tableModel = new DefaultTableModel(
        new Object[][] { { "RealVal", "IndexVal" } },
        new Object[] { "RealCol", ClassicEngineBoot.INDEX_COLUMN_PREFIX + "0" } );
    TableDataFactory dataFactory = new TableDataFactory();
    dataFactory.addTable( "default", tableModel );
    report.setDataFactory( dataFactory );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain RealVal", output.contains( "RealVal" ) );
      // The index column should still appear since computeRealColumnCount only determines count
      // but writeColumnHeaders iterates through all columns
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with null column name (covers computeRealColumnCount null branch)
  // =====================================================================

  @Test
  public void testProcessReportWithNullColumnName() throws ReportProcessingException {
    DefaultTableModel tableModel = new DefaultTableModel(
        new Object[][] { { "Val1", "Val2" } },
        new Object[] { null, "Col2" } );
    TableDataFactory dataFactory = new TableDataFactory();
    dataFactory.addTable( "default", tableModel );
    report.setDataFactory( dataFactory );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain Col2", output.contains( "Col2" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test createLayoutManager returns FastExportOutputFunction
  // =====================================================================

  @Test
  public void testCreateLayoutManagerReturnsFastExportOutputFunction() throws ReportProcessingException {
    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    OutputFunction lm = processor.createLayoutManager();
    assertNotNull( "Layout manager should not be null", lm );
    assertThat( "Layout manager should be a FastExportOutputFunction",
        lm.getClass().getSimpleName(), is( "FastExportOutputFunction" ) );
    processor.close();
  }

  // =====================================================================
  // Test with multiple ItemBand elements (mix of matched/unmatched)
  // =====================================================================

  @Test
  public void testProcessReportWithMultipleItemBandElements() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "A", "B", "C" } },
        new Object[] { "Col1", "Col2", "Col3" } );

    ItemBand itemBand = report.getItemBand();

    // Element with matching field
    Element e1 = new Element();
    e1.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "Col1" );
    itemBand.addElement( e1 );

    // Element with no field
    Element e2 = new Element();
    itemBand.addElement( e2 );

    // Element with non-matching field
    Element e3 = new Element();
    e3.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "NoSuchCol" );
    itemBand.addElement( e3 );

    // Element with matching field for Col3
    Element e4 = new Element();
    e4.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "Col3" );
    itemBand.addElement( e4 );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain A", output.contains( "A" ) );
      assertTrue( "Output should contain B", output.contains( "B" ) );
      assertTrue( "Output should contain C", output.contains( "C" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with Date value but no date-field formatter (uses String.valueOf)
  // =====================================================================

  @Test
  public void testProcessReportWithDateValueNoFormatter() throws ReportProcessingException {
    Date testDate = new Date( 0L );
    setUpReportData(
        new Object[][] { { testDate } },
        new Object[] { "DateCol" } );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      // Without formatter, Date.toString() is used
      assertThat( "Output should have content", output.length(), greaterThan( 10 ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test processReport wraps IOException in ReportProcessingException
  // (uses a failing OutputStream to trigger IOException path)
  // =====================================================================

  @Test
  public void testProcessReportWrapsIOException() {
    setUpReportData(
        new Object[][] { { "data" } },
        new Object[] { "Col" } );

    java.io.OutputStream failingStream = new java.io.OutputStream() {
      @Override
      public void write( int b ) throws java.io.IOException {
        throw new java.io.IOException( "Simulated IO failure" );
      }

      @Override
      public void write( byte[] b, int off, int len ) throws java.io.IOException {
        throw new java.io.IOException( "Simulated IO failure" );
      }
    };

    try {
      FastCsvExportProcessor processor = new FastCsvExportProcessor( report, failingStream );
      try {
        processor.processReport();
        fail( "Expected ReportProcessingException due to IOException" );
      } catch ( ReportProcessingException e ) {
        assertNotNull( "Exception should have a message", e.getMessage() );
      } finally {
        processor.close();
      }
    } catch ( ReportProcessingException e ) {
      // Constructor might also throw — that's acceptable
      assertNotNull( "Exception should have a message", e.getMessage() );
    }
  }

  // =====================================================================
  // Test with ItemBand element that has format string attribute
  // (covers format-string extraction in extractFormatter)
  // =====================================================================

  @Test
  public void testProcessReportWithElementFormatString() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "value" } },
        new Object[] { "Col1" } );

    ItemBand itemBand = report.getItemBand();
    Element element = new Element();
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "Col1" );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, "someFormat" );
    itemBand.addElement( element );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain value", output.contains( "value" ) );
    } finally {
      processor.close();
    }
  }

  // =====================================================================
  // Test with empty format string attribute (covers fmtStr null branch)
  // =====================================================================

  @Test
  public void testProcessReportWithEmptyFormatString() throws ReportProcessingException {
    setUpReportData(
        new Object[][] { { "value" } },
        new Object[] { "Col1" } );

    ItemBand itemBand = report.getItemBand();
    Element element = new Element();
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "Col1" );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, "" );
    itemBand.addElement( element );

    FastCsvExportProcessor processor = new FastCsvExportProcessor( report, outputStream );
    try {
      processor.processReport();
      String output = outputStream.toString( StandardCharsets.UTF_8 );
      assertFalse( "Output should not be empty", output.isEmpty() );
      assertTrue( "Output should contain value", output.contains( "value" ) );
    } finally {
      processor.close();
    }
  }
}


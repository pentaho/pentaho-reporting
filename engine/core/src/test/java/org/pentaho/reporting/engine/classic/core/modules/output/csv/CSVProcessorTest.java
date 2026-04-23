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


package org.pentaho.reporting.engine.classic.core.modules.output.csv;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;

public class CSVProcessorTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testCreateLayoutManagerReadsForcedEnclosureConfig() throws Exception {
    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( CSVProcessor.CSV_ENCLOSURE_FORCED, "true" );

    final TestableCSVProcessor processor = new TestableCSVProcessor( report );

    assertTrue( processor.isAlwaysDoQuotesOnLayoutManager() );
  }

  public void testCreateLayoutManagerDefaultForcedEnclosureIsFalse() throws Exception {
    final MasterReport report = new MasterReport();
    final TestableCSVProcessor processor = new TestableCSVProcessor( report );

    assertFalse( processor.isAlwaysDoQuotesOnLayoutManager() );
  }

  public void testCreateLayoutManagerReadsEnclosureCharConfig() throws Exception {
    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( CSVProcessor.CSV_ENCLOSURE_CHAR, "'" );

    final TestableCSVProcessor processor = new TestableCSVProcessor( report );

    assertEquals( '\'', processor.getEnclosureOnLayoutManager() );
  }

  public void testCreateLayoutManagerDefaultEnclosureCharIsDoubleQuote() throws Exception {
    final MasterReport report = new MasterReport();
    final TestableCSVProcessor processor = new TestableCSVProcessor( report );

    assertEquals( '"', processor.getEnclosureOnLayoutManager() );
  }

  public void testConstructorThrowsOnMultiCharEnclosure() throws Exception {
    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( CSVProcessor.CSV_ENCLOSURE_CHAR, "!!" );

    try {
      new TestableCSVProcessor( report );
      fail( "Expected IllegalArgumentException for multi-character enclosure" );
    } catch ( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testConstructorThrowsOnEmptyEnclosure() throws Exception {
    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( CSVProcessor.CSV_ENCLOSURE_CHAR, "" );

    try {
      new TestableCSVProcessor( report );
      fail( "Expected IllegalArgumentException for empty enclosure" );
    } catch ( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testConstructorThrowsOnEmptySeparator() throws Exception {
    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( CSVProcessor.CSV_SEPARATOR, "" );

    try {
      new TestableCSVProcessor( report );
      fail( "Expected IllegalArgumentException for empty separator" );
    } catch ( final IllegalArgumentException e ) {
      // expected
    }
  }

  private static class TestableCSVProcessor extends CSVProcessor {
    private TestableCSVProcessor( final MasterReport report ) throws ReportProcessingException {
      super( report );
    }

    private boolean isAlwaysDoQuotesOnLayoutManager() {
      final CSVWriter layoutManager = (CSVWriter) createLayoutManager();
      return layoutManager.isAlwaysDoQuotes();
    }

    private char getEnclosureOnLayoutManager() {
      final CSVWriter layoutManager = (CSVWriter) createLayoutManager();
      return layoutManager.getEnclosure();
    }
  }
}

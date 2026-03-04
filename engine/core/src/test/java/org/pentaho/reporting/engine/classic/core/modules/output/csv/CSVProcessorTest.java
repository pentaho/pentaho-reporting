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

  private static class TestableCSVProcessor extends CSVProcessor {
    private TestableCSVProcessor( final MasterReport report ) throws ReportProcessingException {
      super( report );
    }

    private boolean isAlwaysDoQuotesOnLayoutManager() {
      final CSVWriter layoutManager = (CSVWriter) createLayoutManager();
      return layoutManager.isAlwaysDoQuotes();
    }
  }
}

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


package org.pentaho.reporting.engine.classic.testcases;

import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldenSampleGenerator;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;

import java.awt.*;

public class GoldGenerator extends GoldenSampleGenerator {
  protected FilesystemFilter createReportFilter() {
    return new FilesystemFilter( new String[] { ".prpt", ".report", ".xml" }, "Reports", false );
  }

  public static void main( String[] args )
    throws Exception {
    FixAllBrokenLogging.fixBrokenLogging();
    if ( GraphicsEnvironment.isHeadless() == false ) {
      throw new IllegalStateException();
    }
    new GoldGenerator().runAllGoldReports();

    //    new GoldGenerator().runSingleGoldReport("Income Statement.xml", ReportProcessingMode.current);
    //    new GoldGenerator().runSingleGoldReport("Income Statement.xml", ReportProcessingMode.migration);
    //    new GoldGenerator().runSingleGoldReport("Income Statement.xml", ReportProcessingMode.legacy);

  }
}

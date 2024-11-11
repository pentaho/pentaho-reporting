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


package org.pentaho.reporting.engine.classic.core.testsupport.gold;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @noinspection HardCodedStringLiteral
 */
public class GoldenSampleGenerator extends GoldTestBase {
  protected void handleXmlContent( final byte[] reportOutput, final File file ) throws Exception {
    final OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( file ) );
    try {
      outputStream.write( reportOutput );
    } finally {
      outputStream.close();
    }
  }

  protected void initializeTestEnvironment() throws Exception {
    super.setUp();

    final File marker = findMarker();
    final File gold = new File( marker, "gold" );
    gold.mkdirs();
    final File goldLegacy = new File( marker, "gold-legacy" );
    goldLegacy.mkdirs();
    final File goldMigrated = new File( marker, "gold-migrated" );
    goldMigrated.mkdirs();
  }

  public static void main( final String[] args ) throws Exception {
    new GoldenSampleGenerator().runAllGoldReports();

    // generateSingleSample("Prd-2087-Widow-2.prpt", false);
  }

  private static void generateSingleSample( final String name, final boolean legacy ) throws Exception {
    if ( legacy ) {
      new GoldenSampleGenerator().runSingleGoldReport( name, ReportProcessingMode.legacy );
    }
    new GoldenSampleGenerator().runSingleGoldReport( name, ReportProcessingMode.migration );
    new GoldenSampleGenerator().runSingleGoldReport( name, ReportProcessingMode.current );
  }
}

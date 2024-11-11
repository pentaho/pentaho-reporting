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


package org.pentaho.reporting.engine.classic.core;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;

public class GoldExecuteIT extends GoldTestBase {
  public GoldExecuteIT() {
  }

  @Test
  public void testExecuteReports() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    runAllGoldReports();
  }
  /*
   * 
   * protected void run(final File file, final File gold) throws Exception { final MasterReport originalReport =
   * parseReport(file);
   * 
   * final MasterReport report = postProcess(GoldenSampleGenerator.tuneForTesting(originalReport)); final String
   * fileName = IOUtils.getInstance().stripFileExtension(file.getName()); handleXmlContent(executePageable(report), new
   * File(gold, fileName + "-page.xml")); }
   * 
   * protected FilesystemFilter createReportFilter() { return new FilesystemFilter("Prd-3245.prpt", "Reports"); }
   */
}

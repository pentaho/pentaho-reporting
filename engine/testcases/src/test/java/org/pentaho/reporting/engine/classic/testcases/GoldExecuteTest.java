/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.testcases;

import junit.framework.Assert;
import net.sourceforge.barbecue.env.EnvironmentFactory;
import net.sourceforge.barbecue.env.HeadlessEnvironment;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;

public class GoldExecuteTest extends GoldTestBase {
  public GoldExecuteTest() {
  }

  @Test
  public void testExecuteReports() throws Exception {
    if ( "false".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.engine.classic.test.ExecuteLongRunningTest" ) ) ) {
      return;
    }
    Assert.assertTrue( EnvironmentFactory.getEnvironment() instanceof HeadlessEnvironment );
    runAllGoldReports();
  }
/*
  protected FilesystemFilter createReportFilter()
  {
    return new FilesystemFilter
        (new String[]{".prpt", ".report", ".xml"}, "Reports", false);
  }

  protected void run(final File file, final File gold)
      throws Exception
  {
    final MasterReport originalReport = parseReport(file);

    final MasterReport report = postProcess(GoldenSampleGenerator.tuneForTesting(originalReport));
    final String fileName = IOUtils.getInstance().stripFileExtension(file.getName());
    handleXmlContent(executePageable(report), new File(gold, fileName + "-page.xml"));
  }

  protected FilesystemFilter createReportFilter()
  {
    return new FilesystemFilter("opensource.xml", "Reports");
  }
*/
}

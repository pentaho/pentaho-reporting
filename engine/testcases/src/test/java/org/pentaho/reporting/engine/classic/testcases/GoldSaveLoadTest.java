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

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import static junit.framework.Assert.assertTrue;

public class GoldSaveLoadTest extends GoldTestBase {
  public GoldSaveLoadTest() {
  }

  protected MasterReport postProcess( final MasterReport originalReport ) throws Exception {
    final DocumentMetaData originalMeta = originalReport.getBundle().getMetaData();
    final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( originalReport, bout, originalMeta );
    assertTrue( bout.getLength() > 0 );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource reportRes = mgr.createDirectly( bout.toByteArray(), MasterReport.class );
    return (MasterReport) reportRes.getResource();
  }

  /**
   * Load/Save does not work on legacy reports. We can only read them, not write them.
   *
   * @return
   */
  protected FilesystemFilter createReportFilter() {
    return new FilesystemFilter
      ( new String[] { ".prpt" }, "Reports", false );
  }

  @Test
  public void testExecuteReports() throws Exception {
    if ( "false".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.engine.classic.test.ExecuteLongRunningTest" ) ) ) {
      return;
    }
    runAllGoldReports();
  }
}

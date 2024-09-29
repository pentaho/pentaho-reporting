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

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static junit.framework.Assert.assertNotNull;

public class GoldSerializeTest extends GoldTestBase {
  public GoldSerializeTest() {
  }

  protected MasterReport postProcess( final MasterReport originalReport ) throws Exception {
    //  if (true) return originalReport;
    final byte[] bytes = serializeReportObject( originalReport );
    final MasterReport report = deserializeReportObject( bytes );
    return report;
  }

  private byte[] serializeReportObject( final MasterReport report ) throws IOException {
    // we don't test whether our demo models are serializable :)
    // clear all report properties, which may cause trouble ...
    final MemoryByteArrayOutputStream bo = new MemoryByteArrayOutputStream();
    final ObjectOutputStream oout = new ObjectOutputStream( bo );
    oout.writeObject( report );
    oout.close();
    return bo.toByteArray();
  }

  private MasterReport deserializeReportObject( final byte[] data ) throws IOException, ClassNotFoundException {
    final ByteArrayInputStream bin = new ByteArrayInputStream( data );
    final ObjectInputStream oin = new ObjectInputStream( bin );
    final MasterReport report2 = (MasterReport) oin.readObject();
    assertNotNull( report2 );
    return report2;
  }

  protected FilesystemFilter createReportFilter() {
    return new FilesystemFilter( new String[] { ".prpt", ".report", ".xml" }, "Reports", false );
  }

  @Test
  public void testExecuteReports() throws Exception {
    if ( "false".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.engine.classic.test.ExecuteLongRunningTest" ) ) ) {
      return;
    }
    //    runSingleGoldReport("Prd-5044.prpt", ReportProcessingMode.migration);
    runAllGoldReports();
  }

}

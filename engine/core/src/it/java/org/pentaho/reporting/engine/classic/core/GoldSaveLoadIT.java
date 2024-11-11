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
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class GoldSaveLoadIT extends GoldTestBase {
  public GoldSaveLoadIT() {
  }

  protected MasterReport postProcess( final MasterReport originalReport ) throws Exception {
    final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( originalReport, bout );
    assertTrue( bout.getLength() > 0 );
    /*
     * final File f = File.createTempFile("test-output-", ".prpt", new File ("test-output")); final FileOutputStream
     * outputStream = new FileOutputStream(f); outputStream.write(bout.toByteArray()); outputStream.close();
     */
    final ResourceManager mgr = new ResourceManager();
    final Resource reportRes = mgr.createDirectly( bout.toByteArray(), MasterReport.class );
    return (MasterReport) reportRes.getResource();
  }

  @Test
  public void testExecuteReports() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    runAllGoldReports();
  }

  /**
   * Load/Save does not work on legacy reports. We can only read them, not write them.
   *
   * @return
   */
  protected FilesystemFilter createReportFilter() {
    return new FilesystemFilter( new String[] { ".prpt" }, "Reports", false );
  }

  @Test
  public void testParallelExecutionIsSafe() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final boolean[] error = { false };
    final ExecutorService threadPool =
        new ThreadPoolExecutor( 3, 3, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            new TestThreadFactory(), new ThreadPoolExecutor.AbortPolicy() );
    for ( int i = 0; i < 20; i++ ) {
      threadPool.execute( new Runnable() {
        public void run() {
          try {
            runSingleGoldReport( "Prd-3931.prpt", ReportProcessingMode.current );
            // runSingleGoldReport("Crashing-crosstab.prpt", ReportProcessingMode.current);
          } catch ( Exception e ) {
            e.printStackTrace();
            error[0] = true;
          }
        }
      } );
    }
    threadPool.shutdown();
    while ( threadPool.isTerminated() == false ) {
      threadPool.awaitTermination( 5, TimeUnit.MINUTES );
    }

    assertFalse( error[0] );

  }
}

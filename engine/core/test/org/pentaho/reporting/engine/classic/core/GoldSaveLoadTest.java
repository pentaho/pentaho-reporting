/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2000 - 2011 Pentaho Corporation and Contributors...  
 * All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class GoldSaveLoadTest extends GoldTestBase
{
  public GoldSaveLoadTest()
  {
  }

  protected MasterReport postProcess(final MasterReport originalReport) throws Exception
  {
    final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
    BundleWriter.writeReportToZipStream(originalReport, bout);
    assertTrue(bout.getLength() > 0);
/*
    final File f = File.createTempFile("test-output-", ".prpt", new File ("test-output"));
    final FileOutputStream outputStream = new FileOutputStream(f);
    outputStream.write(bout.toByteArray());
    outputStream.close();
*/
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource reportRes = mgr.createDirectly(bout.toByteArray(), MasterReport.class);
    return (MasterReport) reportRes.getResource();
  }

  @Test
  public void testExecuteReports() throws Exception
  {
    if ("false".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.test.ExecuteLongRunningTest")))
    {
      return;
    }
    runAllGoldReports();
  }

  @Test
  public void testParallelExecutionIsSafe() throws Exception
  {
    final ExecutorService threadPool = new ThreadPoolExecutor(3, 3,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>(),
                                  new TestThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    for (int i = 0; i < 20; i++)
    threadPool.execute(new Runnable () {
      public void run()
      {
        try
        {
          runSingleGoldReport("Prd-3931.prpt", ReportProcessingMode.current);
          //runSingleGoldReport("Crashing-crosstab.prpt", ReportProcessingMode.current);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);

        }
      }
    });
    threadPool.shutdown();
    while (threadPool.isTerminated() == false)
    {
      threadPool.awaitTermination(5, TimeUnit.MINUTES);
    }

  }
}

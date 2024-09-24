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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

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

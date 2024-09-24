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

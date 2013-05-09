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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class Prd2087Test extends TestCase
{
  public Prd2087Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testWidow1Crash() throws Exception
  {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport("Prd-2087-Widow-1.prpt");
   // masterReport.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));
    DebugReportRunner.createXmlTablePageable(masterReport);
  }

  public void testOrphan1Crash() throws Exception
  {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport("Prd-2087-Orphan-0.prpt");
    masterReport.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));
    DebugReportRunner.createXmlTablePageable(masterReport);
  }

  public void testWidow1Crash2() throws Exception
  {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport("Prd-2087-Widow-1.prpt");
    // masterReport.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));
    DebugReportRunner.createXmlPageable(masterReport);

//    DebugReportRunner.showDialog(masterReport);

  }
}

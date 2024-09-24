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

import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldenSampleGenerator;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;

import java.awt.*;

public class GoldGenerator extends GoldenSampleGenerator {
  protected FilesystemFilter createReportFilter() {
    return new FilesystemFilter( new String[] { ".prpt", ".report", ".xml" }, "Reports", false );
  }

  public static void main( String[] args )
    throws Exception {
    FixAllBrokenLogging.fixBrokenLogging();
    if ( GraphicsEnvironment.isHeadless() == false ) {
      throw new IllegalStateException();
    }
    new GoldGenerator().runAllGoldReports();

    //    new GoldGenerator().runSingleGoldReport("Income Statement.xml", ReportProcessingMode.current);
    //    new GoldGenerator().runSingleGoldReport("Income Statement.xml", ReportProcessingMode.migration);
    //    new GoldGenerator().runSingleGoldReport("Income Statement.xml", ReportProcessingMode.legacy);

  }
}

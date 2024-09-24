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

package org.pentaho.reporting.engine.classic.core.testsupport.gold;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @noinspection HardCodedStringLiteral
 */
public class GoldenSampleGenerator extends GoldTestBase {
  protected void handleXmlContent( final byte[] reportOutput, final File file ) throws Exception {
    final OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( file ) );
    try {
      outputStream.write( reportOutput );
    } finally {
      outputStream.close();
    }
  }

  protected void initializeTestEnvironment() throws Exception {
    super.setUp();

    final File marker = findMarker();
    final File gold = new File( marker, "gold" );
    gold.mkdirs();
    final File goldLegacy = new File( marker, "gold-legacy" );
    goldLegacy.mkdirs();
    final File goldMigrated = new File( marker, "gold-migrated" );
    goldMigrated.mkdirs();
  }

  public static void main( final String[] args ) throws Exception {
    new GoldenSampleGenerator().runAllGoldReports();

    // generateSingleSample("Prd-2087-Widow-2.prpt", false);
  }

  private static void generateSingleSample( final String name, final boolean legacy ) throws Exception {
    if ( legacy ) {
      new GoldenSampleGenerator().runSingleGoldReport( name, ReportProcessingMode.legacy );
    }
    new GoldenSampleGenerator().runSingleGoldReport( name, ReportProcessingMode.migration );
    new GoldenSampleGenerator().runSingleGoldReport( name, ReportProcessingMode.current );
  }
}

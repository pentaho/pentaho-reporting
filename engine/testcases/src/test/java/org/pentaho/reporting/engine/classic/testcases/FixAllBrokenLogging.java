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

public class FixAllBrokenLogging {
  public static void fixBrokenLogging() {
    System.setProperty( "KETTLE_REDIRECT_STDOUT", "N" );
    System.setProperty( "KETTLE_REDIRECT_STDERR", "N" );
    //  System.setProperty("org.apache.commons.logging.diagnostics.dest", "diagnostic-log.txt");
  }
}

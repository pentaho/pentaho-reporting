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

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class InlineSubreportOnGroupIT extends TestCase {
  public InlineSubreportOnGroupIT() {
  }

  public InlineSubreportOnGroupIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRun() throws Exception {
    MasterReport report = new MasterReport();
    report.getRelationalGroup( 0 ).getHeader().addElement( new SubReport() );

    DebugReportRunner.executeAll( report );

  }
}

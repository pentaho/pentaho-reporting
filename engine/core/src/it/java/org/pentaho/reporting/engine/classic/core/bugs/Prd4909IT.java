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


package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class Prd4909IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testReportRun() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4909.prpt" );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // crashes if not fixed.
    Assert.assertNotNull( MatchFactory.findElementByName( logicalPageBox, "sr0" ) );
    Assert.assertNotNull( MatchFactory.findElementByName( logicalPageBox, "sr-0-0" ) );
    Assert.assertNotNull( MatchFactory.findElementByName( logicalPageBox, "sr-0-1" ) );
  }
}

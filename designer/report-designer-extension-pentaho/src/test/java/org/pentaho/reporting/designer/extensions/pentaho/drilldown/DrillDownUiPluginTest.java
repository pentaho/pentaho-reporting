/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DrillDownUiPluginTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testDrillDownUiPlugin() {
    DrillDownUiPlugin plugin = new DrillDownUiPlugin();
    assertNotNull( plugin );
  }

  @Test
  public void testGetOverlaySources() {
    DrillDownUiPlugin plugin = new DrillDownUiPlugin();
    String[] sources = plugin.getOverlaySources();
    assertEquals( sources.length, 1 );
    assertTrue( sources[0].equals( "org/pentaho/reporting/designer/extensions/pentaho/drilldown/ui-overlay.xul" ) );
  }

}

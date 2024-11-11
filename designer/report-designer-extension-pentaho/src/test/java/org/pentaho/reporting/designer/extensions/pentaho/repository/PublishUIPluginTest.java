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


package org.pentaho.reporting.designer.extensions.pentaho.repository;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PublishUIPluginTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testPublishUIPlugin() {
    PublishUIPlugin plugin = new PublishUIPlugin();
    assertNotNull( plugin );
  }

  @Test
  public void testGetOverlaySources() {
    PublishUIPlugin plugin = new PublishUIPlugin();
    assertNotNull( plugin );
    String[] sources = plugin.getOverlaySources();
    assertNotNull( sources );
    assertEquals( 1, sources.length );
    assertEquals( "org/pentaho/reporting/designer/extensions/pentaho/repository/ui-overlay.xul", sources[0] );
  }

}

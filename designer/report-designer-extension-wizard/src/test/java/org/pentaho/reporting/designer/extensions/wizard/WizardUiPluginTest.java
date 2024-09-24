/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.extensions.wizard;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WizardUiPluginTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testWizardUiPlugin() {
    WizardUiPlugin plugin = new WizardUiPlugin();
    assertNotNull( plugin );
  }

  @Test
  public void testGetOverlaySources() {
    WizardUiPlugin plugin = new WizardUiPlugin();
    String[] result = plugin.getOverlaySources();
    assert ( result.length == 1 );
    assertEquals( result[0], "org/pentaho/reporting/designer/extensions/wizard/ui-overlay.xul" );
  }

}

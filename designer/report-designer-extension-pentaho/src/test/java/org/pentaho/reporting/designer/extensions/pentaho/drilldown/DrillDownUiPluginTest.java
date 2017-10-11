/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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

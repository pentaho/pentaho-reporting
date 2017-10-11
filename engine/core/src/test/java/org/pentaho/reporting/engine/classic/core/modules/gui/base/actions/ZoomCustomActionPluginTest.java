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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportEventSource;

public class ZoomCustomActionPluginTest extends ActionTestingUtil {

  private ZoomCustomActionPlugin plugin;
  protected ReportEventSource source;

  @Before
  public void setUp() {
    super.setUp();
    source = mock( ReportEventSource.class );
    doReturn( source ).when( context ).getEventSource();
    plugin = new ZoomCustomActionPlugin();
  }

  @Override
  protected String getSmallIconKey() {
    return "action.zoomCustom.small-icon";
  }

  @Override
  protected String getNameValue() {
    return "test zoomcustom name";
  }

  @Override
  protected String getDescriptionValue() {
    return "test zoomcustom description";
  }

  @Override
  protected String getLargeIconKey() {
    return "action.zoomCustom.icon";
  }

  @Override
  protected String getExpectedPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.zoom-custom.";
  }

  @Override
  protected String getPrefix() {
    return plugin.getConfigurationPrefix();
  }

  @Override
  protected ControlActionPlugin getPlugin() {
    return plugin;
  }
}

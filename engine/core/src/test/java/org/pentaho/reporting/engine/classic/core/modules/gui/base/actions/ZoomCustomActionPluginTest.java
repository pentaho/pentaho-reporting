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

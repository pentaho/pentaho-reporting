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

import org.junit.Before;

public class AboutActionPluginTest extends ActionTestingUtil {

  private AboutActionPlugin plugin;

  @Before
  public void setUp() {
    super.setUp();
    plugin = new AboutActionPlugin();
  }

  @Override
  protected String getSmallIconKey() {
    return "action.about.small-icon";
  }

  @Override
  protected String getNameValue() {
    return "test about name";
  }

  @Override
  protected String getDescriptionValue() {
    return "test about description";
  }

  @Override
  protected String getLargeIconKey() {
    return "action.about.icon";
  }

  @Override
  protected ControlActionPlugin getPlugin() {
    return plugin;
  }

  @Override
  protected String getExpectedPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.about.";
  }

  @Override
  protected String getPrefix() {
    return plugin.getConfigurationPrefix();
  }

}

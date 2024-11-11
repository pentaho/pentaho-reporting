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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;

public class ExitActionPluginTest extends ActionTestingUtil {

  private ExitActionPlugin plugin;

  @Before
  public void setUp() {
    super.setUp();
    plugin = new ExitActionPlugin();
  }

  @Test
  public void testConfigure() {
    plugin.initialize( context );
    PreviewPane reportPane = mock( PreviewPane.class );

    boolean result = plugin.configure( reportPane );

    verify( reportPane ).setClosed( true );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Override
  protected String getSmallIconKey() {
    return "action.close.small-icon";
  }

  @Override
  protected String getNameValue() {
    return "test close name";
  }

  @Override
  protected String getDescriptionValue() {
    return "test close description";
  }

  @Override
  protected String getLargeIconKey() {
    return "action.close.icon";
  }

  @Override
  protected ControlActionPlugin getPlugin() {
    return plugin;
  }

  @Override
  protected String getExpectedPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.close.";
  }

  @Override
  protected String getPrefix() {
    return plugin.getConfigurationPrefix();
  }
}

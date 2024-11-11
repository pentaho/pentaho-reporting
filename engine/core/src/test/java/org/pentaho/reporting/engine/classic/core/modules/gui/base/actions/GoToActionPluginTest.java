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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportEventSource;

public class GoToActionPluginTest extends ActionTestingUtil {

  private GoToActionPlugin plugin;
  protected ReportEventSource source;

  @Before
  public void setUp() {
    super.setUp();
    source = mock( ReportEventSource.class );
    doReturn( source ).when( context ).getEventSource();
    plugin = new GoToActionPlugin();
  }

  @Override
  protected String getSmallIconKey() {
    return "action.gotopage.small-icon";
  }

  @Override
  protected String getNameValue() {
    return "test gotopage name";
  }

  @Override
  protected String getDescriptionValue() {
    return "test gotopage description";
  }

  @Override
  protected String getLargeIconKey() {
    return "action.gotopage.icon";
  }

  @Override
  protected String getExpectedPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.go-to.";
  }

  @Override
  protected String getPrefix() {
    return plugin.getConfigurationPrefix();
  }

  @Override
  protected ControlActionPlugin getPlugin() {
    return plugin;
  }

  @Test
  public void testDeinitialize() {
    plugin.deinitialize( context );
    verify( source ).removePropertyChangeListener( any( PropertyChangeListener.class ) );
  }
}

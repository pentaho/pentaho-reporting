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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportEventSource;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class ZoomListActionPluginTest {

  private ZoomListActionPlugin plugin;
  protected ReportEventSource source;
  protected SwingGuiContext context;

  @Before
  public void setUp() {
    Locale locale = new Locale( "test_test" );
    Configuration conf = mock( Configuration.class );
    context = mock( SwingGuiContext.class );
    source = mock( ReportEventSource.class );
    doReturn( source ).when( context ).getEventSource();
    doReturn( conf ).when( context ).getConfiguration();
    doReturn( locale ).when( context ).getLocale();
    plugin = new ZoomListActionPlugin();
  }

  @Test
  public void testInitialize() {
    boolean result = plugin.initialize( context );
    assertThat( result, is( equalTo( true ) ) );
    verify( source ).addPropertyChangeListener( anyString(), any( PropertyChangeListener.class ) );

    assertThat( plugin.getConfigurationPrefix(),
        is( equalTo( "org.pentaho.reporting.engine.classic.core.modules.gui.base.zoom-list." ) ) );
    assertThat( plugin.getDisplayName(), is( nullValue() ) );
    assertThat( plugin.getShortDescription(), is( nullValue() ) );
    assertThat( plugin.getSmallIcon(), is( nullValue() ) );
    assertThat( plugin.getLargeIcon(), is( nullValue() ) );
    assertThat( plugin.getAcceleratorKey(), is( nullValue() ) );
    assertThat( plugin.getMnemonicKey(), is( nullValue() ) );
  }

  @Test
  public void testDeinitialize() {
    plugin.deinitialize( context );
    verify( source ).removePropertyChangeListener( anyString(), any( PropertyChangeListener.class ) );
  }

}

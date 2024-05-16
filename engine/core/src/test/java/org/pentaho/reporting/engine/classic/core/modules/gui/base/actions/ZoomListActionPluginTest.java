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
 * Copyright (c) 2000 - 2024 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

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

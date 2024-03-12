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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportEventSource;

public class ZoomOutActionPluginTest extends ActionTestingUtil {

  private ZoomOutActionPlugin plugin;
  protected ReportEventSource source;

  @Before
  public void setUp() {
    super.setUp();
    source = mock( ReportEventSource.class );
    doReturn( source ).when( context ).getEventSource();
    plugin = new ZoomOutActionPlugin();
  }

  @Override
  protected String getSmallIconKey() {
    return "action.zoomOut.small-icon";
  }

  @Override
  protected String getNameValue() {
    return "test zoomout name";
  }

  @Override
  protected String getDescriptionValue() {
    return "test zoomout description";
  }

  @Override
  protected String getLargeIconKey() {
    return "action.zoomOut.icon";
  }

  @Override
  protected String getExpectedPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.zoom-out.";
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
  public void testConfigure() {
    PreviewPane reportPane = mock( PreviewPane.class );
    doReturn( 5.5 ).when( reportPane ).getZoom();
    doReturn( new double[] { 5.0, 5.5, 10.4, 15.0 } ).when( reportPane ).getZoomFactors();
    boolean result = plugin.configure( reportPane );
    verify( reportPane ).setZoom( 5.0 );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testDeinitialize() {
    plugin.deinitialize( context );
    verify( source ).removePropertyChangeListener( any( PropertyChangeListener.class ) );
  }
}

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

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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;

public class ZoomActionTest {

  private static final double ZOOM = 10.0;

  private ZoomAction action;
  private PreviewPane previewPane;

  @Before
  public void setUp() {
    MasterReport report = mock( MasterReport.class );
    previewPane = mock( PreviewPane.class );
    doReturn( new Locale( "test_test" ) ).when( previewPane ).getLocale();
    doReturn( report ).when( previewPane ).getReportJob();
    action = new ZoomAction( ZOOM, previewPane );
    assertThat( action.isEnabled(), is( equalTo( true ) ) );
  }

  @Test
  public void testActionPerformed() {
    action.actionPerformed( null );
    verify( previewPane ).setZoom( ZOOM );
  }

  @Test
  public void testDeinitialize() {
    action.deinitialize();
    verify( previewPane ).removePropertyChangeListener( anyString(), any( PropertyChangeListener.class ) );
  }
}

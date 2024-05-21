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

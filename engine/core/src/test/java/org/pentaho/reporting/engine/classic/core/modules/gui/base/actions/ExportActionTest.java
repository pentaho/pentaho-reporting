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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExportActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;

public class ExportActionTest {

  private ExportAction action;
  private ExportActionPlugin actionPlugin;
  private PreviewPane previewPane;

  @Before
  public void setUp() {
    actionPlugin = mock( ExportActionPlugin.class );
    previewPane = mock( PreviewPane.class );
  }

  @Test( expected = NullPointerException.class )
  public void testExportActionWithoutPlugin() {
    action = new ExportAction( null, null );
  }

  @Test( expected = NullPointerException.class )
  public void testExportActionWithoutPane() {
    ExportActionPlugin actionPlugin = mock( ExportActionPlugin.class );
    action = new ExportAction( actionPlugin, null );
  }

  @Test
  public void testExportAction() {
    Icon smallIcon = mock( Icon.class );
    Icon largeIcon = mock( Icon.class );
    ArgumentCaptor<String> propCaptor = ArgumentCaptor.forClass( String.class );

    doReturn( true ).when( actionPlugin ).isEnabled();
    doNothing().when( actionPlugin ).addPropertyChangeListener( propCaptor.capture(),
        any( PropertyChangeListener.class ) );
    doReturn( "Action name" ).when( actionPlugin ).getDisplayName();
    doReturn( "Action description" ).when( actionPlugin ).getShortDescription();
    doReturn( null ).when( actionPlugin ).getAcceleratorKey();
    doReturn( 65 ).when( actionPlugin ).getMnemonicKey();
    doReturn( smallIcon ).when( actionPlugin ).getSmallIcon();
    doReturn( largeIcon ).when( actionPlugin ).getLargeIcon();

    action = new ExportAction( actionPlugin, previewPane );

    assertThat( action.isEnabled(), is( equalTo( true ) ) );
    verify( actionPlugin ).addPropertyChangeListener( anyString(), any( PropertyChangeListener.class ) );
    assertThat( propCaptor.getValue(), is( equalTo( "enabled" ) ) );
    assertThat( (String) action.getValue( Action.NAME ), is( equalTo( "Action name" ) ) );
    assertThat( (String) action.getValue( Action.SHORT_DESCRIPTION ), is( equalTo( "Action description" ) ) );
    assertThat( action.getValue( Action.ACCELERATOR_KEY ), is( nullValue() ) );
    assertThat( (Integer) action.getValue( Action.MNEMONIC_KEY ), is( equalTo( 65 ) ) );
    assertThat( (Icon) action.getValue( Action.SMALL_ICON ), is( equalTo( smallIcon ) ) );
    assertThat( (Icon) action.getValue( SwingCommonModule.LARGE_ICON_PROPERTY ), is( equalTo( largeIcon ) ) );
  }

  @Test
  public void testPerformAction() {
    action = new ExportAction( actionPlugin, previewPane );

    doReturn( false ).when( actionPlugin ).isEnabled();
    action.actionPerformed( null );
    verify( actionPlugin, never() ).performExport( any( MasterReport.class ) );

    doReturn( true ).when( actionPlugin ).isEnabled();
    doReturn( null ).when( previewPane ).getReportJob();
    action.actionPerformed( null );
    verify( actionPlugin, never() ).performExport( any( MasterReport.class ) );

    MasterReport report = mock( MasterReport.class );
    doReturn( report ).when( previewPane ).getReportJob();
    doReturn( report ).when( report ).clone();
    action.actionPerformed( null );
    verify( actionPlugin ).performExport( report );
  }
}

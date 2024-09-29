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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;

public class ControlActionTest {

  private ControlAction action;
  private ControlActionPlugin actionPlugin;
  private PreviewPane previewPane;

  @Before
  public void setUp() {
    actionPlugin = mock( ControlActionPlugin.class );
    previewPane = mock( PreviewPane.class );
  }

  @Test( expected = NullPointerException.class )
  public void testControlActionWithoutPlugin() {
    action = new ControlAction( null, null );
  }

  @Test( expected = NullPointerException.class )
  public void testControlActionWithoutPane() {
    ControlActionPlugin actionPlugin = mock( ControlActionPlugin.class );
    action = new ControlAction( actionPlugin, null );
  }

  @Test
  public void testControlAction() {
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

    action = new ControlAction( actionPlugin, previewPane );

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
    action = new ControlAction( actionPlugin, previewPane );
    action.actionPerformed( null );

    verify( actionPlugin ).configure( previewPane );
  }
}

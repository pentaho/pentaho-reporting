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


package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;

public class LoadReportFromRepositoryActionTest {

  @Before
  public void setUp() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @After
  public void tearDown() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @Test
  public void testLoadReportFromRepositoryAction() {
    LoadReportFromRepositoryAction repoAction = new LoadReportFromRepositoryAction();
    assertEquals( Messages.getInstance().getString( "LoadReportFromRepositoryAction.Text" ),
      repoAction.getValue( Action.NAME ) );
    assertEquals( Messages.getInstance().getString( "LoadReportFromRepositoryAction.Description" ),
      repoAction.getValue( Action.SHORT_DESCRIPTION ) );
    assertEquals( IconLoader.getInstance().getOpenIcon(), repoAction.getValue( Action.SMALL_ICON ) );
    assertEquals( Messages.getInstance().getOptionalKeyStroke( "LoadReportFromRepositoryAction.Accelerator" ),
      repoAction.getValue( Action.ACCELERATOR_KEY ) );
  }

  @Test
  public void testActionPerformedWithNullDesignerContext() {
    LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
    // No designer context set → immediate return
    action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
    // No crash
    assertNotNull( action );
  }

  @Test
  public void testActionPerformedWithCachedSessionUsesIt() {
    LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    action.setReportDesignerContext( ctx );

    AuthenticationData cachedSession = mock( AuthenticationData.class );
    when( cachedSession.getOption( "browserAuth" ) ).thenReturn( "true" );
    when( cachedSession.getUsername() ).thenReturn( "admin" );
    RepositorySessionManager.getInstance().setSession( cachedSession, "admin" );

    try ( var swingMock = mockStatic( SwingUtilities.class );
          var sessMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      sessMock.when( () -> SessionAuthenticationUtil.isSessionExplicitlyExpired( cachedSession ) )
        .thenReturn( false );
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      swingMock.verify( () -> SwingUtilities.invokeLater( any( OpenFileFromRepositoryTask.class ) ) );
    }
  }

  @Test
  public void testActionPerformedUsernamePasswordCachedSessionSkipsProbe() {
    LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    action.setReportDesignerContext( ctx );

    AuthenticationData cachedSession = mock( AuthenticationData.class );
    // No browserAuth option => username/password
    when( cachedSession.getOption( "browserAuth" ) ).thenReturn( null );
    RepositorySessionManager.getInstance().setSession( cachedSession, "admin" );

    try ( var swingMock = mockStatic( SwingUtilities.class );
          var sessMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      // Probe must NOT have been invoked for username/password
      sessMock.verifyNoInteractions();
      // Open task must run with the cached session
      swingMock.verify( () -> SwingUtilities.invokeLater( any( OpenFileFromRepositoryTask.class ) ) );
    }
  }

  @Test
  public void testActionPerformedWithCachedSessionUnknownValidityProceeds() {
    LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    action.setReportDesignerContext( ctx );

    AuthenticationData cachedSession = mock( AuthenticationData.class );
    when( cachedSession.getOption( "browserAuth" ) ).thenReturn( "true" );
    RepositorySessionManager.getInstance().setSession( cachedSession, "admin" );

    try ( var swingMock = mockStatic( SwingUtilities.class );
          var sessMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      // unknown / network error -> not explicitly expired
      sessMock.when( () -> SessionAuthenticationUtil.isSessionExplicitlyExpired( cachedSession ) )
        .thenReturn( false );
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      // proceeds with cached session, not re-login
      swingMock.verify( () -> SwingUtilities.invokeLater( any( OpenFileFromRepositoryTask.class ) ) );
    }
  }

  @Test
  public void testActionPerformedExpiredSsoSessionShowsDialogAndReLogs() {
    LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getGlobalAuthenticationStore() ).thenReturn( mock( GlobalAuthenticationStore.class ) );
    action.setReportDesignerContext( ctx );

    AuthenticationData cachedSession = mock( AuthenticationData.class );
    when( cachedSession.getOption( "browserAuth" ) ).thenReturn( "true" );
    RepositorySessionManager.getInstance().setSession( cachedSession, "sso-user" );

    try ( var swingMock = mockStatic( SwingUtilities.class );
          var sessMock = mockStatic( SessionAuthenticationUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {
      sessMock.when( () -> SessionAuthenticationUtil.isSessionExplicitlyExpired( cachedSession ) )
        .thenReturn( true );
      jopMock.when( () -> JOptionPane.showOptionDialog(
          any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.YES_OPTION );
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      jopMock.verify( () -> JOptionPane.showOptionDialog(
          any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) );
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
      swingMock.verify( () -> SwingUtilities.invokeLater( any( LoginTask.class ) ) );
    }
  }

  @Test
  public void testActionPerformedExpiredSsoSessionUserCancelsDoesNothing() {
    LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    action.setReportDesignerContext( ctx );

    AuthenticationData cachedSession = mock( AuthenticationData.class );
    when( cachedSession.getOption( "browserAuth" ) ).thenReturn( "true" );
    RepositorySessionManager.getInstance().setSession( cachedSession, "sso-user" );

    try ( var swingMock = mockStatic( SwingUtilities.class );
          var sessMock = mockStatic( SessionAuthenticationUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {
      sessMock.when( () -> SessionAuthenticationUtil.isSessionExplicitlyExpired( cachedSession ) )
        .thenReturn( true );
      jopMock.when( () -> JOptionPane.showOptionDialog(
          any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.NO_OPTION );
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      swingMock.verifyNoInteractions();
      assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testActionPerformedWithNoCachedSessionCreatesLoginTask() {
    LoadReportFromRepositoryAction action = new LoadReportFromRepositoryAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getGlobalAuthenticationStore() ).thenReturn( mock( GlobalAuthenticationStore.class ) );
    action.setReportDesignerContext( ctx );

    try ( var swingMock = mockStatic( SwingUtilities.class ) ) {
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      // Verify invokeLater was called with a LoginTask
      swingMock.verify( () -> SwingUtilities.invokeLater( any( LoginTask.class ) ) );
    }
  }
}

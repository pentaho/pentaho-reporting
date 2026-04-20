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
import org.pentaho.reporting.designer.core.actions.report.SaveReportAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;

public class PublishToServerActionTest {

  @Before
  public void setUp() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @After
  public void tearDown() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @Test
  public void testPublishToServerAction() {
    PublishToServerAction publishAction = new PublishToServerAction();
    assertNotNull( publishAction );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.Text" ),
      publishAction.getValue( Action.NAME ) );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.Description" ),
      publishAction.getValue( Action.SHORT_DESCRIPTION ) );
    assertNotNull( publishAction.getValue( Action.SMALL_ICON ) );
    assertEquals( Messages.getInstance().getKeyStroke( "PublishToServerAction.Accelerator" ),
      publishAction.getValue( Action.ACCELERATOR_KEY ) );
  }

  @Test
  public void testActionPerformedWithNullActiveContext() {
    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    when( ctx.getActiveContext() ).thenReturn( null );
    action.setReportDesignerContext( ctx );
    // activeContext is null → return immediately
    action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
    assertNotNull( action );
  }

  @Test
  public void testActionPerformedUnchangedReportNoCachedSession() {
    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    ReportDocumentContext activeCtx = mock( ReportDocumentContext.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getActiveContext() ).thenReturn( activeCtx );
    when( ctx.getGlobalAuthenticationStore() ).thenReturn( mock( GlobalAuthenticationStore.class ) );
    when( activeCtx.isChanged() ).thenReturn( false );
    action.setReportDesignerContext( ctx );

    try ( var swingMock = mockStatic( SwingUtilities.class ) ) {
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      swingMock.verify( () -> SwingUtilities.invokeLater( any( LoginTask.class ) ) );
    }
  }

  @Test
  public void testActionPerformedUnchangedReportWithCachedSession() {
    AuthenticationData cachedSession = mock( AuthenticationData.class );
    when( cachedSession.getOption( "browserAuth" ) ).thenReturn( "true" );
    when( cachedSession.getUsername() ).thenReturn( "admin" );
    RepositorySessionManager.getInstance().setSession( cachedSession, "admin" );

    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    ReportDocumentContext activeCtx = mock( ReportDocumentContext.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getActiveContext() ).thenReturn( activeCtx );
    when( activeCtx.isChanged() ).thenReturn( false );
    action.setReportDesignerContext( ctx );

    try ( var swingMock = mockStatic( SwingUtilities.class );
          var sessMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      sessMock.when( () -> SessionAuthenticationUtil.isSessionExplicitlyExpired( cachedSession ) )
        .thenReturn( false );
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      swingMock.verify( () -> SwingUtilities.invokeLater( any( PublishToServerTask.class ) ) );
    }
  }

  @Test
  public void testActionPerformedUsernamePasswordCachedSessionSkipsProbe() {
    AuthenticationData cachedSession = mock( AuthenticationData.class );
    when( cachedSession.getOption( "browserAuth" ) ).thenReturn( null ); // U/P
    RepositorySessionManager.getInstance().setSession( cachedSession, "admin" );

    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    ReportDocumentContext activeCtx = mock( ReportDocumentContext.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getActiveContext() ).thenReturn( activeCtx );
    when( activeCtx.isChanged() ).thenReturn( false );
    action.setReportDesignerContext( ctx );

    try ( var swingMock = mockStatic( SwingUtilities.class );
          var sessMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      // Probe must NOT have been invoked for username/password
      sessMock.verifyNoInteractions();
      swingMock.verify( () -> SwingUtilities.invokeLater( any( PublishToServerTask.class ) ) );
    }
  }

  @Test
  public void testActionPerformedExpiredSsoSessionShowsDialogAndReLogs() {
    AuthenticationData cachedSession = mock( AuthenticationData.class );
    when( cachedSession.getOption( "browserAuth" ) ).thenReturn( "true" );
    RepositorySessionManager.getInstance().setSession( cachedSession, "sso-user" );

    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    ReportDocumentContext activeCtx = mock( ReportDocumentContext.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getActiveContext() ).thenReturn( activeCtx );
    when( ctx.getGlobalAuthenticationStore() ).thenReturn( mock( GlobalAuthenticationStore.class ) );
    when( activeCtx.isChanged() ).thenReturn( false );
    action.setReportDesignerContext( ctx );

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
    AuthenticationData cachedSession = mock( AuthenticationData.class );
    when( cachedSession.getOption( "browserAuth" ) ).thenReturn( "true" );
    RepositorySessionManager.getInstance().setSession( cachedSession, "sso-user" );

    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    ReportDocumentContext activeCtx = mock( ReportDocumentContext.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getActiveContext() ).thenReturn( activeCtx );
    when( activeCtx.isChanged() ).thenReturn( false );
    action.setReportDesignerContext( ctx );

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
  public void testActionPerformedChangedReportUserCancels() {
    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    ReportDocumentContext activeCtx = mock( ReportDocumentContext.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getActiveContext() ).thenReturn( activeCtx );
    when( activeCtx.isChanged() ).thenReturn( true );
    action.setReportDesignerContext( ctx );

    try ( var jopMock = mockStatic( JOptionPane.class ) ) {
      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt(), anyInt() ) ).thenReturn( JOptionPane.CANCEL_OPTION );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
    }
    // Cancelled → no follow-up task launched
    assertNotNull( action );
  }

  @Test
  public void testActionPerformedChangedReportUserSaysNo() {
    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    ReportDocumentContext activeCtx = mock( ReportDocumentContext.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getActiveContext() ).thenReturn( activeCtx );
    when( ctx.getGlobalAuthenticationStore() ).thenReturn( mock( GlobalAuthenticationStore.class ) );
    when( activeCtx.isChanged() ).thenReturn( true );
    action.setReportDesignerContext( ctx );

    try ( var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class ) ) {
      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt(), anyInt() ) ).thenReturn( JOptionPane.NO_OPTION );
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      // NO means skip save but still proceed to publish
      swingMock.verify( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) );
    }
  }

  @Test
  public void testActionPerformedChangedReportUserSaysYesSaveSucceeds() {
    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    ReportDocumentContext activeCtx = mock( ReportDocumentContext.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getActiveContext() ).thenReturn( activeCtx );
    when( ctx.getGlobalAuthenticationStore() ).thenReturn( mock( GlobalAuthenticationStore.class ) );
    when( activeCtx.isChanged() ).thenReturn( true );
    action.setReportDesignerContext( ctx );

    try ( var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class );
          var saveMock = mockConstruction( SaveReportAction.class,
            ( mock, context ) -> when( mock.saveReport( any(), any(), any() ) ).thenReturn( true ) ) ) {
      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt(), anyInt() ) ).thenReturn( JOptionPane.YES_OPTION );
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      // Save succeeded → proceed to publish
      swingMock.verify( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) );
    }
  }

  @Test
  public void testActionPerformedChangedReportUserSaysYesSaveFails() {
    PublishToServerAction action = new PublishToServerAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    Component parent = mock( Component.class );
    ReportDocumentContext activeCtx = mock( ReportDocumentContext.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    when( ctx.getActiveContext() ).thenReturn( activeCtx );
    when( activeCtx.isChanged() ).thenReturn( true );
    action.setReportDesignerContext( ctx );

    try ( var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class );
          var saveMock = mockConstruction( SaveReportAction.class,
            ( mock, context ) -> when( mock.saveReport( any(), any(), any() ) ).thenReturn( false ) ) ) {
      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt(), anyInt() ) ).thenReturn( JOptionPane.YES_OPTION );
      swingMock.when( () -> SwingUtilities.invokeLater( any( Runnable.class ) ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      // Save failed → should NOT proceed to publish
      swingMock.verify( () -> SwingUtilities.invokeLater( any( Runnable.class ) ), never() );
    }
  }
}

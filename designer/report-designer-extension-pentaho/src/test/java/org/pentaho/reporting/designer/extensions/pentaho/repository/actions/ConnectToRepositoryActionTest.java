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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.BrowserLoginHandler;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;

public class ConnectToRepositoryActionTest {

  private static final String ADMIN_USERNAME = "admin";

  @Before
  public void setUp() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @After
  public void tearDown() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @Test
  public void testConstructorSetsNameAndDescription() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    assertNotNull( action );
    assertEquals( Messages.getInstance().getString( "ConnectToRepositoryAction.Text" ),
      action.getValue( Action.NAME ) );
    assertEquals( Messages.getInstance().getString( "ConnectToRepositoryAction.Description" ),
      action.getValue( Action.SHORT_DESCRIPTION ) );
  }

  @Test
  public void testEnabledWhenNoActiveSession() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    assertTrue( action.isEnabled() );
  }

  @Test
  public void testDisabledWhenActiveSession() {
    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    assertFalse( action.isEnabled() );
  }

  @Test
  public void testPropertyChangeDisablesOnSessionSet() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    assertTrue( action.isEnabled() );

    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

    assertFalse( action.isEnabled() );
  }

  @Test
  public void testPropertyChangeEnablesOnSessionClear() {
    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    assertFalse( action.isEnabled() );

    RepositorySessionManager.getInstance().clearSession();
    assertTrue( action.isEnabled() );
  }

  @Test
  public void testUpdateDesignerContextNullDisables() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    action.updateDesignerContext( null, null );
    assertFalse( action.isEnabled() );
  }

  @Test
  public void testUpdateDesignerContextNonNullEnablesWhenNoSession() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    action.updateDesignerContext( null, mock( ReportDesignerContext.class ) );
    assertTrue( action.isEnabled() );
  }

  @Test
  public void testUpdateDesignerContextNonNullDisablesWhenSession() {
    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    action.updateDesignerContext( null, mock( ReportDesignerContext.class ) );
    assertFalse( action.isEnabled() );
  }

  @Test
  public void testDescriptionSetInConstructor() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    assertEquals( Messages.getInstance().getString( "ConnectToRepositoryAction.Description" ),
      action.getValue( Action.SHORT_DESCRIPTION ) );
  }

  @Test
  public void testNameSetInConstructor() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    assertEquals( Messages.getInstance().getString( "ConnectToRepositoryAction.Text" ),
      action.getValue( Action.NAME ) );
  }

  @Test
  public void testMultipleSessionChangesToggleEnabled() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    assertTrue( action.isEnabled() );

    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );
    assertFalse( action.isEnabled() );

    RepositorySessionManager.getInstance().clearSession();
    assertTrue( action.isEnabled() );

    RepositorySessionManager.getInstance().setSession( session, "user2" );
    assertFalse( action.isEnabled() );
  }

  // ---- actionPerformed coverage ----

  @Test
  public void testActionPerformedReturnsEarlyOnNullContext() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    // No designer context set → null path
    action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void testActionPerformedReturnsWhenDialogReturnsNull() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    action.setReportDesignerContext( newMockContext() );

    try ( var construction = mockConstruction( RepositoryLoginDialog.class, ( mock, ctx ) -> {
      when( mock.performLogin( any(), any() ) ).thenReturn( null );
    } ) ) {
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );
      assertFalse( construction.constructed().isEmpty() );
    }
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void testActionPerformedSsoBranchSuccess() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    action.setReportDesignerContext( newMockContext() );

    AuthenticationData dialogData = mock( AuthenticationData.class );
    when( dialogData.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );
    when( dialogData.getUsername() ).thenReturn( ADMIN_USERNAME );

    AuthenticationData ssoResult = mock( AuthenticationData.class );
    when( ssoResult.getUsername() ).thenReturn( ADMIN_USERNAME );

    try ( var dialogCtor = mockConstruction( RepositoryLoginDialog.class, ( mock, ctx ) -> {
            when( mock.performLogin( any(), any() ) ).thenReturn( dialogData );
            when( mock.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.SSO );
            when( mock.getSelectedOAuthProvider() ).thenReturn( null );
          } );
          var browserMock = mockStatic( BrowserLoginHandler.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {
      browserMock.when( () -> BrowserLoginHandler.performBrowserLoginWithRetry( any(), any(), any() ) )
        .thenReturn( ssoResult );
      jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );
      assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testActionPerformedSsoBranchCancelled() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    action.setReportDesignerContext( newMockContext() );

    AuthenticationData dialogData = mock( AuthenticationData.class );
    when( dialogData.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );

    try ( var dialogCtor = mockConstruction( RepositoryLoginDialog.class, ( mock, ctx ) -> {
            when( mock.performLogin( any(), any() ) ).thenReturn( dialogData );
            when( mock.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.SSO );
            when( mock.getSelectedOAuthProvider() ).thenReturn( null );
          } );
          var browserMock = mockStatic( BrowserLoginHandler.class ) ) {
      browserMock.when( () -> BrowserLoginHandler.performBrowserLoginWithRetry( any(), any(), any() ) )
        .thenReturn( null ); // user cancelled

      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testActionPerformedUserPassValidationSuccess() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    action.setReportDesignerContext( newMockContext() );

    AuthenticationData dialogData = mock( AuthenticationData.class );
    when( dialogData.getUsername() ).thenReturn( ADMIN_USERNAME );

    try ( var dialogCtor = mockConstruction( RepositoryLoginDialog.class, ( mock, ctx ) -> {
            when( mock.performLogin( any(), any() ) ).thenReturn( dialogData );
            when( mock.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
          } );
          var sessionAuthMock = mockStatic( SessionAuthenticationUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {
      sessionAuthMock.when( () -> SessionAuthenticationUtil.isServerReachable( any() ) ).thenReturn( true );
      jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );
      assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testActionPerformedUserPassValidationFailureShowsError() {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    action.setReportDesignerContext( newMockContext() );

    AuthenticationData dialogData = mock( AuthenticationData.class );
    when( dialogData.getUsername() ).thenReturn( ADMIN_USERNAME );

    try ( var dialogCtor = mockConstruction( RepositoryLoginDialog.class, ( mock, ctx ) -> {
            when( mock.performLogin( any(), any() ) ).thenReturn( dialogData );
            when( mock.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
          } );
          var sessionAuthMock = mockStatic( SessionAuthenticationUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {
      sessionAuthMock.when( () -> SessionAuthenticationUtil.isServerReachable( any() ) ).thenReturn( false );
      jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );
      // Failure path: no session is stored, but a dialog is shown.
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
      jopMock.verify( () -> JOptionPane.showMessageDialog( any(), any(), any(), eq( JOptionPane.ERROR_MESSAGE ) ) );
    }
  }

  // ---- private helper coverage via reflection ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testValidateCredentialsDelegatesToSessionAuthenticationUtil() throws Exception {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    Method m = ConnectToRepositoryAction.class.getDeclaredMethod( "validateCredentials", AuthenticationData.class );
    m.setAccessible( true );
    AuthenticationData ad = mock( AuthenticationData.class );
    try ( var sessionAuthMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      sessionAuthMock.when( () -> SessionAuthenticationUtil.isServerReachable( ad ) ).thenReturn( true );
      assertEquals( true, m.invoke( action, ad ) );
      sessionAuthMock.when( () -> SessionAuthenticationUtil.isServerReachable( ad ) ).thenReturn( false );
      assertEquals( false, m.invoke( action, ad ) );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCreateLoginDialogFrameBranch() throws Exception {
    Method m = ConnectToRepositoryAction.class.getDeclaredMethod( "createLoginDialog", Window.class );
    m.setAccessible( true );
    Frame frame = mock( Frame.class );
    try ( var ctor = mockConstruction( RepositoryLoginDialog.class ) ) {
      Object dialog = m.invoke( null, frame );
      assertNotNull( dialog );
      assertEquals( 1, ctor.constructed().size() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCreateLoginDialogDialogBranch() throws Exception {
    Method m = ConnectToRepositoryAction.class.getDeclaredMethod( "createLoginDialog", Window.class );
    m.setAccessible( true );
    Dialog dialog = mock( Dialog.class );
    try ( var ctor = mockConstruction( RepositoryLoginDialog.class ) ) {
      Object result = m.invoke( null, dialog );
      assertNotNull( result );
      assertEquals( 1, ctor.constructed().size() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCreateLoginDialogNullBranch() throws Exception {
    Method m = ConnectToRepositoryAction.class.getDeclaredMethod( "createLoginDialog", Window.class );
    m.setAccessible( true );
    try ( var ctor = mockConstruction( RepositoryLoginDialog.class ) ) {
      Object result = m.invoke( null, (Window) null );
      assertNotNull( result );
      assertEquals( 1, ctor.constructed().size() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testStoreSessionWithNullUsername() throws Exception {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    ReportDesignerContext ctx = newMockContext();
    action.setReportDesignerContext( ctx );
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUsername() ).thenReturn( null );
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.isRememberSettings() ).thenReturn( false );
    Method m = ConnectToRepositoryAction.class.getDeclaredMethod(
      "storeSession", ReportDesignerContext.class, RepositoryLoginDialog.class, AuthenticationData.class );
    m.setAccessible( true );
    try ( var jopMock = mockStatic( JOptionPane.class ) ) {
      jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );
      m.invoke( action, ctx, dlg, ad );
    }
    assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testStoreSessionRememberSsoDoesNotTouchAuthStore() throws Exception {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    ReportDesignerContext ctx = newMockContext();
    GlobalAuthenticationStore store = mock( GlobalAuthenticationStore.class );
    when( ctx.getGlobalAuthenticationStore() ).thenReturn( store );
    action.setReportDesignerContext( ctx );

    AuthenticationData ssoLogin =
      new AuthenticationData( "http://srv/p", "ssoUser", "ssoPwd", 30 );
    ssoLogin.setOption( "browserAuth", "true" );
    ssoLogin.setOption( "sessionId", "SESS" );

    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.isRememberSettings() ).thenReturn( true );

    Method m = ConnectToRepositoryAction.class.getDeclaredMethod(
      "storeSession", ReportDesignerContext.class, RepositoryLoginDialog.class, AuthenticationData.class );
    m.setAccessible( true );
    try ( var jopMock = mockStatic( JOptionPane.class ) ) {
      jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );
      m.invoke( action, ctx, dlg, ssoLogin );
    }

    verify( store, never() ).add( any( AuthenticationData.class ), anyBoolean() );
    verify( store, never() ).removeCredentials( anyString() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testStoreSessionRememberNonSsoPersistsUsernameAndPassword() throws Exception {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    ReportDesignerContext ctx = newMockContext();
    GlobalAuthenticationStore store = mock( GlobalAuthenticationStore.class );
    when( ctx.getGlobalAuthenticationStore() ).thenReturn( store );
    action.setReportDesignerContext( ctx );

    AuthenticationData typed =
      new AuthenticationData( "http://srv/p", ADMIN_USERNAME, "secret", 30 );

    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.isRememberSettings() ).thenReturn( true );

    Method m = ConnectToRepositoryAction.class.getDeclaredMethod(
      "storeSession", ReportDesignerContext.class, RepositoryLoginDialog.class, AuthenticationData.class );
    m.setAccessible( true );
    try ( var jopMock = mockStatic( JOptionPane.class ) ) {
      jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );
      m.invoke( action, ctx, dlg, typed );
    }

    verify( store ).add( same( typed ), eq( true ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testStoreSessionRememberNonSsoAlsoUpdatesActiveReportStore() throws Exception {
    ConnectToRepositoryAction action = new ConnectToRepositoryAction();
    ReportDesignerContext ctx = newMockContext();
    GlobalAuthenticationStore store = mock( GlobalAuthenticationStore.class );
    when( ctx.getGlobalAuthenticationStore() ).thenReturn( store );

    org.pentaho.reporting.designer.core.editor.ReportDocumentContext active =
      mock( org.pentaho.reporting.designer.core.editor.ReportDocumentContext.class );
    org.pentaho.reporting.designer.core.auth.AuthenticationStore reportStore =
      mock( org.pentaho.reporting.designer.core.auth.AuthenticationStore.class );
    when( ctx.getActiveContext() ).thenReturn( active );
    when( active.getAuthenticationStore() ).thenReturn( reportStore );
    action.setReportDesignerContext( ctx );

    AuthenticationData typed =
      new AuthenticationData( "http://srv/p", ADMIN_USERNAME, "secret", 30 );

    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.isRememberSettings() ).thenReturn( true );

    Method m = ConnectToRepositoryAction.class.getDeclaredMethod(
      "storeSession", ReportDesignerContext.class, RepositoryLoginDialog.class, AuthenticationData.class );
    m.setAccessible( true );
    try ( var jopMock = mockStatic( JOptionPane.class ) ) {
      jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );
      m.invoke( action, ctx, dlg, typed );
    }

    verify( store ).add( same( typed ), eq( true ) );
    verify( reportStore ).add( same( typed ), eq( true ) );
  }

  private static ReportDesignerContext newMockContext() {
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    ReportDesignerView view = mock( ReportDesignerView.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( mock( Component.class ) );
    return ctx;
  }
}

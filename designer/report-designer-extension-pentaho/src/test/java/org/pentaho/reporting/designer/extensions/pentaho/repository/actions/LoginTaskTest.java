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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.BrowserLoginHandler;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProvider;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

public class LoginTaskTest {

  ReportDesignerContext reportDesignerContext;
  Component component;
  AuthenticatedServerTask authServerTask;
  GlobalAuthenticationStore globalAuthStore;
  AuthenticationData loginData;

  @Before
  public void setUp() {
    reportDesignerContext = mock( ReportDesignerContext.class );
    component = mock( Component.class );
    authServerTask = mock( AuthenticatedServerTask.class );
    globalAuthStore = mock( GlobalAuthenticationStore.class );
    loginData = mock( AuthenticationData.class );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    when( globalAuthStore.getKnownURLs() ).thenReturn( new String[0] );
    when( loginData.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );
    RepositorySessionManager.getInstance().clearSession();
  }

  @After
  public void tearDown() {
    RepositorySessionManager.getInstance().clearSession();
  }

  private LoginTask newTask() {
    return new LoginTask( reportDesignerContext, component, authServerTask, loginData, false, false );
  }

  @SuppressWarnings( "java:S3011" )
  private static Object invoke( LoginTask task, String name ) throws Exception {
    Method m = LoginTask.class.getDeclaredMethod( name );
    m.setAccessible( true );
    return m.invoke( task );
  }

  @SuppressWarnings( "java:S3011" )
  private static void setField( LoginTask task, String name, Object value ) throws Exception {
    Field f = LoginTask.class.getDeclaredField( name );
    f.setAccessible( true );
    f.set( task, value );
  }

  // ===== Constructors =====

  @Test
  public void testThreeArgConstructor() {
    assertNotNull( new LoginTask( reportDesignerContext, component, authServerTask ) );
  }

  @Test
  public void testFourArgConstructor() {
    LoginTask t = new LoginTask( reportDesignerContext, component, authServerTask, loginData );
    assertEquals( loginData, t.getLoginData() );
  }

  @Test
  public void testFiveArgConstructor() {
    assertNotNull( new LoginTask( reportDesignerContext, component, authServerTask, loginData, true ) );
  }

  @Test
  public void testSixArgConstructor() {
    assertNotNull( new LoginTask( reportDesignerContext, component, authServerTask, loginData, true, true ) );
  }

  @Test( expected = NullPointerException.class )
  public void testNullDesignerContextThrows() {
    new LoginTask( null, component, authServerTask );
  }

  @Test( expected = NullPointerException.class )
  public void testNullUiContextThrows() {
    new LoginTask( reportDesignerContext, null, authServerTask );
  }

  @Test
  public void testNullFollowUpTaskAllowed() {
    assertNotNull( new LoginTask( reportDesignerContext, component, null ) );
  }

  @Test
  public void testNullLoginDataFallsBack() {
    doReturn( null ).when( reportDesignerContext ).getActiveContext();
    assertNotNull( new LoginTask( reportDesignerContext, component, authServerTask, null, false ) );
  }

  @Test
  public void testNullLoginDataWithActiveContext() {
    AuthenticationData stored = mock( AuthenticationData.class );
    AuthenticationStore ctxStore = mock( AuthenticationStore.class );
    ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    when( rdc.getAuthenticationStore() ).thenReturn( ctxStore );
    java.util.HashMap<String, Object> props = new java.util.HashMap<>();
    props.put( "pentaho-login-url", "http://ctx/p" );
    when( rdc.getProperties() ).thenReturn( props );
    when( reportDesignerContext.getActiveContext() ).thenReturn( rdc );

    try ( var rop = mockStatic( RepositoryLoginDialog.class ) ) {
      rop.when( () -> RepositoryLoginDialog.getStoredLoginData( "http://ctx/p", reportDesignerContext ) )
        .thenReturn( stored );
      assertNotNull( new LoginTask( reportDesignerContext, component, authServerTask ).getLoginData() );
    }
  }

  // ===== getRetryUrl =====

  @Test
  public void testGetRetryUrlReturnsUrl() throws Exception {
    when( loginData.getUrl() ).thenReturn( "http://srv/p" );
    assertEquals( "http://srv/p", invoke( newTask(), "getRetryUrl" ) );
  }

  @Test
  public void testGetRetryUrlFallsBackToDefault() throws Exception {
    when( loginData.getUrl() ).thenReturn( null );
    AuthenticationData dflt = mock( AuthenticationData.class );
    when( dflt.getUrl() ).thenReturn( "http://default/p" );
    when( globalAuthStore.getKnownURLs() ).thenReturn( new String[] { "http://default/p" } );
    when( globalAuthStore.getCredentials( "http://default/p" ) ).thenReturn( dflt );
    when( reportDesignerContext.getActiveContext() ).thenReturn( null );
    assertEquals( "http://default/p", invoke( newTask(), "getRetryUrl" ) );
  }

  @Test
  public void testGetRetryUrlHardcodedFallback() throws Exception {
    when( loginData.getUrl() ).thenReturn( "" );
    when( reportDesignerContext.getActiveContext() ).thenReturn( null );
    assertEquals( "http://localhost:8080/pentaho", invoke( newTask(), "getRetryUrl" ) );
  }

  @Test
  public void testGetRetryUrlFromActiveContext() throws Exception {
    when( loginData.getUrl() ).thenReturn( null );
    AuthenticationData dflt = mock( AuthenticationData.class );
    when( dflt.getUrl() ).thenReturn( "http://active/p" );
    AuthenticationStore ctxStore = mock( AuthenticationStore.class );
    when( ctxStore.getKnownURLs() ).thenReturn( new String[] { "http://active/p" } );
    when( ctxStore.getCredentials( "http://active/p" ) ).thenReturn( dflt );
    ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    when( rdc.getAuthenticationStore() ).thenReturn( ctxStore );
    when( reportDesignerContext.getActiveContext() ).thenReturn( rdc );
    assertEquals( "http://active/p", invoke( newTask(), "getRetryUrl" ) );
  }

  // ===== performRetryAfterAuthFailure =====

  @Test
  public void testRetryNullReturnsFalse() throws Exception {
    LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false, true );
    try ( var bh = mockStatic( BrowserLoginHandler.class ) ) {
      bh.when( () -> BrowserLoginHandler.recoverOAuthProvider( any() ) ).thenReturn( null );
      bh.when( () -> BrowserLoginHandler.performBrowserLoginWithRetry( any(), any(), any() ) ).thenReturn( null );
      assertFalse( (Boolean) invoke( task, "performRetryAfterAuthFailure" ) );
    }
  }

  @Test
  public void testRetrySuccessUpdatesLoginData() throws Exception {
    AuthenticationData newData = mock( AuthenticationData.class );
    LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false, true );
    try ( var bh = mockStatic( BrowserLoginHandler.class ) ) {
      bh.when( () -> BrowserLoginHandler.recoverOAuthProvider( any() ) ).thenReturn( null );
      bh.when( () -> BrowserLoginHandler.performBrowserLoginWithRetry( any(), any(), any() ) ).thenReturn( newData );
      assertTrue( (Boolean) invoke( task, "performRetryAfterAuthFailure" ) );
      assertSame( newData, task.getLoginData() );
    }
  }

  // ===== createLoginDialog =====

  @Test
  public void testCreateLoginDialog() throws Exception {
    try ( var ctor = mockConstruction( RepositoryLoginDialog.class ) ) {
      RepositoryLoginDialog dlg = (RepositoryLoginDialog) invoke( newTask(), "createLoginDialog" );
      assertNotNull( dlg );
      verify( dlg ).setDialogMode( RepositoryLoginDialog.DialogMode.FULL );
    }
  }

  // ===== performSSOLogin =====

  @Test
  public void testSSOLoginNullReturnsFalse() throws Exception {
    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.getSelectedOAuthProvider() ).thenReturn( mock( OAuthProvider.class ) );
    setField( task, "loginDialog", dlg );
    try ( var bh = mockStatic( BrowserLoginHandler.class ) ) {
      bh.when( () -> BrowserLoginHandler.performBrowserLoginWithRetry( any(), any(), any() ) ).thenReturn( null );
      assertFalse( (Boolean) invoke( task, "performSSOLogin" ) );
    }
  }

  @Test
  public void testSSOLoginSuccessReturnsTrue() throws Exception {
    AuthenticationData sso = mock( AuthenticationData.class );
    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.getSelectedOAuthProvider() ).thenReturn( mock( OAuthProvider.class ) );
    setField( task, "loginDialog", dlg );
    try ( var bh = mockStatic( BrowserLoginHandler.class ) ) {
      bh.when( () -> BrowserLoginHandler.performBrowserLoginWithRetry( any(), any(), any() ) ).thenReturn( sso );
      assertTrue( (Boolean) invoke( task, "performSSOLogin" ) );
      assertSame( sso, task.getLoginData() );
    }
  }

  // ===== verifyServerReachable =====

  @Test
  public void testVerifyBrowserAuthShortCircuits() throws Exception {
    when( loginData.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( "true" );
    assertTrue( (Boolean) invoke( newTask(), "verifyServerReachable" ) );
  }

  @Test
  public void testVerifyServerReachableTrue() throws Exception {
    when( loginData.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( null );
    try ( var sa = mockStatic( SessionAuthenticationUtil.class ) ) {
      sa.when( () -> SessionAuthenticationUtil.isServerReachable( any() ) ).thenReturn( true );
      assertTrue( (Boolean) invoke( newTask(), "verifyServerReachable" ) );
    }
  }

  @Test
  public void testVerifyServerUnreachableShowsDialog() throws Exception {
    when( loginData.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( null );
    try ( var sa = mockStatic( SessionAuthenticationUtil.class );
          var ed = mockStatic( ExceptionDialog.class ) ) {
      sa.when( () -> SessionAuthenticationUtil.isServerReachable( any() ) ).thenReturn( false );
      assertFalse( (Boolean) invoke( newTask(), "verifyServerReachable" ) );
      ed.verify( () -> ExceptionDialog.showExceptionDialog( any(), any(), any(), any() ) );
    }
  }

  // ===== getServerDownTitle / getServerDownMessage =====

  @Test
  public void testServerDownMessagesNonPublish() throws Exception {
    LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false );
    assertNotNull( invoke( task, "getServerDownTitle" ) );
    assertNotNull( invoke( task, "getServerDownMessage" ) );
  }

  @Test
  public void testServerDownMessagesPublish() throws Exception {
    LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, true );
    assertNotNull( invoke( task, "getServerDownTitle" ) );
    assertNotNull( invoke( task, "getServerDownMessage" ) );
  }

  // ===== storeLoginSession =====

  @Test
  public void testStoreLoginSession() throws Exception {
    when( loginData.getUsername() ).thenReturn( "joe" );
    invoke( newTask(), "storeLoginSession" );
    assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  // ===== executeFollowUpTasks =====

  @Test
  public void testExecuteFollowUpTasksWithTask() throws Exception {
    try ( var swing = mockStatic( SwingUtilities.class ) ) {
      invoke( newTask(), "executeFollowUpTasks" );
      verify( authServerTask ).setLoginData( eq( loginData ), anyBoolean() );
      swing.verify( () -> SwingUtilities.invokeLater( any() ), times( 2 ) );
    }
  }

  @Test
  public void testExecuteFollowUpTasksNullTask() throws Exception {
    LoginTask task = new LoginTask( reportDesignerContext, component, null, loginData, false, false );
    try ( var swing = mockStatic( SwingUtilities.class ) ) {
      invoke( task, "executeFollowUpTasks" );
      swing.verify( () -> SwingUtilities.invokeLater( any() ), times( 1 ) );
    }
  }
  @Test
  public void testStoreRememberedNoDialog() throws Exception {
    when( reportDesignerContext.getActiveContext() ).thenReturn( null );
    invoke( newTask(), "storeRememberedSettings" );
    verify( globalAuthStore ).add( any( AuthenticationData.class ), eq( true ) );
  }

  @Test
  public void testStoreRememberedDialogNotRemember() throws Exception {
    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.isRememberSettings() ).thenReturn( false );
    setField( task, "loginDialog", dlg );
    when( loginData.getUrl() ).thenReturn( "http://srv/p" );
    invoke( task, "storeRememberedSettings" );
    verify( globalAuthStore, never() ).add( any(), anyBoolean() );
    verify( globalAuthStore ).removeCredentials( "http://srv/p" );
  }

  @Test
  public void testStoreRememberedWithActiveContext() throws Exception {
    AuthenticationStore ctxStore = mock( AuthenticationStore.class );
    ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    when( rdc.getAuthenticationStore() ).thenReturn( ctxStore );
    when( reportDesignerContext.getActiveContext() ).thenReturn( rdc );
    invoke( newTask(), "storeRememberedSettings" );
    verify( ctxStore ).add( any( AuthenticationData.class ), eq( true ) );
  }

  @Test
  public void testStoreRememberedBrowserAuthSkipsStore() throws Exception {
    AuthenticationData realData = new AuthenticationData( "http://srv/p", "u", "pw", 0 );
    realData.setOption( AuthenticationHelper.OPTION_BROWSER_AUTH, "true" );
    realData.setOption( AuthenticationHelper.OPTION_SESSION_ID, "abc" );
    when( reportDesignerContext.getActiveContext() ).thenReturn( null );
    LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, realData, false, false );
    invoke( task, "storeRememberedSettings" );
    verify( globalAuthStore, never() ).add( any( AuthenticationData.class ), anyBoolean() );
  }

  @Test
  public void testUsernamePasswordLoginFailed() throws Exception {
    try ( var bcph = mockStatic( BackgroundCancellableProcessHelper.class ) ) {
      bcph.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          any(), any(), any(), any() ) ).thenAnswer( inv -> null );
      assertEquals( "FAILED", invoke( newTask(), "performUsernamePasswordLogin" ).toString() );
    }
  }

  @Test
  public void testUsernamePasswordLoginCancelled() throws Exception {
    try ( var bcph = mockStatic( BackgroundCancellableProcessHelper.class ) ) {
      bcph.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          any(), any(), any(), any() ) ).thenAnswer( inv -> {
        ( (GenericCancelHandler) inv.getArgument( 1 ) ).cancelProcessing( null );
        return null;
      } );
      assertEquals( "CANCELLED", invoke( newTask(), "performUsernamePasswordLogin" ).toString() );
    }
  }

  @Test
  public void testRunRetryFlow() {
    AuthenticationData newData = mock( AuthenticationData.class );
    when( newData.getUrl() ).thenReturn( "http://srv/p" );
    LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false, true );
    try ( var bh = mockStatic( BrowserLoginHandler.class );
          var sa = mockStatic( SessionAuthenticationUtil.class );
          var swing = mockStatic( SwingUtilities.class ) ) {
      bh.when( () -> BrowserLoginHandler.recoverOAuthProvider( any() ) ).thenReturn( null );
      bh.when( () -> BrowserLoginHandler.performBrowserLoginWithRetry( any(), any(), any() ) ).thenReturn( newData );
      sa.when( () -> SessionAuthenticationUtil.isServerReachable( any() ) ).thenReturn( true );
      task.run();
      assertSame( newData, task.getLoginData() );
    }
  }

  @Test
  public void testRunDialogReturnsNullExitsEarly() throws Exception {
    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.performLogin( any(), any() ) ).thenReturn( null );
    setField( task, "loginDialog", dlg );
    task.run();
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void testRunServerDownClearsSession() throws Exception {
    AuthenticationData upData = mock( AuthenticationData.class );
    when( upData.getUrl() ).thenReturn( "http://srv/p" );
    when( upData.getUsername() ).thenReturn( "admin" );
    when( upData.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( null );

    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.performLogin( any(), any() ) ).thenReturn( upData ).thenReturn( null );
    when( dlg.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
    setField( task, "loginDialog", dlg );

    try ( var sa = mockStatic( SessionAuthenticationUtil.class );
          var bcph = mockStatic( BackgroundCancellableProcessHelper.class );
          var ed = mockStatic( ExceptionDialog.class ) ) {
      sa.when( () -> SessionAuthenticationUtil.isServerReachable( any() ) ).thenReturn( false );
      bcph.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          any(), any(), any(), any() ) ).thenAnswer( inv -> null );
      task.run();
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testSanitizedForStoreReturnsNullForNullInput() {
    assertNull( LoginTask.sanitizedForStore( null ) );
  }

  @Test
  public void testSanitizedForStoreReturnsSameInstanceWhenUrlNull() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getUrl() ).thenReturn( null );
    assertSame( data, LoginTask.sanitizedForStore( data ) );
  }

  @Test
  public void testSanitizedForStoreStripsSessionAndBrowserAuthOptions() {
    AuthenticationData original = new AuthenticationData( "http://srv/p", "u", "pw", 30 );
    original.setOption( "sessionId", "SHOULD-NOT-LEAK" );
    original.setOption( "browserAuth", "true" );
    original.setOption( "server-version", "5" );
    original.setOption( "lastFilename", "/x.prpt" );

    AuthenticationData copy = LoginTask.sanitizedForStore( original );

    assertNotSame( original, copy );
    assertEquals( "http://srv/p", copy.getUrl() );
    assertEquals( "u", copy.getUsername() );
    assertEquals( "pw", copy.getPassword() );
    assertNull( copy.getOption( "sessionId" ) );
    assertNull( copy.getOption( "browserAuth" ) );
    assertEquals( "5", copy.getOption( "server-version" ) );
    assertEquals( "/x.prpt", copy.getOption( "lastFilename" ) );
  }

  @Test
  public void testSanitizedForStorePreservesUsernamePasswordForNonSsoLogin() {
    AuthenticationData original = new AuthenticationData( "http://srv/p", "u", "pw", 30 );
    original.setOption( "server-version", "5" );

    AuthenticationData copy = LoginTask.sanitizedForStore( original );

    assertNotSame( original, copy );
    assertEquals( "u", copy.getUsername() );
    assertEquals( "pw", copy.getPassword() );
    assertEquals( "5", copy.getOption( "server-version" ) );
    assertNull( copy.getOption( "sessionId" ) );
    assertNull( copy.getOption( "browserAuth" ) );
  }

  @Test
  public void testSanitizedForStoreSkipsAbsentPreservedOptions() {
    AuthenticationData original = new AuthenticationData( "http://srv/p", "u", "pw", 30 );
    AuthenticationData copy = LoginTask.sanitizedForStore( original );
    assertNotSame( original, copy );
    assertNull( copy.getOption( "server-version" ) );
    assertNull( copy.getOption( "lastFilename" ) );
  }

  @Test
  public void testRunSsoSuccessPath() throws Exception {
    AuthenticationData ssoResult = mock( AuthenticationData.class );
    when( ssoResult.getUrl() ).thenReturn( "http://srv/p" );
    when( ssoResult.getUsername() ).thenReturn( "ssoUser" );
    when( ssoResult.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( "true" );

    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.performLogin( any(), any() ) ).thenReturn( loginData );
    when( dlg.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.SSO );
    when( dlg.getSelectedOAuthProvider() ).thenReturn( mock( OAuthProvider.class ) );
    when( dlg.isRememberSettings() ).thenReturn( false );
    setField( task, "loginDialog", dlg );

    try ( var bh = mockStatic( BrowserLoginHandler.class );
          var swing = mockStatic( SwingUtilities.class ) ) {
      bh.when( () -> BrowserLoginHandler.performBrowserLoginWithRetry( any(), any(), any() ) )
        .thenReturn( ssoResult );
      task.run();
      assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testRunUpSuccessServerReachable() throws Exception {
    when( loginData.getUsername() ).thenReturn( "admin" );
    when( loginData.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( null );

    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.performLogin( any(), any() ) ).thenReturn( loginData );
    when( dlg.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
    when( dlg.isRememberSettings() ).thenReturn( false );
    setField( task, "loginDialog", dlg );

    try ( var bcph = mockStatic( BackgroundCancellableProcessHelper.class );
          var sa = mockStatic( SessionAuthenticationUtil.class );
          var swing = mockStatic( SwingUtilities.class );
          var vtCtor = mockConstruction( ValidateLoginTask.class, ( mock, ctx ) -> {
            when( mock.isLoginComplete() ).thenReturn( true );
          } ) ) {
      bcph.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          any(), any(), any(), any() ) ).thenAnswer( inv -> null );
      sa.when( () -> SessionAuthenticationUtil.isServerReachable( any() ) ).thenReturn( true );
      task.run();
      assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testRunUpSuccessServerUnreachable() throws Exception {
    when( loginData.getUsername() ).thenReturn( "admin" );
    when( loginData.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( null );

    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.performLogin( any(), any() ) ).thenReturn( loginData );
    when( dlg.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
    when( dlg.isRememberSettings() ).thenReturn( false );
    setField( task, "loginDialog", dlg );

    try ( var bcph = mockStatic( BackgroundCancellableProcessHelper.class );
          var sa = mockStatic( SessionAuthenticationUtil.class );
          var ed = mockStatic( ExceptionDialog.class );
          var vtCtor = mockConstruction( ValidateLoginTask.class, ( mock, ctx ) -> {
            when( mock.isLoginComplete() ).thenReturn( true );
          } ) ) {
      bcph.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          any(), any(), any(), any() ) ).thenAnswer( inv -> null );
      sa.when( () -> SessionAuthenticationUtil.isServerReachable( any() ) ).thenReturn( false );
      task.run();
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testRunUpCancelledPath() throws Exception {
    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.performLogin( any(), any() ) ).thenReturn( loginData );
    when( dlg.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
    when( dlg.isRememberSettings() ).thenReturn( false );
    setField( task, "loginDialog", dlg );

    try ( var bcph = mockStatic( BackgroundCancellableProcessHelper.class ) ) {
      bcph.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          any(), any(), any(), any() ) ).thenAnswer( inv -> {
        ( (GenericCancelHandler) inv.getArgument( 1 ) ).cancelProcessing( null );
        return null;
      } );
      task.run();
    }
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void testRunRetryFailsThenSsoSucceeds() throws Exception {
    AuthenticationData ssoResult = mock( AuthenticationData.class );
    when( ssoResult.getUrl() ).thenReturn( "http://srv/p" );
    when( ssoResult.getUsername() ).thenReturn( "u" );
    when( ssoResult.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( "true" );

    LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false, true );
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.performLogin( any(), any() ) ).thenReturn( loginData );
    when( dlg.getLoginMethod() ).thenReturn( RepositoryLoginDialog.LoginMethod.SSO );
    when( dlg.getSelectedOAuthProvider() ).thenReturn( null );
    when( dlg.isRememberSettings() ).thenReturn( false );
    setField( task, "loginDialog", dlg );

    try ( var bh = mockStatic( BrowserLoginHandler.class );
          var swing = mockStatic( SwingUtilities.class ) ) {
      // First call: retry fails; second call: SSO succeeds
      bh.when( () -> BrowserLoginHandler.recoverOAuthProvider( any() ) ).thenReturn( null );
      bh.when( () -> BrowserLoginHandler.performBrowserLoginWithRetry( any(), any(), any() ) )
        .thenReturn( null )   // retry fails
        .thenReturn( ssoResult ); // SSO succeeds
      task.run();
      assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testConstructorActiveContextNoLoginUrlProperty() {
    ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    when( rdc.getProperties() ).thenReturn( new java.util.HashMap<>() );
    when( reportDesignerContext.getActiveContext() ).thenReturn( rdc );

    try ( var rop = mockStatic( RepositoryLoginDialog.class ) ) {
      rop.when( () -> RepositoryLoginDialog.getDefaultData( reportDesignerContext ) ).thenReturn( loginData );
      LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, null, false );
      assertSame( loginData, task.getLoginData() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCreateLoginDialogFrameWindow() throws Exception {
    LoginTask task = newTask();
    // Set uiContext to a Frame via mocking LibSwingUtil
    java.awt.Frame frame = mock( java.awt.Frame.class );
    try ( var lib = mockStatic( org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil.class );
          var ctor = mockConstruction( RepositoryLoginDialog.class ) ) {
      lib.when( () -> org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil.getWindowAncestor( any() ) )
        .thenReturn( frame );
      Object dlg = invoke( task, "createLoginDialog" );
      assertNotNull( dlg );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCreateLoginDialogDialogWindow() throws Exception {
    LoginTask task = newTask();
    java.awt.Dialog dialog = mock( java.awt.Dialog.class );
    try ( var lib = mockStatic( org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil.class );
          var ctor = mockConstruction( RepositoryLoginDialog.class ) ) {
      lib.when( () -> org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil.getWindowAncestor( any() ) )
        .thenReturn( dialog );
      Object dlg = invoke( task, "createLoginDialog" );
      assertNotNull( dlg );
    }
  }

  @Test
  public void testGetRetryUrlDefaultDataUrlNull() throws Exception {
    when( loginData.getUrl() ).thenReturn( "  " );
    AuthenticationData dflt = mock( AuthenticationData.class );
    when( dflt.getUrl() ).thenReturn( null );
    when( globalAuthStore.getKnownURLs() ).thenReturn( new String[] { "http://old/p" } );
    when( globalAuthStore.getCredentials( "http://old/p" ) ).thenReturn( dflt );
    when( reportDesignerContext.getActiveContext() ).thenReturn( null );
    assertEquals( "http://localhost:8080/pentaho", invoke( newTask(), "getRetryUrl" ) );
  }

  @Test
  public void testStoreRememberedDialogFalseUrlNull() throws Exception {
    LoginTask task = newTask();
    RepositoryLoginDialog dlg = mock( RepositoryLoginDialog.class );
    when( dlg.isRememberSettings() ).thenReturn( false );
    setField( task, "loginDialog", dlg );
    when( loginData.getUrl() ).thenReturn( null );
    invoke( task, "storeRememberedSettings" );
    verify( globalAuthStore, never() ).add( any(), anyBoolean() );
    verify( globalAuthStore, never() ).removeCredentials( any() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testGetDefaultDataActiveContextEmptyUrls() throws Exception {
    AuthenticationStore ctxStore = mock( AuthenticationStore.class );
    when( ctxStore.getKnownURLs() ).thenReturn( new String[0] );
    ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    when( rdc.getAuthenticationStore() ).thenReturn( ctxStore );
    when( reportDesignerContext.getActiveContext() ).thenReturn( rdc );
    Method m = LoginTask.class.getDeclaredMethod( "getDefaultData" );
    m.setAccessible( true );
    assertNull( m.invoke( newTask() ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testGetDefaultDataNoContextWithKnownUrls() throws Exception {
    AuthenticationData cred = mock( AuthenticationData.class );
    when( globalAuthStore.getKnownURLs() ).thenReturn( new String[] { "http://k/p" } );
    when( globalAuthStore.getCredentials( "http://k/p" ) ).thenReturn( cred );
    when( reportDesignerContext.getActiveContext() ).thenReturn( null );
    Method m = LoginTask.class.getDeclaredMethod( "getDefaultData" );
    m.setAccessible( true );
    assertSame( cred, m.invoke( newTask() ) );
  }

  @Test
  public void testUsernamePasswordLoginException() throws Exception {
    try ( var bcph = mockStatic( BackgroundCancellableProcessHelper.class );
          var ed = mockStatic( ExceptionDialog.class );
          var vtCtor = mockConstruction( ValidateLoginTask.class, ( mock, ctx ) -> {
            when( mock.getException() ).thenReturn( new org.apache.commons.vfs2.FileSystemException( "boom" ) );
            when( mock.isLoginComplete() ).thenReturn( false );
          } ) ) {
      bcph.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          any(), any(), any(), any() ) ).thenAnswer( inv -> null );
      assertEquals( "FAILED", invoke( newTask(), "performUsernamePasswordLogin" ).toString() );
      ed.verify( () -> ExceptionDialog.showExceptionDialog( any(), any(), any(), any() ) );
    }
  }
}

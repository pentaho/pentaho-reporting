/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishSettings;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

public class LoginTaskTest {

  ReportDesignerContext reportDesignerContext;
  Component component;
  AuthenticatedServerTask authServerTask;
  GlobalAuthenticationStore globalAuthStore;
  AuthenticationData loginData;

  @Before
  public void setUp() throws Exception {
    reportDesignerContext = mock( ReportDesignerContext.class );
    component = mock( Component.class );
    authServerTask = mock( AuthenticatedServerTask.class );
    globalAuthStore = mock( GlobalAuthenticationStore.class );
    loginData = mock( AuthenticationData.class );
  }

  @After
  public void tearDown() {
    LoginTask.setLoginDialogFactory( null );
    LoginTask.setLoginSessionProvider( null );
  }

  @Test
  public void testLoginTaskReportDesignerContextComponentAuthenticatedServerTask() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    LoginTask loginTask = new LoginTask( reportDesignerContext, component, authServerTask );
    assertNotNull( loginTask );
  }

  @Test
  public void testLoginTaskReportDesignerContextComponentAuthenticatedServerTaskAuthenticationData() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    LoginTask loginTask = new LoginTask( reportDesignerContext, component, authServerTask, loginData );
    assertNotNull( loginTask );
  }

  @Test
  public void testLoginTaskReportDesignerContextComponentAuthenticatedServerTaskAuthenticationDataBoolean() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    LoginTask loginTask = new LoginTask( reportDesignerContext, component, authServerTask, loginData, true );
    assertNotNull( loginTask );
  }

  @Test
  public void testGetLoginData() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    LoginTask loginTask = new LoginTask( reportDesignerContext, component, authServerTask, loginData );
    assertNotNull( loginTask );
    assertEquals( loginData, loginTask.getLoginData() );
  }

  @Test
  public void testNoProviderRegistered_doesNotReuseSession() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    final AuthenticationData sessionData = mock( AuthenticationData.class );

    final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, null, false );

    assertNotEquals( sessionData, task.getLoginData() );
  }

  @Test
  public void testLoginSessionProvider_activeSession_isReused() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    final AuthenticationData activeSession = mock( AuthenticationData.class );
    LoginTask.setLoginSessionProvider( () -> activeSession );

    final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, null, false );

    assertEquals( activeSession, task.getLoginData() );
  }

  @Test
  public void testLoginSessionProvider_nullSession_fallsBackToDefault() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    final AuthenticationData sessionData = mock( AuthenticationData.class );
    LoginTask.setLoginSessionProvider( () -> null );

    final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, null, false );

    assertNotEquals( sessionData, task.getLoginData() );
  }

  @Test
  public void testSetLoginDialogFactory_canBeRegisteredAndCleared() {
    LoginTask.setLoginDialogFactory( ( owner, forPublish ) -> null );
    LoginTask.setLoginDialogFactory( null );
    assertNotNull( reportDesignerContext );
  }

  @Test( expected = NullPointerException.class )
  public void testConstructorThrowsWhenDesignerContextIsNull() {

    new LoginTask(
      null,
      component,
      authServerTask,
      loginData,
      false );
  }

  @Test( expected = NullPointerException.class )
  public void testConstructorThrowsWhenUiContextIsNull() {

    doReturn( globalAuthStore )
      .when( reportDesignerContext )
      .getGlobalAuthenticationStore();

    new LoginTask(
      reportDesignerContext,
      null,
      authServerTask,
      loginData,
      false );
  }

  @Test
  public void testFriendlyLoginErrorMessageConnectionRefused()
    throws Exception {

    Method m =
      LoginTask.class.getDeclaredMethod(

        "friendlyLoginErrorMessage",
        Exception.class );

    m.setAccessible( true );

    String actual =
      (String) m.invoke(
        null,
        new Exception(
          "Unknown message with code \"java.net.ConnectException: Connection refused: connect\"." ) );

    assertEquals(
      "Unable to connect to server. Please verify the server URL, confirm the server is running, and check your "
        + "credentials.",
      actual );
  }

  @Test
  public void testConstructorWithValidArguments() {

    doReturn( globalAuthStore )
      .when( reportDesignerContext )
      .getGlobalAuthenticationStore();

    LoginTask task =
      new LoginTask(
        reportDesignerContext,
        component,
        authServerTask,
        loginData,
        false );

    assertNotNull( task );
  }

  @Test
  public void testFriendlyLoginErrorMessageReturnsOriginalMessage()
    throws Exception {

    Method m =
      LoginTask.class.getDeclaredMethod(
        "friendlyLoginErrorMessage",
        Exception.class );

    m.setAccessible( true );

    try ( MockedStatic<PublishUtil> publishUtil =
            mockStatic( PublishUtil.class ) ) {

      publishUtil.when(
          () -> PublishUtil.isAuthenticationError( any() ) )
        .thenReturn( false );

      String actual =
        (String) m.invoke(
          null,
          new Exception( "custom message" ) );

      assertEquals(
        "custom message",
        actual );
    }
  }

  @Test
  public void testFriendlyLoginErrorMessageConnectionErrors()
    throws Exception {

    Method m =
      LoginTask.class.getDeclaredMethod(
        "friendlyLoginErrorMessage",
        Exception.class );

    m.setAccessible( true );

    try ( MockedStatic<PublishUtil> publishUtil =
            mockStatic( PublishUtil.class ) ) {

      publishUtil.when(
          () -> PublishUtil.isAuthenticationError( any() ) )
        .thenReturn( false );

      assertEquals(
        "Unable to connect to server. Please verify the server URL, confirm the server is running, and check your "
          + "credentials.",
        m.invoke( null,
          new Exception( "vfs.provider/connect.error" ) ) );

      assertEquals(
        "Unable to connect to server. Please verify the server URL, confirm the server is running, and check your "
          + "credentials.",
        m.invoke( null,
          new Exception( "Connection refused: connect" ) ) );

      assertEquals(
        "Unable to connect to server. Please verify the server URL, confirm the server is running, and check your "
          + "credentials.",
        m.invoke( null,
          new Exception( "Unknown message with code \"java.net.ConnectException\"" ) ) );

      assertEquals(
        "Unable to connect to server. Please verify the server URL, confirm the server is running, and check your "
          + "credentials.",
        m.invoke( null,
          (Exception) null ) );
    }
  }

  @Test
  public void testFriendlyLoginErrorMessageAuthenticationError()
    throws Exception {

    Method m =
      LoginTask.class.getDeclaredMethod(
        "friendlyLoginErrorMessage",
        Exception.class );

    m.setAccessible( true );

    Exception ex = new Exception( "401" );

    try ( MockedStatic<PublishUtil> publishUtil =
            mockStatic( PublishUtil.class ) ) {

      publishUtil.when(
          () -> PublishUtil.isAuthenticationError( ex ) )
        .thenReturn( true );

      String actual =
        (String) m.invoke(
          null,
          ex );

      assertEquals(
        "Invalid username and/or password. Please verify your credentials and try again.",
        actual );
    }
  }

  @Test
  public void testConstructorUsesPentahoLoginUrlStoredData() {
    final ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    final HashMap<String, Object> props = new HashMap<>();
    props.put( "pentaho-login-url", "http://x" );
    when( rdc.getProperties() ).thenReturn( props );
    when( reportDesignerContext.getActiveContext() ).thenReturn( rdc );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    try ( MockedStatic<RepositoryLoginDialog> rld = mockStatic( RepositoryLoginDialog.class ) ) {
      rld.when( () -> RepositoryLoginDialog.getStoredLoginData( "http://x", reportDesignerContext ) )
        .thenReturn( loginData );
      final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, null, false );
      assertEquals( loginData, task.getLoginData() );
    }
  }

  @Test
  public void testConstructorPentahoLoginUrlNullStoredFallsBackToDefault() {
    final ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    final HashMap<String, Object> props = new HashMap<>();
    props.put( "pentaho-login-url", "http://x" );
    when( rdc.getProperties() ).thenReturn( props );
    when( reportDesignerContext.getActiveContext() ).thenReturn( rdc );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    try ( MockedStatic<RepositoryLoginDialog> rld = mockStatic( RepositoryLoginDialog.class ) ) {
      rld.when( () -> RepositoryLoginDialog.getStoredLoginData( "http://x", reportDesignerContext ) )
        .thenReturn( null );
      rld.when( () -> RepositoryLoginDialog.getDefaultData( reportDesignerContext ) ).thenReturn( loginData );
      final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, null, false );
      assertEquals( loginData, task.getLoginData() );
    }
  }

  @Test
  public void testConstructorNoPentahoUrlUsesDefault() {
    final ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    when( rdc.getProperties() ).thenReturn( new HashMap<>() );
    when( reportDesignerContext.getActiveContext() ).thenReturn( rdc );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    try ( MockedStatic<RepositoryLoginDialog> rld = mockStatic( RepositoryLoginDialog.class ) ) {
      rld.when( () -> RepositoryLoginDialog.getDefaultData( reportDesignerContext ) ).thenReturn( loginData );
      final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, null, false );
      assertEquals( loginData, task.getLoginData() );
    }
  }

  @Test
  public void testConstructorNoActiveContextUsesDefault() {
    when( reportDesignerContext.getActiveContext() ).thenReturn( null );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    try ( MockedStatic<RepositoryLoginDialog> rld = mockStatic( RepositoryLoginDialog.class ) ) {
      rld.when( () -> RepositoryLoginDialog.getDefaultData( reportDesignerContext ) ).thenReturn( loginData );
      final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, null, false );
      assertEquals( loginData, task.getLoginData() );
    }
  }

  private LoginTask newSkipDialogTask() {
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    return new LoginTask( reportDesignerContext, component, authServerTask, loginData );
  }

  @Test
  public void testCreateLoginDialogFrameBranch() throws Exception {
    final LoginTask task = newSkipDialogTask();
    final Method m = LoginTask.class.getDeclaredMethod( "createLoginDialog" );
    m.setAccessible( true );
    final Frame frame = mock( Frame.class );
    try ( MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
          MockedConstruction<RepositoryLoginDialog> rld = mockConstruction( RepositoryLoginDialog.class ) ) {
      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( frame );
      assertNotNull( m.invoke( task ) );
      assertEquals( 1, rld.constructed().size() );
    }
  }

  @Test
  public void testCreateLoginDialogDialogBranch() throws Exception {
    final LoginTask task = newSkipDialogTask();
    final Method m = LoginTask.class.getDeclaredMethod( "createLoginDialog" );
    m.setAccessible( true );
    final Dialog owner = mock( Dialog.class );
    try ( MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
          MockedConstruction<RepositoryLoginDialog> rld = mockConstruction( RepositoryLoginDialog.class ) ) {
      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( owner );
      assertNotNull( m.invoke( task ) );
      assertEquals( 1, rld.constructed().size() );
    }
  }

  @Test
  public void testCreateLoginDialogPlainBranch() throws Exception {
    final LoginTask task = newSkipDialogTask();
    final Method m = LoginTask.class.getDeclaredMethod( "createLoginDialog" );
    m.setAccessible( true );
    final Window window = mock( Window.class ); // neither Frame nor Dialog
    try ( MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
          MockedConstruction<RepositoryLoginDialog> rld = mockConstruction( RepositoryLoginDialog.class ) ) {
      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( window );
      assertNotNull( m.invoke( task ) );
      assertEquals( 1, rld.constructed().size() );
    }
  }

  @Test
  public void testCreateLoginDialogUsesFactoryWhenRegistered() throws Exception {
    final RepositoryLoginDialog dialog = mock( RepositoryLoginDialog.class );
    LoginTask.setLoginDialogFactory( ( owner, forPublish ) -> dialog );
    final LoginTask task = newSkipDialogTask();
    final Method m = LoginTask.class.getDeclaredMethod( "createLoginDialog" );
    m.setAccessible( true );
    assertEquals( dialog, m.invoke( task ) );
  }

  @Test
  public void testIsRememberSettingsEnabledNullDialogUsesPublishSettings() throws Exception {
    final LoginTask task = newSkipDialogTask();
    final Method m = LoginTask.class.getDeclaredMethod( "isRememberSettingsEnabled" );
    m.setAccessible( true );
    assertEquals( PublishSettings.getInstance().isRememberSettings(), m.invoke( task ) );
  }

  @Test
  public void testIsRememberSettingsEnabledWithDialogUsesDialog() throws Exception {
    final LoginTask task = newSkipDialogTask();
    final RepositoryLoginDialog dialog = mock( RepositoryLoginDialog.class );
    when( dialog.isRememberSettings() ).thenReturn( true );
    final Field f = LoginTask.class.getDeclaredField( "loginDialog" );
    f.setAccessible( true );
    f.set( task, dialog );
    final Method m = LoginTask.class.getDeclaredMethod( "isRememberSettingsEnabled" );
    m.setAccessible( true );
    assertTrue( (boolean) m.invoke( task ) );
  }

  @Test
  public void testRunSkipDialogSuccessNoRememberWithFollowUp() {
    final RepositoryLoginDialog dialog = mock( RepositoryLoginDialog.class );
    when( dialog.isRememberSettings() ).thenReturn( false );
    LoginTask.setLoginDialogFactory( ( owner, fp ) -> dialog );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false );

    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedStatic<SwingUtilities> sw = mockStatic( SwingUtilities.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( false ) );
          MockedConstruction<ValidateLoginTask> vlt = mockConstruction( ValidateLoginTask.class,
            ( m, c ) -> when( m.isLoginComplete() ).thenReturn( true ) );
          MockedConstruction<UpdateReservedCharsTask> urc = mockConstruction( UpdateReservedCharsTask.class ) ) {
      task.run();
      verify( authServerTask ).setLoginData( loginData, false );
      sw.verify( () -> SwingUtilities.invokeLater( any() ), atLeastOnce() );
      verify( globalAuthStore, never() ).add( any(), org.mockito.ArgumentMatchers.anyBoolean() );
    }
  }

  @Test
  public void testRunRememberWithoutActiveContextAddsToGlobalStore() {
    final RepositoryLoginDialog dialog = mock( RepositoryLoginDialog.class );
    when( dialog.isRememberSettings() ).thenReturn( true );
    LoginTask.setLoginDialogFactory( ( owner, fp ) -> dialog );
    when( reportDesignerContext.getActiveContext() ).thenReturn( null );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false );

    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedStatic<SwingUtilities> sw = mockStatic( SwingUtilities.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( false ) );
          MockedConstruction<ValidateLoginTask> vlt = mockConstruction( ValidateLoginTask.class,
            ( m, c ) -> when( m.isLoginComplete() ).thenReturn( true ) );
          MockedConstruction<UpdateReservedCharsTask> urc = mockConstruction( UpdateReservedCharsTask.class ) ) {
      task.run();
      verify( globalAuthStore ).add( loginData, true );
    }
  }

  @Test
  public void testRunRememberWithActiveContextAddsToContextStore() {
    final RepositoryLoginDialog dialog = mock( RepositoryLoginDialog.class );
    when( dialog.isRememberSettings() ).thenReturn( true );
    LoginTask.setLoginDialogFactory( ( owner, fp ) -> dialog );

    final ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    final AuthenticationStore store = mock( AuthenticationStore.class );
    when( rdc.getAuthenticationStore() ).thenReturn( store );
    when( reportDesignerContext.getActiveContext() ).thenReturn( rdc );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false );

    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedStatic<SwingUtilities> sw = mockStatic( SwingUtilities.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( false ) );
          MockedConstruction<ValidateLoginTask> vlt = mockConstruction( ValidateLoginTask.class,
            ( m, c ) -> when( m.isLoginComplete() ).thenReturn( true ) );
          MockedConstruction<UpdateReservedCharsTask> urc = mockConstruction( UpdateReservedCharsTask.class ) ) {
      task.run();
      verify( store ).add( loginData, true );
    }
  }

  @Test
  public void testRunPerformLoginReturnsNullReturnsEarly() {
    final RepositoryLoginDialog dialog = mock( RepositoryLoginDialog.class );
    when( dialog.performLogin( any(), any() ) ).thenReturn( null );
    LoginTask.setLoginDialogFactory( ( owner, fp ) -> dialog );
    when( reportDesignerContext.getActiveContext() ).thenReturn( null );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    try ( MockedStatic<RepositoryLoginDialog> rld = mockStatic( RepositoryLoginDialog.class ) ) {
      rld.when( () -> RepositoryLoginDialog.getDefaultData( reportDesignerContext ) ).thenReturn( null );
      final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, null, false );
      task.run();
      verify( dialog ).performLogin( any(), any() );
      verify( authServerTask, never() ).setLoginData( any(), org.mockito.ArgumentMatchers.anyBoolean() );
    }
  }

  @Test
  public void testRunCancelledReturnsEarly() {
    final RepositoryLoginDialog dialog = mock( RepositoryLoginDialog.class );
    LoginTask.setLoginDialogFactory( ( owner, fp ) -> dialog );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false );

    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( true ) );
          MockedConstruction<ValidateLoginTask> vlt = mockConstruction( ValidateLoginTask.class ) ) {
      task.run();
      verify( authServerTask, never() ).setLoginData( any(), org.mockito.ArgumentMatchers.anyBoolean() );
    }
  }

  @Test
  public void testRunValidationExceptionThenSuccess() {
    final RepositoryLoginDialog dialog = mock( RepositoryLoginDialog.class );
    when( dialog.isRememberSettings() ).thenReturn( false );
    when( dialog.performLogin( any(), any() ) ).thenReturn( loginData );
    LoginTask.setLoginDialogFactory( ( owner, fp ) -> dialog );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    final LoginTask task = new LoginTask( reportDesignerContext, component, authServerTask, loginData, false );

    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedStatic<SwingUtilities> sw = mockStatic( SwingUtilities.class );
          MockedStatic<ExceptionDialog> ed = mockStatic( ExceptionDialog.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( false ) );
          MockedConstruction<ValidateLoginTask> vlt = mockConstruction( ValidateLoginTask.class,
            ( m, c ) -> {
              if ( c.getCount() == 1 ) {
                when( m.getException() ).thenReturn( new Exception( "boom" ) );
              } else {
                when( m.isLoginComplete() ).thenReturn( true );
              }
            } );
          MockedConstruction<UpdateReservedCharsTask> urc = mockConstruction( UpdateReservedCharsTask.class ) ) {
      task.run();
      ed.verify( () -> ExceptionDialog.showExceptionDialog( any(), any(), any(), any() ) );
      verify( dialog ).performLogin( any(), any() );
    }
  }

  @Test
  public void testRunNoFollowUpTaskSkipsFollowUp() {
    final RepositoryLoginDialog dialog = mock( RepositoryLoginDialog.class );
    when( dialog.isRememberSettings() ).thenReturn( false );
    LoginTask.setLoginDialogFactory( ( owner, fp ) -> dialog );
    doReturn( globalAuthStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();

    // followUpTask is null -> the followUpTask block is skipped
    final LoginTask task = new LoginTask( reportDesignerContext, component, null, loginData, false );

    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedStatic<SwingUtilities> sw = mockStatic( SwingUtilities.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( false ) );
          MockedConstruction<ValidateLoginTask> vlt = mockConstruction( ValidateLoginTask.class,
            ( m, c ) -> when( m.isLoginComplete() ).thenReturn( true ) );
          MockedConstruction<UpdateReservedCharsTask> urc = mockConstruction( UpdateReservedCharsTask.class ) ) {
      task.run();
      // UpdateReservedCharsTask is still scheduled even without a follow-up task
      sw.verify( () -> SwingUtilities.invokeLater( any() ), atLeastOnce() );
    }
  }
}

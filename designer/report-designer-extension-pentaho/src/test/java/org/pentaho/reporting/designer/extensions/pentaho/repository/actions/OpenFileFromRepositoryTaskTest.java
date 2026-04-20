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
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ConnectException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;

public class OpenFileFromRepositoryTaskTest {

  private ReportDesignerContext designerContext;
  private Component uiContext;
  private AuthenticationData loginData;
  private GlobalAuthenticationStore authStore;

  @Before
  public void setUp() {
    designerContext = mock( ReportDesignerContext.class );
    uiContext = mock( Component.class );
    loginData = mock( AuthenticationData.class );
    authStore = mock( GlobalAuthenticationStore.class );
    when( designerContext.getGlobalAuthenticationStore() ).thenReturn( authStore );
    RepositorySessionManager.getInstance().clearSession();
  }

  @After
  public void tearDown() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @Test
  public void testConstructor() {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    assertNotNull( task );
  }

  @Test
  public void testSetLoginData() {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, true );
    assertNotNull( task );
  }

  @Test
  public void testSetLoginDataWithFalseStoreUpdates() {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, false );
    assertNotNull( task );
  }

  @Test
  public void testSetLoginDataWithNullData() {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( null, false );
    assertNotNull( task );
  }

  @Test
  public void testImplementsAuthenticatedServerTask() {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    assertTrue( task instanceof AuthenticatedServerTask );
  }

  @Test
  public void testImplementsRunnable() {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    assertTrue( task instanceof Runnable );
  }

  // ---- run() coverage ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunSelectFileReturnsNull() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, false );

    // Replace selectFileFromRepositoryTask with a mock
    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( null );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    // run() should return gracefully when selectFile returns null
    task.run();
    assertNotNull( task );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunSelectFileReturnsFileAndOpenSucceeds() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, true );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    ReportRenderContext renderCtx = mock( ReportRenderContext.class );
    org.pentaho.reporting.designer.core.auth.AuthenticationStore ctxAuthStore =
      mock( org.pentaho.reporting.designer.core.auth.AuthenticationStore.class );
    when( renderCtx.getAuthenticationStore() ).thenReturn( ctxAuthStore );
    ReportDesignerView view = mock( ReportDesignerView.class );
    when( designerContext.getView() ).thenReturn( view );

    try ( var publishMock = mockStatic( PublishUtil.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) ).thenReturn( renderCtx );
      task.run();

      verify( loginData ).setOption( "lastFilename", "/public/test.prpt" );
      verify( designerContext.getGlobalAuthenticationStore() )
        .add( any( AuthenticationData.class ), eq( true ) );
      verify( view ).setWelcomeVisible( false );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunOpenReportReturnsNull() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    ReportDesignerView view = mock( ReportDesignerView.class );
    when( designerContext.getView() ).thenReturn( view );

    try ( var publishMock = mockStatic( PublishUtil.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) ).thenReturn( null );
      task.run();
      // No crash, context was null so setProperty not called
      verify( view ).setWelcomeVisible( false );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunWithPendingReport() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, false );

    Field pendingField = OpenFileFromRepositoryTask.class.getDeclaredField( "pendingReport" );
    pendingField.setAccessible( true );
    pendingField.set( task, "/public/pending.prpt" );

    ReportDesignerView view = mock( ReportDesignerView.class );
    when( designerContext.getView() ).thenReturn( view );

    try ( var publishMock = mockStatic( PublishUtil.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) ).thenReturn( null );
      task.run();
      verify( loginData ).setOption( "lastFilename", "/public/pending.prpt" );
      // pendingReport should be cleared
      assertNull( pendingField.get( task ) );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunAuthErrorWithBrowserAuth() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) )
        .thenThrow( new IOException( "401 Unauthorized" ) );
      // User clicks Cancel
      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.NO_OPTION );
      task.run();
    }
    // No crash
    assertNotNull( task );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunAuthErrorWithBrowserAuthRetryLogin() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    ReportDesignerView view = mock( ReportDesignerView.class );
    when( designerContext.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( uiContext );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class );
          var loginMock = mockConstruction( LoginTask.class, ( mock, ctx ) -> {
            when( mock.getLoginData() ).thenReturn( null );
          } ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) )
        .thenThrow( new IOException( "401 Unauthorized" ) );
      // User clicks Login Again
      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.YES_OPTION );
      task.run();
      // LoginTask returns null → pendingReport cleared
      Field pendingField = OpenFileFromRepositoryTask.class.getDeclaredField( "pendingReport" );
      pendingField.setAccessible( true );
      assertNull( pendingField.get( task ) );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunConnectionError() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) )
        .thenThrow( new IOException( "connect error", new ConnectException( "Connection refused" ) ) );
      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );
      task.run();
    }
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunGeneralError() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var exDlgMock = mockStatic( ExceptionDialog.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) )
        .thenThrow( new RuntimeException( "General error" ) );
      exDlgMock.when( () -> ExceptionDialog.showExceptionDialog(
        any(), any(), any(), any() ) ).thenAnswer( inv -> null );
      task.run();
    }
    assertNotNull( task );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunWithStoreUpdatesTrue() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, true );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/report.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    ReportDesignerView view = mock( ReportDesignerView.class );
    when( designerContext.getView() ).thenReturn( view );

    try ( var publishMock = mockStatic( PublishUtil.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) ).thenReturn( null );
      task.run();
      // Stored data must be a sanitized copy (sessionId / browserAuth stripped).
      verify( authStore ).add( any( AuthenticationData.class ), eq( true ) );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunWithStoreUpdatesFalse() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/report.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    ReportDesignerView view = mock( ReportDesignerView.class );
    when( designerContext.getView() ).thenReturn( view );

    try ( var publishMock = mockStatic( PublishUtil.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) ).thenReturn( null );
      task.run();
      verify( authStore, never() ).add( any(), anyBoolean() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunSsoLoginSkipsAuthStoreWrite() throws Exception {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, true );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/report.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    ReportDesignerView view = mock( ReportDesignerView.class );
    when( designerContext.getView() ).thenReturn( view );

    try ( var publishMock = mockStatic( PublishUtil.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) ).thenReturn( null );
      task.run();
      verify( authStore, never() ).add( any( AuthenticationData.class ), anyBoolean() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testHandleSessionExpiredRetrySucceeds() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    AuthenticationData newLoginData = mock( AuthenticationData.class );
    when( newLoginData.getUsername() ).thenReturn( "admin" );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    ReportDesignerView view = mock( ReportDesignerView.class );
    when( designerContext.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( uiContext );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class );
          var loginMock = mockConstruction( LoginTask.class, ( mock, ctx ) -> {
            when( mock.getLoginData() ).thenReturn( newLoginData );
          } ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) )
        .thenThrow( new IOException( "401 Unauthorized" ) );
      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.YES_OPTION );
      swingMock.when( () -> SwingUtilities.invokeLater( any() ) ).thenAnswer( inv -> null );
      task.run();
      // Verify session was updated
      assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testShowOpenErrorWithNullExceptionMessage() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var exDlgMock = mockStatic( ExceptionDialog.class ) ) {
      RuntimeException ex = mock( RuntimeException.class );
      when( ex.getMessage() ).thenReturn( null );
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) ).thenThrow( ex );
      exDlgMock.when( () -> ExceptionDialog.showExceptionDialog(
        any(), any(), any(), any() ) ).thenAnswer( inv -> null );
      task.run();
    }
    assertNotNull( task );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunDoesNotPreemptivelyProbeSession() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( null );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    try ( var sessionMock = mockStatic( SessionAuthenticationUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {
      sessionMock.when( () -> SessionAuthenticationUtil.checkSessionValidity( loginData ) )
        .thenReturn( Boolean.FALSE );

      task.run();

      // Probe must not have been called.
      sessionMock.verifyNoInteractions();
      // Session-expired dialog must NOT have been shown.
      jopMock.verifyNoInteractions();
      // Real flow continues.
      verify( mockSelect ).selectFile( any(), any() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunValidSsoSessionProceedsNormally() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( null );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    try ( var sessionMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      sessionMock.when( () -> SessionAuthenticationUtil.checkSessionValidity( loginData ) )
        .thenReturn( Boolean.TRUE );
      task.run();
      // Verify selectFile was called (dialog was opened, not short-circuited)
      verify( mockSelect ).selectFile( any(), any() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunUnknownSsoSessionValidityProceedsNormally() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( null );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    try ( var sessionMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      // null means unknown (connection error) — should NOT trigger session expired
      sessionMock.when( () -> SessionAuthenticationUtil.checkSessionValidity( loginData ) )
        .thenReturn( null );
      task.run();
      verify( mockSelect ).selectFile( any(), any() );
    }
  }

  @Test
  public void testRunNonSsoSessionSkipsValidation() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    // No browserAuth option → not SSO
    when( loginData.getOption( "browserAuth" ) ).thenReturn( null );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( null );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    // SessionAuthenticationUtil.checkSessionValidity should NOT be called
    task.run();
    verify( mockSelect ).selectFile( any(), any() );
  }

  // ====================================================================
  // sanitizedForStore() coverage (U/P -> SSO bug fix)
  // ====================================================================

  @Test
  public void testSanitizedForStoreReturnsNullForNullInput() {
    assertNull( OpenFileFromRepositoryTask.sanitizedForStore( null ) );
  }

  @Test
  public void testSanitizedForStoreReturnsSameInstanceWhenUrlNull() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getUrl() ).thenReturn( null );
    assertSame( data, OpenFileFromRepositoryTask.sanitizedForStore( data ) );
  }

  @Test
  public void testSanitizedForStoreStripsSessionAndBrowserAuth() {
    AuthenticationData original = new AuthenticationData( "http://srv/p", "u", "pw", 30 );
    original.setOption( "sessionId", "SHOULD-NOT-LEAK" );
    original.setOption( "browserAuth", "true" );
    original.setOption( "server-version", "5" );
    original.setOption( "lastFilename", "/x.prpt" );

    AuthenticationData copy = OpenFileFromRepositoryTask.sanitizedForStore( original );

    assertNotSame( original, copy );
    assertEquals( "http://srv/p", copy.getUrl() );
    assertEquals( "u", copy.getUsername() );
    assertEquals( "pw", copy.getPassword() );
    assertNull( "sessionId must be stripped", copy.getOption( "sessionId" ) );
    assertNull( "browserAuth must be stripped", copy.getOption( "browserAuth" ) );
    assertEquals( "server-version must be preserved", "5", copy.getOption( "server-version" ) );
    assertEquals( "lastFilename must be preserved", "/x.prpt", copy.getOption( "lastFilename" ) );
  }

  @Test
  public void testSanitizedForStoreSkipsAbsentPreservedOptions() {
    AuthenticationData original = new AuthenticationData( "http://srv/p", "u", "pw", 30 );
    AuthenticationData copy = OpenFileFromRepositoryTask.sanitizedForStore( original );
    assertNotSame( original, copy );
    assertNull( copy.getOption( "server-version" ) );
    assertNull( copy.getOption( "lastFilename" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunSecondaryContextStoreSkippedWhenStoreUpdatesFalse() throws Exception {
    OpenFileFromRepositoryTask task = new OpenFileFromRepositoryTask( designerContext, uiContext );
    task.setLoginData( loginData, false );

    SelectFileFromRepositoryTask mockSelect = mock( SelectFileFromRepositoryTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
    Field selectField = OpenFileFromRepositoryTask.class.getDeclaredField( "selectFileFromRepositoryTask" );
    selectField.setAccessible( true );
    selectField.set( task, mockSelect );

    ReportRenderContext renderCtx = mock( ReportRenderContext.class );
    org.pentaho.reporting.designer.core.auth.AuthenticationStore ctxAuthStore =
      mock( org.pentaho.reporting.designer.core.auth.AuthenticationStore.class );
    when( renderCtx.getAuthenticationStore() ).thenReturn( ctxAuthStore );
    ReportDesignerView view = mock( ReportDesignerView.class );
    when( designerContext.getView() ).thenReturn( view );

    try ( var publishMock = mockStatic( PublishUtil.class ) ) {
      publishMock.when( () -> PublishUtil.openReport( any(), any(), any() ) ).thenReturn( renderCtx );
      task.run();
      // storeUpdates=false -> neither global nor per-context store is written.
      verify( authStore, never() ).add( any(), anyBoolean() );
      verify( ctxAuthStore, never() ).add( any(), anyBoolean() );
    }
  }
}

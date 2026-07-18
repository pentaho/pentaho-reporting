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


package org.pentaho.reporting.designer.extensions.pentaho.repository.auth;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BrowserLoginHandlerTest {

  private static final String OPTION_BROWSER_AUTH = "browserAuth";
  private static final String OPTION_OAUTH_AUTH_URI = "oauthAuthorizationUri";

  @Test
  public void testSessionDataConstructorSetsFields() {
    final BrowserLoginHandler.SessionData sd = new BrowserLoginHandler.SessionData( "sess-1", "alice" );
    assertEquals( "sess-1", sd.getSessionId() );
    assertEquals( "alice", sd.getUsername() );
  }

  @Test
  public void testSessionDataNullFields() {
    final BrowserLoginHandler.SessionData sd = new BrowserLoginHandler.SessionData( null, null );
    assertNull( sd.getSessionId() );
    assertNull( sd.getUsername() );
  }

  @Test
  public void testSetOAuthProviderStores() {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final OAuthProvider provider = new OAuthProvider();
    handler.setOAuthProvider( provider );
    assertNotNull( handler );
  }

  @Test
  public void testRecoverOAuthProviderNullDataReturnsNull() {
    assertNull( BrowserLoginHandler.recoverOAuthProvider( null ) );
  }

  @Test
  public void testRecoverOAuthProviderNoBrowserAuthReturnsNull() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( OPTION_BROWSER_AUTH ) ).thenReturn( null );
    assertNull( BrowserLoginHandler.recoverOAuthProvider( data ) );
  }

  @Test
  public void testRecoverOAuthProviderBrowserAuthNotTrueReturnsNull() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( OPTION_BROWSER_AUTH ) ).thenReturn( "false" );
    assertNull( BrowserLoginHandler.recoverOAuthProvider( data ) );
  }

  @Test
  public void testRecoverOAuthProviderBrowserAuthNullAuthUriReturnsNull() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( OPTION_BROWSER_AUTH ) ).thenReturn( "true" );
    when( data.getOption( OPTION_OAUTH_AUTH_URI ) ).thenReturn( null );
    assertNull( BrowserLoginHandler.recoverOAuthProvider( data ) );
  }

  @Test
  public void testRecoverOAuthProviderBrowserAuthEmptyAuthUriReturnsNull() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( OPTION_BROWSER_AUTH ) ).thenReturn( "true" );
    when( data.getOption( OPTION_OAUTH_AUTH_URI ) ).thenReturn( "   " );
    assertNull( BrowserLoginHandler.recoverOAuthProvider( data ) );
  }

  @Test
  public void testRecoverOAuthProviderValidDataReturnsProvider() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( OPTION_BROWSER_AUTH ) ).thenReturn( "true" );
    when( data.getOption( OPTION_OAUTH_AUTH_URI ) ).thenReturn( "http://auth" );
    when( data.getOption( "oauthClientName" ) ).thenReturn( "Google" );
    when( data.getOption( "oauthRegistrationId" ) ).thenReturn( "google" );

    final OAuthProvider provider = BrowserLoginHandler.recoverOAuthProvider( data );

    assertNotNull( provider );
    assertEquals( "http://auth", provider.getAuthorizationUri() );
    assertEquals( "Google", provider.getClientName() );
    assertEquals( "google", provider.getRegistrationId() );
    assertTrue( provider.isEnabled() );
  }

  @Test
  public void testResolveCallbackHostValidUrlWithHostReturnsLocalhost() {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final String host = handler.resolveCallbackHost( "http://my-server.example.com:8080/pentaho" );
    assertEquals( "localhost", host );
  }

  @Test
  public void testResolveCallbackHostInvalidUrlFallsBackToLocalhost() {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final String host = handler.resolveCallbackHost( "not a valid url :::" );
    assertNotNull( host );
  }

  @Test
  public void testGetLocalCallbackHostReturnsNonNull() {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final String host = handler.getLocalCallbackHost();
    assertNotNull( host );
    assertFalse( host.isEmpty() );
  }

  @Test
  public void testVerifySessionWithRetryImmediateSuccessReturnsTrue() {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    try ( MockedStatic<SessionAuthenticationUtil> sau = mockStatic( SessionAuthenticationUtil.class ) ) {
      sau.when( () -> SessionAuthenticationUtil.checkSessionValidity( any() ) )
        .thenReturn( Boolean.TRUE );

      final BrowserLoginHandler handler = new BrowserLoginHandler();
      assertTrue( handler.verifySessionWithRetry( loginData ) );
    }
  }

  @Test
  public void testVerifySessionWithRetryAllNullReturnsTrue() {
    // null means connection error (unknown) — treated as "may still be valid"
    final AuthenticationData loginData = mock( AuthenticationData.class );
    try ( MockedStatic<SessionAuthenticationUtil> sau = mockStatic( SessionAuthenticationUtil.class ) ) {
      sau.when( () -> SessionAuthenticationUtil.checkSessionValidity( any() ) )
        .thenReturn( null );

      final BrowserLoginHandler handler = new BrowserLoginHandler();
      assertTrue( handler.verifySessionWithRetry( loginData ) );
    }
  }

  @Test
  public void testVerifySessionWithRetryAllFalseReturnsFalse() {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    try ( MockedStatic<SessionAuthenticationUtil> sau = mockStatic( SessionAuthenticationUtil.class ) ) {
      sau.when( () -> SessionAuthenticationUtil.checkSessionValidity( any() ) )
        .thenReturn( Boolean.FALSE );

      final BrowserLoginHandler handler = new BrowserLoginHandler();
      assertFalse( handler.verifySessionWithRetry( loginData ) );
    }
  }

  @Test
  public void testVerifySessionWithRetryNullThenSuccessReturnsTrue() {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    try ( MockedStatic<SessionAuthenticationUtil> sau = mockStatic( SessionAuthenticationUtil.class ) ) {
      sau.when( () -> SessionAuthenticationUtil.checkSessionValidity( any() ) )
        .thenReturn( null )
        .thenReturn( Boolean.TRUE );

      final BrowserLoginHandler handler = new BrowserLoginHandler();
      assertTrue( handler.verifySessionWithRetry( loginData ) );
    }
  }

  @Test
  public void testVerifySessionWithRetryFalseThenNullReturnsFalse() {
    // false followed by null — hadExplicitAuthFailure=true, so returns false
    final AuthenticationData loginData = mock( AuthenticationData.class );
    try ( MockedStatic<SessionAuthenticationUtil> sau = mockStatic( SessionAuthenticationUtil.class ) ) {
      sau.when( () -> SessionAuthenticationUtil.checkSessionValidity( any() ) )
        .thenReturn( Boolean.FALSE )
        .thenReturn( null )
        .thenReturn( null );

      final BrowserLoginHandler handler = new BrowserLoginHandler();
      assertFalse( handler.verifySessionWithRetry( loginData ) );
    }
  }

  @Test
  public void testBuildAuthUrlWithoutProvider() throws Exception {

    BrowserLoginHandler handler = new BrowserLoginHandler();

    Method m = BrowserLoginHandler.class
      .getDeclaredMethod( "buildAuthUrl",
        String.class,
        String.class );

    m.setAccessible( true );

    String result = (String) m.invoke(
      handler,
      "http://localhost:8080/pentaho",
      "http://localhost:8183/callback" );

    assertTrue( result.contains( "/browser-auth?callback=" ) );
  }

  @Test
  public void testBuildAuthUrlWithProvider() throws Exception {

    BrowserLoginHandler handler = new BrowserLoginHandler();

    OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri( "http://oauth" );

    handler.setOAuthProvider( provider );

    Method m = BrowserLoginHandler.class
      .getDeclaredMethod( "buildAuthUrl",
        String.class,
        String.class );

    m.setAccessible( true );

    String result = (String) m.invoke(
      handler,
      "http://localhost:8080/pentaho",
      "http://localhost/callback" );

    assertTrue( result.contains( "authorizationUri" ) );
  }

  @Test
  public void testWindowsRundll32Path() throws Exception {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    Method m =
      BrowserLoginHandler.class.getDeclaredMethod(
        "windowsRundll32Path" );

    m.setAccessible( true );

    String path =
      (String) m.invoke( handler );

    assertNotNull( path );
    assertTrue( path.contains( "rundll32" ) );
  }

  @Test
  public void testBuildBrowserProcessAllPlatforms() throws Exception {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    Method m =
      BrowserLoginHandler.class.getDeclaredMethod(
        "buildBrowserProcess",
        String.class,
        String.class );

    m.setAccessible( true );

    for ( String os : new String[] { "windows", "mac", "linux" } ) {

      ProcessBuilder pb =
        (ProcessBuilder) m.invoke(
          handler,
          os,
          "http://test" );

      assertNotNull( pb );
    }
  }

  @Test
  public void testBuildAuthUrlProviderWithNullAuthorizationUri() throws Exception {

    BrowserLoginHandler handler = new BrowserLoginHandler();

    OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri( null );

    handler.setOAuthProvider( provider );

    Method m =
      BrowserLoginHandler.class.getDeclaredMethod(
        "buildAuthUrl",
        String.class,
        String.class );

    m.setAccessible( true );

    String result =
      (String) m.invoke(
        handler,
        "http://server",
        "http://callback" );

    assertFalse( result.contains( "authorizationUri" ) );
  }

  @Test
  public void testOpenBrowserDesktopSupportedButBrowseNotSupported()
    throws Exception {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    Desktop desktop =
      mock( Desktop.class );

    when(
      desktop.isSupported(
        Desktop.Action.BROWSE ) )
      .thenReturn( false );

    try (
      MockedStatic<Desktop> ds =
        mockStatic( Desktop.class );

      MockedConstruction<ProcessBuilder> pbc =
        mockConstruction( ProcessBuilder.class )
    ) {

      ds.when(
          Desktop::isDesktopSupported )
        .thenReturn( true );

      ds.when(
          Desktop::getDesktop )
        .thenReturn( desktop );

      handler.openBrowser(
        "http://localhost" );

      assertFalse(
        pbc.constructed().isEmpty() );
    }
  }

  @Test
  public void testOpenBrowserUnknownOsReturnsWithoutStartingProcess()
    throws Exception {

    BrowserLoginHandler handler =
      spy( new BrowserLoginHandler() );

    try (
      MockedStatic<Desktop> ds =
        mockStatic( Desktop.class )
    ) {

      ds.when(
          Desktop::isDesktopSupported )
        .thenReturn( false );

      doReturn( null )
        .when( handler )
        .buildBrowserProcess(
          anyString(),
          anyString() );

      handler.openBrowser(
        "http://localhost" );

      verify( handler )
        .buildBrowserProcess(
          org.mockito.ArgumentMatchers.anyString(),
          org.mockito.ArgumentMatchers.eq( "http://localhost" ) );
    }
  }

  @Test
  public void testBuildBrowserProcessUnixNix() throws Exception {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    Method m =
      BrowserLoginHandler.class.getDeclaredMethod(
        "buildBrowserProcess",
        String.class,
        String.class );

    m.setAccessible( true );

    ProcessBuilder pb =
      (ProcessBuilder) m.invoke(
        handler,
        "unix",
        "http://test" );

    assertNotNull( pb );
  }

  @Test
  public void testWindowsRundll32PathUsesSystemRoot()
    throws Exception {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    Method m =
      BrowserLoginHandler.class.getDeclaredMethod(
        "windowsRundll32Path" );

    m.setAccessible( true );

    String path =
      (String) m.invoke(
        handler );

    assertNotNull( path );
  }

  @Test
  public void testXdgOpenPathDefaultExecutable()
    throws Exception {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    Method m =
      BrowserLoginHandler.class.getDeclaredMethod(
        "xdgOpenPath" );

    m.setAccessible( true );

    try (
      MockedConstruction<java.io.File> mc =
        mockConstruction(
          java.io.File.class,
          ( mockFile, ctx ) ->
            when(
              mockFile.canExecute() )
              .thenReturn( true ) )
    ) {

      String result =
        (String) m.invoke(
          handler );

      assertEquals(
        "/usr/bin/xdg-open",
        result );
    }
  }

  @Test
  public void testCallbackHandlerEmptyErrorFallsThrough()
    throws Exception {

    BrowserLoginHandler handler =
      handlerWithFreshFuture(
        "STATE" );

    HttpHandler ch =
      newCallbackHandler(
        handler );

    ch.handle(
      mockExchange(
        "GET",
        "state=STATE&error=&jsessionid=sid1&username=admin" ) );

    assertTrue(
      sessionFutureOf( handler ).isDone() );
  }

  @Test
  public void testParseQueryParamsWithEmptyQuery()
    throws Exception {

    BrowserLoginHandler handler =
      handlerWithFreshFuture(
        "STATE" );

    HttpHandler ch =
      newCallbackHandler(
        handler );

    ch.handle(
      mockExchange(
        "GET",
        "" ) );

    assertTrue(
      sessionFutureOf( handler )
        .isCompletedExceptionally() );
  }

  @Test
  public void testParseQueryParamsWithNullQuery()
    throws Exception {

    BrowserLoginHandler handler =
      handlerWithFreshFuture(
        "STATE" );

    HttpHandler ch =
      newCallbackHandler(
        handler );

    ch.handle(
      mockExchange(
        "GET",
        null ) );

    assertTrue(
      sessionFutureOf( handler )
        .isCompletedExceptionally() );
  }

  @Test
  public void testBuildBrowserProcessUnknownOs() throws Exception {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    Method m =
      BrowserLoginHandler.class.getDeclaredMethod(
        "buildBrowserProcess",
        String.class,
        String.class );

    m.setAccessible( true );

    Object pb =
      m.invoke(
        handler,
        "abcxyz",
        "http://test" );

    assertNull( pb );
  }

  @Test
  public void testCleanupResetsState() throws Exception {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    CompletableFuture<
      BrowserLoginHandler.SessionData> future =
      new CompletableFuture<>();

    Field futureField =
      BrowserLoginHandler.class
        .getDeclaredField( "sessionFuture" );

    futureField.setAccessible( true );
    futureField.set( handler, future );

    Method cleanup =
      BrowserLoginHandler.class
        .getDeclaredMethod( "cleanup" );

    cleanup.setAccessible( true );
    cleanup.invoke( handler );

    assertTrue( future.isCancelled() );
  }

  @Test
  public void testVerifySessionWithRetryInterruptedReturnsFalse() {

    AuthenticationData data = mock( AuthenticationData.class );

    Thread.currentThread().interrupt();

    try {
      BrowserLoginHandler handler = new BrowserLoginHandler();

      assertFalse(
        handler.verifySessionWithRetry( data ) );
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  public void testBuildAuthUrlWithOAuthProvider() throws Exception {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    OAuthProvider provider =
      new OAuthProvider();

    provider.setAuthorizationUri(
      "http://oauth" );

    handler.setOAuthProvider( provider );

    Method m =
      BrowserLoginHandler.class
        .getDeclaredMethod(
          "buildAuthUrl",
          String.class,
          String.class );

    m.setAccessible( true );

    String result =
      (String) m.invoke(
        handler,
        "http://server",
        "http://callback" );

    assertTrue(
      result.contains(
        "authorizationUri" ) );
  }

  @Test
  public void testStartBrowserLoginInvalidSession()
    throws Exception {

    BrowserLoginHandler handler =
      spy( new BrowserLoginHandler() );

    BrowserLoginHandler.SessionData session =
      new BrowserLoginHandler.SessionData(
        "sid",
        "admin" );

    doReturn( session )
      .when( handler )
      .performBrowserLogin( anyString() );

    doReturn( false )
      .when( handler )
      .verifySessionWithRetry( any() );

    AuthenticationData result =
      handler.startBrowserLogin(
        "http://server" );

    assertNull( result );
  }

  @Test
  public void testStartBrowserLoginSuccess()
    throws Exception {

    BrowserLoginHandler handler =
      spy( new BrowserLoginHandler() );

    BrowserLoginHandler.SessionData session =
      new BrowserLoginHandler.SessionData(
        "sid123",
        "admin" );

    doReturn( session )
      .when( handler )
      .performBrowserLogin( anyString() );

    doReturn( true )
      .when( handler )
      .verifySessionWithRetry( any() );

    AuthenticationData result =
      handler.startBrowserLogin(
        "http://server" );

    assertNotNull( result );

    assertEquals(
      "sid123",
      result.getOption( "sessionId" ) );

    assertEquals(
      "true",
      result.getOption( "browserAuth" ) );
  }

  @Test
  public void testStartBrowserLoginIOExceptionReturnsNull() {

    BrowserLoginHandler handler =
      mock(
        BrowserLoginHandler.class,
        CALLS_REAL_METHODS );

    try {
      doThrow( new IOException( "boom" ) )
        .when( handler )
        .performBrowserLogin( anyString() );

      AuthenticationData result =
        handler.startBrowserLogin(
          "http://localhost" );

      assertNull( result );

    } catch ( Exception e ) {
      fail( e.getMessage() );
    }
  }

  private static void setField( Object target, String name, Object value ) throws Exception {
    final Field f = BrowserLoginHandler.class.getDeclaredField( name );
    f.setAccessible( true );
    f.set( target, value );
  }

  private static Object getField( Object target, String name ) throws Exception {
    final Field f = BrowserLoginHandler.class.getDeclaredField( name );
    f.setAccessible( true );
    return f.get( target );
  }

  @SuppressWarnings( "unchecked" )
  private static CompletableFuture<BrowserLoginHandler.SessionData> sessionFutureOf( BrowserLoginHandler handler )
    throws Exception {
    return (CompletableFuture<BrowserLoginHandler.SessionData>) getField( handler, "sessionFuture" );
  }

  private static String awaitCallbackState( BrowserLoginHandler handler ) throws Exception {
    for ( int i = 0; i < 200; i++ ) {
      final Object state = getField( handler, "callbackState" );
      if ( state != null ) {
        return (String) state;
      }
      java.util.concurrent.locks.LockSupport.parkNanos(
        java.util.concurrent.TimeUnit.MILLISECONDS.toNanos( 25 ) );
    }
    return null;
  }

  private static int httpGet( String urlStr ) throws Exception {
    final HttpURLConnection conn =
      (HttpURLConnection) URI.create( urlStr )
        .toURL()
        .openConnection();
    conn.setRequestMethod( "GET" );
    conn.setConnectTimeout( 5000 );
    conn.setReadTimeout( 5000 );
    final int code = conn.getResponseCode();
    final java.io.InputStream is = ( code >= 400 ) ? conn.getErrorStream() : conn.getInputStream();
    if ( is != null ) {
      is.readAllBytes();
      is.close();
    }
    conn.disconnect();
    return code;
  }

  @Test( timeout = 30000 )
  public void testPerformBrowserLoginSuccessViaCallback() throws Exception {
    final BrowserLoginHandler handler = spy( new BrowserLoginHandler() );
    doNothing().when( handler ).openBrowser( anyString() );

    final BrowserLoginHandler.SessionData[] out = new BrowserLoginHandler.SessionData[ 1 ];
    final Thread t = new Thread( () -> {
      try {
        out[ 0 ] = handler.performBrowserLogin( "http://localhost:8080/pentaho/" );
      } catch ( IOException ignored ) {
        // ignored — test asserts via out[0]
      }
    } );
    t.setDaemon( true );
    t.start();

    final String state = awaitCallbackState( handler );
    assertNotNull( state );

    final int code = httpGet( "http://localhost:8183/pentaho-auth-callback?state="
      + URLEncoder.encode( state, StandardCharsets.UTF_8 ) + "&jsessionid=sid-1&username=bob" );
    assertEquals( 200, code );

    t.join( 15000 );
    assertNotNull( out[ 0 ] );
    assertEquals( "sid-1", out[ 0 ].getSessionId() );
    assertEquals( "bob", out[ 0 ].getUsername() );
  }

  @Test( timeout = 30000 )
  public void testPerformBrowserLoginErrorCallbackReturnsNull() throws Exception {
    final BrowserLoginHandler handler = spy( new BrowserLoginHandler() );
    doNothing().when( handler ).openBrowser( anyString() );

    final BrowserLoginHandler.SessionData[] out =
      new BrowserLoginHandler.SessionData[] { new BrowserLoginHandler.SessionData( "x", "y" ) };
    final Thread t = new Thread( () -> {
      try {
        out[ 0 ] = handler.performBrowserLogin( "http://localhost:8080/pentaho" );
      } catch ( IOException ignored ) {
        // ignored
      }
    } );
    t.setDaemon( true );
    t.start();

    final String state = awaitCallbackState( handler );
    assertNotNull( state );

    final int code = httpGet( "http://localhost:8183/pentaho-auth-callback?state="
      + URLEncoder.encode( state, StandardCharsets.UTF_8 ) + "&error=accessdenied" );
    assertEquals( 400, code );

    t.join( 15000 );
    assertNull( out[ 0 ] );
  }

  @Test( timeout = 30000 )
  public void testPerformBrowserLoginInterruptedReturnsNull() throws Exception {
    final BrowserLoginHandler handler = spy( new BrowserLoginHandler() );
    doNothing().when( handler ).openBrowser( anyString() );

    final BrowserLoginHandler.SessionData[] out =
      new BrowserLoginHandler.SessionData[] { new BrowserLoginHandler.SessionData( "x", "y" ) };
    final Thread t = new Thread( () -> {
      try {
        out[ 0 ] = handler.performBrowserLogin( "http://localhost:8080/pentaho" );
      } catch ( IOException ignored ) {
        // ignored
      }
    } );
    t.setDaemon( true );
    t.start();

    assertNotNull( awaitCallbackState( handler ) );
    t.interrupt();
    t.join( 15000 );
    assertNull( out[ 0 ] );
  }

  @Test
  public void testStartBrowserLoginPopulatesOAuthOptions() throws Exception {
    final BrowserLoginHandler handler = spy( new BrowserLoginHandler() );
    final OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri( "http://auth" );
    provider.setClientName( "Google" );
    provider.setRegistrationId( "google" );
    handler.setOAuthProvider( provider );

    doReturn( new BrowserLoginHandler.SessionData( "sid", "admin" ) ).when( handler )
      .performBrowserLogin( anyString() );
    doReturn( true ).when( handler ).verifySessionWithRetry( any() );

    final AuthenticationData result = handler.startBrowserLogin( "http://server" );
    assertNotNull( result );
    assertEquals( "http://auth", result.getOption( "oauthAuthorizationUri" ) );
    assertEquals( "Google", result.getOption( "oauthClientName" ) );
    assertEquals( "google", result.getOption( "oauthRegistrationId" ) );
  }

  @Test
  public void testStartBrowserLoginStoresOAuthAuthorizationUri() throws Exception {
    final BrowserLoginHandler handler = spy( new BrowserLoginHandler() );
    final OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri(
      "http://localhost:8080/pentaho/oauth2/authorization/azure" );
    provider.setClientName( "Azure" );
    provider.setRegistrationId( "azure" );
    handler.setOAuthProvider( provider );

    doReturn( new BrowserLoginHandler.SessionData( "sid", "admin" ) )
      .when( handler ).performBrowserLogin( anyString() );
    doReturn( true ).when( handler ).verifySessionWithRetry( any() );

    final AuthenticationData result = handler.startBrowserLogin( "http://server" );

    assertNotNull( result );
    assertEquals(
      "http://localhost:8080/pentaho/oauth2/authorization/azure",
      result.getOption( "oauthAuthorizationUri" ) );
  }

  @Test
  public void testStartBrowserLoginProviderWithNullFieldsSetsNoOptions() throws Exception {
    final BrowserLoginHandler handler = spy( new BrowserLoginHandler() );
    handler.setOAuthProvider( new OAuthProvider() ); // all fields null

    doReturn( new BrowserLoginHandler.SessionData( "sid", "admin" ) ).when( handler )
      .performBrowserLogin( anyString() );
    doReturn( true ).when( handler ).verifySessionWithRetry( any() );

    final AuthenticationData result = handler.startBrowserLogin( "http://server" );
    assertNotNull( result );
    assertNull( result.getOption( "oauthAuthorizationUri" ) );
    assertNull( result.getOption( "oauthClientName" ) );
    assertNull( result.getOption( "oauthRegistrationId" ) );
  }

  @Test
  public void testStartBrowserLoginNullSessionReturnsNull() throws Exception {
    final BrowserLoginHandler handler = spy( new BrowserLoginHandler() );
    doReturn( null ).when( handler ).performBrowserLogin( anyString() );
    assertNull( handler.startBrowserLogin( "http://server" ) );
  }

  @Test
  public void testPerformBrowserLoginWithRetryCancelledReturnsNull() {
    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( true ) ) ) {
      bg.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( any(), any(), any(), any() ) )
        .thenAnswer( inv -> null );
      assertNull( BrowserLoginHandler.performBrowserLoginWithRetry( null, "http://server", null ) );
    }
  }

  @Test
  public void testPerformBrowserLoginWithRetrySuccessReturnsResult() {
    final AuthenticationData authData = mock( AuthenticationData.class );
    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( false ) );
          MockedConstruction<BrowserLoginHandler> blh = mockConstruction( BrowserLoginHandler.class,
            ( m, c ) -> when( m.startBrowserLogin( anyString() ) ).thenReturn( authData ) ) ) {
      bg.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( any(), any(), any(), any() ) )
        .thenAnswer( inv -> {
          ( (Thread) inv.getArgument( 0 ) ).run();
          return null;
        } );
      final OAuthProvider provider = new OAuthProvider();
      assertEquals( authData, BrowserLoginHandler.performBrowserLoginWithRetry( null, "http://server", provider ) );
    }
  }

  @Test
  public void testPerformBrowserLoginWithRetryFailureUserDeclinesReturnsNull() {
    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedStatic<JOptionPane> jop = mockStatic( JOptionPane.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( false ) );
          MockedConstruction<BrowserLoginHandler> blh = mockConstruction( BrowserLoginHandler.class,
            ( m, c ) -> when( m.startBrowserLogin( anyString() ) ).thenReturn( null ) ) ) {
      bg.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( any(), any(), any(), any() ) )
        .thenAnswer( inv -> {
          ( (Thread) inv.getArgument( 0 ) ).run();
          return null;
        } );
      jop.when( () -> JOptionPane.showOptionDialog( any(), any(), any(), org.mockito.ArgumentMatchers.anyInt(),
        org.mockito.ArgumentMatchers.anyInt(), any(), any(), any() ) ).thenReturn( JOptionPane.NO_OPTION );
      assertNull( BrowserLoginHandler.performBrowserLoginWithRetry( null, "http://server", null ) );
    }
  }

  @Test
  public void testPerformBrowserLoginWithRetryUserRetriesThenCancels() {
    try ( MockedStatic<BackgroundCancellableProcessHelper> bg = mockStatic( BackgroundCancellableProcessHelper.class );
          MockedStatic<JOptionPane> jop = mockStatic( JOptionPane.class );
          MockedConstruction<GenericCancelHandler> gch = mockConstruction( GenericCancelHandler.class,
            ( m, c ) -> when( m.isCancelled() ).thenReturn( c.getCount() >= 2 ) );
          MockedConstruction<BrowserLoginHandler> blh = mockConstruction( BrowserLoginHandler.class,
            ( m, c ) -> when( m.startBrowserLogin( anyString() ) ).thenReturn( null ) ) ) {
      bg.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( any(), any(), any(), any() ) )
        .thenAnswer( inv -> {
          ( (Thread) inv.getArgument( 0 ) ).run();
          return null;
        } );
      jop.when( () -> JOptionPane.showOptionDialog( any(), any(), any(), org.mockito.ArgumentMatchers.anyInt(),
        org.mockito.ArgumentMatchers.anyInt(), any(), any(), any() ) ).thenReturn( JOptionPane.YES_OPTION );
      assertNull( BrowserLoginHandler.performBrowserLoginWithRetry( null, "http://server", null ) );
    }
  }

  @Test
  public void testBuildAuthUrlTrimsTrailingSlash() throws Exception {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final Method m = BrowserLoginHandler.class.getDeclaredMethod( "buildAuthUrl", String.class, String.class );
    m.setAccessible( true );
    final String result = (String) m.invoke( handler, "http://localhost:8080/pentaho/", "http://localhost/callback" );
    assertTrue( result.startsWith( "http://localhost:8080/pentaho/plugin/login" ) );
    assertFalse( result.startsWith( "http://localhost:8080/pentaho//plugin" ) );
  }

  @Test
  public void testBuildAuthUrlEmptyProviderAuthUriUsesDefaultFlow() throws Exception {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri( "   " ); // blank → default flow
    handler.setOAuthProvider( provider );
    final Method m = BrowserLoginHandler.class.getDeclaredMethod( "buildAuthUrl", String.class, String.class );
    m.setAccessible( true );
    final String result = (String) m.invoke( handler, "http://localhost:8080/pentaho", "http://localhost/callback" );
    assertFalse( result.contains( "authorizationUri" ) );
  }

  @Test
  public void testResolveCallbackHostReturnsLocalhost() {

    BrowserLoginHandler handler =
      new BrowserLoginHandler();

    String[] urls = {
      "http://192.168.1.50:8080/pentaho",
      null,
      "",
      "http://localhost:8080/pentaho",
      "http://127.0.0.1:8080/pentaho"
    };

    for ( String url : urls ) {

      assertEquals(
        "localhost",
        handler.resolveCallbackHost( url ) );
    }
  }

  @Test
  public void testResolveCallbackHostUriWithoutHostFallsBackToLocalhost() {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    assertEquals( "localhost", handler.resolveCallbackHost( "file:/tmp/pentaho" ) );
  }

  @Test
  public void testBuildAuthUrlSendsAuthorizationUriUnchanged() throws Exception {
    // The server expects authorizationUri as a relative provider path; it must be sent as-is.
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri( "oauth2/authorization/azure" );
    handler.setOAuthProvider( provider );
    final Method m = BrowserLoginHandler.class.getDeclaredMethod( "buildAuthUrl", String.class, String.class );
    m.setAccessible( true );
    final String result = (String) m.invoke( handler,
      "https://172.16.13.216:8443/pentaho",
      "http://localhost:8183/pentaho-auth-callback?state=xyz" );
    assertTrue( "authorizationUri must be sent unchanged",
      result.contains(
        "authorizationUri=" + URLEncoder.encode( "oauth2/authorization/azure", StandardCharsets.UTF_8 ) ) );
  }

  @Test
  public void testWindowsRundll32PathFallsBackWhenSystemRootMissing() throws Exception {
    final BrowserLoginHandler handler = mock( BrowserLoginHandler.class, CALLS_REAL_METHODS );
    doReturn( null ).when( handler ).getSystemRootEnv();

    final Method m = BrowserLoginHandler.class.getDeclaredMethod( "windowsRundll32Path" );
    m.setAccessible( true );

    final String path = (String) m.invoke( handler );
    assertEquals( "C:\\Windows\\System32\\rundll32.exe", path );
  }

  @Test
  public void testWindowsRundll32PathFallsBackWhenSystemRootEmpty() throws Exception {
    final BrowserLoginHandler handler = mock( BrowserLoginHandler.class, CALLS_REAL_METHODS );
    doReturn( "" ).when( handler ).getSystemRootEnv();

    final Method m = BrowserLoginHandler.class.getDeclaredMethod( "windowsRundll32Path" );
    m.setAccessible( true );

    final String path = (String) m.invoke( handler );
    assertEquals( "C:\\Windows\\System32\\rundll32.exe", path );
  }

  @Test
  public void testXdgOpenPathReturnsAlternateWhenLocalBinExecutable() throws Exception {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final Method m = BrowserLoginHandler.class.getDeclaredMethod( "xdgOpenPath" );
    m.setAccessible( true );
    try ( MockedConstruction<java.io.File> mc = mockConstruction( java.io.File.class,
      ( mockFile, ctx ) -> {
        final String p = String.valueOf( ctx.arguments().get( 0 ) );
        when( mockFile.canExecute() ).thenReturn( "/usr/local/bin/xdg-open".equals( p ) );
      } ) ) {
      final String result = (String) m.invoke( handler );
      assertEquals( "/usr/local/bin/xdg-open", result );
    }
  }

  @Test
  public void testXdgOpenPathReturnsDefaultWhenNeitherExecutable() throws Exception {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final Method m = BrowserLoginHandler.class.getDeclaredMethod( "xdgOpenPath" );
    m.setAccessible( true );
    try ( MockedConstruction<java.io.File> mc = mockConstruction( java.io.File.class,
      ( mockFile, ctx ) -> when( mockFile.canExecute() ).thenReturn( false ) ) ) {
      final String result = (String) m.invoke( handler );
      assertEquals( "/usr/bin/xdg-open", result );
    }
  }

  @Test
  public void testCleanupStopsCallbackServer() throws Exception {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final HttpServer server = mock( HttpServer.class );
    setField( handler, "callbackServer", server );

    final Method cleanup = BrowserLoginHandler.class.getDeclaredMethod( "cleanup" );
    cleanup.setAccessible( true );
    cleanup.invoke( handler );

    verify( server ).stop( 1 );
    assertNull( getField( handler, "callbackServer" ) );
  }

  @Test
  public void testOpenBrowserUsesDesktopWhenSupported() throws Exception {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    final java.awt.Desktop desktop = mock( java.awt.Desktop.class );
    when( desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ).thenReturn( true );
    try ( MockedStatic<java.awt.Desktop> ds = mockStatic( java.awt.Desktop.class ) ) {
      ds.when( java.awt.Desktop::isDesktopSupported ).thenReturn( true );
      ds.when( java.awt.Desktop::getDesktop ).thenReturn( desktop );
      handler.openBrowser( "http://localhost/x" );
      verify( desktop ).browse( any( URI.class ) );
    }
  }

  @Test
  public void testOpenBrowserFallsBackToProcessBuilder() throws Exception {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    try ( MockedStatic<java.awt.Desktop> ds = mockStatic( java.awt.Desktop.class );
          MockedConstruction<ProcessBuilder> pbc = mockConstruction( ProcessBuilder.class ) ) {
      ds.when( java.awt.Desktop::isDesktopSupported ).thenReturn( false );
      handler.openBrowser( "http://localhost/x" );
      assertFalse( pbc.constructed().isEmpty() );
      verify( pbc.constructed().get( 0 ) ).start();
    }
  }

  private HttpHandler newCallbackHandler( BrowserLoginHandler outer ) throws Exception {
    final Class<?> ch = Class.forName(
      "org.pentaho.reporting.designer.extensions.pentaho.repository.auth.BrowserLoginHandler$CallbackHandler" );
    final Constructor<?> ctor = ch.getDeclaredConstructor( BrowserLoginHandler.class );
    ctor.setAccessible( true );
    return (HttpHandler) ctor.newInstance( outer );
  }

  private HttpExchange mockExchange( String method, String query ) throws Exception {
    final HttpExchange ex = mock( HttpExchange.class );
    when( ex.getRequestMethod() ).thenReturn( method );
    when( ex.getRequestURI() ).thenReturn( new URI( "http://localhost/cb" + ( query == null ? "" : "?" + query ) ) );
    when( ex.getResponseHeaders() ).thenReturn( new Headers() );
    when( ex.getResponseBody() ).thenReturn( new ByteArrayOutputStream() );
    return ex;
  }

  private BrowserLoginHandler handlerWithFreshFuture( String state ) throws Exception {
    final BrowserLoginHandler handler = new BrowserLoginHandler();
    setField( handler, "sessionFuture", new CompletableFuture<BrowserLoginHandler.SessionData>() );
    setField( handler, "callbackState", state );
    return handler;
  }

  @Test
  public void testCallbackHandlerGetSuccessCompletesFuture() throws Exception {
    final BrowserLoginHandler handler = handlerWithFreshFuture( "STATE" );
    final HttpHandler ch = newCallbackHandler( handler );
    ch.handle( mockExchange( "GET", "state=STATE&jsessionid=sid-9&username=alice" ) );

    final CompletableFuture<BrowserLoginHandler.SessionData> future = sessionFutureOf( handler );
    assertTrue( future.isDone() );
    assertFalse( future.isCompletedExceptionally() );
    assertEquals( "sid-9", future.get().getSessionId() );
  }

  @Test
  public void testCallbackHandlerGetUsesSessionIdFallback() throws Exception {
    final BrowserLoginHandler handler = handlerWithFreshFuture( "STATE" );
    final HttpHandler ch = newCallbackHandler( handler );
    // no jsessionid → falls back to sessionId param; also exercises a pair with no '='
    ch.handle( mockExchange( "GET", "state=STATE&novalue&sessionId=sid-fallback&username=bob" ) );

    final CompletableFuture<BrowserLoginHandler.SessionData> future = sessionFutureOf( handler );
    assertTrue( future.isDone() );
    assertEquals( "sid-fallback", future.get().getSessionId() );
  }

  @Test
  public void testCallbackHandlerPostSuccessCompletesFuture() throws Exception {
    final BrowserLoginHandler handler = handlerWithFreshFuture( "STATE" );
    final HttpHandler ch = newCallbackHandler( handler );
    ch.handle( mockExchange( "POST", "state=STATE&jsessionid=sid-post&username=carol" ) );

    final CompletableFuture<BrowserLoginHandler.SessionData> future = sessionFutureOf( handler );
    assertTrue( future.isDone() );
    assertEquals( "sid-post", future.get().getSessionId() );
  }

  @Test
  public void testCallbackHandlerMethodNotAllowedDoesNotCompleteFuture() throws Exception {
    final BrowserLoginHandler handler = handlerWithFreshFuture( "STATE" );
    final HttpHandler ch = newCallbackHandler( handler );
    ch.handle( mockExchange( "DELETE", "state=STATE" ) );

    assertFalse( sessionFutureOf( handler ).isDone() );
  }

  @Test
  public void testCallbackHandlerErrorParamCompletesExceptionally() throws Exception {
    final BrowserLoginHandler handler = handlerWithFreshFuture( "STATE" );
    final HttpHandler ch = newCallbackHandler( handler );
    ch.handle( mockExchange( "GET", "state=STATE&error="
      + URLEncoder.encode( "<bad> & \"quote\" 'x'", StandardCharsets.UTF_8 ) ) );

    assertTrue( sessionFutureOf( handler ).isCompletedExceptionally() );
  }

  @Test
  public void testInvalidCallbackCasesCompleteExceptionally() throws Exception {

    Object[][] cases = {
      { "STATE", "state=WRONG&jsessionid=sid&username=u" },
      { null, "state=STATE&jsessionid=sid" },
      { "STATE", null },
      { "STATE", "state=STATE&username=u" },
      { "STATE", "state=STATE&jsessionid=&username=u" }
    };

    for ( Object[] c : cases ) {
      BrowserLoginHandler handler =
        handlerWithFreshFuture( (String) c[ 0 ] );

      HttpHandler ch = newCallbackHandler( handler );

      ch.handle( mockExchange( "GET", (String) c[ 1 ] ) );

      assertTrue( sessionFutureOf( handler ).isCompletedExceptionally() );
    }
  }

  @Test
  public void testCallbackHandlerEscapeHtmlNullReturnsEmpty() throws Exception {
    final BrowserLoginHandler handler = handlerWithFreshFuture( "STATE" );
    final HttpHandler ch = newCallbackHandler( handler );
    final Method escapeHtml = ch.getClass().getDeclaredMethod( "escapeHtml", String.class );
    escapeHtml.setAccessible( true );
    assertEquals( "", escapeHtml.invoke( ch, (Object) null ) );
  }
}

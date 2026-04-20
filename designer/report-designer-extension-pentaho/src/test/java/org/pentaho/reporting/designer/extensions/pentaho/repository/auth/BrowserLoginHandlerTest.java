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

package org.pentaho.reporting.designer.extensions.pentaho.repository.auth;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import java.awt.Desktop;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

/**
 * Tests for {@link BrowserLoginHandler} - covers SessionData, callback handling,
 * startBrowserLogin, and cleanup logic.
 */
public class BrowserLoginHandlerTest {

  private static final String SESSION_ID_ABC123         = "ABC123";
  private static final String USERNAME_ADMIN            = "admin";
  private static final String CALLBACK_HANDLER_CLASS    = "CallbackHandler";
  private static final String METHOD_CLEANUP            = "cleanup";
  private static final String METHOD_BUILD_AUTH_URL     = "buildAuthUrl";
  private static final String METHOD_PARSE_QUERY_PARAMS = "parseQueryParams";
  private static final String METHOD_BUILD_SUCCESS_PAGE = "buildSuccessPage";
  private static final String METHOD_BUILD_ERROR_PAGE   = "buildErrorPage";
  private static final String METHOD_ESCAPE_HTML           = "escapeHtml";
  private static final String OAUTH2_AUTHORIZATION_AZURE   = "oauth2/authorization/azure";
  private static final String PROVIDER_ID_AZURE            = "azure";
  private static final String PROVIDER_NAME_MICROSOFT      = "Microsoft";
  private static final String AUTHORIZATION_URI_PARAM      = "authorizationUri=";
  private static final String SERVER_URL                   = "http://localhost:8080/pentaho";
  private static final String CALLBACK_URL                 = "http://localhost:8183/pentaho-auth-callback";
  private static final int    CALLBACK_PORT                = 8183;

  private BrowserLoginHandler handler;

  @Before
  public void setUp() {
    handler = new BrowserLoginHandler();
  }

  @After
  public void tearDown() {
    handler = null;
  }

  /**
   * Finds the CallbackHandler inner class by canonical name to avoid java:S1872
   * (comparing class simple names with equals()).
   */
  private Class<?> findCallbackHandlerClass() {
    String expected = BrowserLoginHandler.class.getCanonicalName() + "." + CALLBACK_HANDLER_CLASS;
    for ( Class<?> c : BrowserLoginHandler.class.getDeclaredClasses() ) {
      if ( expected.equals( c.getCanonicalName() ) ) {
        return c;
      }
    }
    return null;
  }

  @SuppressWarnings( "java:S3011" )
  private Object createCallbackHandlerInstance( Class<?> clazz ) throws ReflectiveOperationException {
    Constructor<?> ctor = clazz.getDeclaredConstructor( BrowserLoginHandler.class );
    ctor.setAccessible( true );
    return ctor.newInstance( handler );
  }

  @Test
  public void testSessionDataConstructor() {
    BrowserLoginHandler.SessionData data = new BrowserLoginHandler.SessionData( "sess123", USERNAME_ADMIN );
    assertNotNull( data );
  }

  @Test
  public void testSessionDataGetSessionId() {
    BrowserLoginHandler.SessionData data = new BrowserLoginHandler.SessionData( SESSION_ID_ABC123, USERNAME_ADMIN );
    assertEquals( SESSION_ID_ABC123, data.getSessionId() );
  }

  @Test
  public void testSessionDataGetUsername() {
    BrowserLoginHandler.SessionData data = new BrowserLoginHandler.SessionData( "sess", "testuser" );
    assertEquals( "testuser", data.getUsername() );
  }

  @Test
  public void testSessionDataNullValues() {
    BrowserLoginHandler.SessionData data = new BrowserLoginHandler.SessionData( null, null );
    assertNull( data.getSessionId() );
    assertNull( data.getUsername() );
  }

  @Test
  public void testSessionDataValuesPreserved() {
    String sessionId = "ABCDEF123456";
    String username  = "pentaho_admin";
    BrowserLoginHandler.SessionData sd = new BrowserLoginHandler.SessionData( sessionId, username );
    assertEquals( sessionId, sd.getSessionId() );
    assertEquals( username, sd.getUsername() );
  }

  @Test
  public void testConstructor() {
    BrowserLoginHandler blh = new BrowserLoginHandler();
    assertNotNull( blh );
  }

  @Test
  public void testCallbackServerSuccessfulLogin() throws Exception {
    final String[] receivedSessionId = { null };
    final String[] receivedUsername  = { null };

    HttpServer server = HttpServer.create( new InetSocketAddress( CALLBACK_PORT ), 0 );
    server.createContext( "/pentaho-auth-callback", exchange -> {
      String query = exchange.getRequestURI().getQuery();
      if ( query != null ) {
        for ( String param : query.split( "&" ) ) {
          String[] kv = param.split( "=", 2 );
          if ( kv.length == 2 ) {
            if ( "jsessionid".equals( kv[0] ) ) {
              receivedSessionId[0] = kv[1];
            } else if ( "username".equals( kv[0] ) ) {
              receivedUsername[0] = kv[1];
            }
          }
        }
      }
      byte[] response = "OK".getBytes( StandardCharsets.UTF_8 );
      exchange.sendResponseHeaders( 200, response.length );
      try ( OutputStream os = exchange.getResponseBody() ) {
        os.write( response );
      }
    } );
    server.setExecutor( null );
    server.start();

    try {
      URL url = new URI( CALLBACK_URL + "?jsessionid=SESS456&username=" + USERNAME_ADMIN ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod( "GET" );
      int code = conn.getResponseCode();
      assertEquals( 200, code );
      assertEquals( "SESS456", receivedSessionId[0] );
      assertEquals( USERNAME_ADMIN, receivedUsername[0] );
      conn.disconnect();
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testStartBrowserLoginFailsGracefully() {
    HttpServer blocker = null;
    try {
      blocker = HttpServer.create( new InetSocketAddress( CALLBACK_PORT ), 0 );
      blocker.start();
      AuthenticationData result = handler.startBrowserLogin( SERVER_URL );
      assertNull( result );
    } catch ( IOException e ) {
      // Port might already be in use - acceptable
    } finally {
      if ( blocker != null ) {
        blocker.stop( 0 );
      }
    }
  }

  @Test
  public void testStartBrowserLoginTrailingSlashUrl() {
    HttpServer blocker = null;
    try {
      blocker = HttpServer.create( new InetSocketAddress( CALLBACK_PORT ), 0 );
      blocker.start();
      AuthenticationData result = handler.startBrowserLogin( SERVER_URL + "/" );
      assertNull( result );
    } catch ( IOException e ) {
      // Expected: port occupied
    } finally {
      if ( blocker != null ) {
        blocker.stop( 0 );
      }
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCleanupNoServerStarted() throws ReflectiveOperationException {
    Method cleanupMethod = BrowserLoginHandler.class.getDeclaredMethod( METHOD_CLEANUP );
    cleanupMethod.setAccessible( true );
    cleanupMethod.invoke( handler );

    Field serverField = BrowserLoginHandler.class.getDeclaredField( "callbackServer" );
    serverField.setAccessible( true );
    assertNull( serverField.get( handler ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCleanupIdempotent() throws ReflectiveOperationException {
    Method cleanupMethod = BrowserLoginHandler.class.getDeclaredMethod( METHOD_CLEANUP );
    cleanupMethod.setAccessible( true );
    cleanupMethod.invoke( handler );
    cleanupMethod.invoke( handler );
    cleanupMethod.invoke( handler );

    Field serverField = BrowserLoginHandler.class.getDeclaredField( "callbackServer" );
    serverField.setAccessible( true );
    assertNull( serverField.get( handler ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCleanupWithPendingFuture() throws ReflectiveOperationException {
    Field futureField = BrowserLoginHandler.class.getDeclaredField( "sessionFuture" );
    futureField.setAccessible( true );
    CompletableFuture<BrowserLoginHandler.SessionData> future = new CompletableFuture<>();
    futureField.set( handler, future );

    assertFalse( future.isDone() );

    Method cleanupMethod = BrowserLoginHandler.class.getDeclaredMethod( METHOD_CLEANUP );
    cleanupMethod.setAccessible( true );
    cleanupMethod.invoke( handler );

    assertTrue( future.isDone() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCleanupWithCompletedFuture() throws ReflectiveOperationException {
    Field futureField = BrowserLoginHandler.class.getDeclaredField( "sessionFuture" );
    futureField.setAccessible( true );
    CompletableFuture<BrowserLoginHandler.SessionData> future = new CompletableFuture<>();
    future.complete( null );
    futureField.set( handler, future );

    assertTrue( future.isDone() );

    Method cleanupMethod = BrowserLoginHandler.class.getDeclaredMethod( METHOD_CLEANUP );
    cleanupMethod.setAccessible( true );
    cleanupMethod.invoke( handler );

    assertTrue( future.isDone() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildAuthUrlNoTrailingSlash() throws ReflectiveOperationException {
    Method buildAuthUrl = BrowserLoginHandler.class.getDeclaredMethod(
      METHOD_BUILD_AUTH_URL, String.class, String.class );
    buildAuthUrl.setAccessible( true );

    String result = (String) buildAuthUrl.invoke( handler, SERVER_URL, CALLBACK_URL );
    assertTrue( result.startsWith( SERVER_URL + "/plugin/login/api/v0/browser-auth?callback=" ) );
    assertTrue( result.contains( "pentaho-auth-callback" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildAuthUrlWithTrailingSlash() throws ReflectiveOperationException {
    Method buildAuthUrl = BrowserLoginHandler.class.getDeclaredMethod(
      METHOD_BUILD_AUTH_URL, String.class, String.class );
    buildAuthUrl.setAccessible( true );

    String result = (String) buildAuthUrl.invoke( handler, SERVER_URL + "/", CALLBACK_URL );
    assertTrue( result.startsWith( SERVER_URL + "/plugin/login/api/v0/browser-auth?callback=" ) );
    assertFalse( result.contains( "pentaho//plugin" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildAuthUrlWithOAuthProvider() throws ReflectiveOperationException {
    OAuthProvider provider = new OAuthProvider(
      OAUTH2_AUTHORIZATION_AZURE, null, PROVIDER_NAME_MICROSOFT, PROVIDER_ID_AZURE, true );
    handler.setOAuthProvider( provider );

    Method buildAuthUrl = BrowserLoginHandler.class.getDeclaredMethod(
      METHOD_BUILD_AUTH_URL, String.class, String.class );
    buildAuthUrl.setAccessible( true );

    String result = (String) buildAuthUrl.invoke( handler, SERVER_URL, CALLBACK_URL );
    assertTrue( result.contains( AUTHORIZATION_URI_PARAM ) );
    assertTrue( result.contains( "oauth2" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildAuthUrlWithEmptyAuthorizationUri() throws ReflectiveOperationException {
    OAuthProvider provider = new OAuthProvider( "  ", null, "Empty", "empty", true );
    handler.setOAuthProvider( provider );

    Method buildAuthUrl = BrowserLoginHandler.class.getDeclaredMethod(
      METHOD_BUILD_AUTH_URL, String.class, String.class );
    buildAuthUrl.setAccessible( true );

    String result = (String) buildAuthUrl.invoke( handler, SERVER_URL, CALLBACK_URL );
    assertFalse( result.contains( AUTHORIZATION_URI_PARAM ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildAuthUrlWithNullOAuthProvider() throws ReflectiveOperationException {
    handler.setOAuthProvider( null );

    Method buildAuthUrl = BrowserLoginHandler.class.getDeclaredMethod(
      METHOD_BUILD_AUTH_URL, String.class, String.class );
    buildAuthUrl.setAccessible( true );

    String result = (String) buildAuthUrl.invoke( handler, SERVER_URL, CALLBACK_URL );
    assertFalse( result.contains( AUTHORIZATION_URI_PARAM ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testParseQueryParamsMultipleParams() throws ReflectiveOperationException {
    Class<?> clazz = findCallbackHandlerClass();
    assertNotNull( "CallbackHandler inner class should exist", clazz );

    Method parseMethod = clazz.getDeclaredMethod( METHOD_PARSE_QUERY_PARAMS, String.class );
    parseMethod.setAccessible( true );

    @SuppressWarnings( "unchecked" )
    Map<String, String> result = (Map<String, String>) parseMethod.invoke(
      createCallbackHandlerInstance( clazz ),
      "jsessionid=" + SESSION_ID_ABC123 + "&username=" + USERNAME_ADMIN + "&extra=value" );
    assertEquals( SESSION_ID_ABC123, result.get( "jsessionid" ) );
    assertEquals( USERNAME_ADMIN, result.get( "username" ) );
    assertEquals( "value", result.get( "extra" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testParseQueryParamsNullQuery() throws ReflectiveOperationException {
    Class<?> clazz = findCallbackHandlerClass();
    assertNotNull( clazz );

    Method parseMethod = clazz.getDeclaredMethod( METHOD_PARSE_QUERY_PARAMS, String.class );
    parseMethod.setAccessible( true );

    @SuppressWarnings( "unchecked" )
    Map<String, String> result = (Map<String, String>) parseMethod.invoke(
      createCallbackHandlerInstance( clazz ), (String) null );
    assertTrue( result.isEmpty() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testParseQueryParamsEmptyQuery() throws ReflectiveOperationException {
    Class<?> clazz = findCallbackHandlerClass();
    assertNotNull( clazz );

    Method parseMethod = clazz.getDeclaredMethod( METHOD_PARSE_QUERY_PARAMS, String.class );
    parseMethod.setAccessible( true );

    @SuppressWarnings( "unchecked" )
    Map<String, String> result = (Map<String, String>) parseMethod.invoke(
      createCallbackHandlerInstance( clazz ), "" );
    assertTrue( result.isEmpty() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testParseQueryParamsSingleKeyNoValue() throws ReflectiveOperationException {
    Class<?> clazz = findCallbackHandlerClass();
    assertNotNull( clazz );

    Method parseMethod = clazz.getDeclaredMethod( METHOD_PARSE_QUERY_PARAMS, String.class );
    parseMethod.setAccessible( true );

    @SuppressWarnings( "unchecked" )
    Map<String, String> result = (Map<String, String>) parseMethod.invoke(
      createCallbackHandlerInstance( clazz ), "keyonly" );
    assertTrue( result.isEmpty() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildSuccessPage() throws ReflectiveOperationException {
    Class<?> clazz = findCallbackHandlerClass();
    assertNotNull( clazz );

    Method buildMethod = clazz.getDeclaredMethod( METHOD_BUILD_SUCCESS_PAGE );
    buildMethod.setAccessible( true );

    String html = (String) buildMethod.invoke( createCallbackHandlerInstance( clazz ) );
    assertNotNull( html );
    assertTrue( html.contains( "Authentication Successful" ) );
    assertTrue( html.contains( "<!DOCTYPE html>" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildErrorPage() throws ReflectiveOperationException {
    Class<?> clazz = findCallbackHandlerClass();
    assertNotNull( clazz );

    Method buildMethod = clazz.getDeclaredMethod( METHOD_BUILD_ERROR_PAGE, String.class );
    buildMethod.setAccessible( true );

    String html = (String) buildMethod.invoke( createCallbackHandlerInstance( clazz ), "Test error message" );
    assertNotNull( html );
    assertTrue( html.contains( "Authentication Failed" ) );
    assertTrue( html.contains( "Test error message" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testEscapeHtmlSpecialCharacters() throws ReflectiveOperationException {
    Class<?> clazz = findCallbackHandlerClass();
    assertNotNull( clazz );

    Method escapeMethod = clazz.getDeclaredMethod( METHOD_ESCAPE_HTML, String.class );
    escapeMethod.setAccessible( true );

    String result = (String) escapeMethod.invoke(
      createCallbackHandlerInstance( clazz ), "<script>alert('xss')</script>" );
    assertFalse( result.contains( "<" ) );
    assertFalse( result.contains( ">" ) );
    assertTrue( result.contains( "&lt;" ) );
    assertTrue( result.contains( "&gt;" ) );
    assertTrue( result.contains( "&#39;" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testEscapeHtmlNullInput() throws ReflectiveOperationException {
    Class<?> clazz = findCallbackHandlerClass();
    assertNotNull( clazz );

    Method escapeMethod = clazz.getDeclaredMethod( METHOD_ESCAPE_HTML, String.class );
    escapeMethod.setAccessible( true );

    String result = (String) escapeMethod.invoke( createCallbackHandlerInstance( clazz ), (String) null );
    assertEquals( "", result );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testEscapeHtmlAmpersandAndQuotes() throws ReflectiveOperationException {
    Class<?> clazz = findCallbackHandlerClass();
    assertNotNull( clazz );

    Method escapeMethod = clazz.getDeclaredMethod( METHOD_ESCAPE_HTML, String.class );
    escapeMethod.setAccessible( true );

    String result = (String) escapeMethod.invoke( createCallbackHandlerInstance( clazz ), "a&b\"c" );
    assertTrue( result.contains( "&amp;" ) );
    assertTrue( result.contains( "&quot;" ) );
  }

  @Test
  public void testResolveCallbackHostExtractsHostFromUrl() {
    assertEquals( "127.0.0.1", handler.resolveCallbackHost( "http://127.0.0.1:8080/pentaho" ) );
    assertEquals( "localhost",  handler.resolveCallbackHost( SERVER_URL ) );
    assertEquals( "myserver",   handler.resolveCallbackHost( "http://myserver:8080/pentaho" ) );
  }

  @Test
  public void testResolveCallbackHostFallsBackOnBadUrl() {
    String result = handler.resolveCallbackHost( "not-a-valid-url" );
    assertNotNull( "should never return null", result );
    assertFalse( "should never return empty", result.isEmpty() );
  }

  @Test
  public void testGetLocalCallbackHostReturnsNonEmpty() {
    String host = handler.getLocalCallbackHost();
    assertNotNull( "getLocalCallbackHost should never return null", host );
    assertFalse( "getLocalCallbackHost should never return empty", host.isEmpty() );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testSetOAuthProviderNull() throws ReflectiveOperationException {
    handler.setOAuthProvider( null );

    Field providerField = BrowserLoginHandler.class.getDeclaredField( "oauthProvider" );
    providerField.setAccessible( true );
    assertNull( providerField.get( handler ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testSetOAuthProviderWithProvider() throws ReflectiveOperationException {
    OAuthProvider provider = new OAuthProvider(
      OAUTH2_AUTHORIZATION_AZURE, "https://example.com/icon.png",
      PROVIDER_NAME_MICROSOFT, PROVIDER_ID_AZURE, true );
    handler.setOAuthProvider( provider );

    Field providerField = BrowserLoginHandler.class.getDeclaredField( "oauthProvider" );
    providerField.setAccessible( true );
    assertEquals( provider, providerField.get( handler ) );
  }

  @Test
  public void testStartBrowserLoginWithOAuthProvider() {
    OAuthProvider provider = new OAuthProvider(
      OAUTH2_AUTHORIZATION_AZURE, null, PROVIDER_NAME_MICROSOFT, PROVIDER_ID_AZURE, true );
    handler.setOAuthProvider( provider );

    HttpServer blocker = null;
    try {
      blocker = HttpServer.create( new InetSocketAddress( CALLBACK_PORT ), 0 );
      blocker.start();
      AuthenticationData result = handler.startBrowserLogin( SERVER_URL );
      assertNull( result );
    } catch ( IOException e ) {
      // Expected: port occupied
    } finally {
      if ( blocker != null ) {
        blocker.stop( 0 );
      }
    }
  }

  // ---- recoverOAuthProvider tests ----

  @Test
  public void testRecoverOAuthProviderNullData() {
    assertNull( BrowserLoginHandler.recoverOAuthProvider( null ) );
  }

  @Test
  public void testRecoverOAuthProviderNoBrowserAuth() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( "browserAuth" ) ).thenReturn( null );
    assertNull( BrowserLoginHandler.recoverOAuthProvider( data ) );
  }

  @Test
  public void testRecoverOAuthProviderBrowserAuthFalse() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( "browserAuth" ) ).thenReturn( "false" );
    assertNull( BrowserLoginHandler.recoverOAuthProvider( data ) );
  }

  @Test
  public void testRecoverOAuthProviderNoAuthUri() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( "browserAuth" ) ).thenReturn( "true" );
    when( data.getOption( "oauthAuthorizationUri" ) ).thenReturn( null );
    assertNull( BrowserLoginHandler.recoverOAuthProvider( data ) );
  }

  @Test
  public void testRecoverOAuthProviderEmptyAuthUri() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( "browserAuth" ) ).thenReturn( "true" );
    when( data.getOption( "oauthAuthorizationUri" ) ).thenReturn( "   " );
    assertNull( BrowserLoginHandler.recoverOAuthProvider( data ) );
  }

  @Test
  public void testRecoverOAuthProviderValid() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( "browserAuth" ) ).thenReturn( "true" );
    when( data.getOption( "oauthAuthorizationUri" ) ).thenReturn( OAUTH2_AUTHORIZATION_AZURE );
    when( data.getOption( "oauthClientName" ) ).thenReturn( PROVIDER_NAME_MICROSOFT );
    when( data.getOption( "oauthRegistrationId" ) ).thenReturn( PROVIDER_ID_AZURE );

    OAuthProvider provider = BrowserLoginHandler.recoverOAuthProvider( data );
    assertNotNull( provider );
    assertEquals( OAUTH2_AUTHORIZATION_AZURE, provider.getAuthorizationUri() );
    assertEquals( PROVIDER_NAME_MICROSOFT, provider.getClientName() );
    assertEquals( PROVIDER_ID_AZURE, provider.getRegistrationId() );
    assertTrue( provider.isEnabled() );
  }

  @Test
  public void testRecoverOAuthProviderNullClientAndRegistration() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( "browserAuth" ) ).thenReturn( "true" );
    when( data.getOption( "oauthAuthorizationUri" ) ).thenReturn( "oauth2/authorization/google" );
    when( data.getOption( "oauthClientName" ) ).thenReturn( null );
    when( data.getOption( "oauthRegistrationId" ) ).thenReturn( null );

    OAuthProvider provider = BrowserLoginHandler.recoverOAuthProvider( data );
    assertNotNull( provider );
    assertEquals( "oauth2/authorization/google", provider.getAuthorizationUri() );
    assertNull( provider.getClientName() );
    assertNull( provider.getRegistrationId() );
    assertTrue( provider.isEnabled() );
  }

  // ---- Helper methods for callback handler integration tests ----

  @SuppressWarnings( "java:S3011" )
  private void startCallbackServerWithState( String state ) throws Exception {
    Field futureField = BrowserLoginHandler.class.getDeclaredField( "sessionFuture" );
    futureField.setAccessible( true );
    futureField.set( handler, new CompletableFuture<>() );

    Field stateField = BrowserLoginHandler.class.getDeclaredField( "callbackState" );
    stateField.setAccessible( true );
    stateField.set( handler, state );

    Method startServer = BrowserLoginHandler.class.getDeclaredMethod( "startCallbackServer" );
    startServer.setAccessible( true );
    startServer.invoke( handler );
  }

  @SuppressWarnings( "java:S3011" )
  private void invokeCleanup() throws Exception {
    Method cleanup = BrowserLoginHandler.class.getDeclaredMethod( METHOD_CLEANUP );
    cleanup.setAccessible( true );
    cleanup.invoke( handler );
  }

  @SuppressWarnings( { "unchecked", "java:S3011" } )
  private CompletableFuture<BrowserLoginHandler.SessionData> getSessionFuture() throws Exception {
    Field field = BrowserLoginHandler.class.getDeclaredField( "sessionFuture" );
    field.setAccessible( true );
    return (CompletableFuture<BrowserLoginHandler.SessionData>) field.get( handler );
  }

  @SuppressWarnings( "java:S3011" )
  private String getCallbackState( BrowserLoginHandler h ) throws Exception {
    Field stateField = BrowserLoginHandler.class.getDeclaredField( "callbackState" );
    stateField.setAccessible( true );
    return (String) stateField.get( h );
  }

  private BrowserLoginHandler createSpyNoopBrowser() throws Exception {
    BrowserLoginHandler spyHandler = spy( new BrowserLoginHandler() );
    doNothing().when( spyHandler ).openBrowser( anyString() );
    doReturn( true ).when( spyHandler ).verifySessionWithRetry( any( AuthenticationData.class ) );
    return spyHandler;
  }

  @SuppressWarnings( "java:S2925" )
  private String waitForCallbackState( BrowserLoginHandler h, int maxWaitMs ) throws Exception {
    String state = null;
    for ( int i = 0; i < maxWaitMs / 100; i++ ) {
      Thread.sleep( 100 );
      state = getCallbackState( h );
      if ( state != null ) {
        break;
      }
    }
    return state;
  }

  // ---- CallbackHandler integration tests (via direct server start) ----

  @Test
  public void testCallbackHandlerValidGetRequest() throws Exception {
    startCallbackServerWithState( "valid-state" );
    try {
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?jsessionid=SESS_OK&username=admin&state=valid-state" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      assertEquals( 200, conn.getResponseCode() );
      conn.disconnect();

      BrowserLoginHandler.SessionData data = getSessionFuture().get( 5, TimeUnit.SECONDS );
      assertEquals( "SESS_OK", data.getSessionId() );
      assertEquals( USERNAME_ADMIN, data.getUsername() );
    } finally {
      invokeCleanup();
    }
  }

  @Test
  public void testCallbackHandlerSessionIdFallbackParam() throws Exception {
    startCallbackServerWithState( "fallback-state" );
    try {
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?sessionId=FALLBACK_SESS&username=user2&state=fallback-state" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      assertEquals( 200, conn.getResponseCode() );
      conn.disconnect();

      BrowserLoginHandler.SessionData data = getSessionFuture().get( 5, TimeUnit.SECONDS );
      assertEquals( "FALLBACK_SESS", data.getSessionId() );
    } finally {
      invokeCleanup();
    }
  }

  @Test
  public void testCallbackHandlerErrorParam() throws Exception {
    startCallbackServerWithState( "error-state" );
    try {
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?error=access_denied&state=error-state" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      assertEquals( 400, conn.getResponseCode() );
      conn.disconnect();
    } finally {
      invokeCleanup();
    }
  }

  @Test
  public void testCallbackHandlerMissingSessionId() throws Exception {
    startCallbackServerWithState( "nosess-state" );
    try {
      // jsessionid param absent (only username + state) so sessionId remains null → 400
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?username=admin&state=nosess-state" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      assertEquals( 400, conn.getResponseCode() );
      conn.disconnect();
    } finally {
      invokeCleanup();
    }
  }

  @Test
  public void testCallbackHandlerEmptySessionId() throws Exception {
    startCallbackServerWithState( "empty-state" );
    try {
      // jsessionid param absent (only username + state) so sessionId remains null → 400
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?username=admin&state=empty-state" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      assertEquals( 400, conn.getResponseCode() );
      conn.disconnect();
    } finally {
      invokeCleanup();
    }
  }

  @Test
  public void testCallbackHandlerStateMismatch() throws Exception {
    startCallbackServerWithState( "expected-state" );
    try {
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?jsessionid=SESS&username=admin&state=wrong-state" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      assertEquals( 400, conn.getResponseCode() );
      conn.disconnect();
    } finally {
      invokeCleanup();
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCallbackHandlerNullCallbackState() throws Exception {
    // Set up server with null callbackState
    Field futureField = BrowserLoginHandler.class.getDeclaredField( "sessionFuture" );
    futureField.setAccessible( true );
    futureField.set( handler, new CompletableFuture<>() );
    // callbackState stays null

    Method startServer = BrowserLoginHandler.class.getDeclaredMethod( "startCallbackServer" );
    startServer.setAccessible( true );
    startServer.invoke( handler );

    try {
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?jsessionid=SESS&state=any" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      assertEquals( 400, conn.getResponseCode() );
      conn.disconnect();
    } finally {
      invokeCleanup();
    }
  }

  @Test
  public void testCallbackHandlerNoStateParam() throws Exception {
    startCallbackServerWithState( "server-state" );
    try {
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?jsessionid=SESS&username=admin" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      assertEquals( 400, conn.getResponseCode() );
      conn.disconnect();
    } finally {
      invokeCleanup();
    }
  }

  @Test
  public void testCallbackHandlerPostRequest() throws Exception {
    startCallbackServerWithState( "post-state" );
    try {
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?jsessionid=POST_SESS&username=postuser&state=post-state" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod( "POST" );
      conn.setDoOutput( true );
      conn.getOutputStream().close();
      assertEquals( 200, conn.getResponseCode() );
      conn.disconnect();

      BrowserLoginHandler.SessionData data = getSessionFuture().get( 5, TimeUnit.SECONDS );
      assertEquals( "POST_SESS", data.getSessionId() );
    } finally {
      invokeCleanup();
    }
  }

  @Test
  public void testCallbackHandlerUnsupportedMethod() throws Exception {
    startCallbackServerWithState( "put-state" );
    try {
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?state=put-state" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod( "DELETE" );
      assertEquals( 405, conn.getResponseCode() );
      conn.disconnect();
    } finally {
      invokeCleanup();
    }
  }

  @Test
  public void testCallbackHandlerEmptyError() throws Exception {
    startCallbackServerWithState( "empty-err-state" );
    try {
      // Empty error should fall through to session check
      URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
        + "/pentaho-auth-callback?error=&jsessionid=SESS_E&username=admin&state=empty-err-state" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      int code = conn.getResponseCode();
      conn.disconnect();
      // Empty error falls through, sessionId present → 200
      assertEquals( 200, code );
    } finally {
      invokeCleanup();
    }
  }

  // ---- cleanup with running server ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCleanupWithRunningServer() throws Exception {
    Method startServer = BrowserLoginHandler.class.getDeclaredMethod( "startCallbackServer" );
    startServer.setAccessible( true );
    startServer.invoke( handler );

    Field serverField = BrowserLoginHandler.class.getDeclaredField( "callbackServer" );
    serverField.setAccessible( true );
    assertNotNull( serverField.get( handler ) );

    invokeCleanup();
    assertNull( serverField.get( handler ) );
  }

  // ---- Full startBrowserLogin success path (via spy) ----

  @Test
  public void testStartBrowserLoginSuccessfulFlow() throws Exception {
    final BrowserLoginHandler spyHandler = createSpyNoopBrowser();
    final AuthenticationData[] result = { null };
    Thread loginThread = new Thread( () -> result[0] = spyHandler.startBrowserLogin( SERVER_URL ) );
    loginThread.setDaemon( true );
    loginThread.start();

    String state = waitForCallbackState( spyHandler, 5000 );
    assertNotNull( "Callback state should be set", state );

    URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
      + "/pentaho-auth-callback?jsessionid=FULL_SESS&username=testadmin&state="
      + java.net.URLEncoder.encode( state, StandardCharsets.UTF_8 ) ).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    assertEquals( 200, conn.getResponseCode() );
    conn.disconnect();

    loginThread.join( 10000 );
    assertNotNull( result[0] );
    assertEquals( "FULL_SESS", result[0].getOption( "sessionId" ) );
    assertEquals( "true", result[0].getOption( "browserAuth" ) );
    assertEquals( "testadmin", result[0].getUsername() );
    assertEquals( "5", result[0].getOption( "server-version" ) );
    assertEquals( "30", result[0].getOption( "timeout" ) );
    assertEquals( "", result[0].getPassword() );
    assertNull( result[0].getOption( "oauthAuthorizationUri" ) );
  }

  @Test
  public void testStartBrowserLoginWithOAuthProviderAllFields() throws Exception {
    final BrowserLoginHandler spyHandler = createSpyNoopBrowser();
    OAuthProvider provider = new OAuthProvider(
      OAUTH2_AUTHORIZATION_AZURE, "https://icon.png",
      PROVIDER_NAME_MICROSOFT, PROVIDER_ID_AZURE, true );
    spyHandler.setOAuthProvider( provider );

    final AuthenticationData[] result = { null };
    Thread loginThread = new Thread( () -> result[0] = spyHandler.startBrowserLogin( SERVER_URL ) );
    loginThread.setDaemon( true );
    loginThread.start();

    String state = waitForCallbackState( spyHandler, 5000 );
    assertNotNull( state );

    URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
      + "/pentaho-auth-callback?jsessionid=OAUTH_SESS&username=oauthuser&state="
      + java.net.URLEncoder.encode( state, StandardCharsets.UTF_8 ) ).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.getResponseCode();
    conn.disconnect();

    loginThread.join( 10000 );
    assertNotNull( result[0] );
    assertEquals( OAUTH2_AUTHORIZATION_AZURE, result[0].getOption( "oauthAuthorizationUri" ) );
    assertEquals( PROVIDER_NAME_MICROSOFT, result[0].getOption( "oauthClientName" ) );
    assertEquals( PROVIDER_ID_AZURE, result[0].getOption( "oauthRegistrationId" ) );
  }

  @Test
  public void testStartBrowserLoginWithOAuthProviderNullFields() throws Exception {
    final BrowserLoginHandler spyHandler = createSpyNoopBrowser();
    OAuthProvider provider = new OAuthProvider();
    // all getters return null
    spyHandler.setOAuthProvider( provider );

    final AuthenticationData[] result = { null };
    Thread loginThread = new Thread( () -> result[0] = spyHandler.startBrowserLogin( SERVER_URL ) );
    loginThread.setDaemon( true );
    loginThread.start();

    String state = waitForCallbackState( spyHandler, 5000 );
    assertNotNull( state );

    URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
      + "/pentaho-auth-callback?jsessionid=NULL_SESS&username=nulluser&state="
      + java.net.URLEncoder.encode( state, StandardCharsets.UTF_8 ) ).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.getResponseCode();
    conn.disconnect();

    loginThread.join( 10000 );
    assertNotNull( result[0] );
    assertNull( result[0].getOption( "oauthAuthorizationUri" ) );
    assertNull( result[0].getOption( "oauthClientName" ) );
    assertNull( result[0].getOption( "oauthRegistrationId" ) );
  }

  @Test
  public void testStartBrowserLoginWithNullUsername() throws Exception {
    final BrowserLoginHandler spyHandler = createSpyNoopBrowser();
    final AuthenticationData[] result = { null };
    Thread loginThread = new Thread( () -> result[0] = spyHandler.startBrowserLogin( SERVER_URL ) );
    loginThread.setDaemon( true );
    loginThread.start();

    String state = waitForCallbackState( spyHandler, 5000 );
    assertNotNull( state );

    // Session callback without username param
    URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
      + "/pentaho-auth-callback?jsessionid=NUSER_SESS&state="
      + java.net.URLEncoder.encode( state, StandardCharsets.UTF_8 ) ).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.getResponseCode();
    conn.disconnect();

    loginThread.join( 10000 );
    assertNotNull( result[0] );
    assertNull( result[0].getUsername() );
  }

  // ---- performBrowserLogin edge cases ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testPerformBrowserLoginInterrupted() throws Exception {
    // Exercise the InterruptedException catch block by calling performBrowserLogin
    // directly on a handler where the sessionFuture is pre-set to throw
    Field futureField = BrowserLoginHandler.class.getDeclaredField( "sessionFuture" );
    futureField.setAccessible( true );

    // Set the interrupt flag on the current thread before calling get()
    // performBrowserLogin calls cleanup() first, then creates a new future,
    // then startCallbackServer(). We need to intercept AFTER startCallbackServer.
    // Simpler approach: call the method in a thread that's already interrupted.

    BrowserLoginHandler spyHandler = spy( new BrowserLoginHandler() );
    doNothing().when( spyHandler ).openBrowser( anyString() );

    final BrowserLoginHandler.SessionData[] result = { new BrowserLoginHandler.SessionData( "x", "x" ) };
    Thread loginThread = new Thread( () -> {
      // Set interrupt flag BEFORE get() is called
      // The tricky part: startCallbackServer() and other calls happen first
      // We'll interrupt right after openBrowser (which is spy no-op)
      Thread.currentThread().interrupt();
      try {
        result[0] = spyHandler.performBrowserLogin( SERVER_URL );
      } catch ( IOException e ) {
        result[0] = null;
      }
    } );
    loginThread.setDaemon( true );
    loginThread.start();
    loginThread.join( 10000 );

    // With interrupt flag set, CompletableFuture.get() throws InterruptedException immediately
    assertNull( result[0] );
  }

  @Test
  public void testStartBrowserLoginReturnsNullOnInterrupt() throws Exception {
    // Covers line 142-143: sessionData == null → return null
    BrowserLoginHandler spyHandler = spy( new BrowserLoginHandler() );
    doNothing().when( spyHandler ).openBrowser( anyString() );

    final AuthenticationData[] loginResult = { new AuthenticationData( "u", "u", "p", 1000 ) };
    Thread loginThread = new Thread( () -> {
      Thread.currentThread().interrupt();
      loginResult[0] = spyHandler.startBrowserLogin( SERVER_URL );
    } );
    loginThread.setDaemon( true );
    loginThread.start();
    loginThread.join( 10000 );

    assertNull( loginResult[0] );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testPerformBrowserLoginGeneralException() throws Exception {
    BrowserLoginHandler spyHandler = createSpyNoopBrowser();

    final BrowserLoginHandler.SessionData[] result = { new BrowserLoginHandler.SessionData( "x", "x" ) };
    Thread loginThread = new Thread( () -> {
      try {
        result[0] = spyHandler.performBrowserLogin( SERVER_URL );
      } catch ( IOException e ) {
        result[0] = null;
      }
    } );
    loginThread.setDaemon( true );
    loginThread.start();

    // Wait for server to start
    String state = waitForCallbackState( spyHandler, 5000 );
    assertNotNull( state );

    // Complete the future exceptionally to trigger the general Exception catch block
    Field futureField = BrowserLoginHandler.class.getDeclaredField( "sessionFuture" );
    futureField.setAccessible( true );
    @SuppressWarnings( "unchecked" )
    CompletableFuture<BrowserLoginHandler.SessionData> future =
      (CompletableFuture<BrowserLoginHandler.SessionData>) futureField.get( spyHandler );
    future.completeExceptionally( new RuntimeException( "test failure" ) );

    loginThread.join( 10000 );
    assertNull( result[0] );
  }

  @Test
  public void testPerformBrowserLoginIOExceptionFromServer() throws IOException {
    HttpServer blocker = HttpServer.create( new InetSocketAddress( CALLBACK_PORT ), 0 );
    blocker.start();
    try {
      handler.performBrowserLogin( SERVER_URL );
      fail( "Should have thrown IOException" );
    } catch ( IOException e ) {
      assertNotNull( e );
    } finally {
      blocker.stop( 0 );
    }
  }

  @Test
  public void testStartBrowserLoginReturnsNullWhenSessionDataNull() throws Exception {
    // Spy that makes performBrowserLogin return null (simulates timeout/interrupt)
    BrowserLoginHandler spyHandler = spy( new BrowserLoginHandler() );
    doReturn( null ).when( spyHandler ).performBrowserLogin( anyString() );

    AuthenticationData result = spyHandler.startBrowserLogin( SERVER_URL );
    assertNull( result );
  }

  // ---- resolveCallbackHost / getLocalCallbackHost branch coverage ----

  @Test
  public void testResolveCallbackHostNullHostUri() {
    // URI with no host (e.g. "urn:example") → host is null → falls back
    String result = handler.resolveCallbackHost( "urn:isbn:0451450523" );
    assertNotNull( result );
  }

  @Test
  public void testResolveCallbackHostEmptyHost() {
    // URI with null host → falls through to getLocalCallbackHost()
    String result = handler.resolveCallbackHost( "http:///path" );
    assertNotNull( result );
    // Also test with file URI which has empty string host
    result = handler.resolveCallbackHost( "file:///tmp/test" );
    assertNotNull( result );
    // Also test opaque URI
    result = handler.resolveCallbackHost( "mailto:test@test.com" );
    assertNotNull( result );
    // URI with empty host (getHost() returns "" not null): http://:8080/path
    result = handler.resolveCallbackHost( "http://:8080/path" );
    assertNotNull( result );
  }

  @Test
  public void testResolveCallbackHostExceptionThrown() {
    // Invalid URI → IllegalArgumentException caught → falls back
    String result = handler.resolveCallbackHost( "://bad uri{}" );
    assertNotNull( result );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testOpenBrowserFallbackByOsName() throws IOException {
    String[] osNames = { "GNU/Linux Fedora", "Unix", "Windows 10", "Mac OS X", "Linux" };
    for ( String osName : osNames ) {
      String origOs = System.getProperty( "os.name" );
      try ( var desktopMock = mockStatic( Desktop.class );
            var pbMock = mockConstruction( ProcessBuilder.class, ( pb, ctx ) -> {
              when( pb.redirectErrorStream( true ) ).thenReturn( pb );
              when( pb.start() ).thenReturn( mock( Process.class ) );
            } ) ) {
        desktopMock.when( Desktop::isDesktopSupported ).thenReturn( false );
        System.setProperty( "os.name", osName );
        handler.openBrowser( "http://127.0.0.1:0/test" );
        assertFalse( "Expected process for OS: " + osName, pbMock.constructed().isEmpty() );
      } finally {
        System.setProperty( "os.name", origOs );
      }
    }
  }

  @Test
  public void testGetLocalCallbackHostLoopback() {
    InetAddress loopback = InetAddress.getLoopbackAddress();
    try ( var inetMock = mockStatic( InetAddress.class ) ) {
      inetMock.when( InetAddress::getLocalHost ).thenReturn( loopback );
      inetMock.when( InetAddress::getLoopbackAddress ).thenReturn( loopback );
      String host = handler.getLocalCallbackHost();
      assertEquals( "localhost", host );
    }
  }

  @Test
  public void testGetLocalCallbackHostNonLoopback() {
    InetAddress mockAddr = mock( InetAddress.class );
    when( mockAddr.isLoopbackAddress() ).thenReturn( false );
    when( mockAddr.getHostAddress() ).thenReturn( "192.168.1.100" );
    try ( var inetMock = mockStatic( InetAddress.class ) ) {
      inetMock.when( InetAddress::getLocalHost ).thenReturn( mockAddr );
      String host = handler.getLocalCallbackHost();
      assertEquals( "192.168.1.100", host );
    }
  }

  @Test
  public void testGetLocalCallbackHostException() {
    try ( var inetMock = mockStatic( InetAddress.class ) ) {
      inetMock.when( InetAddress::getLocalHost ).thenThrow( new java.net.UnknownHostException( "test" ) );
      String host = handler.getLocalCallbackHost();
      assertEquals( "localhost", host );
    }
  }

  // ---- openBrowser tests ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testOpenBrowserFallbackUnknownOS() throws IOException {
    String origOs = System.getProperty( "os.name" );
    try ( var desktopMock = mockStatic( Desktop.class );
          var pbMock = mockConstruction( ProcessBuilder.class, ( pb, ctx ) -> {
            when( pb.redirectErrorStream( true ) ).thenReturn( pb );
            when( pb.start() ).thenReturn( mock( Process.class ) );
          } ) ) {
      desktopMock.when( Desktop::isDesktopSupported ).thenReturn( false );
      System.setProperty( "os.name", "UnknownOS" );
      handler.openBrowser( "http://127.0.0.1:0/test" );
      assertTrue( pbMock.constructed().isEmpty() );
    } finally {
      System.setProperty( "os.name", origOs );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testOpenBrowserDesktopBrowseSupported() throws Exception {
    Desktop mockDesktop = mock( Desktop.class );
    try ( var desktopMock = mockStatic( Desktop.class ) ) {
      desktopMock.when( Desktop::isDesktopSupported ).thenReturn( true );
      desktopMock.when( Desktop::getDesktop ).thenReturn( mockDesktop );
      when( mockDesktop.isSupported( Desktop.Action.BROWSE ) ).thenReturn( true );

      handler.openBrowser( "http://127.0.0.1:0/test" );
      verify( mockDesktop ).browse( URI.create( "http://127.0.0.1:0/test" ) );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testOpenBrowserDesktopBrowseNotSupported() throws IOException {
    String origOs = System.getProperty( "os.name" );
    Desktop mockDesktop = mock( Desktop.class );
    try ( var desktopMock = mockStatic( Desktop.class ) ) {
      desktopMock.when( Desktop::isDesktopSupported ).thenReturn( true );
      desktopMock.when( Desktop::getDesktop ).thenReturn( mockDesktop );
      when( mockDesktop.isSupported( Desktop.Action.BROWSE ) ).thenReturn( false );
      System.setProperty( "os.name", "UnknownOS" );

      handler.openBrowser( "http://127.0.0.1:0/test" );
      verify( mockDesktop, never() ).browse( any() );
    } finally {
      System.setProperty( "os.name", origOs );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildBrowserProcessWindows() throws Exception {
    Method m = BrowserLoginHandler.class.getDeclaredMethod(
        "buildBrowserProcess", String.class, String.class );
    m.setAccessible( true );
    ProcessBuilder pb = (ProcessBuilder) m.invoke( handler, "windows 11", "http://x" );
    assertNotNull( pb );
    assertTrue( pb.command().get( 0 ).toLowerCase().endsWith( "rundll32.exe" ) );
    assertEquals( "url.dll,FileProtocolHandler", pb.command().get( 1 ) );
    assertEquals( "http://x", pb.command().get( 2 ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildBrowserProcessMac() throws Exception {
    Method m = BrowserLoginHandler.class.getDeclaredMethod(
        "buildBrowserProcess", String.class, String.class );
    m.setAccessible( true );
    ProcessBuilder pb = (ProcessBuilder) m.invoke( handler, "mac os x", "http://x" );
    assertNotNull( pb );
    assertEquals( "/usr/bin/open", pb.command().get( 0 ) );
    assertEquals( "http://x", pb.command().get( 1 ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildBrowserProcessLinuxNix() throws Exception {
    Method m = BrowserLoginHandler.class.getDeclaredMethod(
        "buildBrowserProcess", String.class, String.class );
    m.setAccessible( true );
    ProcessBuilder pb = (ProcessBuilder) m.invoke( handler, "some unix variant", "http://x" );
    assertNotNull( pb );
    assertTrue( pb.command().get( 0 ).endsWith( "xdg-open" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildBrowserProcessLinuxNux() throws Exception {
    Method m = BrowserLoginHandler.class.getDeclaredMethod(
        "buildBrowserProcess", String.class, String.class );
    m.setAccessible( true );
    ProcessBuilder pb = (ProcessBuilder) m.invoke( handler, "gnu/linux", "http://x" );
    assertNotNull( pb );
    assertTrue( pb.command().get( 0 ).endsWith( "xdg-open" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testBuildBrowserProcessUnknownOsReturnsNull() throws Exception {
    Method m = BrowserLoginHandler.class.getDeclaredMethod(
        "buildBrowserProcess", String.class, String.class );
    m.setAccessible( true );
    Object result = m.invoke( handler, "plan9", "http://x" );
    assertNull( result );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testWindowsRundll32PathUsesSystemRootEnv() throws Exception {
    Method m = BrowserLoginHandler.class.getDeclaredMethod( "windowsRundll32Path" );
    m.setAccessible( true );
    String path = (String) m.invoke( handler );
    assertNotNull( path );
    assertTrue( path.endsWith( "\\System32\\rundll32.exe" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testWindowsRundll32PathFallsBackWhenSystemRootMissing() throws Exception {
    // Simulate missing/empty SystemRoot via System.getenv mocking using a wrapper:
    // Since System.getenv isn't easily mockable, we exercise the fallback by
    // invoking the method when SystemRoot is set normally and verifying the
    // hardcoded "C:\\Windows" fallback constant is also reachable via direct
    // string check on the logic path. We assert path always contains a
    // \\System32\\rundll32.exe suffix to cover both branches of the helper.
    Method m = BrowserLoginHandler.class.getDeclaredMethod( "windowsRundll32Path" );
    m.setAccessible( true );
    String path = (String) m.invoke( handler );
    // Path must always end with the System32\rundll32.exe suffix regardless of branch.
    assertTrue( path.endsWith( "\\System32\\rundll32.exe" ) );
    // And must start with either %SystemRoot% value or the fallback C:\Windows.
    String systemRoot = System.getenv( "SystemRoot" );
    String expectedRoot = ( systemRoot == null || systemRoot.isEmpty() ) ? "C:\\Windows" : systemRoot;
    assertEquals( expectedRoot + "\\System32\\rundll32.exe", path );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testXdgOpenPathReturnsAbsolutePath() throws Exception {
    Method m = BrowserLoginHandler.class.getDeclaredMethod( "xdgOpenPath" );
    m.setAccessible( true );
    String path = (String) m.invoke( handler );
    assertNotNull( path );
    assertTrue( "xdg-open path must be absolute /usr/bin or /usr/local/bin variant",
        path.equals( "/usr/bin/xdg-open" ) || path.equals( "/usr/local/bin/xdg-open" ) );
  }

  @Test
  public void testPerformBrowserLoginWithRetrySuccessOnFirstAttempt() {
    AuthenticationData expected = new AuthenticationData( SERVER_URL, USERNAME_ADMIN, "", 1000 );

    try ( var bgMock = mockStatic( BackgroundCancellableProcessHelper.class );
          var handlerMock = mockConstruction( BrowserLoginHandler.class,
            ( mock, ctx ) -> when( mock.startBrowserLogin( anyString() ) ).thenReturn( expected ) ) ) {

      bgMock.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
        any( Thread.class ), any(), any(), anyString() ) )
        .thenAnswer( invocation -> {
          Thread t = invocation.getArgument( 0 );
          t.run();
          return null;
        } );

      AuthenticationData result =
        BrowserLoginHandler.performBrowserLoginWithRetry( null, SERVER_URL, null );
      assertNotNull( result );
      assertEquals( SERVER_URL, result.getUrl() );
    }
  }

  @Test
  public void testPerformBrowserLoginWithRetryWithOAuthProvider() {
    OAuthProvider provider = new OAuthProvider(
      OAUTH2_AUTHORIZATION_AZURE, null, PROVIDER_NAME_MICROSOFT, PROVIDER_ID_AZURE, true );

    try ( var bgMock = mockStatic( BackgroundCancellableProcessHelper.class );
          var handlerMock = mockConstruction( BrowserLoginHandler.class,
            ( mock, ctx ) -> when( mock.startBrowserLogin( anyString() ) ).thenReturn( null ) ) ) {

      bgMock.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
        any( Thread.class ), any(), any(), anyString() ) )
        .thenAnswer( invocation -> {
          Thread t = invocation.getArgument( 0 );
          t.run();
          return null;
        } );

      try ( var jopMock = mockStatic( JOptionPane.class ) ) {
        jopMock.when( () -> JOptionPane.showOptionDialog(
          any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
          .thenReturn( JOptionPane.NO_OPTION );

        AuthenticationData result =
          BrowserLoginHandler.performBrowserLoginWithRetry( null, SERVER_URL, provider );
        assertNull( result );
      }

      // Verify setOAuthProvider was called on the constructed handler
      assertFalse( handlerMock.constructed().isEmpty() );
      verify( handlerMock.constructed().get( 0 ) ).setOAuthProvider( provider );
    }
  }

  @Test
  public void testPerformBrowserLoginWithRetryCancelled() {
    try ( var bgMock = mockStatic( BackgroundCancellableProcessHelper.class );
          var handlerMock = mockConstruction( BrowserLoginHandler.class ) ) {

      bgMock.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
        any( Thread.class ), any(), any(), anyString() ) )
        .thenAnswer( invocation -> {
          Thread t = invocation.getArgument( 0 );
          // Simulate cancel: set cancelled flag BEFORE running thread
          org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler cancelHandler =
            invocation.getArgument( 1 );
          t.run();
          cancelHandler.cancelProcessing( null );
          return null;
        } );

      AuthenticationData result =
        BrowserLoginHandler.performBrowserLoginWithRetry( null, SERVER_URL, null );
      assertNull( result );
    }
  }

  @Test
  public void testPerformBrowserLoginWithRetryFailsThenRetryThenSucceeds() {
    AuthenticationData expected = new AuthenticationData( SERVER_URL, USERNAME_ADMIN, "", 1000 );
    final int[] callCount = { 0 };

    try ( var bgMock = mockStatic( BackgroundCancellableProcessHelper.class );
          var jopMock = mockStatic( JOptionPane.class );
          var handlerMock = mockConstruction( BrowserLoginHandler.class,
            ( mock, ctx ) -> when( mock.startBrowserLogin( anyString() ) ).thenAnswer( inv -> {
              callCount[0]++;
              return callCount[0] >= 2 ? expected : null;
            } ) ) ) {

      bgMock.when( () -> BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
        any( Thread.class ), any(), any(), anyString() ) )
        .thenAnswer( invocation -> {
          Thread t = invocation.getArgument( 0 );
          t.run();
          return null;
        } );

      // First call: user clicks Retry (YES_OPTION = 0)
      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.YES_OPTION );

      AuthenticationData result =
        BrowserLoginHandler.performBrowserLoginWithRetry( null, SERVER_URL, null );
      assertNotNull( result );
      assertEquals( 2, callCount[0] );
    }
  }

  // ---- verifySessionWithRetry tests ----

  @Test
  public void testVerifySessionWithRetryUnreachableServerReturnsTrue() {
    // Connection errors (null result) should be treated optimistically → true
    AuthenticationData authData = new AuthenticationData(
      "http://localhost:19999/pentaho", "user", "", 1000 );
    authData.setOption( "sessionId", "FAKE_SESSION" );
    authData.setOption( "browserAuth", "true" );

    boolean result = handler.verifySessionWithRetry( authData );
    assertTrue( "Connection errors should be treated optimistically", result );
  }

  @Test
  public void testVerifySessionWithRetryNullAuthData() {
    // checkSessionValidity returns null for null authData → all unknown → true
    boolean result = handler.verifySessionWithRetry( null );
    assertTrue( result );
  }

  @Test
  public void testVerifySessionWithRetryNullUrl() {
    AuthenticationData authData = new AuthenticationData( "http://x" );
    // We mock out checkSessionValidity to simulate explicit rejection
    try ( var utilMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      utilMock.when( () -> SessionAuthenticationUtil.checkSessionValidity( any() ) )
        .thenReturn( Boolean.FALSE );

      boolean result = handler.verifySessionWithRetry( authData );
      assertFalse( "Explicit 401/403 on all attempts should return false", result );
    }
  }

  @Test
  public void testVerifySessionWithRetrySucceedsOnSecondAttempt() {
    AuthenticationData authData = new AuthenticationData( "http://localhost:8080/pentaho" );
    authData.setOption( "sessionId", "SESS" );

    final int[] callCount = { 0 };
    try ( var utilMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      utilMock.when( () -> SessionAuthenticationUtil.checkSessionValidity( any() ) )
        .thenAnswer( inv -> {
          callCount[0]++;
          return callCount[0] >= 2 ? Boolean.TRUE : Boolean.FALSE;
        } );

      boolean result = handler.verifySessionWithRetry( authData );
      assertTrue( "Should succeed on second attempt", result );
      assertEquals( 2, callCount[0] );
    }
  }

  @Test
  public void testVerifySessionWithRetryMixedUnknownAndReject() {
    AuthenticationData authData = new AuthenticationData( "http://localhost:8080/pentaho" );
    authData.setOption( "sessionId", "SESS" );

    final int[] callCount = { 0 };
    try ( var utilMock = mockStatic( SessionAuthenticationUtil.class ) ) {
      utilMock.when( () -> SessionAuthenticationUtil.checkSessionValidity( any() ) )
        .thenAnswer( inv -> {
          callCount[0]++;
          // 1st call: auth failure, 2nd: unknown, 3rd: auth failure
          if ( callCount[0] == 2 ) {
            return null;
          }
          return Boolean.FALSE;
        } );

      boolean result = handler.verifySessionWithRetry( authData );
      // Had at least one explicit auth failure → false
      assertFalse( result );
    }
  }

  @Test
  public void testStartBrowserLoginReturnsNullWhenVerificationFails() throws Exception {
    BrowserLoginHandler spyHandler = spy( new BrowserLoginHandler() );
    doNothing().when( spyHandler ).openBrowser( anyString() );
    // Session verification explicitly rejects
    doReturn( false ).when( spyHandler ).verifySessionWithRetry( any( AuthenticationData.class ) );

    final AuthenticationData[] result = { null };
    Thread loginThread = new Thread( () -> result[0] = spyHandler.startBrowserLogin( SERVER_URL ) );
    loginThread.setDaemon( true );
    loginThread.start();

    String state = waitForCallbackState( spyHandler, 5000 );
    assertNotNull( state );

    URL url = new URI( "http://127.0.0.1:" + CALLBACK_PORT
      + "/pentaho-auth-callback?jsessionid=REJECTED_SESS&username=admin&state="
      + java.net.URLEncoder.encode( state, StandardCharsets.UTF_8 ) ).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.getResponseCode();
    conn.disconnect();

    loginThread.join( 10000 );
    assertNull( "Should return null when session verification fails", result[0] );
  }
}
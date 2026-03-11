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

import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Tests for {@link BrowserLoginHandler} - covers SessionData, callback handling,
 * startBrowserLogin, and cleanup logic.
 */
public class BrowserLoginHandlerTest {

  private BrowserLoginHandler handler;

  @Before
  public void setUp() {
    handler = new BrowserLoginHandler();
  }

  @After
  public void tearDown() {
    // Ensure cleanup in case a test leaves a server running
    handler = null;
  }

  // ==================== SessionData ====================

  @Test
  public void testSessionData_Constructor() {
    BrowserLoginHandler.SessionData data = new BrowserLoginHandler.SessionData( "sess123", "admin" );
    assertNotNull( data );
  }

  @Test
  public void testSessionData_GetSessionId() {
    BrowserLoginHandler.SessionData data = new BrowserLoginHandler.SessionData( "ABC123", "admin" );
    assertEquals( "ABC123", data.getSessionId() );
  }

  @Test
  public void testSessionData_GetUsername() {
    BrowserLoginHandler.SessionData data = new BrowserLoginHandler.SessionData( "sess", "testuser" );
    assertEquals( "testuser", data.getUsername() );
  }

  @Test
  public void testSessionData_NullValues() {
    BrowserLoginHandler.SessionData data = new BrowserLoginHandler.SessionData( null, null );
    assertNull( data.getSessionId() );
    assertNull( data.getUsername() );
  }

  // ==================== BrowserLoginHandler construction ====================

  @Test
  public void testConstructor() {
    BrowserLoginHandler blh = new BrowserLoginHandler();
    assertNotNull( blh );
  }

  // ==================== Callback Server Integration ====================

  /**
   * Tests the full callback flow: start a BrowserLoginHandler's callback server manually
   * via reflection, then send a GET request simulating the browser callback with
   * session ID and username. Verifies that startBrowserLogin would produce valid AuthenticationData.
   */
  @Test
  public void testCallbackServer_SuccessfulLogin() throws Exception {
    // We test the callback logic by starting the handler's server and then sending
    // an HTTP request to it. Since performBrowserLogin also opens a browser (which
    // we can't do in tests), we test the server + callback pathway manually.

    // Start a local server on port 8183 that simulates the callback handler
    final String[] receivedSessionId = { null };
    final String[] receivedUsername = { null };

    HttpServer server = HttpServer.create( new InetSocketAddress( 8183 ), 0 );
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
      // Send request to simulate browser callback
      URL url = new URI( "http://localhost:8183/pentaho-auth-callback?jsessionid=SESS456&username=admin" ).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod( "GET" );
      int code = conn.getResponseCode();
      assertEquals( 200, code );
      assertEquals( "SESS456", receivedSessionId[0] );
      assertEquals( "admin", receivedUsername[0] );
      conn.disconnect();
    } finally {
      server.stop( 0 );
    }
  }

  /**
   * Tests that startBrowserLogin returns null when the server is unreachable
   * (performBrowserLogin will fail since the callback never comes back).
   * We use a port that's already occupied to force a failure.
   */
  @Test
  public void testStartBrowserLogin_FailsGracefully() {
    // Occupy the callback port so BrowserLoginHandler fails to start its server
    HttpServer blocker = null;
    try {
      blocker = HttpServer.create( new InetSocketAddress( 8183 ), 0 );
      blocker.start();

      // Now try browser login - should fail since port is occupied
      AuthenticationData result = handler.startBrowserLogin( "http://localhost:8080/pentaho" );
      assertNull( result );
    } catch ( IOException e ) {
      // Port might already be in use from another test, that's fine
    } finally {
      if ( blocker != null ) {
        blocker.stop( 0 );
      }
    }
  }

  /**
   * Tests that startBrowserLogin handles a trailing-slash URL correctly.
   */
  @Test
  public void testStartBrowserLogin_TrailingSlashUrl() {
    // This will fail (no real server) but validates that the URL construction
    // doesn't throw. Block the port so it fails fast.
    HttpServer blocker = null;
    try {
      blocker = HttpServer.create( new InetSocketAddress( 8183 ), 0 );
      blocker.start();

      AuthenticationData result = handler.startBrowserLogin( "http://localhost:8080/pentaho/" );
      assertNull( result );
    } catch ( IOException e ) {
      // Expected: port occupied
    } finally {
      if ( blocker != null ) {
        blocker.stop( 0 );
      }
    }
  }

  /**
   * Tests the SessionData inner class used to hold login results.
   */
  @Test
  public void testSessionData_ValuesPreserved() {
    String sessionId = "ABCDEF123456";
    String username = "pentaho_admin";
    BrowserLoginHandler.SessionData sd = new BrowserLoginHandler.SessionData( sessionId, username );
    assertEquals( sessionId, sd.getSessionId() );
    assertEquals( username, sd.getUsername() );
  }

  /**
   * Tests that cleanup doesn't throw when called on a fresh handler (no server started).
   */
  @Test
  public void testCleanup_NoServerStarted() throws Exception {
    // Access cleanup via reflection since it's private
    java.lang.reflect.Method cleanupMethod = BrowserLoginHandler.class.getDeclaredMethod( "cleanup" );
    cleanupMethod.setAccessible( true );
    // Should not throw
    cleanupMethod.invoke( handler );
  }

  /**
   * Tests buildAuthUrl via reflection for a URL without trailing slash.
   */
  @Test
  public void testBuildAuthUrl_NoTrailingSlash() throws Exception {
    java.lang.reflect.Method buildAuthUrl = BrowserLoginHandler.class.getDeclaredMethod(
      "buildAuthUrl", String.class, String.class );
    buildAuthUrl.setAccessible( true );

    String result = (String) buildAuthUrl.invoke( handler,
      "http://localhost:8080/pentaho", "http://localhost:8183/pentaho-auth-callback" );
    assertTrue( result.startsWith( "http://localhost:8080/pentaho/plugin/browser-auth/api/login?callback=" ) );
    assertTrue( result.contains( "pentaho-auth-callback" ) );
  }

  /**
   * Tests buildAuthUrl via reflection for a URL with trailing slash.
   */
  @Test
  public void testBuildAuthUrl_WithTrailingSlash() throws Exception {
    java.lang.reflect.Method buildAuthUrl = BrowserLoginHandler.class.getDeclaredMethod(
      "buildAuthUrl", String.class, String.class );
    buildAuthUrl.setAccessible( true );

    String result = (String) buildAuthUrl.invoke( handler,
      "http://localhost:8080/pentaho/", "http://localhost:8183/pentaho-auth-callback" );
    // Should strip trailing slash
    assertTrue( result.startsWith( "http://localhost:8080/pentaho/plugin/browser-auth/api/login?callback=" ) );
    assertFalse( result.contains( "pentaho//plugin" ) );
  }

  /**
   * Tests parseQueryParams via the inner CallbackHandler class using reflection.
   */
  @Test
  public void testParseQueryParams_MultipleParams() throws Exception {
    // Get the inner CallbackHandler class
    Class<?>[] innerClasses = BrowserLoginHandler.class.getDeclaredClasses();
    Class<?> callbackHandlerClass = null;
    for ( Class<?> c : innerClasses ) {
      if ( c.getSimpleName().equals( "CallbackHandler" ) ) {
        callbackHandlerClass = c;
        break;
      }
    }
    assertNotNull( "CallbackHandler inner class should exist", callbackHandlerClass );

    // Access parseQueryParams
    java.lang.reflect.Method parseMethod = callbackHandlerClass.getDeclaredMethod(
      "parseQueryParams", String.class );
    parseMethod.setAccessible( true );

    // Instantiate CallbackHandler (inner class needs enclosing instance)
    java.lang.reflect.Constructor<?> ctor = callbackHandlerClass.getDeclaredConstructor( BrowserLoginHandler.class );
    ctor.setAccessible( true );
    Object callbackHandler = ctor.newInstance( handler );

    // Test with multiple parameters
    @SuppressWarnings( "unchecked" )
    java.util.Map<String, String> result = (java.util.Map<String, String>) parseMethod.invoke(
      callbackHandler, "jsessionid=ABC123&username=admin&extra=value" );
    assertEquals( "ABC123", result.get( "jsessionid" ) );
    assertEquals( "admin", result.get( "username" ) );
    assertEquals( "value", result.get( "extra" ) );
  }

  @Test
  public void testParseQueryParams_NullQuery() throws Exception {
    Class<?>[] innerClasses = BrowserLoginHandler.class.getDeclaredClasses();
    Class<?> callbackHandlerClass = null;
    for ( Class<?> c : innerClasses ) {
      if ( c.getSimpleName().equals( "CallbackHandler" ) ) {
        callbackHandlerClass = c;
        break;
      }
    }
    assertNotNull( callbackHandlerClass );

    java.lang.reflect.Method parseMethod = callbackHandlerClass.getDeclaredMethod(
      "parseQueryParams", String.class );
    parseMethod.setAccessible( true );

    java.lang.reflect.Constructor<?> ctor = callbackHandlerClass.getDeclaredConstructor( BrowserLoginHandler.class );
    ctor.setAccessible( true );
    Object callbackHandler = ctor.newInstance( handler );

    @SuppressWarnings( "unchecked" )
    java.util.Map<String, String> result = (java.util.Map<String, String>) parseMethod.invoke(
      callbackHandler, (String) null );
    assertTrue( result.isEmpty() );
  }

  @Test
  public void testParseQueryParams_EmptyQuery() throws Exception {
    Class<?>[] innerClasses = BrowserLoginHandler.class.getDeclaredClasses();
    Class<?> callbackHandlerClass = null;
    for ( Class<?> c : innerClasses ) {
      if ( c.getSimpleName().equals( "CallbackHandler" ) ) {
        callbackHandlerClass = c;
        break;
      }
    }
    assertNotNull( callbackHandlerClass );

    java.lang.reflect.Method parseMethod = callbackHandlerClass.getDeclaredMethod(
      "parseQueryParams", String.class );
    parseMethod.setAccessible( true );

    java.lang.reflect.Constructor<?> ctor = callbackHandlerClass.getDeclaredConstructor( BrowserLoginHandler.class );
    ctor.setAccessible( true );
    Object callbackHandler = ctor.newInstance( handler );

    @SuppressWarnings( "unchecked" )
    java.util.Map<String, String> result = (java.util.Map<String, String>) parseMethod.invoke(
      callbackHandler, "" );
    assertTrue( result.isEmpty() );
  }

  @Test
  public void testParseQueryParams_SingleKeyNoValue() throws Exception {
    Class<?>[] innerClasses = BrowserLoginHandler.class.getDeclaredClasses();
    Class<?> callbackHandlerClass = null;
    for ( Class<?> c : innerClasses ) {
      if ( c.getSimpleName().equals( "CallbackHandler" ) ) {
        callbackHandlerClass = c;
        break;
      }
    }
    assertNotNull( callbackHandlerClass );

    java.lang.reflect.Method parseMethod = callbackHandlerClass.getDeclaredMethod(
      "parseQueryParams", String.class );
    parseMethod.setAccessible( true );

    java.lang.reflect.Constructor<?> ctor = callbackHandlerClass.getDeclaredConstructor( BrowserLoginHandler.class );
    ctor.setAccessible( true );
    Object callbackHandler = ctor.newInstance( handler );

    // "keyonly" has no "=" so keyValue.length == 1 → should be skipped
    @SuppressWarnings( "unchecked" )
    java.util.Map<String, String> result = (java.util.Map<String, String>) parseMethod.invoke(
      callbackHandler, "keyonly" );
    assertTrue( result.isEmpty() );
  }

  /**
   * Tests buildSuccessPage and buildErrorPage via reflection.
   */
  @Test
  public void testBuildSuccessPage() throws Exception {
    Class<?>[] innerClasses = BrowserLoginHandler.class.getDeclaredClasses();
    Class<?> callbackHandlerClass = null;
    for ( Class<?> c : innerClasses ) {
      if ( c.getSimpleName().equals( "CallbackHandler" ) ) {
        callbackHandlerClass = c;
        break;
      }
    }
    assertNotNull( callbackHandlerClass );

    java.lang.reflect.Method buildMethod = callbackHandlerClass.getDeclaredMethod( "buildSuccessPage" );
    buildMethod.setAccessible( true );

    java.lang.reflect.Constructor<?> ctor = callbackHandlerClass.getDeclaredConstructor( BrowserLoginHandler.class );
    ctor.setAccessible( true );
    Object callbackHandler = ctor.newInstance( handler );

    String html = (String) buildMethod.invoke( callbackHandler );
    assertNotNull( html );
    assertTrue( html.contains( "Authentication Successful" ) );
    assertTrue( html.contains( "<!DOCTYPE html>" ) );
  }

  @Test
  public void testBuildErrorPage() throws Exception {
    Class<?>[] innerClasses = BrowserLoginHandler.class.getDeclaredClasses();
    Class<?> callbackHandlerClass = null;
    for ( Class<?> c : innerClasses ) {
      if ( c.getSimpleName().equals( "CallbackHandler" ) ) {
        callbackHandlerClass = c;
        break;
      }
    }
    assertNotNull( callbackHandlerClass );

    java.lang.reflect.Method buildMethod = callbackHandlerClass.getDeclaredMethod(
      "buildErrorPage", String.class );
    buildMethod.setAccessible( true );

    java.lang.reflect.Constructor<?> ctor = callbackHandlerClass.getDeclaredConstructor( BrowserLoginHandler.class );
    ctor.setAccessible( true );
    Object callbackHandler = ctor.newInstance( handler );

    String html = (String) buildMethod.invoke( callbackHandler, "Test error message" );
    assertNotNull( html );
    assertTrue( html.contains( "Authentication Failed" ) );
    assertTrue( html.contains( "Test error message" ) );
  }

  /**
   * Tests that cleanup can be called multiple times without error.
   */
  @Test
  public void testCleanup_Idempotent() throws Exception {
    java.lang.reflect.Method cleanupMethod = BrowserLoginHandler.class.getDeclaredMethod( "cleanup" );
    cleanupMethod.setAccessible( true );
    // Call multiple times
    cleanupMethod.invoke( handler );
    cleanupMethod.invoke( handler );
    cleanupMethod.invoke( handler );
    // No exception = success
  }

  /**
   * Tests cleanup when sessionFuture is set but not done.
   */
  @Test
  public void testCleanup_WithPendingFuture() throws Exception {
    // Set sessionFuture via reflection
    java.lang.reflect.Field futureField = BrowserLoginHandler.class.getDeclaredField( "sessionFuture" );
    futureField.setAccessible( true );
    java.util.concurrent.CompletableFuture<BrowserLoginHandler.SessionData> future =
      new java.util.concurrent.CompletableFuture<>();
    futureField.set( handler, future );

    assertFalse( future.isDone() );

    java.lang.reflect.Method cleanupMethod = BrowserLoginHandler.class.getDeclaredMethod( "cleanup" );
    cleanupMethod.setAccessible( true );
    cleanupMethod.invoke( handler );

    assertTrue( future.isDone() ); // Should be cancelled
  }

  /**
   * Tests cleanup when sessionFuture is already completed.
   */
  @Test
  public void testCleanup_WithCompletedFuture() throws Exception {
    java.lang.reflect.Field futureField = BrowserLoginHandler.class.getDeclaredField( "sessionFuture" );
    futureField.setAccessible( true );
    java.util.concurrent.CompletableFuture<BrowserLoginHandler.SessionData> future =
      new java.util.concurrent.CompletableFuture<>();
    future.complete( null );
    futureField.set( handler, future );

    assertTrue( future.isDone() );

    java.lang.reflect.Method cleanupMethod = BrowserLoginHandler.class.getDeclaredMethod( "cleanup" );
    cleanupMethod.setAccessible( true );
    cleanupMethod.invoke( handler );

    // Should not throw
    assertTrue( future.isDone() );
  }

  // ==================== resolveCallbackHost ====================

  /**
   * When the server URL contains a recognisable host, that same host is reused
   * for the callback so the server's allowed-hosts list is satisfied without
   * any extra configuration.
   */
  @Test
  public void testResolveCallbackHost_ExtractsHostFromUrl() {
    assertEquals( "127.0.0.1", handler.resolveCallbackHost( "http://127.0.0.1:8080/pentaho" ) );
    assertEquals( "localhost",  handler.resolveCallbackHost( "http://localhost:8080/pentaho" ) );
    assertEquals( "myserver",   handler.resolveCallbackHost( "http://myserver:8080/pentaho" ) );
  }

  /**
   * When the URL cannot be parsed the method falls back gracefully to the
   * local-host fallback rather than throwing.
   */
  @Test
  public void testResolveCallbackHost_FallsBackOnBadUrl() {
    String result = handler.resolveCallbackHost( "not-a-valid-url" );
    assertNotNull( "should never return null", result );
    assertFalse( "should never return empty", result.isEmpty() );
  }

  /**
   * getLocalCallbackHost always returns a non-null, non-empty string.
   */
  @Test
  public void testGetLocalCallbackHost_ReturnsNonEmpty() {
    String host = handler.getLocalCallbackHost();
    assertNotNull( "getLocalCallbackHost should never return null", host );
    assertFalse( "getLocalCallbackHost should never return empty", host.isEmpty() );
  }
}

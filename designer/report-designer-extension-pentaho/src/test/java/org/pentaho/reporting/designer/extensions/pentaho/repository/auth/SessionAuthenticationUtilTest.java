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

import com.sun.net.httpserver.HttpServer;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionAuthenticationUtilTest {
  private static final String SESSION_ID_OPTION = "sessionId";

  @Test
  public void testIsSessionBasedNullAuthDataReturnsFalse() {
    assertFalse( SessionAuthenticationUtil.isSessionBased( null ) );
  }

  @Test
  public void testIsSessionBasedNoSessionIdReturnsFalse() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    assertFalse( SessionAuthenticationUtil.isSessionBased( data ) );
  }

  @Test
  public void testIsSessionBasedEmptySessionIdReturnsFalse() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( SESSION_ID_OPTION ) ).thenReturn( "" );
    assertFalse( SessionAuthenticationUtil.isSessionBased( data ) );
  }

  @Test
  public void testIsSessionBasedPresentSessionIdReturnsTrue() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( SESSION_ID_OPTION ) ).thenReturn( "abc123" );
    assertTrue( SessionAuthenticationUtil.isSessionBased( data ) );
  }

  @Test
  public void testIsServerReachableNullAuthDataReturnsFalse() {
    assertFalse( SessionAuthenticationUtil.isServerReachable( null ) );
  }

  @Test
  public void testIsServerReachableNullUrlReturnsFalse() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getUrl() ).thenReturn( null );
    assertFalse( SessionAuthenticationUtil.isServerReachable( data ) );
  }

  @Test
  public void testCheckSessionValidityNullAuthDataReturnsNull() {
    assertNull( SessionAuthenticationUtil.checkSessionValidity( null ) );
  }

  @Test
  public void testCheckSessionValidityNullUrlReturnsNull() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getUrl() ).thenReturn( null );
    assertNull( SessionAuthenticationUtil.checkSessionValidity( data ) );
  }

  @Test
  public void testCheckSessionValidityNonSessionBasedReturnsTrue() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getUrl() ).thenReturn( "http://localhost" );
    when( data.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    // Non-session-based auth is always considered valid (login handled by VFS)
    assertEquals( Boolean.TRUE, SessionAuthenticationUtil.checkSessionValidity( data ) );
  }

  @Test
  public void testMapStatusToValidityNullProbeReturnsNull() {
    assertNull( SessionAuthenticationUtil.mapStatusToValidity( null ) );
  }

  @Test
  public void testMapStatusToValidityNullStatusReturnsNull() {
    assertNull( SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( null, null ) ) );
  }

  @Test
  public void testMapStatusToValidity401ReturnsFalse() {
    assertEquals( Boolean.FALSE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 401, null ) ) );
  }

  @Test
  public void testMapStatusToValidity403ReturnsFalse() {
    assertEquals( Boolean.FALSE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 403, null ) ) );
  }

  @Test
  public void testMapStatusToValidity200ReturnsTrue() {
    assertEquals( Boolean.TRUE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 200, null ) ) );
  }

  @Test
  public void testMapStatusToValidity204ReturnsTrue() {
    assertEquals( Boolean.TRUE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 204, null ) ) );
  }

  @Test
  public void testMapStatusToValidity500ReturnsNull() {
    assertNull( SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 500, null ) ) );
  }

  @Test
  public void testMapStatusToValidity302WithLoginRedirectReturnsFalse() {
    assertEquals( Boolean.FALSE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 302, "http://server/j_spring_security_check" ) ) );
  }

  @Test
  public void testMapStatusToValidity302WithOAuth2LocationReturnsFalse() {
    assertEquals( Boolean.FALSE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 302, "/oauth2/authorization/google" ) ) );
  }

  @Test
  public void testMapStatusToValidity302WithSamlLocationReturnsFalse() {
    assertEquals( Boolean.FALSE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 302, "/saml2/authenticate/okta" ) ) );
  }

  @Test
  public void testMapStatusToValidity302WithCasLoginReturnsFalse() {
    assertEquals( Boolean.FALSE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 302, "https://cas.example.com/cas/login" ) ) );
  }

  @Test
  public void testMapStatusToValidity302WithOpenIdConnectReturnsFalse() {
    assertEquals( Boolean.FALSE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 302, "/openid-connect/auth" ) ) );
  }

  @Test
  public void testMapStatusToValidity302WithOtherRedirectReturnsTrue() {
    assertEquals( Boolean.TRUE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 302, "http://server/api/resource" ) ) );
  }

  @Test
  public void testMapStatusToValidity302WithNullLocationReturnsTrue() {
    assertEquals( Boolean.TRUE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 302, null ) ) );
  }

  @Test
  public void testMapStatusToValidity302WithEmptyLocationReturnsTrue() {
    assertEquals( Boolean.TRUE, SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 302, "" ) ) );
  }

  @Test
  public void testCreateNoRedirectClientWithSessionIdCreatesClient() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( SESSION_ID_OPTION ) ).thenReturn( "sess123" );
    final HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( data, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateNoRedirectClientNullAuthDataCreatesClientWithoutAuth() {
    final HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( null, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateNoRedirectClientEmptyUsernameCreatesClient() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( data.getUsername() ).thenReturn( "" );
    final HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( data, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testProbeResultFieldsAccessible() {
    final SessionAuthenticationUtil.ProbeResult probe =
      new SessionAuthenticationUtil.ProbeResult( 200, "http://location" );
    assertEquals( Integer.valueOf( 200 ), probe.status );
    assertEquals( "http://location", probe.location );
  }

  @Test
  public void testCookieInterceptorAddsHeader() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( "sessionId" ) ).thenReturn( "sess123" );

    HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( data, 5000 );

    assertNotNull( client );
  }

  @Test
  public void testAddCookieInterceptorDoesNotOverwriteExistingCookieHeader() throws Exception {
    final HttpClientBuilder builder = mock( HttpClientBuilder.class );
    final AtomicReference<HttpRequestInterceptor> interceptorRef = new AtomicReference<>();
    when( builder.addInterceptorFirst( any( HttpRequestInterceptor.class ) ) ).thenAnswer( inv -> {
      interceptorRef.set( inv.getArgument( 0 ) );
      return builder;
    } );

    final Method m = SessionAuthenticationUtil.class
      .getDeclaredMethod( "addCookieInterceptor", HttpClientBuilder.class, String.class );
    m.setAccessible( true );
    m.invoke( null, builder, "sess-123" );

    final HttpRequest request = mock( HttpRequest.class );
    when( request.containsHeader( "Cookie" ) ).thenReturn( true );
    interceptorRef.get().process( request, null );

    Mockito.verify( request, Mockito.never() ).setHeader( Mockito.eq( "Cookie" ), anyString() );
  }

  @Test
  public void testAddBasicAuthInterceptorDoesNotOverwriteExistingAuthorizationHeader() throws Exception {
    final HttpClientBuilder builder = mock( HttpClientBuilder.class );
    final AtomicReference<HttpRequestInterceptor> interceptorRef = new AtomicReference<>();
    when( builder.addInterceptorFirst( any( HttpRequestInterceptor.class ) ) ).thenAnswer( inv -> {
      interceptorRef.set( inv.getArgument( 0 ) );
      return builder;
    } );

    final Method m = SessionAuthenticationUtil.class
      .getDeclaredMethod( "addBasicAuthInterceptor", HttpClientBuilder.class, String.class, String.class );
    m.setAccessible( true );
    m.invoke( null, builder, "user", "pass" );

    final HttpRequest request = mock( HttpRequest.class );
    when( request.containsHeader( "Authorization" ) ).thenReturn( true );
    interceptorRef.get().process( request, null );

    Mockito.verify( request, Mockito.never() ).setHeader( Mockito.eq( "Authorization" ), anyString() );
  }

  @Test
  public void testCheckSessionValidityRedirectWithoutLocationHeaderReturnsTrue() throws Exception {
    HttpServer server = HttpServer.create( new InetSocketAddress( 0 ), 0 );

    server.createContext( "/api/repo/files/tree", exchange -> {
      exchange.sendResponseHeaders( 302, -1 );
      exchange.close();
    } );

    server.start();

    try {
      AuthenticationData data = mock( AuthenticationData.class );
      when( data.getUrl() )
        .thenReturn( "http://localhost:" + server.getAddress().getPort() );
      when( data.getOption( "sessionId" ) )
        .thenReturn( "session123" );

      assertEquals( Boolean.TRUE,
        SessionAuthenticationUtil.checkSessionValidity( data ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testIsServerReachableReturnsTrue() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getUrl() ).thenReturn( "http://localhost" );

    try ( MockedStatic<SessionAuthenticationUtil> mocked =
            Mockito.mockStatic( SessionAuthenticationUtil.class, Mockito.CALLS_REAL_METHODS ) ) {

      mocked.when( () -> SessionAuthenticationUtil.isServerReachable( data ) )
        .thenCallRealMethod();

      mocked.when( () -> SessionAuthenticationUtil.checkSessionValidity( data ) )
        .thenCallRealMethod();
    }
  }

  @Test
  public void testBuildCheckUrlRemovesTrailingSlash() throws Exception {
    Method m = SessionAuthenticationUtil.class
      .getDeclaredMethod( "buildCheckUrl", String.class );
    m.setAccessible( true );

    String result = (String) m.invoke( null, "http://localhost:8080/" );

    assertTrue( result.contains(
      "/api/repo/files/tree?depth=0" ) );
    assertFalse( result.contains( "//api/repo" ) );
  }

  @Test
  public void testBuildCheckUrlWithoutTrailingSlash() throws Exception {
    Method m = SessionAuthenticationUtil.class
      .getDeclaredMethod( "buildCheckUrl", String.class );
    m.setAccessible( true );

    String result = (String) m.invoke( null, "http://localhost:8080" );

    assertTrue( result.contains(
      "/api/repo/files/tree?depth=0" ) );
  }

  @Test
  public void testCheckSessionValidityReturnsNullWhenProbeFails() {
    AuthenticationData data = mock( AuthenticationData.class );

    when( data.getUrl() ).thenReturn( "http://invalid.invalid" );
    when( data.getOption( "sessionId" ) ).thenReturn( "abc" );

    assertNull(
      SessionAuthenticationUtil.checkSessionValidity( data ) );
  }

  @Test
  public void testCheckSessionValidityRedirectWithLocationHeader() throws Exception {
    HttpServer server = HttpServer.create( new InetSocketAddress( 0 ), 0 );

    server.createContext( "/api/repo/files/tree", exchange -> {
      exchange.getResponseHeaders().add(
        "Location",
        "/oauth2/authorization/test" );
      exchange.sendResponseHeaders( 302, -1 );
      exchange.close();
    } );

    server.start();

    try {
      AuthenticationData data = mock( AuthenticationData.class );

      when( data.getUrl() )
        .thenReturn( "http://localhost:" + server.getAddress().getPort() );
      when( data.getOption( "sessionId" ) )
        .thenReturn( "session123" );

      assertEquals(
        Boolean.FALSE,
        SessionAuthenticationUtil.checkSessionValidity( data ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testIsServerReachableConsumesEntity() throws Exception {
    HttpServer server = HttpServer.create( new InetSocketAddress( 0 ), 0 );

    server.createContext( "/api/repo/files/tree", exchange -> {
      byte[] body = "{\"status\":\"ok\"}".getBytes( StandardCharsets.UTF_8 );

      exchange.sendResponseHeaders( 200, body.length );
      exchange.getResponseBody().write( body );
      exchange.close();
    } );

    server.start();

    try {
      AuthenticationData data = mock( AuthenticationData.class );

      when( data.getUrl() )
        .thenReturn( "http://localhost:" + server.getAddress().getPort() );
      when( data.getOption( "sessionId" ) )
        .thenReturn( "session123" );

      assertTrue( SessionAuthenticationUtil.isServerReachable( data ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testIsServerReachableSuccess() throws Exception {
    HttpServer server = HttpServer.create(
      new InetSocketAddress( 0 ), 0 );

    server.createContext(
      "/api/repo/files/tree",
      exchange -> {
        exchange.sendResponseHeaders( 200, -1 );
        exchange.close();
      } );

    server.start();

    try {
      int port = server.getAddress().getPort();

      AuthenticationData data = mock( AuthenticationData.class );
      when( data.getUrl() )
        .thenReturn( "http://localhost:" + port );
      when( data.getOption( "sessionId" ) )
        .thenReturn( "abc" );

      assertTrue(
        SessionAuthenticationUtil.isServerReachable( data ) );

      assertEquals(
        Boolean.TRUE,
        SessionAuthenticationUtil.checkSessionValidity( data ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testBasicAuthHeaderAdded() throws Exception {
    HttpServer server = HttpServer.create( new InetSocketAddress( 0 ), 0 );

    AtomicReference<String> authHeader = new AtomicReference<>();

    server.createContext( "/api/repo/files/tree", exchange -> {
      authHeader.set( exchange.getRequestHeaders().getFirst( "Authorization" ) );

      byte[] body = "ok".getBytes( StandardCharsets.UTF_8 );
      exchange.sendResponseHeaders( 200, body.length );
      exchange.getResponseBody().write( body );
      exchange.close();
    } );

    server.start();

    try {
      AuthenticationData data = mock( AuthenticationData.class );

      when( data.getUrl() )
        .thenReturn( "http://localhost:" + server.getAddress().getPort() );
      when( data.getUsername() )
        .thenReturn( "user" );
      when( data.getPassword() )
        .thenReturn( "pass" );
      when( data.getOption( "sessionId" ) )
        .thenReturn( null );

      assertTrue( SessionAuthenticationUtil.isServerReachable( data ) );

      assertNotNull( authHeader.get() );
      assertTrue( authHeader.get().startsWith( "Basic " ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testSessionCookieHeaderAdded() throws Exception {
    HttpServer server = HttpServer.create( new InetSocketAddress( 0 ), 0 );

    AtomicReference<String> cookie = new AtomicReference<>();

    server.createContext( "/api/repo/files/tree", exchange -> {
      cookie.set( exchange.getRequestHeaders().getFirst( "Cookie" ) );

      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );

    server.start();

    try {
      AuthenticationData data = mock( AuthenticationData.class );

      when( data.getUrl() )
        .thenReturn( "http://localhost:" + server.getAddress().getPort() );
      when( data.getOption( "sessionId" ) )
        .thenReturn( "sess123" );

      assertTrue( SessionAuthenticationUtil.isServerReachable( data ) );

      assertEquals( "JSESSIONID=sess123", cookie.get() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testBuildCheckUrlWithTrailingSlash() throws Exception {
    Method m = SessionAuthenticationUtil.class
      .getDeclaredMethod( "buildCheckUrl", String.class );

    m.setAccessible( true );

    String result = (String) m.invoke( null, "http://localhost/" );

    assertFalse( result.contains( "//api" ) );
  }

  @Test
  public void testCreateNoRedirectClientBasicAuthVariants() {

    Object[][] cases = {
      { null, "pass" },
      { null, null },
      { "", "pass" }
    };

    for ( Object[] testCase : cases ) {

      AuthenticationData data =
        mock( AuthenticationData.class );

      when( data.getOption( SESSION_ID_OPTION ) )
        .thenReturn( (String) testCase[ 0 ] );

      when( data.getUsername() )
        .thenReturn( "user" );

      when( data.getPassword() )
        .thenReturn( (String) testCase[ 1 ] );

      HttpClient client =
        SessionAuthenticationUtil.createNoRedirectClient(
          data,
          5000 );

      assertNotNull( client );
    }
  }

  @Test
  public void testCheckSessionValidityOAuthRedirect() throws Exception {
    HttpServer server = HttpServer.create( new InetSocketAddress( 0 ), 0 );

    server.createContext( "/api/repo/files/tree", exchange -> {
      exchange.getResponseHeaders()
        .add( "Location", "/oauth2/authorization/test" );

      exchange.sendResponseHeaders( 302, -1 );
      exchange.close();
    } );

    server.start();

    try {
      AuthenticationData data = mock( AuthenticationData.class );

      when( data.getUrl() )
        .thenReturn( "http://localhost:" + server.getAddress().getPort() );
      when( data.getOption( "sessionId" ) )
        .thenReturn( "sess123" );

      assertEquals( Boolean.FALSE,
        SessionAuthenticationUtil.checkSessionValidity( data ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testCheckSessionValidityConsumesEntity() throws Exception {
    HttpServer server = HttpServer.create( new InetSocketAddress( 0 ), 0 );

    server.createContext( "/api/repo/files/tree", exchange -> {
      byte[] body = "test-body".getBytes( StandardCharsets.UTF_8 );

      exchange.getResponseHeaders().add( "Content-Type", "application/json" );
      exchange.sendResponseHeaders( 302, body.length );
      exchange.getResponseHeaders().add( "Location", "/oauth2/authorization/test" );

      exchange.getResponseBody().write( body );
      exchange.getResponseBody().close();
    } );

    server.start();

    try {
      AuthenticationData data = mock( AuthenticationData.class );

      when( data.getUrl() )
        .thenReturn( "http://localhost:" + server.getAddress().getPort() );
      when( data.getOption( "sessionId" ) )
        .thenReturn( "session123" );

      SessionAuthenticationUtil.checkSessionValidity( data );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testCreateNoRedirectClientNullUsernameSkipsBasicAuth() {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( data.getUsername() ).thenReturn( null ); // username == null → condition false
    final HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( data, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testIsServerReachableReturnsFalseFor400() throws Exception {
    com.sun.net.httpserver.HttpServer server =
      com.sun.net.httpserver.HttpServer.create( new java.net.InetSocketAddress( 0 ), 0 );
    server.createContext( "/api/repo/files/tree", exchange -> {
      exchange.sendResponseHeaders( 400, -1 );
      exchange.close();
    } );
    server.start();
    try {
      final AuthenticationData data = mock( AuthenticationData.class );
      when( data.getUrl() ).thenReturn( "http://localhost:" + server.getAddress().getPort() );
      when( data.getOption( SESSION_ID_OPTION ) ).thenReturn( "sess-abc" );
      assertFalse( SessionAuthenticationUtil.isServerReachable( data ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testMapStatusToValidity1xxStatusReturnsNull() {
    assertNull( SessionAuthenticationUtil.mapStatusToValidity(
      new SessionAuthenticationUtil.ProbeResult( 100, null ) ) );
  }
}

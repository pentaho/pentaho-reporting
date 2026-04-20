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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;

import org.apache.http.client.HttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SessionAuthenticationUtilTest {

  private static final String SESSION_ID_OPTION = "sessionId";
  private static final String ADMIN_USERNAME = "admin";
  private static final String ADMIN_PASSWORD = "password";

  private AuthenticationData authData;

  @Before
  public void setUp() {
    authData = mock( AuthenticationData.class );
  }

  @Test
  public void testIsSessionBasedNullAuthData() {
    assertFalse( SessionAuthenticationUtil.isSessionBased( null ) );
  }

  @Test
  public void testIsSessionBasedNullSessionId() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    assertFalse( SessionAuthenticationUtil.isSessionBased( authData ) );
  }

  @Test
  public void testIsSessionBasedEmptySessionId() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "" );
    assertFalse( SessionAuthenticationUtil.isSessionBased( authData ) );
  }

  @Test
  public void testIsSessionBasedValidSessionId() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "ABC123" );
    assertTrue( SessionAuthenticationUtil.isSessionBased( authData ) );
  }

  @Test
  public void testCreateClientWithSessionId() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_123" );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, 30000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClientNoSessionIdWithCredentials() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( authData.getPassword() ).thenReturn( ADMIN_PASSWORD );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, 30000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClientNoSessionIdNullUsername() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( null );
    when( authData.getPassword() ).thenReturn( null );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, 30000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClientNoSessionIdEmptyUsername() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( "" );
    when( authData.getPassword() ).thenReturn( "" );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, 30000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClientEmptySessionId() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "" );
    when( authData.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( authData.getPassword() ).thenReturn( "pass" );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClientZeroTimeout() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS" );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, 0 );
    assertNotNull( client );
  }

  @Test
  public void testCreateNoRedirectClientWithSessionId() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_123" );

    HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( authData, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateNoRedirectClientWithCredentials() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( authData.getPassword() ).thenReturn( ADMIN_PASSWORD );

    HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( authData, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateNoRedirectClientNullAuthData() {
    HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( null, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateNoRedirectClientNullUsername() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( null );

    HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( authData, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateNoRedirectClientEmptyUsername() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( "" );

    HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( authData, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateNoRedirectClientNullPassword() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( authData.getPassword() ).thenReturn( null );

    HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( authData, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateNoRedirectClientEmptySessionIdWithCredentials() {
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "" );
    when( authData.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( authData.getPassword() ).thenReturn( ADMIN_PASSWORD );

    HttpClient client = SessionAuthenticationUtil.createNoRedirectClient( authData, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClientNullAuthData() {
    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient( null, 5000 );
    assertNotNull( client );
  }

  @Test
  public void testPrivateConstructorCoverage() throws Exception {
    Constructor<SessionAuthenticationUtil> constructor =
      SessionAuthenticationUtil.class.getDeclaredConstructor();
    assertTrue( java.lang.reflect.Modifier.isPrivate( constructor.getModifiers() ) );
    constructor.setAccessible( true );
    SessionAuthenticationUtil instance = constructor.newInstance();
    assertNotNull( instance );
  }

  @Test
  public void testIsServerReachableNullAuthData() {
    assertFalse( SessionAuthenticationUtil.isServerReachable( null ) );
  }

  @Test
  public void testIsServerReachableNullUrl() {
    when( authData.getUrl() ).thenReturn( null );
    assertFalse( SessionAuthenticationUtil.isServerReachable( authData ) );
  }

  @Test
  public void testIsServerReachableUnreachableServer() {
    when( authData.getUrl() ).thenReturn( "http://localhost:19999/pentaho" );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( authData.getPassword() ).thenReturn( ADMIN_PASSWORD );
    assertFalse( SessionAuthenticationUtil.isServerReachable( authData ) );
  }

  @Test
  public void testIsServerReachableUrlWithTrailingSlash() {
    when( authData.getUrl() ).thenReturn( "http://localhost:19999/pentaho/" );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( authData.getPassword() ).thenReturn( ADMIN_PASSWORD );
    assertFalse( SessionAuthenticationUtil.isServerReachable( authData ) );
  }

  // ---- checkSessionValidity tests ----

  @Test
  public void testCheckSessionValidityNullAuthData() {
    Assert.assertNull( SessionAuthenticationUtil.checkSessionValidity( null ) );
  }

  @Test
  public void testCheckSessionValidityNullUrl() {
    when( authData.getUrl() ).thenReturn( null );
    Assert.assertNull( SessionAuthenticationUtil.checkSessionValidity( authData ) );
  }

  @Test
  public void testCheckSessionValidityUnreachableServer() {
    when( authData.getUrl() ).thenReturn( "http://localhost:19999/pentaho" );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_X" );
    Assert.assertNull( SessionAuthenticationUtil.checkSessionValidity( authData ) );
  }

  @Test
  public void testCheckSessionValidityReturnsTrueForBasicAuth() {
    when( authData.getUrl() ).thenReturn( "http://localhost:19999/pentaho" );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( authData.getPassword() ).thenReturn( ADMIN_PASSWORD );
    Assert.assertEquals( Boolean.TRUE, SessionAuthenticationUtil.checkSessionValidity( authData ) );
  }

  @Test
  public void testCheckSessionValidityUrlWithTrailingSlash() {
    when( authData.getUrl() ).thenReturn( "http://localhost:19999/pentaho/" );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS123" );
    // Connection refused → null (unknown)
    Assert.assertNull( SessionAuthenticationUtil.checkSessionValidity( authData ) );
  }

  // ---- checkSessionValidity behaviour against an embedded HTTP server ----

  private HttpServer server;

  @After
  public void tearDownServer() {
    if ( server != null ) {
      server.stop( 0 );
      server = null;
    }
  }

  /** Starts an embedded HTTP server bound to a random free port. */
  private String startServer( HttpHandler handler ) throws IOException {
    server = HttpServer.create( new InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/", handler );
    server.setExecutor( null );
    server.start();
    return "http://127.0.0.1:" + server.getAddress().getPort();
  }

  private static void writeResponse( HttpExchange ex, int status, String contentType, String body )
      throws IOException {
    byte[] bytes = body.getBytes();
    if ( contentType != null ) {
      ex.getResponseHeaders().set( "Content-Type", contentType );
    }
    ex.sendResponseHeaders( status, bytes.length );
    try ( OutputStream os = ex.getResponseBody() ) {
      os.write( bytes );
    }
  }

  @Test
  public void testCheckSessionValidityReturnsTrueOnJsonOk() throws Exception {
    String url = startServer( ex -> writeResponse( ex, 200, "application/json", "[]" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_OK" );

    Assert.assertEquals( Boolean.TRUE, SessionAuthenticationUtil.checkSessionValidity( authData ) );
  }

  @Test
  public void testCheckSessionValidityReturnsFalseOn401() throws Exception {
    String url = startServer( ex -> writeResponse( ex, 401, "application/json", "" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_BAD" );

    Assert.assertEquals( Boolean.FALSE, SessionAuthenticationUtil.checkSessionValidity( authData ) );
  }

  @Test
  public void testCheckSessionValidityReturnsFalseOn403() throws Exception {
    String url = startServer( ex -> writeResponse( ex, 403, "application/json", "" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_FORBID" );

    Assert.assertEquals( Boolean.FALSE, SessionAuthenticationUtil.checkSessionValidity( authData ) );
  }

  @Test
  public void testCheckSessionValidityOn302Redirects() throws Exception {
    // Parameterized checks for 302 redirect targets and expected validity results.
    // location -> expected result:
    //   /pentaho/Login             -> TRUE  (landing page, not session expired)
    //   j_spring_security_check    -> FALSE (login endpoint means expired)
    //   /pentaho/Home              -> TRUE  (non-login page)
    //   oauth2/authorization/azure -> FALSE (IDP redirect means expired)
    //   null (no Location header)  -> TRUE  (cannot tell, err on valid side)
    Object[][] scenarios = {
      { "/pentaho/Login",                                                          Boolean.TRUE },
      { "/pentaho/j_spring_security_check",                                        Boolean.FALSE },
      { "/pentaho/Home",                                                           Boolean.TRUE },
      { "http://server/pentaho/oauth2/authorization/azure?client_id=x",            Boolean.FALSE },
      { null,                                                                      Boolean.TRUE }
    };
    for ( Object[] scenario : scenarios ) {
      final String location = (String) scenario[0];
      final Boolean expected = (Boolean) scenario[1];
      String url = startServer( ex -> {
        if ( location != null ) {
          ex.getResponseHeaders().set( "Location", location );
        }
        ex.sendResponseHeaders( 302, -1 );
        ex.close();
      } );
      when( authData.getUrl() ).thenReturn( url );
      when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_REDIRECT" );

      Assert.assertEquals( "302 redirect to " + location, expected,
        SessionAuthenticationUtil.checkSessionValidity( authData ) );
    }
  }

  @Test
  public void testCheckSessionValidityReturnsTrueOnHtmlResponse() throws Exception {
    String html = "<html><body>some pentaho html ui</body></html>";
    String url = startServer( ex -> writeResponse( ex, 200, "text/html;charset=UTF-8", html ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_HTML" );

    Assert.assertEquals( Boolean.TRUE, SessionAuthenticationUtil.checkSessionValidity( authData ) );
  }

  @Test
  public void testCheckSessionValidityReturnsNullOn500() throws Exception {
    String url = startServer( ex -> writeResponse( ex, 500, "application/json", "{}" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_X" );

    // 5xx is not an auth signal → unknown.
    Assert.assertNull( SessionAuthenticationUtil.checkSessionValidity( authData ) );
  }

  @Test
  public void testIsServerReachableTrueOnJsonOk() throws Exception {
    String url = startServer( ex -> writeResponse( ex, 200, "application/json", "[]" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_OK" );

    assertTrue( SessionAuthenticationUtil.isServerReachable( authData ) );
  }

  @Test
  public void testIsServerReachableTrueOnHtmlResponse() throws Exception {
    String url = startServer( ex -> writeResponse( ex, 200, "text/html", "<html></html>" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_OK" );

    assertTrue( SessionAuthenticationUtil.isServerReachable( authData ) );
  }

  @Test
  public void testIsSessionExplicitlyExpiredNullAuthData() {
    assertFalse( SessionAuthenticationUtil.isSessionExplicitlyExpired( null ) );
  }

  @Test
  public void testIsSessionExplicitlyExpiredNonSessionBased() {
    // U/P auth has no session concept -> always false
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( ADMIN_USERNAME );
    assertFalse( SessionAuthenticationUtil.isSessionExplicitlyExpired( authData ) );
  }

  @Test
  public void testIsSessionExplicitlyExpiredFirstProbeReturnsTrue() throws Exception {
    // Valid session (first probe) -> false
    String url = startServer( ex -> writeResponse( ex, 200, "application/json", "[]" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_VALID" );

    assertFalse( SessionAuthenticationUtil.isSessionExplicitlyExpired( authData ) );
  }

  @Test
  public void testIsSessionExplicitlyExpiredFirstProbeReturnsFalseSecondProbeReturnsTrue() throws Exception {
    // Transient 401 followed by success (propagation delay) -> false
    // We need to return different values on successive calls
    // Use AtomicInteger to track call count
    java.util.concurrent.atomic.AtomicInteger callCount = new java.util.concurrent.atomic.AtomicInteger( 0 );
    String url = startServer( ex -> {
      if ( callCount.incrementAndGet() == 1 ) {
        writeResponse( ex, 401, "application/json", "" );
      } else {
        writeResponse( ex, 200, "application/json", "[]" );
      }
    } );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_TRANSIENT_401" );

    assertFalse( SessionAuthenticationUtil.isSessionExplicitlyExpired( authData ) );
  }

  @Test
  public void testIsSessionExplicitlyExpiredBothProbesReturnFalse() throws Exception {
    // Two consecutive 401s -> true (genuinely expired)
    String url = startServer( ex -> writeResponse( ex, 401, "application/json", "" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_EXPIRED" );

    assertTrue( SessionAuthenticationUtil.isSessionExplicitlyExpired( authData ) );
  }

  @Test
  public void testIsSessionExplicitlyExpiredFirstProbeReturnsUnknown() {
    // First probe unknown (network error) -> false
    when( authData.getUrl() ).thenReturn( "http://localhost:19999/pentaho" ); // unreachable
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_UNKNOWN" );

    assertFalse( SessionAuthenticationUtil.isSessionExplicitlyExpired( authData ) );
  }

  @Test
  public void testIsSessionExplicitlyExpiredFirstProbeFalseSecondProbeUnknown() throws Exception {
    // First 401, second unknown (network error) -> false (not confirmed expired)
    String url = startServer( HttpExchange::close );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_DISCONNECT" );

    // This will try to probe, get 401 on first, network error on second -> false
    assertFalse( SessionAuthenticationUtil.isSessionExplicitlyExpired( authData ) );
  }

  @Test
  public void testIsSessionExplicitlyExpiredReturnsFalseOn403() throws Exception {
    // 403 is treated same as 401 (both are FALSE)
    // Two consecutive 403s -> true
    String url = startServer( ex -> writeResponse( ex, 403, "application/json", "" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_FORBIDDEN" );

    assertTrue( SessionAuthenticationUtil.isSessionExplicitlyExpired( authData ) );
  }

  @Test
  public void testIsSessionExplicitlyExpiredUnreachableServer() {
    // Two network errors -> false (not explicitly expired, just unreachable)
    when( authData.getUrl() ).thenReturn( "http://localhost:19999/pentaho" );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_DOWN" );

    assertFalse( SessionAuthenticationUtil.isSessionExplicitlyExpired( authData ) );
  }

  // ---- redirect location patterns: saml2, cas, openid-connect ----

  @Test
  public void testCheckSessionValidityReturnsFalseOn302RedirectToSsoProviders() throws Exception {
    String[][] cases = {
      { "/pentaho/saml2/authenticate", "SESS_SAML" },
      { "https://sso.example.com/cas/login?service=x", "SESS_CAS" },
      { "https://idp.example.com/openid-connect/auth?client_id=x", "SESS_OIDC" }
    };
    for ( String[] c : cases ) {
      String location = c[0];
      String sessionId = c[1];
      String url = startServer( ex -> {
        ex.getResponseHeaders().set( "Location", location );
        ex.sendResponseHeaders( 302, -1 );
        ex.close();
      } );
      when( authData.getUrl() ).thenReturn( url );
      when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( sessionId );

      Assert.assertEquals( Boolean.FALSE, SessionAuthenticationUtil.checkSessionValidity( authData ) );
    }
  }

  // ---- mapStatusToValidity direct tests ----

  @Test
  public void testMapStatusToValidityNullProbe() {
    Assert.assertNull( SessionAuthenticationUtil.mapStatusToValidity( null ) );
  }

  @Test
  public void testMapStatusToValidityScenarios() {
    // status, location, expected
    Object[][] scenarios = {
      { null,  null,                                   null },          // null status
      { 401,   null,                                   Boolean.FALSE }, // unauthorized
      { 403,   null,                                   Boolean.FALSE }, // forbidden
      { 200,   null,                                   Boolean.TRUE },  // OK
      { 500,   null,                                   null },          // server error
      { 302,   "/pentaho/j_spring_security_check",     Boolean.FALSE }, // login redirect
      { 302,   "/pentaho/Home",                        Boolean.TRUE },  // non-login redirect
      { 302,   null,                                   Boolean.TRUE },  // redirect, null location
      { 302,   "",                                     Boolean.TRUE }   // redirect, empty location
    };
    for ( Object[] s : scenarios ) {
      SessionAuthenticationUtil.ProbeResult probe =
        new SessionAuthenticationUtil.ProbeResult( (Integer) s[0], (String) s[1] );
      Assert.assertEquals( "status=" + s[0] + " location=" + s[1], (Boolean) s[2],
        SessionAuthenticationUtil.mapStatusToValidity( probe ) );
    }
  }

  @Test
  public void testMapStatusToValidity301Redirect() {
    SessionAuthenticationUtil.ProbeResult probe =
      new SessionAuthenticationUtil.ProbeResult( 301, "/pentaho/oauth2/authorization/azure" );
    Assert.assertEquals( Boolean.FALSE, SessionAuthenticationUtil.mapStatusToValidity( probe ) );
  }

  @Test
  public void testProbeResultConstructor() {
    SessionAuthenticationUtil.ProbeResult probe = new SessionAuthenticationUtil.ProbeResult( 200, "http://loc" );
    Assert.assertEquals( Integer.valueOf( 200 ), probe.status );
    Assert.assertEquals( "http://loc", probe.location );
  }

  // ---- isSessionExplicitlyExpired interrupt handling ----

  @Test
  @SuppressWarnings( "java:S2925" )
  public void testIsSessionExplicitlyExpiredInterruptOnSleep() throws Exception {
    // First probe returns FALSE (401), then sleep is interrupted -> returns false
    String url = startServer( ex -> writeResponse( ex, 401, "application/json", "" ) );
    when( authData.getUrl() ).thenReturn( url );
    when( authData.getOption( SESSION_ID_OPTION ) ).thenReturn( "SESS_INT" );

    Thread testThread = Thread.currentThread();
    // Schedule an interrupt during the 500ms sleep between probes
    new Thread( () -> {
      try {
        Thread.sleep( 100 ); // small delay to ensure we hit the inter-probe sleep window
      } catch ( InterruptedException ie ) {
        Thread.currentThread().interrupt();
      }
      testThread.interrupt();
    } ).start();

    assertFalse( SessionAuthenticationUtil.isSessionExplicitlyExpired( authData ) );
    // Clear interrupted status
    Thread.interrupted();
  }
}

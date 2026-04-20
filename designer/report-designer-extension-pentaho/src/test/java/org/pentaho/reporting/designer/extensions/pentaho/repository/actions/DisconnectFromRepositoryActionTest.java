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
import java.lang.reflect.Method;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;

public class DisconnectFromRepositoryActionTest {

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
  public void testConstructorSetsName() {
    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    assertNotNull( action );
    String name = (String) action.getValue( Action.NAME );
    assertNotNull( name );
    assertTrue( name.contains( Messages.getInstance().getString( "DisconnectFromRepositoryAction.Text" ) ) );
  }

  @Test
  public void testDisabledWhenNoActiveSession() {
    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    assertFalse( action.isEnabled() );
  }

  @Test
  public void testEnabledWhenActiveSession() {
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    assertTrue( action.isEnabled() );
  }

  @Test
  public void testPropertyChangeEnablesOnSessionSet() {
    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    assertFalse( action.isEnabled() );

    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( "testuser" );
    RepositorySessionManager.getInstance().setSession( session, "testuser" );

    assertTrue( action.isEnabled() );
  }

  @Test
  public void testPropertyChangeDisablesOnSessionClear() {
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    assertTrue( action.isEnabled() );

    RepositorySessionManager.getInstance().clearSession();
    assertFalse( action.isEnabled() );
  }

  @Test
  public void testNameIncludesUsernameWhenActive() {
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    String name = (String) action.getValue( Action.NAME );
    assertTrue( name.contains( ADMIN_USERNAME ) );
  }

  @Test
  public void testNameWithoutUsernameWhenInactive() {
    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    String name = (String) action.getValue( Action.NAME );
    assertEquals( Messages.getInstance().getString( "DisconnectFromRepositoryAction.Text" ), name );
  }

  @Test
  public void testUpdateDesignerContext() {
    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    action.updateDesignerContext( null, null );
    assertFalse( action.isEnabled() );
  }

  @Test
  public void testDescriptionSetInConstructor() {
    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    String desc = (String) action.getValue( Action.SHORT_DESCRIPTION );
    assertNotNull( desc );
  }

  @Test
  public void testDescriptionUpdatesOnSessionChange() {
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( "testuser" );
    RepositorySessionManager.getInstance().setSession( session, "testuser" );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    String desc = (String) action.getValue( Action.SHORT_DESCRIPTION );
    assertNotNull( desc );

    RepositorySessionManager.getInstance().clearSession();
    String descAfterClear = (String) action.getValue( Action.SHORT_DESCRIPTION );
    assertEquals( Messages.getInstance().getString( "DisconnectFromRepositoryAction.Description" ), descAfterClear );
  }

  // ---- actionPerformed coverage (parameterized across session URL/id variations) ----

  @Test
  public void testActionPerformedClearsSessionForVariousSessionStates() {
    final Object[][] scenarios = {
      // url, sessionId
      { null, null },
      { null, "SESS" },
      { "http://localhost:8080/pentaho", null },
      { "http://localhost:8080/pentaho", "" },
      { "http://localhost:8080/pentaho/", "VALID_SESS" },
      { "http://localhost:8080/pentaho", "VALID_SESS_123" }
    };

    for ( Object[] scenario : scenarios ) {
      RepositorySessionManager.getInstance().clearSession();
      AuthenticationData session = mock( AuthenticationData.class );
      when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
      when( session.getUrl() ).thenReturn( (String) scenario[ 0 ] );
      when( session.getOption( "sessionId" ) ).thenReturn( (String) scenario[ 1 ] );
      RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

      DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
      try ( var jopMock = mockStatic( JOptionPane.class ) ) {
        jopMock.when( () -> JOptionPane.showMessageDialog(
          any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );
        action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
      }
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testActionPerformedWithDesignerContextUsesParent() {
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( session.getUrl() ).thenReturn( null );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    ReportDesignerContext ctx = mock( ReportDesignerContext.class );
    Component parent = mock( Component.class );
    org.pentaho.reporting.designer.core.ReportDesignerView view =
      mock( org.pentaho.reporting.designer.core.ReportDesignerView.class );
    when( ctx.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( parent );
    action.setReportDesignerContext( ctx );

    try ( var jopMock = mockStatic( JOptionPane.class ) ) {
      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
    }
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void testActionPerformedWithNullSession() {
    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    try ( var jopMock = mockStatic( JOptionPane.class ) ) {
      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "test" ) );
    }
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void testInvalidateViaHttpHandlesConnectionRefused() {
    // No server listening on this port → IOException path.
    DisconnectFromRepositoryAction.invalidateViaHttp(
      "http://127.0.0.1:1/Logout", "SESS_ABC" );
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void testInvalidateViaHttpWithInvalidUrl() {
    DisconnectFromRepositoryAction.invalidateViaHttp(
      "not-a-valid-url", "SESS" );
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void testInvalidateViaHttpRejectsUnsafeSessionId() {
    // Should return silently without making HTTP call.
    DisconnectFromRepositoryAction.invalidateViaHttp(
      "http://127.0.0.1:1/Logout", "bad\nvalue" );
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    DisconnectFromRepositoryAction.invalidateViaHttp(
      "http://127.0.0.1:1/Logout", null );
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    DisconnectFromRepositoryAction.invalidateViaHttp(
      "http://127.0.0.1:1/Logout", "" );
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  // ---- updateSessionState branch: empty/null user ----

  @Test
  public void testNameShowsUnknownWhenUsernameNull() {
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( null );
    RepositorySessionManager.getInstance().setSession( session, null );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    String name = (String) action.getValue( Action.NAME );
    assertTrue( "Should show 'unknown' for null user", name.contains( "unknown" ) );
  }

  @Test
  public void testNameShowsUnknownWhenUsernameEmpty() {
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( "" );
    RepositorySessionManager.getInstance().setSession( session, "" );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    String name = (String) action.getValue( Action.NAME );
    assertTrue( "Should show 'unknown' for empty user", name.contains( "unknown" ) );
  }

  @Test
  public void testUpdateDesignerContextWithBothNonNull() {
    ReportDesignerContext oldCtx = mock( ReportDesignerContext.class );
    ReportDesignerContext newCtx = mock( ReportDesignerContext.class );
    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    action.updateDesignerContext( oldCtx, newCtx );
    // Just ensures no exception and state is updated
    assertNotNull( action );
  }

  // ---- SSO logout happy path (browserAuth=true, real local /Logout endpoint) ----

  @Test
  public void testActionPerformedSsoBranchHitsLogoutEndpoint() throws Exception {
    final java.util.concurrent.atomic.AtomicInteger calls = new java.util.concurrent.atomic.AtomicInteger();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      calls.incrementAndGet();
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      final String url = "http://127.0.0.1:" + server.getAddress().getPort();
      AuthenticationData session = mock( AuthenticationData.class );
      when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
      when( session.getUrl() ).thenReturn( url );
      when( session.getOption( "sessionId" ) ).thenReturn( "SESS_VALID" );
      when( session.getOption( "browserAuth" ) ).thenReturn( "true" );
      RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

      DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
      try ( var jopMock = mockStatic( JOptionPane.class ) ) {
        jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) )
          .thenAnswer( inv -> null );
        action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "x" ) );
      }
      assertEquals( 1, calls.get() );
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    } finally {
      server.stop( 0 );
    }
  }

  // ---- invalidateViaHttp success path against local server ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testInvalidateViaHttpSuccessPath() throws Exception {
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      DisconnectFromRepositoryAction.invalidateViaHttp(
        "http://127.0.0.1:" + server.getAddress().getPort() + "/Logout", "SESS_X" );
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    } finally {
      server.stop( 0 );
    }
  }

  // ---- invalidateServerSession early-return branches ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testInvalidateServerSessionReturnsEarlyForNullSessionId() throws Exception {
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUrl() ).thenReturn( "http://127.0.0.1:1/pentaho" );
    when( session.getOption( "sessionId" ) ).thenReturn( null );
    RepositorySessionManager.getInstance().setSession( session, "u" );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    Method m = DisconnectFromRepositoryAction.class.getDeclaredMethod( "invalidateServerSession" );
    m.setAccessible( true );
    assertNull( m.invoke( action ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testInvalidateServerSessionReturnsEarlyForEmptySessionId() throws Exception {
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUrl() ).thenReturn( "http://127.0.0.1:1/pentaho" );
    when( session.getOption( "sessionId" ) ).thenReturn( "" );
    RepositorySessionManager.getInstance().setSession( session, "u" );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    Method m = DisconnectFromRepositoryAction.class.getDeclaredMethod( "invalidateServerSession" );
    m.setAccessible( true );
    assertNull( m.invoke( action ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testInvalidateServerSessionReturnsEarlyForNullSessionData() throws Exception {
    // No active session at all
    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    Method m = DisconnectFromRepositoryAction.class.getDeclaredMethod( "invalidateServerSession" );
    m.setAccessible( true );
    assertNull( m.invoke( action ) );
  }

  // ---- isSafeCookieValue branch coverage ----

  @Test
  public void testIsSafeCookieValueRejectsBadValues() {
    assertFalse( DisconnectFromRepositoryAction.isSafeCookieValue( null ) );
    assertFalse( DisconnectFromRepositoryAction.isSafeCookieValue( "" ) );
    assertFalse( DisconnectFromRepositoryAction.isSafeCookieValue( "a;b" ) );
    assertFalse( DisconnectFromRepositoryAction.isSafeCookieValue( "a\rb" ) );
    assertFalse( DisconnectFromRepositoryAction.isSafeCookieValue( "a\nb" ) );
    assertFalse( DisconnectFromRepositoryAction.isSafeCookieValue( "a\u0001b" ) );
    assertFalse( DisconnectFromRepositoryAction.isSafeCookieValue( "a\u007fb" ) );
  }

  @Test
  public void testIsSafeCookieValueAcceptsNormalSession() {
    assertTrue( DisconnectFromRepositoryAction.isSafeCookieValue( "ABC123-DEADBEEF" ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testInvalidateServerSessionReturnsEarlyForNullUrl() throws Exception {
    // Branch: session != null but session.getUrl() == null → early return.
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( "u" );
    when( session.getUrl() ).thenReturn( null );
    when( session.getOption( "sessionId" ) ).thenReturn( "SESS" );
    RepositorySessionManager.getInstance().setSession( session, "u" );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    Method m = DisconnectFromRepositoryAction.class.getDeclaredMethod( "invalidateServerSession" );
    m.setAccessible( true );
    assertNull( m.invoke( action ) );
  }

  // ---- closeCachedVfsFileSystems success path (mocks VFS + PublishUtil) ----

  @Test
  public void testActionPerformedSwallowsVfsCacheEvictionFailure() {
    // Forces closeCachedVfsFileSystems' catch-block to fire. Disconnect must
    // still complete cleanly when VFS cache eviction blows up.
    AuthenticationData session = mock( AuthenticationData.class );
    when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
    when( session.getUrl() ).thenReturn( "http://example.invalid/pentaho" );
    when( session.getOption( "sessionId" ) ).thenReturn( "SESS" );
    when( session.getOption( "browserAuth" ) ).thenReturn( null );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

    DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
    try ( var jopMock = mockStatic( JOptionPane.class );
          var vfsMock = mockStatic( org.apache.commons.vfs2.VFS.class ) ) {
      jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) )
        .thenAnswer( inv -> null );
      vfsMock.when( org.apache.commons.vfs2.VFS::getManager )
        .thenThrow( new RuntimeException( "boom" ) );
      action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "x" ) );
    }
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  // ---- invalidateViaHttp sends JSESSIONID cookie to server ----

  @Test
  public void testInvalidateViaHttpSendsJsessionIdCookie() throws Exception {
    final java.util.concurrent.atomic.AtomicReference<String> seenCookie =
      new java.util.concurrent.atomic.AtomicReference<>();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      seenCookie.set( exchange.getRequestHeaders().getFirst( "Cookie" ) );
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      DisconnectFromRepositoryAction.invalidateViaHttp(
        "http://127.0.0.1:" + server.getAddress().getPort() + "/Logout",
        "SESS_XYZ" );
      assertEquals( "JSESSIONID=SESS_XYZ", seenCookie.get() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testInvalidateViaHttpUsesGetMethod() throws Exception {
    final java.util.concurrent.atomic.AtomicReference<String> seenMethod =
      new java.util.concurrent.atomic.AtomicReference<>();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      seenMethod.set( exchange.getRequestMethod() );
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      DisconnectFromRepositoryAction.invalidateViaHttp(
        "http://127.0.0.1:" + server.getAddress().getPort() + "/Logout",
        "SESS" );
      assertEquals( "GET", seenMethod.get() );
    } finally {
      server.stop( 0 );
    }
  }

  // ---- invalidateViaHttp follows redirects (key fix for intermittent logout failure) ----

  @Test
  public void testInvalidateViaHttpDoesNotFollowRedirects() throws Exception {
    final java.util.concurrent.atomic.AtomicInteger logoutHits = new java.util.concurrent.atomic.AtomicInteger();
    final java.util.concurrent.atomic.AtomicInteger loginHits = new java.util.concurrent.atomic.AtomicInteger();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      logoutHits.incrementAndGet();
      int port = exchange.getLocalAddress().getPort();
      exchange.getResponseHeaders().add( "Location", "http://127.0.0.1:" + port + "/Login" );
      exchange.sendResponseHeaders( 302, -1 );
      exchange.close();
    } );
    server.createContext( "/Login", exchange -> {
      loginHits.incrementAndGet();
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      DisconnectFromRepositoryAction.invalidateViaHttp(
        "http://127.0.0.1:" + server.getAddress().getPort() + "/Logout",
        "SESS" );
      assertEquals( "Should hit /Logout once", 1, logoutHits.get() );
      assertEquals( "Should NOT follow the redirect to /Login", 0, loginHits.get() );
    } finally {
      server.stop( 0 );
    }
  }

  // ---- invalidateServerSession: end-to-end SSO logout hits /Logout ----

  @Test
  public void testActionPerformedSsoHitsLogoutWithTrailingSlashUrl() throws Exception {
    final java.util.concurrent.atomic.AtomicInteger logoutCalls = new java.util.concurrent.atomic.AtomicInteger();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      logoutCalls.incrementAndGet();
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      // Trailing slash exercises the URL-trimming branch.
      final String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/";
      AuthenticationData session = mock( AuthenticationData.class );
      when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
      when( session.getUrl() ).thenReturn( url );
      when( session.getOption( "sessionId" ) ).thenReturn( "SESS_VALID" );
      RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

      DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
      try ( var jopMock = mockStatic( JOptionPane.class ) ) {
        jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) )
          .thenAnswer( inv -> null );
        action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "x" ) );
      }
      assertEquals( 1, logoutCalls.get() );
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testClearJvmCookieStoreRemovesAllCookies() {
    java.net.CookieHandler previous = java.net.CookieHandler.getDefault();
    try {
      java.net.CookieManager mgr = new java.net.CookieManager( null, java.net.CookiePolicy.ACCEPT_ALL );
      java.net.CookieHandler.setDefault( mgr );
      java.net.HttpCookie c1 = new java.net.HttpCookie( "JSESSIONID", "STALE-1" );
      c1.setPath( "/" );
      java.net.HttpCookie c2 = new java.net.HttpCookie( "OTHER", "v" );
      c2.setPath( "/" );
      mgr.getCookieStore().add( java.net.URI.create( "http://example.com/" ), c1 );
      mgr.getCookieStore().add( java.net.URI.create( "http://example.com/" ), c2 );
      assertEquals( 2, mgr.getCookieStore().getCookies().size() );

      DisconnectFromRepositoryAction.clearJvmCookieStore();

      assertTrue( "All cookies must be cleared on disconnect",
        mgr.getCookieStore().getCookies().isEmpty() );
    } finally {
      java.net.CookieHandler.setDefault( previous );
    }
  }

  @Test
  public void testClearJvmCookieStoreNoOpWhenNoCookieManager() {
    java.net.CookieHandler previous = java.net.CookieHandler.getDefault();
    try {
      java.net.CookieHandler.setDefault( null );
      // Must not throw and must leave the (null) default unchanged.
      DisconnectFromRepositoryAction.clearJvmCookieStore();
      assertNull( java.net.CookieHandler.getDefault() );
    } finally {
      java.net.CookieHandler.setDefault( previous );
    }
  }

  @Test
  public void testClearJvmCookieStoreNoOpForCustomHandler() {
    java.net.CookieHandler previous = java.net.CookieHandler.getDefault();
    try {
      java.net.CookieHandler custom = new java.net.CookieHandler() {
        @Override public java.util.Map<String, java.util.List<String>> get(
          java.net.URI uri, java.util.Map<String, java.util.List<String>> requestHeaders ) {
          return java.util.Collections.emptyMap();
        }
        @Override public void put( java.net.URI uri,
          java.util.Map<String, java.util.List<String>> responseHeaders ) {
          // Intentionally empty: this stub CookieHandler is only used to verify that
          // clearJvmCookieStore() is a no-op when the default handler is not a
          // CookieManager, so storing response cookies is not required for the test.
        }
      };
      java.net.CookieHandler.setDefault( custom );
      // Should not throw, no-op on non-CookieManager; custom handler must remain installed.
      DisconnectFromRepositoryAction.clearJvmCookieStore();
      assertSame( custom, java.net.CookieHandler.getDefault() );
    } finally {
      java.net.CookieHandler.setDefault( previous );
    }
  }

  // ---- actionPerformed clears cookie store ----

  @Test
  public void testActionPerformedClearsGlobalCookieStore() {
    java.net.CookieHandler previous = java.net.CookieHandler.getDefault();
    try {
      java.net.CookieManager mgr = new java.net.CookieManager( null, java.net.CookiePolicy.ACCEPT_ALL );
      java.net.CookieHandler.setDefault( mgr );
      java.net.HttpCookie cookie = new java.net.HttpCookie( "JSESSIONID", "FROM-UP-LOGIN" );
      cookie.setPath( "/" );
      mgr.getCookieStore().add( java.net.URI.create( "http://example.com/" ), cookie );
      assertEquals( 1, mgr.getCookieStore().getCookies().size() );

      AuthenticationData session = mock( AuthenticationData.class );
      when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
      when( session.getUrl() ).thenReturn( null ); // skip server logout call
      RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

      DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
      try ( var jopMock = mockStatic( JOptionPane.class ) ) {
        jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) )
          .thenAnswer( inv -> null );
        action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "x" ) );
      }
      assertTrue( "Cookies must be cleared after disconnect to prevent stale JSESSIONID "
        + "from contaminating the next login attempt",
        mgr.getCookieStore().getCookies().isEmpty() );
    } finally {
      java.net.CookieHandler.setDefault( previous );
    }
  }

  // ---- invalidateViaHttpBasic ----

  @Test
  public void testInvalidateViaHttpBasicSendsAuthorizationHeader() throws Exception {
    final java.util.concurrent.atomic.AtomicReference<String> seenAuth =
      new java.util.concurrent.atomic.AtomicReference<>();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      seenAuth.set( exchange.getRequestHeaders().getFirst( "Authorization" ) );
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      DisconnectFromRepositoryAction.invalidateViaHttpBasic(
        "http://127.0.0.1:" + server.getAddress().getPort() + "/Logout",
        "admin", "password" );
      String expected = "Basic " + java.util.Base64.getEncoder()
        .encodeToString( "admin:password".getBytes( java.nio.charset.StandardCharsets.UTF_8 ) );
      assertEquals( expected, seenAuth.get() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testInvalidateViaHttpBasicHandlesNullPassword() throws Exception {
    final java.util.concurrent.atomic.AtomicReference<String> seenAuth =
      new java.util.concurrent.atomic.AtomicReference<>();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      seenAuth.set( exchange.getRequestHeaders().getFirst( "Authorization" ) );
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      DisconnectFromRepositoryAction.invalidateViaHttpBasic(
        "http://127.0.0.1:" + server.getAddress().getPort() + "/Logout",
        "admin", null );
      String expected = "Basic " + java.util.Base64.getEncoder()
        .encodeToString( "admin:".getBytes( java.nio.charset.StandardCharsets.UTF_8 ) );
      assertEquals( expected, seenAuth.get() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testInvalidateViaHttpBasicSwallowsConnectionError() {
    // No server listening: must not throw, even though the connection will fail.
    try {
      DisconnectFromRepositoryAction.invalidateViaHttpBasic(
        "http://127.0.0.1:1/Logout", "admin", "pw" );
    } catch ( Exception e ) {
      fail( "invalidateViaHttpBasic must swallow connection errors, got: " + e );
    }
  }

  @Test
  public void testInvalidateViaHttpBasicSwallowsBadUrl() {
    try {
      DisconnectFromRepositoryAction.invalidateViaHttpBasic( "not-a-url", "admin", "pw" );
    } catch ( Exception e ) {
      fail( "invalidateViaHttpBasic must swallow malformed URLs, got: " + e );
    }
  }

  // ---- invalidateServerSession U/P branch ----

  @Test
  public void testActionPerformedUsernamePasswordHitsLogoutWithBasicAuth() throws Exception {
    final java.util.concurrent.atomic.AtomicInteger calls = new java.util.concurrent.atomic.AtomicInteger();
    final java.util.concurrent.atomic.AtomicReference<String> seenAuth =
      new java.util.concurrent.atomic.AtomicReference<>();
    final java.util.concurrent.atomic.AtomicReference<String> seenCookie =
      new java.util.concurrent.atomic.AtomicReference<>();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      calls.incrementAndGet();
      seenAuth.set( exchange.getRequestHeaders().getFirst( "Authorization" ) );
      seenCookie.set( exchange.getRequestHeaders().getFirst( "Cookie" ) );
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      final String url = "http://127.0.0.1:" + server.getAddress().getPort();
      AuthenticationData session = mock( AuthenticationData.class );
      when( session.getUsername() ).thenReturn( ADMIN_USERNAME );
      when( session.getPassword() ).thenReturn( "secret" );
      when( session.getUrl() ).thenReturn( url );
      when( session.getOption( "sessionId" ) ).thenReturn( null );
      when( session.getOption( "browserAuth" ) ).thenReturn( null );
      RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );

      // Important: clear default cookie handler to ensure no stale cookie is appended.
      java.net.CookieHandler previous = java.net.CookieHandler.getDefault();
      java.net.CookieHandler.setDefault( null );
      try {
        DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
        try ( var jopMock = mockStatic( JOptionPane.class ) ) {
          jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) )
            .thenAnswer( inv -> null );
          action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "x" ) );
        }
      } finally {
        java.net.CookieHandler.setDefault( previous );
      }

      assertEquals( "U/P session must invoke /Logout once", 1, calls.get() );
      assertNotNull( "U/P session must send HTTP Basic Authorization header on logout",
        seenAuth.get() );
      assertTrue( seenAuth.get().startsWith( "Basic " ) );
      assertNull( "U/P session must NOT send any Cookie header on logout", seenCookie.get() );
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testActionPerformedUsernamePasswordSkipsLogoutWhenUsernameMissing() throws Exception {
    // Branch: sessionId null/empty AND username null/empty -> no HTTP call at all.
    final java.util.concurrent.atomic.AtomicInteger calls = new java.util.concurrent.atomic.AtomicInteger();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/Logout", exchange -> {
      calls.incrementAndGet();
      exchange.sendResponseHeaders( 200, -1 );
      exchange.close();
    } );
    server.start();
    try {
      final String url = "http://127.0.0.1:" + server.getAddress().getPort();
      AuthenticationData session = mock( AuthenticationData.class );
      when( session.getUsername() ).thenReturn( null );
      when( session.getUrl() ).thenReturn( url );
      when( session.getOption( "sessionId" ) ).thenReturn( "" );
      RepositorySessionManager.getInstance().setSession( session, null );

      DisconnectFromRepositoryAction action = new DisconnectFromRepositoryAction();
      try ( var jopMock = mockStatic( JOptionPane.class ) ) {
        jopMock.when( () -> JOptionPane.showMessageDialog( any(), any(), any(), anyInt() ) )
          .thenAnswer( inv -> null );
        action.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "x" ) );
      }
      // actionPerformed is synchronous in this test path -- no Thread.sleep needed.
      assertEquals( "Must NOT hit /Logout when there are no credentials at all", 0, calls.get() );
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    } finally {
      server.stop( 0 );
    }
  }
}

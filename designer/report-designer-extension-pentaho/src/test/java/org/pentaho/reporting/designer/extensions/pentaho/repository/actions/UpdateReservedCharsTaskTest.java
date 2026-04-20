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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

@RunWith( Parameterized.class )
public class UpdateReservedCharsTaskTest {

  private final String sessionId;
  private final String browserAuth;
  private AuthenticationData loginData;

  public UpdateReservedCharsTaskTest( String sessionId, String browserAuth ) {
    this.sessionId = sessionId;
    this.browserAuth = browserAuth;
  }

  @Parameters( name = "sessionId={0}, browserAuth={1}" )
  public static Collection<Object[]> data() {
    return Arrays.asList( new Object[][] {
      { "SESS_ABC123", "true" },
      { null, null },
      { "SESS_ABC123", "false" },
      { "", "true" },
      { null, "true" }
    } );
  }

  @Before
  public void setUp() {
    loginData = mock( AuthenticationData.class );
    when( loginData.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );
    when( loginData.getUsername() ).thenReturn( "admin" );
    when( loginData.getPassword() ).thenReturn( "password" );
  }

  @Test
  public void testConstructor() {
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
    assertNotNull( task );
  }

  @Test
  public void testConstructorWithNullLoginData() {
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( null );
    assertNotNull( task );
  }

  @Test
  public void testSetLoginData() {
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
    AuthenticationData newData = mock( AuthenticationData.class );
    task.setLoginData( newData, true );
    assertNotNull( task );
    task.setLoginData( newData, false );
    assertNotNull( task );
  }

  @Test
  public void testRunWithVariousAuthOptions() {
    when( loginData.getOption( "sessionId" ) ).thenReturn( sessionId );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( browserAuth );
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
    try {
      task.run();
    } catch ( RuntimeException e ) {
      // Expected - no server running
      assertNotNull( e );
    }
  }

  @Test
  public void testImplementsAuthenticatedServerTask() {
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
    assertTrue( task instanceof AuthenticatedServerTask );
  }

  @Test
  public void testImplementsRunnable() {
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
    assertTrue( task instanceof Runnable );
  }

  // ---- checkResult coverage ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCheckResultReturnsTrue() throws Exception {
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
    Method checkResult = UpdateReservedCharsTask.class.getDeclaredMethod( "checkResult", int.class );
    checkResult.setAccessible( true );
    assertTrue( (boolean) checkResult.invoke( task, HttpStatus.SC_OK ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCheckResultReturnsFalseFor401() throws Exception {
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
    Method checkResult = UpdateReservedCharsTask.class.getDeclaredMethod( "checkResult", int.class );
    checkResult.setAccessible( true );
    assertFalse( (boolean) checkResult.invoke( task, HttpStatus.SC_UNAUTHORIZED ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCheckResultReturnsFalseFor500() throws Exception {
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
    Method checkResult = UpdateReservedCharsTask.class.getDeclaredMethod( "checkResult", int.class );
    checkResult.setAccessible( true );
    assertFalse( (boolean) checkResult.invoke( task, HttpStatus.SC_INTERNAL_SERVER_ERROR ) );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCheckResultReturnsFalseFor403() throws Exception {
    UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
    Method checkResult = UpdateReservedCharsTask.class.getDeclaredMethod( "checkResult", int.class );
    checkResult.setAccessible( true );
    assertFalse( (boolean) checkResult.invoke( task, HttpStatus.SC_FORBIDDEN ) );
  }

  // ---- createHttpClient coverage ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testCreateHttpClientForAllAuthScenarios() throws Exception {
    final String[][] scenarios = {
      { "SESS_TEST", "true" },
      { null, null },
      { "", "true" }
    };
    for ( String[] scenario : scenarios ) {
      when( loginData.getOption( "sessionId" ) ).thenReturn( scenario[ 0 ] );
      when( loginData.getOption( "browserAuth" ) ).thenReturn( scenario[ 1 ] );
      UpdateReservedCharsTask task = new UpdateReservedCharsTask( loginData );
      Method createClient = UpdateReservedCharsTask.class.getDeclaredMethod( "createHttpClient" );
      createClient.setAccessible( true );
      HttpClient client = (HttpClient) createClient.invoke( task );
      assertNotNull( client );
    }
  }

  @Test
  public void testRunSuccessPathPopulatesPublishUtil() throws Exception {
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/api/repo/files/reservedCharacters", exchange -> respond( exchange, "/\\?#@%" ) );
    server.createContext( "/api/repo/files/reservedCharactersDisplay",
      exchange -> respond( exchange, "/,\\,?,#,@,%" ) );
    server.start();
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort();
      AuthenticationData ad = mock( AuthenticationData.class );
      when( ad.getUrl() ).thenReturn( url );
      when( ad.getUsername() ).thenReturn( "u" );
      when( ad.getPassword() ).thenReturn( "p" );
      when( ad.getOption( "sessionId" ) ).thenReturn( null );
      when( ad.getOption( "browserAuth" ) ).thenReturn( null );

      UpdateReservedCharsTask task = new UpdateReservedCharsTask( ad );
      task.run(); // should not throw on 200 OK
      // Verify the static reserved-chars-display state on PublishUtil was
      // populated from the server response.
      assertEquals( "/,\\,?,#,@,%",
        org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil
          .getReservedCharsDisplay() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test( expected = RuntimeException.class )
  public void testRunFailurePathThrowsRuntimeOn500() throws Exception {
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/api/repo/files/reservedCharacters",
      exchange -> respondError( exchange, 500 ) );
    server.start();
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort();
      AuthenticationData ad = mock( AuthenticationData.class );
      when( ad.getUrl() ).thenReturn( url );
      when( ad.getUsername() ).thenReturn( "u" );
      when( ad.getPassword() ).thenReturn( "p" );
      when( ad.getOption( "sessionId" ) ).thenReturn( null );
      when( ad.getOption( "browserAuth" ) ).thenReturn( null );
      new UpdateReservedCharsTask( ad ).run();
    } finally {
      server.stop( 0 );
    }
  }

  @Test( expected = RuntimeException.class )
  public void testRunFailurePathThrowsOnSecondRequest500() throws Exception {
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/api/repo/files/reservedCharacters", exchange -> respond( exchange, "/" ) );
    server.createContext( "/api/repo/files/reservedCharactersDisplay",
      exchange -> respondError( exchange, 500 ) );
    server.start();
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort();
      AuthenticationData ad = mock( AuthenticationData.class );
      when( ad.getUrl() ).thenReturn( url );
      when( ad.getUsername() ).thenReturn( "u" );
      when( ad.getPassword() ).thenReturn( "p" );
      when( ad.getOption( "sessionId" ) ).thenReturn( null );
      when( ad.getOption( "browserAuth" ) ).thenReturn( null );
      new UpdateReservedCharsTask( ad ).run();
    } finally {
      server.stop( 0 );
    }
  }

  private static void respond( com.sun.net.httpserver.HttpExchange ex, String body ) throws java.io.IOException {
    byte[] bytes = body.getBytes( java.nio.charset.StandardCharsets.UTF_8 );
    ex.sendResponseHeaders( 200, bytes.length );
    try ( java.io.OutputStream os = ex.getResponseBody() ) {
      os.write( bytes );
    }
  }

  private static void respondError( com.sun.net.httpserver.HttpExchange ex, int status ) throws java.io.IOException {
    ex.sendResponseHeaders( status, -1 );
    ex.close();
  }
}

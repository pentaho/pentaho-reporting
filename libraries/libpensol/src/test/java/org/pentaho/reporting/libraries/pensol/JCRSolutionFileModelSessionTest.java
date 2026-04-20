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

package org.pentaho.reporting.libraries.pensol;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for {@link JCRSolutionFileModel} session-based constructor and validateSessionId.
 */
@RunWith( Parameterized.class )
public class JCRSolutionFileModelSessionTest {

  private static final String TEST_URL = "http://localhost:8080/pentaho";
  private static final String VALID_SESSION_ID = "ABC123DEF456";
  private static final int DEFAULT_TIMEOUT = 30000;

  private final int timeout;
  private final String sessionId;

  public JCRSolutionFileModelSessionTest( int timeout, String sessionId ) {
    this.timeout = timeout;
    this.sessionId = sessionId;
  }

  @Parameters( name = "timeout={0}, sessionId={1}" )
  public static Collection<Object[]> data() {
    return Arrays.asList( new Object[][] {
      { DEFAULT_TIMEOUT, VALID_SESSION_ID },
      { 0, VALID_SESSION_ID },
      { 120000, VALID_SESSION_ID },
      { DEFAULT_TIMEOUT, "abc-123_DEF.456" },
      { DEFAULT_TIMEOUT, "" }
    } );
  }

  @BeforeClass
  public static void initBoot() {
    LibPensolBoot.getInstance().start();
  }

  @Test
  public void testSessionConstructorCreatesModel() {
    JCRSolutionFileModel model = new JCRSolutionFileModel( TEST_URL, timeout, sessionId );
    assertNotNull( model );
    model.close();
  }

  @Test( expected = NullPointerException.class )
  public void testSessionConstructorNullUrl() {
    new JCRSolutionFileModel( null, DEFAULT_TIMEOUT, VALID_SESSION_ID );
  }

  @Test( expected = NullPointerException.class )
  public void testSessionConstructorNullSessionId() {
    new JCRSolutionFileModel( TEST_URL, DEFAULT_TIMEOUT, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSessionConstructorSessionIdWithCR() {
    new JCRSolutionFileModel( TEST_URL, DEFAULT_TIMEOUT, "bad\rsession" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSessionConstructorSessionIdWithLF() {
    new JCRSolutionFileModel( TEST_URL, DEFAULT_TIMEOUT, "bad\nsession" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSessionConstructorSessionIdWithSemicolon() {
    new JCRSolutionFileModel( TEST_URL, DEFAULT_TIMEOUT, "bad;session" );
  }

  @Test
  public void testCloseWithNullClient() {
    // Test the close path when client is null (package-local constructor allows this)
    JCRSolutionFileModel model = new JCRSolutionFileModel( TEST_URL, null, false );
    model.close(); // Should not throw
    assertNotNull( model );
  }

  @Test
  public void testCloseWithValidClient() {
    JCRSolutionFileModel model = new JCRSolutionFileModel( TEST_URL, DEFAULT_TIMEOUT, VALID_SESSION_ID );
    model.close(); // Should close the client without exception
    assertNotNull( model );
  }

  @Test
  public void testUsernamePasswordConstructorCreatesModel() {
    JCRSolutionFileModel model = new JCRSolutionFileModel( TEST_URL, "admin", "password", DEFAULT_TIMEOUT );
    assertNotNull( model );
    model.close();
  }

  @Test( expected = NullPointerException.class )
  public void testUsernamePasswordConstructorNullUrl() {
    new JCRSolutionFileModel( null, "admin", "password", DEFAULT_TIMEOUT );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValidateSessionIdCRAtStart() {
    new JCRSolutionFileModel( TEST_URL, DEFAULT_TIMEOUT, "\rstart" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValidateSessionIdLFAtEnd() {
    new JCRSolutionFileModel( TEST_URL, DEFAULT_TIMEOUT, "end\n" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValidateSessionIdSemicolonInMiddle() {
    new JCRSolutionFileModel( TEST_URL, DEFAULT_TIMEOUT, "before;after" );
  }

  @Test
  public void testSessionConstructorRegistersCookieFilter() throws Exception {
    java.util.concurrent.atomic.AtomicReference<String> seenCookie = new java.util.concurrent.atomic.AtomicReference<>();
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/", exchange -> {
      seenCookie.set( exchange.getRequestHeaders().getFirst( "Cookie" ) );
      byte[] body = "<r/>".getBytes( java.nio.charset.StandardCharsets.UTF_8 );
      exchange.sendResponseHeaders( 200, body.length );
      try ( java.io.OutputStream os = exchange.getResponseBody() ) {
        os.write( body );
      }
    } );
    server.start();
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/pentaho";
      try ( JCRSolutionFileModel model = new JCRSolutionFileModel( url, DEFAULT_TIMEOUT, "FILTER_TEST_SESSION" ) ) {
        java.lang.reflect.Field f = JCRSolutionFileModel.class.getDeclaredField( "client" );
        f.setAccessible( true );
        jakarta.ws.rs.client.Client c = (jakarta.ws.rs.client.Client) f.get( model );
        try ( jakarta.ws.rs.core.Response resp = c.target( url ).request().get() ) {
          resp.readEntity( String.class );
        }
      }
      assertNotNull( "Server did not see a Cookie header", seenCookie.get() );
      assertTrue( seenCookie.get().contains( "JSESSIONID=FILTER_TEST_SESSION" ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testDefaultVersionFieldsViaReflection() throws Exception {
    try ( JCRSolutionFileModel model = new JCRSolutionFileModel( TEST_URL, DEFAULT_TIMEOUT, "S" ) ) {
      for ( String fld : new String[] { "majorVersion", "minorVersion", "releaseVersion",
                                        "buildVersion", "milestoneVersion" } ) {
        java.lang.reflect.Field f = JCRSolutionFileModel.class.getDeclaredField( fld );
        f.setAccessible( true );
        assertEquals( "999", f.get( model ) );
      }
    }
  }

  @Test
  public void testPackageLocalConstructorAcceptsLoadTreePartiallyTrue() {
    JCRSolutionFileModel model = new JCRSolutionFileModel( TEST_URL, null, true );
    assertNotNull( model );
    model.close();
  }
}

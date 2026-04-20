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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith( Parameterized.class )
public class PublishRestUtilTest {

  /**
   * Use a dynamically allocated port that is guaranteed to be CLOSED at test time.
   * This avoids false positives on developer machines that happen to have a real
   * server running on a fixed port like 8080.
   */
  private static final String SERVER_URL = "http://127.0.0.1:" + findClosedPort() + "/pentaho";
  private static final String ADMIN_USERNAME = "admin";
  private static final String ADMIN_PASSWORD = "password";

  private static int findClosedPort() {
    try ( java.net.ServerSocket socket = new java.net.ServerSocket( 0 ) ) {
      // Bind to an ephemeral port, then close it. The OS will not immediately
      // reuse this port, so connections to it will fail (which is what we want
      // for the "unreachable server returns 504" tests).
      return socket.getLocalPort();
    } catch ( IOException e ) {
      // Fallback: a port unlikely to be in use locally.
      return 1;
    }
  }

  private final String serverUrl;
  private final String sessionId;

  public PublishRestUtilTest( String serverUrl, String sessionId ) {
    this.serverUrl = serverUrl;
    this.sessionId = sessionId;
  }

  @Parameters( name = "url={0}, sessionId={1}" )
  public static Collection<Object[]> data() {
    return Arrays.asList( new Object[][] {
      { SERVER_URL, null },
      { SERVER_URL + "/", null },
      { SERVER_URL, "SESS_ABC123" },
      { SERVER_URL + "/", "SESS_XYZ" },
      { "http://127.0.0.1:" + findClosedPort() + "/pentaho", null }
    } );
  }

  @Test
  public void testConstructor() {
    PublishRestUtil util;
    if ( sessionId != null ) {
      util = PublishRestUtil.withSessionAuth( serverUrl, sessionId );
    } else {
      util = new PublishRestUtil( serverUrl, ADMIN_USERNAME, ADMIN_PASSWORD );
    }
    assertNotNull( util );
  }

  @Test( expected = IOException.class )
  public void testPublishFileNullPath() throws IOException {
    PublishRestUtil util;
    if ( sessionId != null ) {
      util = PublishRestUtil.withSessionAuth( serverUrl, sessionId );
    } else {
      util = new PublishRestUtil( serverUrl, ADMIN_USERNAME, ADMIN_PASSWORD );
    }
    util.publishFile( null, new byte[] { 1, 2, 3 }, null );
  }

  @Test( expected = IOException.class )
  public void testPublishFileNullData() throws IOException {
    PublishRestUtil util;
    if ( sessionId != null ) {
      util = PublishRestUtil.withSessionAuth( serverUrl, sessionId );
    } else {
      util = new PublishRestUtil( serverUrl, ADMIN_USERNAME, ADMIN_PASSWORD );
    }
    util.publishFile( "/public/report.prpt", null, null );
  }

  @Test( expected = IOException.class )
  public void testPublishFileEmptyData() throws IOException {
    PublishRestUtil util;
    if ( sessionId != null ) {
      util = PublishRestUtil.withSessionAuth( serverUrl, sessionId );
    } else {
      util = new PublishRestUtil( serverUrl, ADMIN_USERNAME, ADMIN_PASSWORD );
    }
    util.publishFile( "/public/report.prpt", new byte[0], null );
  }

  @Test
  public void testReportTitleKey() {
    assertEquals( "reportTitle", PublishRestUtil.REPORT_TITLE_KEY );
  }

  @Test
  public void testOverwriteFileKey() {
    assertEquals( "overwriteFile", PublishRestUtil.OVERWRITE_FILE_KEY );
  }

  @Test
  public void testImportPathKey() {
    assertEquals( "importPath", PublishRestUtil.IMPORT_PATH_KEY );
  }

  @Test
  public void testRepoFilesImportPath() {
    assertEquals( "api/repo/publish/file", PublishRestUtil.REPO_FILES_IMPORT );
  }

  @Test
  public void testPublishFileWithUnreachableServerReturns504() throws IOException {
    PublishRestUtil util = newUtil();
    int code = util.publishFile( "/public/report.prpt", new byte[] { 1, 2, 3 }, new java.util.Properties() );
    // No server listening; the network exception is logged and swallowed; default 504.
    assertEquals( 504, code );
  }

  @Test
  public void testPublishFileWithoutSlashTreatsAsName() throws IOException {
    PublishRestUtil util = newUtil();
    // No slash → path stays as-is and fileName is null. The downstream URL encoding
    // will throw, which is swallowed and 504 is returned.
    int code = util.publishFile( "report.prpt", new byte[] { 1 }, null );
    assertEquals( 504, code );
  }

  @Test
  public void testPublishFileSuccess200() throws Exception {
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/pentaho/api/repo/publish/fileWithOptions", exchange -> {
      try ( java.io.InputStream is = exchange.getRequestBody() ) {
        while ( is.read() != -1 ) { /* drain */ }
      }
      byte[] body = "ok".getBytes( java.nio.charset.StandardCharsets.UTF_8 );
      exchange.sendResponseHeaders( 200, body.length );
      try ( java.io.OutputStream os = exchange.getResponseBody() ) {
        os.write( body );
      }
    } );
    server.start();
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/pentaho";
      PublishRestUtil util = sessionId != null
        ? PublishRestUtil.withSessionAuth( url, sessionId )
        : new PublishRestUtil( url, ADMIN_USERNAME, ADMIN_PASSWORD );
      int code = util.publishFile( "/public/report.prpt",
        new byte[] { 1, 2, 3 }, new java.util.Properties() );
      assertEquals( 200, code );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testPublishFileServerReturns500Returns500() throws Exception {
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/pentaho/api/repo/publish/fileWithOptions", exchange -> {
      try ( java.io.InputStream is = exchange.getRequestBody() ) {
        while ( is.read() != -1 ) { /* drain */ }
      }
      exchange.sendResponseHeaders( 500, -1 );
      exchange.close();
    } );
    server.start();
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/pentaho";
      PublishRestUtil util = sessionId != null
        ? PublishRestUtil.withSessionAuth( url, sessionId )
        : new PublishRestUtil( url, ADMIN_USERNAME, ADMIN_PASSWORD );
      int code = util.publishFile( "/public/report.prpt",
        new byte[] { 1 }, new java.util.Properties() );
      assertEquals( 500, code );
    } finally {
      server.stop( 0 );
    }
  }

  // ---- deprecated overloads ----

  @Test
  @SuppressWarnings( "deprecation" )
  public void testDeprecatedPublishFileBytesOverloadDelegates() throws IOException {
    PublishRestUtil util = newUtil();
    int code = util.publishFile( "/p/r.prpt", new byte[] { 1 }, true );
    assertEquals( 504, code );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testDeprecatedPublishFileStreamOverloadDelegates() throws IOException {
    PublishRestUtil util = newUtil();
    int code = util.publishFile( "/p", "r.prpt",
      new java.io.ByteArrayInputStream( new byte[] { 1 } ), true );
    assertEquals( 504, code );
  }

  @Test
  public void testPublishFileStreamEntryPointWithNullProperties() throws IOException {
    PublishRestUtil util = newUtil();
    int code = util.publishFile( "/p", "r.prpt",
      new java.io.ByteArrayInputStream( new byte[] { 1 } ), (java.util.Properties) null );
    assertEquals( 504, code );
  }

  private PublishRestUtil newUtil() {
    return sessionId != null
      ? PublishRestUtil.withSessionAuth( serverUrl, sessionId )
      : new PublishRestUtil( serverUrl, ADMIN_USERNAME, ADMIN_PASSWORD );
  }
}

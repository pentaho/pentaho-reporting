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

package org.pentaho.reporting.libraries.pensol.vfs;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.util.HttpClientManager;
import org.pentaho.reporting.libraries.pensol.LibPensolBoot;

@SuppressWarnings( "java:S1874" )
public class LocalFileModelTest {

  private static final String TEST_URL = "http://localhost:8080/pentaho";

  @BeforeClass
  public static void initBoot() {
    LibPensolBoot.getInstance().start();
  }

  @Test
  public void testConstructorWithClientBuilder() {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, "admin", "password", "localhost", 8080 );
    assertNotNull( model );
  }

  @Test
  public void testConstructorWithClientBuilderNullHost() {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, "admin", "password", null, 8080 );
    assertNotNull( model );
  }

  @Test
  public void testConstructorWithClientBuilderEmptyHost() {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, "admin", "password", "", 8080 );
    assertNotNull( model );
  }

  @Test
  public void testConstructorWithClientBuilderNullCredentials() {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, null, null, null, 0 );
    assertNotNull( model );
  }

  @Test( expected = NullPointerException.class )
  public void testConstructorWithClientBuilderNullUrl() {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    new LocalFileModel( null, builder, "admin", "password", "localhost", 8080 );
  }

  @Test
  public void testConstructorWithSessionCookie() {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    builder.setSessionCookie( "JSESSIONID", "SESSION_123" );
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, null, null, null, 0 );
    assertNotNull( model );
  }

  @Test
  public void testConstructorSetsUpPreemptiveAuth() {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    builder.setCredentials( "admin", "password" );
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, "admin", "password", "proxy.example.com", 3128 );
    assertNotNull( model );
  }

  // ---- deprecated HttpClient-based constructors ----
  // The deprecated 4-arg and 6-arg constructors call client.getParams().setParameter(...),
  // which the modern InternalHttpClient (returned by HttpClientManager) rejects with
  // UnsupportedOperationException. These constructors are themselves @Deprecated and
  // are not exercised in the live application; verifying their NPE-on-null-URL contract
  // is sufficient.

  @Test
  public void testDeprecatedConstructorFourArgNullUrl() {
    org.apache.http.client.HttpClient client = HttpClientManager.getInstance().createDefaultClient();
    try {
      @SuppressWarnings( "deprecation" )
      LocalFileModel m = new LocalFileModel( null, client, "u", "p" );
      fail( "expected NullPointerException, got instance: " + m );
    } catch ( NullPointerException expected ) {
      // expected
    }
  }

  @Test
  public void testDeprecatedConstructorSixArgNullUrl() {
    org.apache.http.client.HttpClient client = HttpClientManager.getInstance().createDefaultClient();
    try {
      @SuppressWarnings( "deprecation" )
      LocalFileModel m = new LocalFileModel( null, client, "u", "p", "h", 1 );
      fail( "expected NullPointerException, got instance: " + m );
    } catch ( NullPointerException expected ) {
      // expected
    }
  }

  // ---- refresh() error-status branches against a local HttpServer ----

  @Test( expected = IOException.class )
  public void testRefreshThrowsOn401() throws Exception {
    runRefreshAgainstStatus( 401 );
  }

  @Test( expected = IOException.class )
  public void testRefreshThrowsOn404() throws Exception {
    runRefreshAgainstStatus( 404 );
  }

  @Test( expected = IOException.class )
  public void testRefreshThrowsOn500() throws Exception {
    runRefreshAgainstStatus( 500 );
  }

  @Test( expected = IOException.class )
  public void testRefreshThrowsOnInvalidUrl() throws Exception {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( "ht tp://bad url", builder, "u", "p", "h", 1 );
    model.refresh();
  }

  // ---- getDataInternally() error-status branches via getData() ----

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testGetDataInternallyThrowsOnInvalidUrlInFileInfo() throws Exception {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( "http://127.0.0.1:1/", builder, "u", "p", null, 0 );
    // Reach getDataInternally via reflection with a FileInfo whose URL is malformed.
    org.pentaho.reporting.libraries.pensol.vfs.FileInfo fi =
      mock( org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
    when( fi.getUrl() ).thenReturn( "ht tp://bad url" );
    java.lang.reflect.Method m = LocalFileModel.class.getDeclaredMethod( "getDataInternally",
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
    m.setAccessible( true );
    try {
      m.invoke( model, fi );
    } catch ( java.lang.reflect.InvocationTargetException e ) {
      throw (Exception) e.getCause();
    }
  }

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testGetDataInternallyThrowsOn401() throws Exception {
    runGetDataAgainstStatus( 401 );
  }

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testGetDataInternallyThrowsOn404() throws Exception {
    runGetDataAgainstStatus( 404 );
  }

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testGetDataInternallyThrowsOn500() throws Exception {
    runGetDataAgainstStatus( 500 );
  }

  @Test
  public void testGetDataInternallyReturnsBytesOn200() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200, "DATA" );
    try {
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, null, null, null, 0 );
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo fi =
        mock( org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
      when( fi.getUrl() ).thenReturn( baseUrl + "/file" );
      java.lang.reflect.Method m = LocalFileModel.class.getDeclaredMethod( "getDataInternally",
        org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
      m.setAccessible( true );
      byte[] data = (byte[]) m.invoke( model, fi );
      assertNotNull( data );
      assertEquals( "DATA", new String( data, java.nio.charset.StandardCharsets.UTF_8 ) );
    } finally {
      server.stop( 0 );
    }
  }

  // ---- additional refresh / getDataInternally branch coverage ----

  @Test
  public void testRefreshSuccessWithMinimalXml() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200,
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><repository/>" );
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, "u", "p", null, 0 );
      model.refresh();
      assertNotNull( model.getRoot() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testRefreshNullUsernameAndPassword() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200,
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><repository/>" );
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, null, null, null, 0 );
      model.refresh();
      assertNotNull( model.getRoot() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testRefreshNullPasswordOnly() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200,
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?><repository/>" );
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, "u", null, null, 0 );
      model.refresh();
      assertNotNull( model.getRoot() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testGetDataInternallyNullUsernameAndPassword() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200, "OK" );
    try {
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, null, null, null, 0 );
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo fi =
        mock( org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
      when( fi.getUrl() ).thenReturn( baseUrl + "/x" );
      java.lang.reflect.Method m = LocalFileModel.class.getDeclaredMethod( "getDataInternally",
        org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
      m.setAccessible( true );
      byte[] data = (byte[]) m.invoke( model, fi );
      assertEquals( "OK", new String( data, java.nio.charset.StandardCharsets.UTF_8 ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testGetDataInternallyServerUnreachable() throws Exception {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( "http://127.0.0.1:1/", builder, "u", "p", null, 0 );
    org.pentaho.reporting.libraries.pensol.vfs.FileInfo fi =
      mock( org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
    when( fi.getUrl() ).thenReturn( "http://127.0.0.1:1/file" );
    java.lang.reflect.Method m = LocalFileModel.class.getDeclaredMethod( "getDataInternally",
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
    m.setAccessible( true );
    try {
      m.invoke( model, fi );
    } catch ( java.lang.reflect.InvocationTargetException e ) {
      throw (Exception) e.getCause();
    }
  }

  // ---- trivial overrides ----

  @Test
  public void testGetContentSizeAlwaysZero() throws Exception {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, null, null, null, 0 );
    assertEquals( 0L, model.getContentSize( mock( org.apache.commons.vfs2.FileName.class ) ) );
  }

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testSetDataInternallyAlwaysThrows() throws Exception {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, null, null, null, 0 );
    java.lang.reflect.Method m = LocalFileModel.class.getDeclaredMethod( "setDataInternally",
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class, byte[].class );
    m.setAccessible( true );
    try {
      m.invoke( model, mock( org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class ), new byte[ 0 ] );
    } catch ( java.lang.reflect.InvocationTargetException e ) {
      throw (Exception) e.getCause();
    }
  }

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testDeleteAlwaysThrows() throws Exception {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, null, null, null, 0 );
    model.delete( mock( org.apache.commons.vfs2.FileName.class ) );
  }

  // ---- createFolder branch coverage ----

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testCreateFolderRootOnlyPathThrows() throws Exception {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, null, null, null, 0 );
    org.apache.commons.vfs2.FileName fn = mock( org.apache.commons.vfs2.FileName.class );
    when( fn.getBaseName() ).thenReturn( "onlyone" );
    when( fn.getParent() ).thenReturn( null );
    model.createFolder( fn ); // computeFileNames -> ["onlyone"], length 1 -> throws
  }

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testCreateFolderMissingParentThrows() throws Exception {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( TEST_URL, builder, null, null, null, 0 );
    setEmptyRoot( model );

    org.apache.commons.vfs2.FileName rootFn = mockFileName( "", null );
    org.apache.commons.vfs2.FileName parent = mockFileName( "doesNotExist", rootFn );
    org.apache.commons.vfs2.FileName fn = mockFileName( "child", parent );
    model.createFolder( fn );
  }

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testCreateFolderServerError() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 500, "" );
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, "u", "p", null, 0 );
      setSingleChildRoot( model, "parent" );
      org.apache.commons.vfs2.FileName rootFn = mockFileName( "", null );
      org.apache.commons.vfs2.FileName parent = mockFileName( "parent", rootFn );
      org.apache.commons.vfs2.FileName fn = mockFileName( "child", parent );
      model.createFolder( fn );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testCreateFolderSuccess() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200, "" );
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, "u", "p", null, 0 );
      setSingleChildRoot( model, "parent" );
      org.apache.commons.vfs2.FileName rootFn = mockFileName( "", null );
      org.apache.commons.vfs2.FileName parent = mockFileName( "parent", rootFn );
      org.apache.commons.vfs2.FileName fn = mockFileName( "child", parent );
      model.createFolder( fn );
      assertNotNull( model.getRoot() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void testCreateFolderSuccessWithDescription() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200, "" );
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, null, null, null, 0 );
      setSingleChildRoot( model, "parent" );
      org.apache.commons.vfs2.FileName rootFn = mockFileName( "", null );
      org.apache.commons.vfs2.FileName parent = mockFileName( "parent", rootFn );
      org.apache.commons.vfs2.FileName fn = mockFileName( "child", parent );
      model.getDescriptionEntries().put( fn, "my description" );
      model.createFolder( fn );
      assertEquals( "my description", model.getDescriptionEntries().get( fn ) );
    } finally {
      server.stop( 0 );
    }
  }

  @Test( expected = org.apache.commons.vfs2.FileSystemException.class )
  public void testCreateFolderIOExceptionWrapped() throws Exception {
    HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
    LocalFileModel model = new LocalFileModel( "http://127.0.0.1:1/", builder, null, null, null, 0 );
    setSingleChildRoot( model, "parent" );
    org.apache.commons.vfs2.FileName rootFn = mockFileName( "", null );
    org.apache.commons.vfs2.FileName parent = mockFileName( "parent", rootFn );
    org.apache.commons.vfs2.FileName fn = mockFileName( "child", parent );
    model.createFolder( fn );
  }

  @Test
  public void testCreateFolderNestedPathExercisesBuildPath() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200, "" );
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, null, null, null, 0 );
      // Build root -> A -> B; then request createFolder for new child C under B.
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo root =
        new org.pentaho.reporting.libraries.pensol.vfs.FileInfo();
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo a =
        new org.pentaho.reporting.libraries.pensol.vfs.FileInfo( root, "A", "" );
      new org.pentaho.reporting.libraries.pensol.vfs.FileInfo( a, "B", "" );
      java.lang.reflect.Method setRoot = LocalFileModel.class.getMethod( "setRoot",
        org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
      setRoot.invoke( model, root );

      org.apache.commons.vfs2.FileName rootFn = mockFileName( "", null );
      org.apache.commons.vfs2.FileName aFn = mockFileName( "A", rootFn );
      org.apache.commons.vfs2.FileName bFn = mockFileName( "B", aFn );
      org.apache.commons.vfs2.FileName cFn = mockFileName( "C", bFn );
      // computeFileNames -> ["","A","B","C"]; parentPath -> ["","A","B"]; buildPath(1,3) -> "A/B"
      model.createFolder( cFn );
      assertNotNull( model.getRoot() );
    } finally {
      server.stop( 0 );
    }
  }

  // ---- deprecated HttpClient constructors: cover their bodies via mocked HttpClient ----

  @Test
  public void testDeprecatedFourArgConstructorBodyExecutesWithMockedClient() {
    org.apache.http.client.HttpClient mockClient = mock( org.apache.http.client.HttpClient.class );
    org.apache.http.params.HttpParams params = mock( org.apache.http.params.HttpParams.class );
    when( mockClient.getParams() ).thenReturn( params );
    when( params.setParameter( anyString(), any() ) ).thenReturn( params );
    @SuppressWarnings( "deprecation" )
    LocalFileModel m = new LocalFileModel( TEST_URL, mockClient, "u", "p" );
    assertNotNull( m );
    verify( params ).setParameter( "http.protocol.cookie-policy", "compatibility" );
  }

  /**
   * Exercises the deprecated 6-arg constructor across the three host-value
   * branches (non-empty hostname enables the {@code AuthCache}, empty and
   * {@code null} both skip it). A single parameterized data table keeps the
   * three branches visible while satisfying SonarLint S5976.
   */
  @Test
  public void testDeprecatedSixArgConstructorBodyExecutesAcrossHostBranches() {
    final String[] hosts = { "host.example.com", "", null };
    for ( String host : hosts ) {
      org.apache.http.client.HttpClient mockClient = mock( org.apache.http.client.HttpClient.class );
      org.apache.http.params.HttpParams params = mock( org.apache.http.params.HttpParams.class );
      when( mockClient.getParams() ).thenReturn( params );
      when( params.setParameter( anyString(), any() ) ).thenReturn( params );
      @SuppressWarnings( "deprecation" )
      LocalFileModel m = new LocalFileModel( TEST_URL, mockClient, "u", "p", host, host == null ? 0 : 8080 );
      assertNotNull( "constructor returned null for host=" + host, m );
    }
  }

  // ---- helpers ----

  private static org.apache.commons.vfs2.FileName mockFileName( String baseName,
                                                                org.apache.commons.vfs2.FileName parent ) {
    org.apache.commons.vfs2.FileName fn = mock( org.apache.commons.vfs2.FileName.class );
    when( fn.getBaseName() ).thenReturn( baseName );
    when( fn.getParent() ).thenReturn( parent );
    return fn;
  }

  /** Set an empty root FileInfo on the model via reflection (bypasses the protected setRoot). */
  private static void setEmptyRoot( LocalFileModel model ) throws Exception {
    org.pentaho.reporting.libraries.pensol.vfs.FileInfo root =
      new org.pentaho.reporting.libraries.pensol.vfs.FileInfo();
    java.lang.reflect.Method setRoot = LocalFileModel.class.getMethod( "setRoot",
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
    setRoot.invoke( model, root );
  }

  /** Set a root FileInfo containing a single child folder with the given name. */
  private static void setSingleChildRoot( LocalFileModel model, String childName ) throws Exception {
    org.pentaho.reporting.libraries.pensol.vfs.FileInfo root =
      new org.pentaho.reporting.libraries.pensol.vfs.FileInfo();
    new org.pentaho.reporting.libraries.pensol.vfs.FileInfo( root, childName, "" );
    java.lang.reflect.Method setRoot = LocalFileModel.class.getMethod( "setRoot",
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
    setRoot.invoke( model, root );
  }

  private static void runRefreshAgainstStatus( int status ) throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( status, "" );
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, "u", "p", null, 0 );
      model.refresh();
    } finally {
      server.stop( 0 );
    }
  }

  private static void runGetDataAgainstStatus( int status ) throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( status, "" );
    try {
      String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
      HttpClientManager.HttpClientBuilderFacade builder = HttpClientManager.getInstance().createBuilder();
      LocalFileModel model = new LocalFileModel( baseUrl, builder, "u", "p", null, 0 );
      org.pentaho.reporting.libraries.pensol.vfs.FileInfo fi =
        mock( org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
      when( fi.getUrl() ).thenReturn( baseUrl + "/file" );
      java.lang.reflect.Method m = LocalFileModel.class.getDeclaredMethod( "getDataInternally",
        org.pentaho.reporting.libraries.pensol.vfs.FileInfo.class );
      m.setAccessible( true );
      try {
        m.invoke( model, fi );
      } catch ( java.lang.reflect.InvocationTargetException e ) {
        throw (Exception) e.getCause();
      }
    } finally {
      server.stop( 0 );
    }
  }

  private static com.sun.net.httpserver.HttpServer startServer( int status, String body ) throws java.io.IOException {
    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
      new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/", exchange -> {
      try ( java.io.InputStream is = exchange.getRequestBody() ) {
        while ( is.read() != -1 ) { /* drain */ }
      }
      byte[] bytes = body.getBytes( java.nio.charset.StandardCharsets.UTF_8 );
      if ( bytes.length == 0 ) {
        exchange.sendResponseHeaders( status, -1 );
      } else {
        exchange.sendResponseHeaders( status, bytes.length );
        try ( java.io.OutputStream os = exchange.getResponseBody() ) {
          os.write( bytes );
        }
      }
      exchange.close();
    } );
    server.start();
    return server;
  }
}

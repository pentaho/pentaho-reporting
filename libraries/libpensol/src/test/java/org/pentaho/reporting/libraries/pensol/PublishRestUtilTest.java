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


package org.pentaho.reporting.libraries.pensol;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class PublishRestUtilTest {

  @Test
  public void testPublishFileByteArrayNullPathThrowsIoException() {
    final PublishRestUtil util = new PublishRestUtil( "http://localhost:8080/pentaho", "u", "p" );
    try {
      util.publishFile( null, new byte[] { 1 }, new Properties() );
      fail( "Expected IOException" );
    } catch ( IOException expected ) {
      assertTrue( expected.getMessage().contains( "missing file path" ) );
    }
  }

  @Test
  public void testPublishFileByteArrayEmptyDataThrowsIoException() {
    final PublishRestUtil util =
      new PublishRestUtil( "http://localhost:8080/pentaho", "u", "p" );

    try {
      util.publishFile( "/public/test.prpt", new byte[ 0 ], new Properties() );
      fail( "Expected IOException" );
    } catch ( IOException expected ) {
      assertEquals(
        "missing file path and/or data",
        expected.getMessage() );
    }
  }

  @Test
  public void testPublishFileInputStreamSuccessReturnsHttpStatus() throws Exception {
    final PublishRestUtil util = new PublishRestUtil( "http://localhost:8080/pentaho", "u", "p", "sess" );
    setClient( util, mockClientReturningStatus( 200, "ok" ) );

    final int status = util.publishFile( "/public", "test.prpt",
      new ByteArrayInputStream( new byte[] { 1, 2, 3 } ), new Properties() );

    assertEquals( 200, status );
  }

  @Test
  public void testPublishFileInputStreamWhenResponseIsNullReturns504() throws Exception {
    final PublishRestUtil util = new PublishRestUtil( "http://localhost:8080/pentaho", "u", "p" );
    setClient( util, mockClientReturningNullResponse() );

    final int status = util.publishFile( "/public", "test.prpt",
      new ByteArrayInputStream( new byte[] { 1 } ), new Properties() );

    assertEquals( 504, status );
  }

  @Test
  public void testConstructorWithTrailingSlash() {

    PublishRestUtil util =
      new PublishRestUtil(
        "http://localhost:8080/pentaho/",
        "u",
        "p" );

    assertNotNull( util );
  }

  @Test
  public void testPublishFileByteArrayNullDataThrowsIOException() {
    PublishRestUtil util =
      new PublishRestUtil(
        "http://localhost:8080/pentaho",
        "u",
        "p" );

    try {
      util.publishFile(
        "/public/test.prpt",
        null,
        new Properties() );

      fail( "Expected IOException" );
    } catch ( IOException expected ) {
      assertTrue(
        expected.getMessage().contains(
          "missing file path" ) );
    }
  }

  @Test
  public void testPublishFileWithoutSlashInName() throws Exception {

    PublishRestUtil util =
      new PublishRestUtil(
        "http://localhost:8080/pentaho",
        "u",
        "p",
        "sess" );

    int status =
      util.publishFile(
        "test.prpt",
        new byte[] { 1 },
        new Properties() );

    assertEquals( 504, status );
  }

  @Test
  public void testPublishFileInputStreamWithNullProperties() throws Exception {

    PublishRestUtil util =
      new PublishRestUtil(
        "http://localhost:8080/pentaho",
        "u",
        "p",
        "sess" );

    setClient(
      util,
      mockClientReturningStatus(
        200,
        "ok" ) );

    int status =
      util.publishFile(
        "/public",
        "test.prpt",
        new ByteArrayInputStream(
          new byte[] { 1, 2, 3 } ),
        null );

    assertEquals( 200, status );
  }

  @Test
  public void testPublishFileByteArrayExceptionBranch() throws Exception {

    PublishRestUtil util =
      org.mockito.Mockito.spy(
        new PublishRestUtil(
          "http://localhost:8080/pentaho",
          "u",
          "p" ) );

    org.mockito.Mockito.doThrow(
        new RuntimeException( "boom" ) )
      .when( util )
      .publishFile(
        org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.any( InputStream.class ),
        org.mockito.ArgumentMatchers.any( Properties.class ) );

    try {

      util.publishFile(
        "/public/test.prpt",
        new byte[] { 1 },
        new Properties() );

      org.junit.Assert.fail();

    } catch ( IOException ex ) {

      assertTrue(
        ex.getCause()
          .getMessage()
          .contains( "boom" ) );
    }
  }

  @Test
  public void testConstructorWithEmptySessionId() {

    PublishRestUtil util =
      new PublishRestUtil(
        "http://localhost:8080/pentaho",
        "u",
        "p",
        "" );

    assertNotNull( util );
  }

  @Test
  public void testSessionCookieBranchExecuted() throws Exception {

    PublishRestUtil util =
      new PublishRestUtil(
        "http://localhost:8080/pentaho",
        "u",
        "p",
        "SESSION123" );

    setClient(
      util,
      mockClientReturningStatus(
        200,
        "ok" ) );

    int status =
      util.publishFile(
        "/public",
        "test.prpt",
        new ByteArrayInputStream(
          new byte[] { 1 } ),
        new Properties() );

    assertEquals( 200, status );
  }

  @Test
  public void testSessionCookieFilterAddsCookieHeader() throws Exception {
    final PublishRestUtil util =
      new PublishRestUtil( "http://localhost:8080/pentaho", "u", "p", "SESSION123" );

    final Field field = PublishRestUtil.class.getDeclaredField( "client" );
    field.setAccessible( true );
    final Client client = (Client) field.get( util );

    ClientRequestFilter filter = null;
    for ( final Object instance : client.getConfiguration().getInstances() ) {
      if ( instance instanceof ClientRequestFilter ) {
        filter = (ClientRequestFilter) instance;
        break;
      }
    }
    assertNotNull( filter );

    final ClientRequestContext context = mock( ClientRequestContext.class );
    final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    org.mockito.Mockito.when( context.getHeaders() ).thenReturn( headers );

    filter.filter( context );
    assertEquals( "JSESSIONID=SESSION123", headers.getFirst( "Cookie" ) );
  }


  @Test
  public void testPublishFileInputStreamWhenPostThrowsReturns504() throws Exception {
    final PublishRestUtil util = new PublishRestUtil( "http://localhost:8080/pentaho", "u", "p" );
    setClient( util, mockClientThrowingOnPost() );

    final int status = util.publishFile( "/public", "test.prpt",
      new ByteArrayInputStream( new byte[] { 1 } ), new Properties() );

    assertEquals( 504, status );
  }

  @SuppressWarnings( "java:S1874" )
  @Test
  public void testDeprecatedWrapperMethodsStillWork() throws Exception {
    final PublishRestUtil util = new PublishRestUtil( "http://localhost:8080/pentaho", "u", "p" );
    setClient( util, mockClientReturningStatus( 200, "ok" ) );

    assertEquals( 200, util.publishFile( "/public/test.prpt", new byte[] { 9 }, true ) );
    assertEquals( 200, util.publishFile( "/public", "test.prpt",
      new ByteArrayInputStream( new byte[] { 9 } ), true ) );
  }

  private static void setClient( final PublishRestUtil util, final Client client ) throws Exception {
    final Field field = PublishRestUtil.class.getDeclaredField( "client" );
    field.setAccessible( true );
    field.set( util, client );
  }

  private static Client mockClientReturningStatus( final int code, final String entity ) {
    final Invocation.Builder builder = mock( Invocation.Builder.class );
    final Response response = mock( Response.class );
    doReturn( entity ).when( response ).readEntity( String.class );
    doReturn( code ).when( response ).getStatus();
    doReturn( response ).when( builder ).post( any() );

    final WebTarget target = mock( WebTarget.class );
    doReturn( builder ).when( target ).request();

    final Client client = mock( Client.class );
    doReturn( target ).when( client ).target( anyString() );
    return client;
  }

  private static Client mockClientReturningNullResponse() {
    final Invocation.Builder builder = mock( Invocation.Builder.class );
    doReturn( null ).when( builder ).post( any() );

    final WebTarget target = mock( WebTarget.class );
    doReturn( builder ).when( target ).request();

    final Client client = mock( Client.class );
    doReturn( target ).when( client ).target( anyString() );
    return client;
  }

  private static Client mockClientThrowingOnPost() {
    final Invocation.Builder builder = mock( Invocation.Builder.class );
    org.mockito.Mockito.doThrow( new RuntimeException( "post failed" ) ).when( builder ).post( any() );

    final WebTarget target = mock( WebTarget.class );
    doReturn( builder ).when( target ).request();

    final Client client = mock( Client.class );
    doReturn( target ).when( client ).target( anyString() );
    return client;
  }
}

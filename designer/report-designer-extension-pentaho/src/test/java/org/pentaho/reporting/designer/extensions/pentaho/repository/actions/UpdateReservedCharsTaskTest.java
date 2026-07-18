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


package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.util.HttpClientManager;
import org.pentaho.reporting.engine.classic.core.util.HttpClientUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateReservedCharsTaskTest {
  private static final String SESSION_ID_OPTION = "sessionId";
  private static final String COOKIE_HEADER = "Cookie";
  private static final String SESSION_COOKIE_PREFIX = "JSESSIONID=";

  private static AuthenticationData dataWithSession( final String sessionId ) {
    final AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( SESSION_ID_OPTION ) ).thenReturn( sessionId );
    when( data.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );
    return data;
  }

  @Test
  public void testConstructorAcceptsLoginData() {
    final AuthenticationData data = dataWithSession( null );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );
    assertNotNull( task );
  }

  @Test
  public void testSetLoginDataUpdatesLoginData() {
    final AuthenticationData original = dataWithSession( null );
    final AuthenticationData replacement = dataWithSession( "newsession" );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( original );
    task.setLoginData( replacement, true );
    assertNotNull( task );
  }

  @Test
  public void testIsSsoSessionWithSessionIdReturnsTrue() {
    final AuthenticationData data = dataWithSession( "abc123" );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );
    assertTrue( task.isSsoSession() );
  }

  @Test
  public void testIsSsoSessionWithEmptySessionIdReturnsFalse() {
    final AuthenticationData data = dataWithSession( "" );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );
    assertFalse( task.isSsoSession() );
  }

  @Test
  public void testIsSsoSessionWithNullSessionIdReturnsFalse() {
    final AuthenticationData data = dataWithSession( null );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );
    assertFalse( task.isSsoSession() );
  }

  @Test
  public void testRunWithEmptySessionIdUsesCredentialsBranch() throws Exception {

    AuthenticationData data = dataWithSession( "" );

    when( data.getUsername() ).thenReturn( "user" );
    when( data.getPassword() ).thenReturn( "pass" );

    UpdateReservedCharsTask task =
      new UpdateReservedCharsTask( data );

    HttpClientManager manager =
      mock( HttpClientManager.class );

    HttpClientManager.HttpClientBuilderFacade builder =
      mock( HttpClientManager.HttpClientBuilderFacade.class );

    CloseableHttpClient client =
      mock( CloseableHttpClient.class );

    CloseableHttpResponse response =
      mock( CloseableHttpResponse.class );

    StatusLine status =
      mock( StatusLine.class );

    when( manager.createBuilder() ).thenReturn( builder );
    when( builder.setSocketTimeout( any( Integer.class ) ) ).thenReturn( builder );
    when( builder.setCookieSpec( any() ) ).thenReturn( builder );
    when( builder.setCredentials( "user", "pass" ) ).thenReturn( builder );
    when( builder.build() ).thenReturn( client );

    when( client.execute( any( HttpGet.class ) ) ).thenReturn( response );
    when( response.getStatusLine() ).thenReturn( status );
    when( status.getStatusCode() ).thenReturn( HttpStatus.SC_OK );

    try (
      MockedStatic<HttpClientManager> managerMock =
        mockStatic( HttpClientManager.class );

      MockedStatic<HttpClientUtil> httpClientUtil =
        mockStatic( HttpClientUtil.class );

      MockedStatic<PublishUtil> publishUtil =
        mockStatic( PublishUtil.class )
    ) {

      managerMock.when(
          HttpClientManager::getInstance )
        .thenReturn( manager );

      httpClientUtil.when(
          () -> HttpClientUtil.responseToString( response ) )
        .thenReturn( "[]" );

      task.run();

      verify( builder )
        .setCredentials( "user", "pass" );
    }
  }

  @Test
  public void testApplySsoCookieSsoSessionSetsCookieHeader() {
    final AuthenticationData data = dataWithSession( "test-session-id" );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );

    final HttpGet request = new HttpGet( "http://localhost" );
    task.applySsoCookie( request );

    assertNotNull( request.getFirstHeader( COOKIE_HEADER ) );
    assertEquals( SESSION_COOKIE_PREFIX + "test-session-id", request.getFirstHeader( COOKIE_HEADER ).getValue() );
  }

  @Test
  public void testApplySsoCookieNoSsoSessionDoesNotSetCookieHeader() {
    final AuthenticationData data = dataWithSession( null );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );

    final HttpGet request = new HttpGet( "http://localhost" );
    task.applySsoCookie( request );

    assertNull( request.getFirstHeader( COOKIE_HEADER ) );
  }

  @Test
  public void testCheckResult200ReturnsTrue() {
    final AuthenticationData data = dataWithSession( null );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );
    assertTrue( task.checkResult( HttpStatus.SC_OK ) );
  }

  @Test
  public void testCheckResultNonOkReturnsFalse() {
    final AuthenticationData data = dataWithSession( null );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );
    assertFalse( task.checkResult( HttpStatus.SC_FORBIDDEN ) );
  }

  @Test
  public void testRunSuccessUpdatesReservedCharsAndDisplay() throws Exception {
    final AuthenticationData data = dataWithSession( "sess-1" );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );

    final HttpClientManager manager = mock( HttpClientManager.class );
    final HttpClientManager.HttpClientBuilderFacade builder = mock( HttpClientManager.HttpClientBuilderFacade.class );
    final CloseableHttpClient client = mock( CloseableHttpClient.class );
    final CloseableHttpResponse response = mock( CloseableHttpResponse.class );
    final StatusLine status = mock( StatusLine.class );

    when( manager.createBuilder() ).thenReturn( builder );
    when( builder.setSocketTimeout( any( Integer.class ) ) ).thenReturn( builder );
    when( builder.setCookieSpec( any() ) ).thenReturn( builder );
    when( builder.setCredentials( any(), any() ) ).thenReturn( builder );
    when( builder.build() ).thenReturn( client );

    when( client.execute( any( HttpGet.class ) ) ).thenReturn( response );
    when( response.getStatusLine() ).thenReturn( status );
    when( status.getStatusCode() ).thenReturn( HttpStatus.SC_OK );

    try ( MockedStatic<HttpClientManager> managerMock = mockStatic( HttpClientManager.class );
          MockedStatic<HttpClientUtil> httpClientUtil = mockStatic( HttpClientUtil.class );
          MockedStatic<PublishUtil> publishUtil = mockStatic( PublishUtil.class ) ) {
      managerMock.when( HttpClientManager::getInstance ).thenReturn( manager );
      httpClientUtil.when( () -> HttpClientUtil.responseToString( response ) ).thenReturn( "[]" );

      task.run();

      verify( client, times( 2 ) ).execute( any( HttpGet.class ) );
      publishUtil.verify( () -> PublishUtil.setReservedChars( "[]" ) );
      publishUtil.verify( () -> PublishUtil.setReservedCharsDisplay( "[]" ) );
    }
  }

  @Test
  public void testRunReservedCharsFailureThrowsIllegalStateException() throws Exception {
    final AuthenticationData data = dataWithSession( null );
    when( data.getUsername() ).thenReturn( "user" );
    when( data.getPassword() ).thenReturn( "pass" );
    final UpdateReservedCharsTask task = new UpdateReservedCharsTask( data );

    final HttpClientManager manager = mock( HttpClientManager.class );
    final HttpClientManager.HttpClientBuilderFacade builder = mock( HttpClientManager.HttpClientBuilderFacade.class );
    final CloseableHttpClient client = mock( CloseableHttpClient.class );
    final CloseableHttpResponse response = mock( CloseableHttpResponse.class );
    final StatusLine status = mock( StatusLine.class );

    when( manager.createBuilder() ).thenReturn( builder );
    when( builder.setSocketTimeout( any( Integer.class ) ) ).thenReturn( builder );
    when( builder.setCookieSpec( any() ) ).thenReturn( builder );
    when( builder.setCredentials( any(), any() ) ).thenReturn( builder );
    when( builder.build() ).thenReturn( client );
    when( client.execute( any( HttpGet.class ) ) ).thenReturn( response );
    when( response.getStatusLine() ).thenReturn( status );
    when( status.getStatusCode() ).thenReturn( HttpStatus.SC_FORBIDDEN );

    try ( MockedStatic<HttpClientManager> managerMock = mockStatic( HttpClientManager.class ) ) {
      managerMock.when( HttpClientManager::getInstance ).thenReturn( manager );
      try {
        task.run();
        org.junit.Assert.fail( "Expected IllegalStateException" );
      } catch ( IllegalStateException expected ) {
        assertTrue( expected.getMessage().contains( "reserved characters" ) );
      }
    }
  }

  @Test
  public void testRunDisplayRequestReturnsForbidden() throws Exception {

    AuthenticationData data = dataWithSession( "sess-1" );

    UpdateReservedCharsTask task =
      new UpdateReservedCharsTask( data );

    HttpClientManager manager =
      mock( HttpClientManager.class );

    HttpClientManager.HttpClientBuilderFacade builder =
      mock( HttpClientManager.HttpClientBuilderFacade.class );

    CloseableHttpClient client =
      mock( CloseableHttpClient.class );

    CloseableHttpResponse firstResponse =
      mock( CloseableHttpResponse.class );

    CloseableHttpResponse secondResponse =
      mock( CloseableHttpResponse.class );

    StatusLine okStatus =
      mock( StatusLine.class );

    StatusLine forbiddenStatus =
      mock( StatusLine.class );

    when( manager.createBuilder() ).thenReturn( builder );
    when( builder.setSocketTimeout( any( Integer.class ) ) ).thenReturn( builder );
    when( builder.setCookieSpec( any() ) ).thenReturn( builder );
    when( builder.build() ).thenReturn( client );

    when( client.execute( any( HttpGet.class ) ) )
      .thenReturn( firstResponse )
      .thenReturn( secondResponse );

    when( firstResponse.getStatusLine() )
      .thenReturn( okStatus );

    when( secondResponse.getStatusLine() )
      .thenReturn( forbiddenStatus );

    when( okStatus.getStatusCode() )
      .thenReturn( HttpStatus.SC_OK );

    when( forbiddenStatus.getStatusCode() )
      .thenReturn( HttpStatus.SC_FORBIDDEN );

    try (
      MockedStatic<HttpClientManager> managerMock =
        mockStatic( HttpClientManager.class );

      MockedStatic<HttpClientUtil> httpClientUtil =
        mockStatic( HttpClientUtil.class );

      MockedStatic<PublishUtil> publishUtil =
        mockStatic( PublishUtil.class )
    ) {

      managerMock.when(
          HttpClientManager::getInstance )
        .thenReturn( manager );

      httpClientUtil.when(
          () -> HttpClientUtil.responseToString(
            firstResponse ) )
        .thenReturn( "[]" );

      try {
        task.run();
        org.junit.Assert.fail(
          "Expected IllegalStateException" );
      } catch ( IllegalStateException ex ) {

        assertEquals(
          "Failed to update reserved characters display",
          ex.getMessage() );
      }
    }
  }
}

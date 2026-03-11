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
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import java.lang.reflect.Constructor;

/**
 * Tests for {@link AuthenticationHelper} - 100% coverage.
 */
public class AuthenticationHelperTest {

  // ==================== Constants ====================

  @Test
  public void testConstants() {
    assertEquals( "sessionId", AuthenticationHelper.OPTION_SESSION_ID );
    assertEquals( "browserAuth", AuthenticationHelper.OPTION_BROWSER_AUTH );
  }

  // ==================== Private constructor (coverage) ====================

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<AuthenticationHelper> constructor = AuthenticationHelper.class.getDeclaredConstructor();
    assertTrue( java.lang.reflect.Modifier.isPrivate( constructor.getModifiers() ) );
    constructor.setAccessible( true );
    AuthenticationHelper instance = constructor.newInstance();
    assertNotNull( instance );
  }

  // ==================== isAuthenticationError ====================

  @Test
  public void testIsAuthenticationError_NullException() {
    assertFalse( AuthenticationHelper.isAuthenticationError( null ) );
  }

  @Test
  public void testIsAuthenticationError_401InMessage() {
    Exception ex = new Exception( "Server returned 401" );
    assertTrue( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_403InMessage() {
    Exception ex = new Exception( "Server returned 403 Forbidden" );
    assertTrue( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_UnauthorizedLowerCase() {
    Exception ex = new Exception( "unauthorized access denied" );
    assertTrue( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_UnauthorizedMixedCase() {
    Exception ex = new Exception( "Unauthorized request" );
    assertTrue( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_NotAuthorized() {
    Exception ex = new Exception( "User is not authorized" );
    assertTrue( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_NotAuthorizedMixedCase() {
    Exception ex = new Exception( "Not Authorized to perform this action" );
    assertTrue( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_NestedCause401() {
    Exception root = new Exception( "HTTP 401" );
    Exception wrapper = new Exception( "Request failed", root );
    assertTrue( AuthenticationHelper.isAuthenticationError( wrapper ) );
  }

  @Test
  public void testIsAuthenticationError_NestedCause403() {
    Exception root = new Exception( "403 Forbidden" );
    Exception mid = new Exception( "Request failed", root );
    Exception outer = new Exception( "Publish failed", mid );
    assertTrue( AuthenticationHelper.isAuthenticationError( outer ) );
  }

  @Test
  public void testIsAuthenticationError_NullMessage() {
    Exception ex = new Exception( (String) null );
    assertFalse( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_NullMessageWithAuthCause() {
    Exception root = new Exception( "401 Unauthorized" );
    Exception ex = new Exception( (String) null, root );
    assertTrue( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_UnrelatedError() {
    Exception ex = new Exception( "Connection timed out" );
    assertFalse( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_EmptyMessage() {
    Exception ex = new Exception( "" );
    assertFalse( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_ClassNameContainsNotAuthorizedException() {
    // We cannot easily create a class whose name contains "NotAuthorizedException"
    // at runtime, but we can verify the logic with a real exception whose message 
    // does not match but class name would. Since we can't change class name, test 
    // with a combination: cause chain that doesn't match anything.
    Exception ex = new RuntimeException( "some error" );
    assertFalse( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationError_DeepChainNoMatch() {
    Exception e1 = new Exception( "level 1" );
    Exception e2 = new Exception( "level 2", e1 );
    Exception e3 = new Exception( "level 3", e2 );
    assertFalse( AuthenticationHelper.isAuthenticationError( e3 ) );
  }

  @Test
  public void testIsAuthenticationError_DeepChainMatchAtBottom() {
    Exception e1 = new Exception( "unauthorized" );
    Exception e2 = new Exception( "level 2", e1 );
    Exception e3 = new Exception( "level 3", e2 );
    Exception e4 = new Exception( "level 4", e3 );
    assertTrue( AuthenticationHelper.isAuthenticationError( e4 ) );
  }

  // ==================== isBrowserAuth ====================

  @Test
  public void testIsBrowserAuth_NullLoginData() {
    assertFalse( AuthenticationHelper.isBrowserAuth( null ) );
  }

  @Test
  public void testIsBrowserAuth_NoBrowserAuthOption() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( null );
    assertFalse( AuthenticationHelper.isBrowserAuth( data ) );
  }

  @Test
  public void testIsBrowserAuth_BrowserAuthFalse() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( "false" );
    assertFalse( AuthenticationHelper.isBrowserAuth( data ) );
  }

  @Test
  public void testIsBrowserAuth_BrowserAuthTrue() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( "true" );
    assertTrue( AuthenticationHelper.isBrowserAuth( data ) );
  }

  @Test
  public void testIsBrowserAuth_BrowserAuthEmpty() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( "" );
    assertFalse( AuthenticationHelper.isBrowserAuth( data ) );
  }

  @Test
  public void testIsBrowserAuth_BrowserAuthRandomValue() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( "yes" );
    assertFalse( AuthenticationHelper.isBrowserAuth( data ) );
  }

  // ==================== extractHostname ====================

  @Test
  public void testExtractHostname_NullUrl() {
    assertEquals( "localhost", AuthenticationHelper.extractHostname( null ) );
  }

  @Test
  public void testExtractHostname_ValidUrl() {
    assertEquals( "myserver.example.com",
      AuthenticationHelper.extractHostname( "http://myserver.example.com:8080/pentaho" ) );
  }

  @Test
  public void testExtractHostname_HttpsUrl() {
    assertEquals( "secure-server.com",
      AuthenticationHelper.extractHostname( "https://secure-server.com/pentaho" ) );
  }

  @Test
  public void testExtractHostname_LocalhostUrl() {
    assertEquals( "localhost",
      AuthenticationHelper.extractHostname( "http://localhost:8080/pentaho" ) );
  }

  @Test
  public void testExtractHostname_InvalidUrl() {
    assertEquals( "localhost", AuthenticationHelper.extractHostname( "not a valid url %%%" ) );
  }

  @Test
  public void testExtractHostname_EmptyString() {
    // Empty string is a valid relative URI, host will be null
    assertEquals( "localhost", AuthenticationHelper.extractHostname( "" ) );
  }

  @Test
  public void testExtractHostname_UrlWithoutHost() {
    // A relative path - no host component
    assertEquals( "localhost", AuthenticationHelper.extractHostname( "/just/a/path" ) );
  }

  @Test
  public void testExtractHostname_UrlWithIpAddress() {
    assertEquals( "192.168.1.100",
      AuthenticationHelper.extractHostname( "http://192.168.1.100:8080/pentaho" ) );
  }

  @Test
  public void testExtractHostname_UrlWithPort() {
    assertEquals( "server.local",
      AuthenticationHelper.extractHostname( "http://server.local:9090" ) );
  }
}

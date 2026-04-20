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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import java.lang.reflect.Constructor;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Tests for {@link AuthenticationHelper} - 100% coverage.
 */
@RunWith( Parameterized.class )
public class AuthenticationHelperTest {
  private static final String LOCALHOST = "localhost";
  private static final String LEVEL_1 = "level 1";
  private static final String LEVEL_2 = "level 2";
  private static final String LEVEL_3 = "level 3";
  private static final String LEVEL_4 = "level 4";
  private static final String SERVER_RETURNED_401 = "Server returned 401";

  @Test
  public void testConstants() {
    assertEquals( "sessionId", AuthenticationHelper.OPTION_SESSION_ID );
    assertEquals( "browserAuth", AuthenticationHelper.OPTION_BROWSER_AUTH );
  }

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<AuthenticationHelper> constructor = AuthenticationHelper.class.getDeclaredConstructor();
    assertTrue( java.lang.reflect.Modifier.isPrivate( constructor.getModifiers() ) );
    constructor.setAccessible( true );
    AuthenticationHelper instance = constructor.newInstance();
    assertNotNull( instance );
  }

  @Test
  public void testIsAuthenticationErrorNullException() {
    assertFalse( AuthenticationHelper.isAuthenticationError( null ) );
  }

  @Parameter( 0 )
  public Exception testException;
  @Parameter( 1 )
  public boolean isAuthErrorTest;
  @Parameter( 2 )
  public boolean expectedResult;

  @Parameters( name = "{index}: exception={0}" )
  public static Collection<Object[]> data() {
    Collection<Object[]> data = new ArrayList<>();
    // isAuthenticationError tests
    data.add( new Object[] { new Exception( SERVER_RETURNED_401 ), true, true } );
    data.add( new Object[] { new Exception( "Server returned 403 Forbidden" ), true, true } );
    data.add( new Object[] { new Exception( "unauthorized access denied" ), true, true } );
    data.add( new Object[] { new Exception( "Unauthorized request" ), true, true } );
    data.add( new Object[] { new Exception( "User is not authorized" ), true, true } );
    data.add( new Object[] { new Exception( "Not Authorized to perform this action" ), true, true } );
    // isBrowserAuth tests (reusing parameter0 as mock option value)
    data.add( new Object[] { null, false, false } );            // NoBrowserAuthOption - returns null
    data.add( new Object[] { new Exception("false"), false, false } );  // BrowserAuthFalse
    data.add( new Object[] { new Exception("true"), false, true } );    // BrowserAuthTrue
    data.add( new Object[] { new Exception(""), false, false } );       // BrowserAuthEmpty
    data.add( new Object[] { new Exception("yes"), false, false } );    // BrowserAuthRandomValue
    return data;
  }

  @Test
  public void testIsAuthenticationErrorOrIsBrowserAuth() {
    if ( isAuthErrorTest ) {
      assertEquals( expectedResult, AuthenticationHelper.isAuthenticationError( testException ) );
    } else {
      AuthenticationData data = mock( AuthenticationData.class );
      String optionValue = testException != null ? testException.getMessage() : null;
      when( data.getOption( AuthenticationHelper.OPTION_BROWSER_AUTH ) ).thenReturn( optionValue );
      assertEquals( expectedResult, AuthenticationHelper.isBrowserAuth( data ) );
    }
  }

  @Test
  public void testIsAuthenticationErrorNestedCause401() {
    Exception root = new Exception( "HTTP 401" );
    Exception wrapper = new Exception( "Request failed", root );
    assertTrue( AuthenticationHelper.isAuthenticationError( wrapper ) );
  }

  @Test
  public void testIsAuthenticationErrorNestedCause403() {
    Exception root = new Exception( "403 Forbidden" );
    Exception mid = new Exception( "Request failed", root );
    Exception outer = new Exception( "Publish failed", mid );
    assertTrue( AuthenticationHelper.isAuthenticationError( outer ) );
  }

  @Test
  public void testIsAuthenticationErrorNullMessage() {
    Exception ex = new Exception( (String) null );
    assertFalse( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationErrorNullMessageWithAuthCause() {
    Exception root = new Exception( "401 Unauthorized" );
    Exception ex = new Exception( (String) null, root );
    assertTrue( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationErrorUnrelatedError() {
    Exception ex = new Exception( "Connection timed out" );
    assertFalse( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationErrorEmptyMessage() {
    Exception ex = new Exception( "" );
    assertFalse( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationErrorClassNameContainsNotAuthorizedException() {
    Exception ex = new RuntimeException( "some error" );
    assertFalse( AuthenticationHelper.isAuthenticationError( ex ) );
  }

  @Test
  public void testIsAuthenticationErrorDeepChainNoMatch() {
    Exception e1 = new Exception( LEVEL_1 );
    Exception e2 = new Exception( LEVEL_2, e1 );
    Exception e3 = new Exception( LEVEL_3, e2 );
    assertFalse( AuthenticationHelper.isAuthenticationError( e3 ) );
  }

  @Test
  public void testIsAuthenticationErrorDeepChainMatchAtBottom() {
    Exception e1 = new Exception( "unauthorized" );
    Exception e2 = new Exception( LEVEL_2, e1 );
    Exception e3 = new Exception( LEVEL_3, e2 );
    Exception e4 = new Exception( LEVEL_4, e3 );
    assertTrue( AuthenticationHelper.isAuthenticationError( e4 ) );
  }

  @Test
  public void testIsBrowserAuthNullLoginData() {
    assertFalse( AuthenticationHelper.isBrowserAuth( null ) );
  }

  @Test
  public void testExtractHostnameNullUrl() {
    assertEquals( LOCALHOST, AuthenticationHelper.extractHostname( null ) );
  }

  @Test
  public void testExtractHostnameValidUrl() {
    assertEquals( "myserver.example.com",
      AuthenticationHelper.extractHostname( "http://myserver.example.com:8080/pentaho" ) );
  }

  @Test
  public void testExtractHostnameHttpsUrl() {
    assertEquals( "secure-server.com",
      AuthenticationHelper.extractHostname( "https://secure-server.com/pentaho" ) );
  }

  @Test
  public void testExtractHostnameLocalhostUrl() {
    assertEquals( LOCALHOST,
      AuthenticationHelper.extractHostname( "http://localhost:8080/pentaho" ) );
  }

  @Test
  public void testExtractHostnameInvalidUrl() {
    assertEquals( LOCALHOST, AuthenticationHelper.extractHostname( "not a valid url %%%" ) );
  }

  @Test
  public void testExtractHostnameEmptyString() {
    // Empty string is a valid relative URI, host will be null
    assertEquals( LOCALHOST, AuthenticationHelper.extractHostname( "" ) );
  }

  @Test
  public void testExtractHostnameUrlWithoutHost() {
    // A relative path - no host component
    assertEquals( LOCALHOST, AuthenticationHelper.extractHostname( "/just/a/path" ) );
  }

  @Test
  public void testExtractHostnameUrlWithIpAddress() {
    assertEquals( "192.168.1.100",
      AuthenticationHelper.extractHostname( "http://192.168.1.100:8080/pentaho" ) );
  }

  @Test
  public void testExtractHostnameUrlWithPort() {
    assertEquals( "server.local",
      AuthenticationHelper.extractHostname( "http://server.local:9090" ) );
  }

  // ---- isConnectionError tests ----

  @Test
  public void testIsConnectionErrorNull() {
    assertFalse( AuthenticationHelper.isConnectionError( null ) );
  }

  @Test
  public void testIsConnectionErrorConnectException() {
    Exception ex = new ConnectException( "Connection refused" );
    assertTrue( AuthenticationHelper.isConnectionError( ex ) );
  }

  @Test
  public void testIsConnectionErrorSocketTimeout() {
    Exception ex = new SocketTimeoutException( "Read timed out" );
    assertTrue( AuthenticationHelper.isConnectionError( ex ) );
  }

  @Test
  public void testIsConnectionErrorUnknownHost() {
    Exception ex = new UnknownHostException( "nosuchhost.example.com" );
    assertTrue( AuthenticationHelper.isConnectionError( ex ) );
  }

  @Test
  public void testIsConnectionErrorNestedConnectException() {
    Exception root = new ConnectException( "Connection refused" );
    Exception wrapper = new Exception( "VFS error", root );
    assertTrue( AuthenticationHelper.isConnectionError( wrapper ) );
  }

  @Test
  public void testIsConnectionErrorMatchingMessages() {
    // Tests for messages that should be recognized as connection errors
    String[] connectionMessages = {
        "vfs.provider/connect.error",
        "Connection refused to host",
        "connect timed out",
        "No route to host"
    };
    for ( String msg : connectionMessages ) {
      assertTrue( "Expected connection error for: " + msg,
          AuthenticationHelper.isConnectionError( new Exception( msg ) ) );
    }
  }

  @Test
  public void testIsConnectionErrorNonMatchingMessages() {
    // Tests for messages that should NOT be recognized as connection errors
    String[] nonConnectionMessages = {
        "File not found",
        "",
        SERVER_RETURNED_401
    };
    for ( String msg : nonConnectionMessages ) {
      assertFalse( "Expected no connection error for: " + msg,
          AuthenticationHelper.isConnectionError( new Exception( msg ) ) );
    }
    // Null message
    assertFalse( AuthenticationHelper.isConnectionError( new Exception( (String) null ) ) );
  }

  @Test
  public void testIsConnectionErrorDeepChainNoMatch() {
    Exception e1 = new Exception( LEVEL_1 );
    Exception e2 = new Exception( LEVEL_2, e1 );
    Exception e3 = new Exception( LEVEL_3, e2 );
    assertFalse( AuthenticationHelper.isConnectionError( e3 ) );
  }

  @Test
  public void testIsConnectionErrorDeepChainMatchAtBottom() {
    Exception e1 = new SocketTimeoutException( "connect timed out" );
    Exception e2 = new Exception( LEVEL_2, e1 );
    Exception e3 = new Exception( LEVEL_3, e2 );
    assertTrue( AuthenticationHelper.isConnectionError( e3 ) );
  }

  @Test
  public void testIsConnectionErrorAuthErrorNotConnection() {
    // 401/403 should NOT be a connection error
    Exception ex = new Exception( SERVER_RETURNED_401 );
    assertFalse( AuthenticationHelper.isConnectionError( ex ) );
  }
}

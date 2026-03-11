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

import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

/**
 * Tests for {@link SessionAuthenticationUtil} - 100% coverage.
 */
public class SessionAuthenticationUtilTest {

  private AuthenticationData authData;

  @Before
  public void setUp() {
    authData = mock( AuthenticationData.class );
  }

  // ==================== isSessionBased ====================

  @Test
  public void testIsSessionBased_NullAuthData() {
    assertFalse( SessionAuthenticationUtil.isSessionBased( null ) );
  }

  @Test
  public void testIsSessionBased_NullSessionId() {
    when( authData.getOption( "sessionId" ) ).thenReturn( null );
    assertFalse( SessionAuthenticationUtil.isSessionBased( authData ) );
  }

  @Test
  public void testIsSessionBased_EmptySessionId() {
    when( authData.getOption( "sessionId" ) ).thenReturn( "" );
    assertFalse( SessionAuthenticationUtil.isSessionBased( authData ) );
  }

  @Test
  public void testIsSessionBased_ValidSessionId() {
    when( authData.getOption( "sessionId" ) ).thenReturn( "ABC123" );
    assertTrue( SessionAuthenticationUtil.isSessionBased( authData ) );
  }

  // ==================== createSessionAuthenticatedClient ====================

  @Test
  public void testCreateClient_WithSessionId() {
    when( authData.getOption( "sessionId" ) ).thenReturn( "SESS_123" );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, "http://localhost:8080/pentaho", 30000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClient_NoSessionId_WithCredentials() {
    when( authData.getOption( "sessionId" ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( "admin" );
    when( authData.getPassword() ).thenReturn( "password" );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, "http://localhost:8080/pentaho", 30000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClient_NoSessionId_NullUsername() {
    when( authData.getOption( "sessionId" ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( null );
    when( authData.getPassword() ).thenReturn( null );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, "http://localhost:8080/pentaho", 30000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClient_NoSessionId_EmptyUsername() {
    when( authData.getOption( "sessionId" ) ).thenReturn( null );
    when( authData.getUsername() ).thenReturn( "" );
    when( authData.getPassword() ).thenReturn( "" );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, "http://localhost:8080/pentaho", 30000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClient_EmptySessionId() {
    when( authData.getOption( "sessionId" ) ).thenReturn( "" );
    when( authData.getUsername() ).thenReturn( "admin" );
    when( authData.getPassword() ).thenReturn( "pass" );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, "http://localhost:8080/pentaho", 5000 );
    assertNotNull( client );
  }

  @Test
  public void testCreateClient_ZeroTimeout() {
    when( authData.getOption( "sessionId" ) ).thenReturn( "SESS" );

    HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
      authData, "http://localhost:8080/pentaho", 0 );
    assertNotNull( client );
  }
}

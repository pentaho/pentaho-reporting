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


package org.pentaho.reporting.designer.extensions.pentaho.repository.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OAuthProviderTest {
  private static final String AUTH_URI = "http://auth";
  private static final String IMAGE_URI = "http://img";
  private static final String CLIENT_GOOGLE = "Google";

  @Test
  public void testDefaultConstructorAllFieldsNullOrDefault() {
    final OAuthProvider provider = new OAuthProvider();
    assertNull( provider.getAuthorizationUri() );
    assertNull( provider.getImageUri() );
    assertNull( provider.getClientName() );
    assertNull( provider.getRegistrationId() );
    assertFalse( provider.isEnabled() );
  }

  @Test
  public void testFullConstructorSetsAllFields() {
    final OAuthProvider provider = new OAuthProvider(
        AUTH_URI, IMAGE_URI, "MyClient", "reg-1", true );
    assertEquals( AUTH_URI, provider.getAuthorizationUri() );
    assertEquals( IMAGE_URI, provider.getImageUri() );
    assertEquals( "MyClient", provider.getClientName() );
    assertEquals( "reg-1", provider.getRegistrationId() );
    assertTrue( provider.isEnabled() );
  }

  @Test
  public void testFullConstructorEnabledFalse() {
    final OAuthProvider provider = new OAuthProvider(
        null, null, null, null, false );
    assertFalse( provider.isEnabled() );
  }

  @Test
  public void testSetAndGetAuthorizationUri() {
    final OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri( AUTH_URI );
    assertEquals( AUTH_URI, provider.getAuthorizationUri() );
  }

  @Test
  public void testSetAndGetImageUri() {
    final OAuthProvider provider = new OAuthProvider();
    provider.setImageUri( IMAGE_URI );
    assertEquals( IMAGE_URI, provider.getImageUri() );
  }

  @Test
  public void testSetAndGetClientName() {
    final OAuthProvider provider = new OAuthProvider();
    provider.setClientName( CLIENT_GOOGLE );
    assertEquals( CLIENT_GOOGLE, provider.getClientName() );
  }

  @Test
  public void testSetAndGetRegistrationId() {
    final OAuthProvider provider = new OAuthProvider();
    provider.setRegistrationId( "google-oidc" );
    assertEquals( "google-oidc", provider.getRegistrationId() );
  }

  @Test
  public void testSetEnabledTrue() {
    final OAuthProvider provider = new OAuthProvider();
    provider.setEnabled( true );
    assertTrue( provider.isEnabled() );
  }

  @Test
  public void testSetEnabledFalse() {
    final OAuthProvider provider = new OAuthProvider();
    provider.setEnabled( true );
    provider.setEnabled( false );
    assertFalse( provider.isEnabled() );
  }

  @Test
  public void testToStringReturnsClientName() {
    final OAuthProvider provider = new OAuthProvider();
    provider.setClientName( CLIENT_GOOGLE );
    assertEquals( CLIENT_GOOGLE, provider.toString() );
  }

  @Test
  public void testToStringNullClientNameReturnsNull() {
    final OAuthProvider provider = new OAuthProvider();
    assertNull( provider.toString() );
  }
}

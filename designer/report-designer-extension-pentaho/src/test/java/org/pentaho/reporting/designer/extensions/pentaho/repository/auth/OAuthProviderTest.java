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

import org.junit.Test;

public class OAuthProviderTest {

  private static final String MICROSOFT = "Microsoft";

  @Test
  public void testDefaultConstructor() {
    OAuthProvider provider = new OAuthProvider();
    assertNotNull( provider );
    assertNull( provider.getAuthorizationUri() );
    assertNull( provider.getImageUri() );
    assertNull( provider.getClientName() );
    assertNull( provider.getRegistrationId() );
    assertFalse( provider.isEnabled() );
  }

  @Test
  public void testParameterisedConstructor() {
    OAuthProvider provider = new OAuthProvider(
      "oauth2/authorization/azure",
      "https://example.com/icon.png",
      MICROSOFT,
      "azure",
      true
    );
    assertEquals( "oauth2/authorization/azure", provider.getAuthorizationUri() );
    assertEquals( "https://example.com/icon.png", provider.getImageUri() );
    assertEquals( MICROSOFT, provider.getClientName() );
    assertEquals( "azure", provider.getRegistrationId() );
    assertTrue( provider.isEnabled() );
  }

  @Test
  public void testParameterisedConstructorNullValues() {
    OAuthProvider provider = new OAuthProvider( null, null, null, null, false );
    assertNull( provider.getAuthorizationUri() );
    assertNull( provider.getImageUri() );
    assertNull( provider.getClientName() );
    assertNull( provider.getRegistrationId() );
    assertFalse( provider.isEnabled() );
  }

  @Test
  public void setAuthorizationUri() {
    OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri( "oauth2/authorization/google" );
    assertEquals( "oauth2/authorization/google", provider.getAuthorizationUri() );
  }

  @Test
  public void setImageUri() {
    OAuthProvider provider = new OAuthProvider();
    provider.setImageUri( "https://example.com/logo.svg" );
    assertEquals( "https://example.com/logo.svg", provider.getImageUri() );
  }

  @Test
  public void setClientName() {
    OAuthProvider provider = new OAuthProvider();
    provider.setClientName( "Google" );
    assertEquals( "Google", provider.getClientName() );
  }

  @Test
  public void setRegistrationId() {
    OAuthProvider provider = new OAuthProvider();
    provider.setRegistrationId( "google" );
    assertEquals( "google", provider.getRegistrationId() );
  }

  @Test
  public void setEnabled() {
    OAuthProvider provider = new OAuthProvider();
    assertFalse( provider.isEnabled() );
    provider.setEnabled( true );
    assertTrue( provider.isEnabled() );
    provider.setEnabled( false );
    assertFalse( provider.isEnabled() );
  }

  @Test
  public void toStringReturnsClientName() {
    OAuthProvider provider = new OAuthProvider();
    provider.setClientName( MICROSOFT );
    assertEquals( MICROSOFT, provider.toString() );
  }

  @Test
  public void toStringNullClientName() {
    OAuthProvider provider = new OAuthProvider();
    assertNull( provider.toString() );
  }
}

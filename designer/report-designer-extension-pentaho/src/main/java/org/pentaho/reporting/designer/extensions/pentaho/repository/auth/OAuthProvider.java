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

/**
 * Represents an OAuth provider available on the Pentaho server.
 */
public class OAuthProvider {
  private String authorizationUri;
  private String imageUri;
  private String clientName;
  private String registrationId;
  private boolean enabled;

  public OAuthProvider() {
  }

  public OAuthProvider( String authorizationUri, String imageUri, String clientName,
                        String registrationId, boolean enabled ) {
    this.authorizationUri = authorizationUri;
    this.imageUri = imageUri;
    this.clientName = clientName;
    this.registrationId = registrationId;
    this.enabled = enabled;
  }

  public String getAuthorizationUri() {
    return authorizationUri;
  }

  public void setAuthorizationUri( String authorizationUri ) {
    this.authorizationUri = authorizationUri;
  }

  public String getImageUri() {
    return imageUri;
  }

  public void setImageUri( String imageUri ) {
    this.imageUri = imageUri;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName( String clientName ) {
    this.clientName = clientName;
  }

  public String getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId( String registrationId ) {
    this.registrationId = registrationId;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
  }

  @Override
  public String toString() {
    return clientName;
  }
}

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

import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Shared utility for authentication-related operations used across repository tasks.
 */
public final class AuthenticationHelper {

  public static final String OPTION_SESSION_ID = "sessionId";
  public static final String OPTION_BROWSER_AUTH = "browserAuth";

  private AuthenticationHelper() {
  }

  /**
   * Checks whether the given exception indicates an authentication failure (401/403).
   */
  public static boolean isAuthenticationError( final Throwable exception ) {
    Throwable current = exception;
    while ( current != null ) {
      final String message = current.getMessage();
      if ( message != null ) {
        final String lower = message.toLowerCase();
        if ( message.contains( "401" ) || message.contains( "403" )
          || lower.contains( "unauthorized" ) || lower.contains( "not authorized" ) ) {
          return true;
        }
      }
      if ( current.getClass().getName().contains( "NotAuthorizedException" ) ) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }

  /**
   * Returns {@code true} if the login data was obtained via browser-based SSO.
   */
  public static boolean isBrowserAuth( final AuthenticationData loginData ) {
    return loginData != null && "true".equals( loginData.getOption( OPTION_BROWSER_AUTH ) );
  }

  /**
   * Extracts the hostname from a URL string.
   * Falls back to "localhost" on parse errors.
   */
  public static String extractHostname( final String url ) {
    if ( url == null ) {
      return "localhost";
    }
    try {
      final URI uri = new URI( url );
      final String host = uri.getHost();
      if ( host != null && !host.isEmpty() ) {
        return host;
      }
    } catch ( URISyntaxException ignored ) {
      // fall through
    }
    return "localhost";
  }
}

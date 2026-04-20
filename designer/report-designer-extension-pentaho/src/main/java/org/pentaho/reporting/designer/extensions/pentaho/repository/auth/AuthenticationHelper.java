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

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.URI;
import java.net.URISyntaxException;

public final class AuthenticationHelper {

  public static final String OPTION_SESSION_ID = "sessionId";
  public static final String OPTION_BROWSER_AUTH = "browserAuth";

  private AuthenticationHelper() {
  }

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

  public static boolean isBrowserAuth( final AuthenticationData loginData ) {
    return loginData != null && "true".equals( loginData.getOption( OPTION_BROWSER_AUTH ) );
  }

  public static boolean isConnectionError( final Throwable exception ) {
    Throwable current = exception;
    while ( current != null ) {
      if ( current instanceof ConnectException
        || current instanceof SocketTimeoutException
        || current instanceof UnknownHostException ) {
        return true;
      }
      final String msg = current.getMessage();
      if ( msg != null ) {
        final String lower = msg.toLowerCase();
        if ( lower.contains( "connect.error" ) || lower.contains( "connection refused" )
          || lower.contains( "connect timed out" ) || lower.contains( "no route to host" ) ) {
          return true;
        }
      }
      current = current.getCause();
    }
    return false;
  }

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

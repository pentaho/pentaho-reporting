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

import org.apache.http.client.HttpClient;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.engine.classic.core.util.HttpClientManager;

/**
 * Utility class to create HTTP clients with session-based authentication.
 */
public class SessionAuthenticationUtil {

  /**
   * Creates an HttpClient configured with session ID authentication.
   * 
   * Note: The current HttpClientBuilderFacade doesn't support cookie-based authentication directly.
   * Session IDs are stored in AuthenticationData but HTTP clients will use basic auth as fallback.
   * 
   * For full session support, you would need to:
   * 1. Manually create HttpClient with BasicCookieStore
   * 2. Add JSESSIONID cookie to the cookie store
   * 3. Use that client for all requests
   * 
   * @param authData Authentication data containing session ID
   * @param serverUrl Base URL of the Pentaho server
   * @param timeout Connection timeout in milliseconds
   * @return Configured HttpClient instance
   */
  public static HttpClient createSessionAuthenticatedClient( AuthenticationData authData, 
                                                               String serverUrl, 
                                                               int timeout ) {
    String sessionId = authData.getOption( "sessionId" );
    
    HttpClientManager.HttpClientBuilderFacade clientBuilder = 
        HttpClientManager.getInstance().createBuilder();
    
    // Configure timeout using the builder methods
    clientBuilder.setConnectionTimeout( timeout );
    clientBuilder.setSocketTimeout( timeout );
    
    if ( sessionId != null && !sessionId.isEmpty() ) {
      // Browser/SSO authentication - use raw Cookie header
      // (bypasses cookie-spec domain validation that rejects IP addresses)
      clientBuilder.setSessionCookie( "JSESSIONID", sessionId );
      return clientBuilder.build();
    } else {
      // Fall back to basic authentication if no session ID
      String username = authData.getUsername();
      String password = authData.getPassword();
      
      if ( username != null && !username.isEmpty() ) {
        return clientBuilder
            .setCredentials( username, password )
            .build();
      }
      
      return clientBuilder.build();
    }
  }

  /**
   * Extracts domain from a URL for cookie configuration.
   * 
   * @param authData Authentication data to check
   * @return true if session ID is present, false otherwise
   */
  public static boolean isSessionBased( AuthenticationData authData ) {
    if ( authData == null ) {
      return false;
    }
    String sessionId = authData.getOption( "sessionId" );
    return sessionId != null && !sessionId.isEmpty();
  }
}

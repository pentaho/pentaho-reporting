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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.engine.classic.core.util.HttpClientManager;

/**
 * Utility class to create HTTP clients with session-based authentication.
 */
public final class SessionAuthenticationUtil {

  private static final String OPTION_SESSION_ID = "sessionId";
  private static final String COOKIE_HEADER = "Cookie";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final int VALIDATION_TIMEOUT_MS = 5000;

  /**
   * System property name used to override the relative URI invoked when probing
   * whether an authenticated session is still valid. Allows operators to point
   * the validity check at a different lightweight, authenticated endpoint when
   * the default REST path is unavailable or restricted.
   */
  static final String SESSION_CHECK_PATH_PROPERTY =
    "org.pentaho.reporting.designer.extensions.pentaho.repository.auth.sessionCheckPath";
  // Default is overridable via the system property above; the literal here is just
  // the out-of-the-box value used when no override is provided.
  @SuppressWarnings( "java:S1075" )
  private static final String DEFAULT_SESSION_CHECK_PATH = "/api/repo/files/tree?depth=0";
  private static final String SESSION_CHECK_PATH =
    System.getProperty( SESSION_CHECK_PATH_PROPERTY, DEFAULT_SESSION_CHECK_PATH );

  private SessionAuthenticationUtil() {
    // utility class
  }

  /**
   * Creates a builder pre-configured with authentication and timeout settings.
   *
   * @param authData Authentication data containing session ID or credentials (may be null)
   * @param timeout  Connection timeout in milliseconds
   * @return configured builder (not yet built)
   */
  private static HttpClientManager.HttpClientBuilderFacade createBaseClientBuilder(
    AuthenticationData authData, int timeout ) {
    HttpClientManager.HttpClientBuilderFacade clientBuilder =
      HttpClientManager.getInstance().createBuilder();
    clientBuilder.setConnectionTimeout( timeout );
    clientBuilder.setSocketTimeout( timeout );

    if ( authData == null ) {
      return clientBuilder;
    }

    String sessionId = authData.getOption( OPTION_SESSION_ID );
    if ( sessionId != null && !sessionId.isEmpty() ) {
      // Browser/SSO authentication - use raw Cookie header
      // (bypasses cookie-spec domain validation that rejects IP addresses)
      clientBuilder.setSessionCookie( "JSESSIONID", sessionId );
    } else {
      // Fall back to basic authentication if no session ID
      String username = authData.getUsername();
      String password = authData.getPassword();
      if ( username != null && !username.isEmpty() ) {
        clientBuilder.setCredentials( username, password );
      }
    }
    return clientBuilder;
  }

  /**
   * Creates an HttpClient configured with session ID authentication.
   *
   * When a session ID is present in the authentication data, the client is configured
   * with a ({@code JSESSIONID}) session cookie. If no session ID is available, the method
   * falls back to basic authentication when username and password are present.
   *
   * @param authData Authentication data containing session ID or credentials
   * @param timeout Connection timeout in milliseconds
   * @return Configured HttpClient instance
   */
  public static HttpClient createSessionAuthenticatedClient( AuthenticationData authData,
                                                             int timeout ) {
    return createBaseClientBuilder( authData, timeout ).build();
  }

  /**
   * Creates an HttpClient that does <em>not</em> follow HTTP redirects and
   * uses its own private connection manager (so closing the client does not
   * shut down any shared pool).
   *
   * <p>This is essential for session-validity checks for two reasons:
   * <ul>
   *   <li>When a session has expired, the server typically responds with a
   *       302 redirect to the SSO login page.  If the client follows that
   *       redirect it receives a 200&nbsp;OK from the login page and the
   *       caller mistakenly treats the session as still valid.</li>
   *   <li>The shared {@link HttpClientManager#getInstance()} pool is shut
   *       down when any client built from it is closed via try-with-resources.
   *       Subsequent session checks would then fail with
   *       <em>Connection pool shut down</em>, get caught and silently turned
   *       into {@code null} (unknown), so {@link #checkSessionValidity} would
   *       never report an expired session.</li>
   * </ul>
   *
   * @param authData Authentication data containing session ID or credentials
   * @param timeout  Connection timeout in milliseconds
   * @return HttpClient that will not follow redirects
   */
  public static HttpClient createNoRedirectClient( AuthenticationData authData, int timeout ) {
    final RequestConfig config = RequestConfig.custom()
      .setConnectTimeout( timeout )
      .setSocketTimeout( timeout )
      .setRedirectsEnabled( false )
      .build();

    final HttpClientBuilder builder = HttpClients.custom()
      .setDefaultRequestConfig( config )
      .disableRedirectHandling();

    applyAuthInterceptor( builder, authData );

    return builder.build();
  }

  private static void applyAuthInterceptor( final HttpClientBuilder builder, final AuthenticationData authData ) {
    if ( authData == null ) {
      return;
    }
    final String sessionId = authData.getOption( OPTION_SESSION_ID );
    if ( sessionId != null && !sessionId.isEmpty() ) {
      addCookieInterceptor( builder, sessionId );
      return;
    }
    final String username = authData.getUsername();
    if ( username != null && !username.isEmpty() ) {
      addBasicAuthInterceptor( builder, username, authData.getPassword() );
    }
  }

  private static void addCookieInterceptor( final HttpClientBuilder builder, final String sessionId ) {
    final String cookie = "JSESSIONID=" + sessionId;
    builder.addInterceptorFirst( (HttpRequestInterceptor) ( request, context ) -> {
      if ( !request.containsHeader( COOKIE_HEADER ) ) {
        request.setHeader( COOKIE_HEADER, cookie );
      }
    } );
  }

  private static void addBasicAuthInterceptor( final HttpClientBuilder builder, final String username,
                                               final String password ) {
    final String pwd = ( password == null ) ? "" : password;
    final String token = Base64.getEncoder()
      .encodeToString( ( username + ":" + pwd ).getBytes( StandardCharsets.UTF_8 ) );
    final String authHeader = "Basic " + token;
    builder.addInterceptorFirst( (HttpRequestInterceptor) ( request, context ) -> {
      if ( !request.containsHeader( AUTHORIZATION_HEADER ) ) {
        request.setHeader( AUTHORIZATION_HEADER, authHeader );
      }
    } );
  }

  /**
   * Returns {@code true} when the given authentication data contains a session ID.
   *
   * @param authData Authentication data to check
   * @return true if session ID is present, false otherwise
   */
  public static boolean isSessionBased( AuthenticationData authData ) {
    if ( authData == null ) {
      return false;
    }
    String sessionId = authData.getOption( OPTION_SESSION_ID );
    return sessionId != null && !sessionId.isEmpty();
  }

  /**
   * Checks whether the server is reachable and the credentials are still valid.
   * Returns {@code true} only when the server responds with a 2xx status.
   * Returns {@code false} if the server is unreachable or responds with any
   * non-2xx status (including 3xx redirects to an SSO login page).
   *
   * @param authData Authentication data to use for the check
   * @return true if server is reachable and credentials are valid
   */
  public static boolean isServerReachable( AuthenticationData authData ) {
    if ( authData == null || authData.getUrl() == null ) {
      return false;
    }
    final ProbeResult probe = executeStatusProbe( authData );
    return probe != null && probe.status != null && probe.status >= 200 && probe.status < 300;
  }

  /**
   * Checks the session validity with three possible outcomes:
   * <ul>
   *   <li>{@link Boolean#TRUE}  – session is confirmed valid (server returned a 2xx response),
   *       or the auth data is not session-based (basic auth has no session concept)</li>
   *   <li>{@link Boolean#FALSE} – session is explicitly rejected: server returned 401, 403,
   *       or a 3xx redirect (typically to the SSO login page)</li>
   *   <li>{@code null}          – result is unknown (connection error, timeout, or non-auth HTTP status)</li>
   * </ul>
   *
   * @param authData Authentication data containing the session to verify
   * @return {@link Boolean#TRUE} if valid, {@link Boolean#FALSE} if rejected, {@code null} if unknown
   */
  @SuppressWarnings( "java:S2447" )
  public static Boolean checkSessionValidity( final AuthenticationData authData ) {
    if ( authData == null || authData.getUrl() == null ) {
      return null;
    }
    if ( !isSessionBased( authData ) ) {
      return Boolean.TRUE;
    }
    final ProbeResult probe = executeStatusProbe( authData );
    return mapStatusToValidity( probe );
  }

  /**
   * Returns {@code true} ONLY when the server explicitly rejects the SSO session
   * with HTTP 401/403 on TWO consecutive probes (with a 500 ms pause between).
   * Returns {@code false} for any "valid" response, any "unknown" result
   * (network error, timeout, etc.), and for sessions that are not session-based
   * (i.e. username/password auth where credentials are sent on every request).
   * <p>
   * This is the conservative check used by the Open / Publish actions to
   * decide whether to show the "Session Expired – Login Again" dialog
   * <em>before</em> opening their file-browser dialog. The retry tolerates
   * the brief window right after SSO login where the JSESSIONID may not yet
   * be propagated server-side, which would otherwise show the dialog
   * immediately after a successful login.
   *
   * @param authData the cached session to test
   * @return {@code true} only on confirmed expiry; {@code false} otherwise
   */
  public static boolean isSessionExplicitlyExpired( final AuthenticationData authData ) {
    if ( authData == null || !isSessionBased( authData ) ) {
      return false;
    }
    for ( int attempt = 0; attempt < 2; attempt++ ) {
      final Boolean result = checkSessionValidity( authData );
      if ( !Boolean.FALSE.equals( result ) ) {
        return false; // valid or unknown -> not "explicitly" expired
      }
      if ( attempt == 0 ) {
        try {
          Thread.sleep( 500L );
        } catch ( InterruptedException ie ) {
          Thread.currentThread().interrupt();
          return false;
        }
      }
    }
    return true;
  }

  @SuppressWarnings( "java:S2447" )
  static Boolean mapStatusToValidity( final ProbeResult probe ) {
    if ( probe == null || probe.status == null ) {
      return null;
    }
    final int status = probe.status;
    if ( status == 401 || status == 403 ) {
      return Boolean.FALSE;
    }
    if ( status >= 300 && status < 400 ) {
      // A 3xx is only a session-expired signal when the redirect target is an
      // unmistakable login / OAuth / SAML start endpoint. SSO-aware Pentaho
      // servers also redirect successful requests for many other reasons
      // (trailing-slash normalisation, default landing page like /Login,
      // CSRF token issuance, etc.). Treating those as "session invalid" causes
      // a spurious "login again" dialog right after a successful SSO login,
      // so we err on the side of TRUE (valid) when in doubt -- the actual
      // API call that follows will surface a real 401 if the session is bad.
      return isUnambiguousLoginRedirect( probe.location ) ? Boolean.FALSE : Boolean.TRUE;
    }
    if ( status >= 200 && status < 300 ) {
      return Boolean.TRUE;
    }
    return null;
  }

  private static boolean isUnambiguousLoginRedirect( final String location ) {
    if ( location == null || location.isEmpty() ) {
      return false;
    }
    final String lower = location.toLowerCase( java.util.Locale.ROOT );
    return lower.contains( "j_spring_security_check" )
        || lower.contains( "/oauth2/authorization" )
        || lower.contains( "/saml2/authenticate" )
        || lower.contains( "/cas/login" )
        || lower.contains( "openid-connect" );
  }

  static final class ProbeResult {
    final Integer status;
    final String location;

    ProbeResult( final Integer status, final String location ) {
      this.status = status;
      this.location = location;
    }
  }

  private static ProbeResult executeStatusProbe( final AuthenticationData authData ) {
    try (
      CloseableHttpClient client =
        (CloseableHttpClient) createNoRedirectClient( authData, VALIDATION_TIMEOUT_MS )
    ) {
      final HttpGet request = new HttpGet( buildCheckUrl( authData.getUrl() ) );
      request.setHeader( "Accept", "application/json" );
      final HttpResponse response = client.execute( request );
      try {
        final int status = response.getStatusLine().getStatusCode();
        String location = null;
        if ( status >= 300 && status < 400 && response.getFirstHeader( "Location" ) != null ) {
          location = response.getFirstHeader( "Location" ).getValue();
        }
        return new ProbeResult( status, location );
      } finally {
        EntityUtils.consumeQuietly( response.getEntity() );
      }
    } catch ( Exception e ) {
      return null;
    }
  }

  private static String buildCheckUrl( final String baseUrl ) {
    String url = baseUrl;
    if ( url.endsWith( "/" ) ) {
      url = url.substring( 0, url.length() - 1 );
    }
    return url + SESSION_CHECK_PATH;
  }
}

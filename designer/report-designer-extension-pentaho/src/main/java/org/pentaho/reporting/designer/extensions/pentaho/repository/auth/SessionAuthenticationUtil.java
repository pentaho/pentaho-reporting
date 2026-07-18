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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for session-based HTTP authentication and session validity checking.
 */
public final class SessionAuthenticationUtil {

  static final String SESSION_CHECK_PATH_PROPERTY =
    "org.pentaho.reporting.designer.extensions.pentaho.repository.auth.sessionCheckPath";
  private static final String OPTION_SESSION_ID = "sessionId";
  private static final String COOKIE_HEADER = "Cookie";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final int VALIDATION_TIMEOUT_MS = 5000;
  @SuppressWarnings( "java:S1075" )
  private static final String DEFAULT_SESSION_CHECK_PATH = "/api/repo/files/tree?depth=0";
  private static final String SESSION_CHECK_PATH =
    System.getProperty( SESSION_CHECK_PATH_PROPERTY, DEFAULT_SESSION_CHECK_PATH );

  private SessionAuthenticationUtil() {
  }

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

  public static boolean isSessionBased( AuthenticationData authData ) {
    if ( authData == null ) {
      return false;
    }
    String sessionId = authData.getOption( OPTION_SESSION_ID );
    return sessionId != null && !sessionId.isEmpty();
  }

  public static boolean isServerReachable( AuthenticationData authData ) {
    if ( authData == null || authData.getUrl() == null ) {
      return false;
    }
    final ProbeResult probe = executeStatusProbe( authData );
    return probe != null && probe.status != null && probe.status >= 200 && probe.status < 300;
  }

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

  static final class ProbeResult {
    final Integer status;
    final String location;

    ProbeResult( final Integer status, final String location ) {
      this.status = status;
      this.location = location;
    }
  }
}

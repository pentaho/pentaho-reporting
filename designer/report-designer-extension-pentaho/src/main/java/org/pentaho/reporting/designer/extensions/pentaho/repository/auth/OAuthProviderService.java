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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service that fetches available OAuth providers from the Pentaho server.
 */
public class OAuthProviderService {
  private static final Log logger = LogFactory.getLog( OAuthProviderService.class );

  private OAuthProviderService() {
  }

  @SuppressWarnings( "java:S1075" )
  private static final String OAUTH_PROVIDERS_PATH = "/plugin/login/api/v0/oauth-providers";
  @SuppressWarnings( "java:S1075" )
  private static final String SYSTEM_SETTINGS_PATH = "/plugin/login/api/v0/system-settings";
  private static final int CONNECT_TIMEOUT_MS = 5_000;
  private static final int READ_TIMEOUT_MS = 5_000;

  private static HttpURLConnection openConnection( String urlStr ) throws IOException {
    return (HttpURLConnection) URI.create( urlStr ).toURL().openConnection();
  }

  @FunctionalInterface
  private interface ResponseReader<T> {
    T read( HttpURLConnection connection ) throws IOException;
  }

  private static <T> T doExchange( final String urlStr, final String method, final boolean sendAcceptJson,
                                   final ResponseReader<T> reader ) throws IOException {
    HttpURLConnection connection = null;
    try {
      connection = openConnection( urlStr );
      connection.setRequestMethod( method );
      connection.setConnectTimeout( CONNECT_TIMEOUT_MS );
      connection.setReadTimeout( READ_TIMEOUT_MS );
      if ( sendAcceptJson ) {
        connection.setRequestProperty( "Accept", "application/json" );
      }
      return reader.read( connection );
    } finally {
      if ( connection != null ) {
        connection.disconnect();
      }
    }
  }

  static String normalizeBaseUrl( String serverUrl ) {
    return serverUrl.endsWith( "/" )
      ? serverUrl.substring( 0, serverUrl.length() - 1 )
      : serverUrl;
  }

  static String buildProvidersUrl( String serverUrl ) {
    return normalizeBaseUrl( serverUrl ) + OAUTH_PROVIDERS_PATH;
  }
  /**
   * Determines whether OAuth/SSO authentication is enabled on the server.
   *
   * @return {@code true} if OAuth is enabled; {@code false} otherwise.
   */
  public static boolean isOAuthEnabled( String serverUrl ) {
    if ( serverUrl == null || serverUrl.trim().isEmpty() ) {
      return false;
    }

    String settingsUrl = normalizeBaseUrl( serverUrl ) + SYSTEM_SETTINGS_PATH;
    try {
      return Boolean.TRUE.equals( doExchange( settingsUrl, "GET", true, connection -> {
        int responseCode = connection.getResponseCode();
        if ( responseCode < 200 || responseCode >= 300 ) {
          return Boolean.FALSE;
        }
        return parseOAuthEnabled( readResponseBody( connection ) );
      } ) );
    } catch ( Exception e ) {
      logger.debug( "Failed to check OAuth enabled status from " + settingsUrl, e );
      return false;
    }
  }

  static boolean parseOAuthEnabled( String json ) {
    if ( json == null || json.trim().isEmpty() ) {
      return false;
    }
    json = json.trim();
    if ( !json.startsWith( "{" ) || !json.endsWith( "}" ) ) {
      return false;
    }
    // Look for "isOAuthEnabled" key
    String boolStr = extractJsonString( json, "isOAuthEnabled" );
    if ( boolStr != null ) {
      return "true".equalsIgnoreCase( boolStr.trim() );
    }
    // Also check for bare boolean value (not quoted)
    String searchKey = "\"isOAuthEnabled\"";
    int keyIdx = json.indexOf( searchKey );
    if ( keyIdx < 0 ) {
      return false;
    }
    int colonIdx = json.indexOf( ':', keyIdx + searchKey.length() );
    if ( colonIdx < 0 ) {
      return false;
    }
    String remainder = json.substring( colonIdx + 1 ).trim();
    return remainder.startsWith( "true" );
  }

  private static String readResponseBody( HttpURLConnection connection ) throws IOException {
    StringBuilder sb = new StringBuilder();
    try ( BufferedReader reader = new BufferedReader(
      new InputStreamReader( connection.getInputStream(), StandardCharsets.UTF_8 ) ) ) {
      String line;
      while ( ( line = reader.readLine() ) != null ) {
        sb.append( line );
      }
    }
    return sb.toString();
  }

  public static List<OAuthProvider> fetchProviders( String serverUrl ) throws IOException {
    if ( serverUrl == null || serverUrl.trim().isEmpty() ) {
      return Collections.emptyList();
    }

    String providersUrl = buildProvidersUrl( serverUrl );
    logger.info( "Fetching OAuth providers from: " + providersUrl );

    return doExchange( providersUrl, "GET", true, connection -> {
      int responseCode = connection.getResponseCode();
      if ( responseCode < 200 || responseCode >= 300 ) {
        throw new IOException( "Server returned HTTP " + responseCode );
      }
      String jsonBody = readResponseBody( connection );
      if ( !isJsonArrayResponse( jsonBody ) ) {
        throw new IOException( "Unexpected OAuth providers response" );
      }
      return parseProviders( jsonBody );
    } );
  }

  public static boolean isProvidersEndpointAvailable( final String serverUrl ) {
    if ( serverUrl == null || serverUrl.trim().isEmpty() ) {
      return false;
    }
    final String providersUrl = buildProvidersUrl( serverUrl );
    try {
      // Treat 404 as OAuth unavailable; all other responses indicate support exists.
      return Boolean.TRUE.equals( doExchange( providersUrl, "HEAD", false, connection ->
        connection.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND ) );
    } catch ( Exception e ) {
      logger.debug( "Could not probe providers endpoint at " + providersUrl, e );
      return false;
    }
  }

  static List<OAuthProvider> parseProviders( String json ) {
    List<OAuthProvider> providers = new ArrayList<>();
    if ( json == null || json.trim().isEmpty() ) {
      return providers;
    }

    json = json.trim();
    if ( !json.startsWith( "[" ) || !json.endsWith( "]" ) || json.length() < 2 ) {
      logger.warn( "Unexpected JSON response (not a well-formed array): "
        + json.substring( 0, Math.min( 100, json.length() ) ) );
      return providers;
    }

    String inner = json.substring( 1, json.length() - 1 ).trim();
    if ( inner.isEmpty() ) {
      return providers;
    }

    List<String> objectStrings = splitJsonObjects( inner );

    for ( String objStr : objectStrings ) {
      try {
        OAuthProvider provider = parseProvider( objStr.trim() );
        if ( provider.isEnabled() ) {
          providers.add( provider );
        }
      } catch ( Exception e ) {
        logger.warn( "Failed to parse OAuth provider entry: " + objStr, e );
      }
    }

    return providers;
  }

  static boolean isJsonArrayResponse( final String json ) {
    if ( json == null ) {
      return false;
    }
    final String trimmed = json.trim();
    return trimmed.startsWith( "[" ) && trimmed.endsWith( "]" );
  }

  static List<String> splitJsonObjects( String inner ) {
    List<String> objects = new ArrayList<>();
    int depth = 0;
    int start = 0;
    for ( int i = 0; i < inner.length(); i++ ) {
      char c = inner.charAt( i );
      if ( c == '{' ) {
        if ( depth == 0 ) {
          start = i;
        }
        depth++;
      } else if ( c == '}' ) {
        depth--;
        if ( depth == 0 ) {
          objects.add( inner.substring( start, i + 1 ) );
        }
      }
    }
    return objects;
  }

  static OAuthProvider parseProvider( String objStr ) {
    OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri( extractJsonString( objStr, "authorizationUri" ) );
    provider.setImageUri( extractJsonString( objStr, "imageUri" ) );
    provider.setClientName( extractJsonString( objStr, "clientName" ) );
    provider.setRegistrationId( extractJsonString( objStr, "registrationId" ) );
    provider.setEnabled( extractJsonBoolean( objStr, "enabled" ) );
    return provider;
  }

  static String extractJsonString( String json, String key ) {
    String searchKey = "\"" + key + "\"";
    int keyIdx = json.indexOf( searchKey );
    if ( keyIdx < 0 ) {
      return null;
    }
    int colonIdx = json.indexOf( ':', keyIdx + searchKey.length() );
    if ( colonIdx < 0 ) {
      return null;
    }
    int quoteStart = json.indexOf( '"', colonIdx + 1 );
    if ( quoteStart < 0 ) {
      return null;
    }
    int quoteEnd = quoteStart + 1;
    while ( quoteEnd < json.length() ) {
      if ( json.charAt( quoteEnd ) == '"' && json.charAt( quoteEnd - 1 ) != '\\' ) {
        break;
      }
      quoteEnd++;
    }
    if ( quoteEnd >= json.length() ) {
      return null;
    }
    return json.substring( quoteStart + 1, quoteEnd );
  }

  static boolean extractJsonBoolean( String json, String key ) {
    String searchKey = "\"" + key + "\"";
    int keyIdx = json.indexOf( searchKey );
    if ( keyIdx < 0 ) {
      return false;
    }
    int colonIdx = json.indexOf( ':', keyIdx + searchKey.length() );
    if ( colonIdx < 0 ) {
      return false;
    }
    String remainder = json.substring( colonIdx + 1 ).trim();
    return remainder.startsWith( "true" );
  }
}

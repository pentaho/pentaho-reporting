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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service that fetches available SSO / OAuth providers from the Pentaho server.
 * Calls the {@code /plugin/login/api/v0/oauth-providers} endpoint and parses
 * the JSON array response into a list of {@link OAuthProvider} objects.
 * <p>
 * Mirrors the same approach used by PDI's {@code SsoProviderService} — standard
 * {@link HttpURLConnection} with no external HTTP-client dependency.
 */
public class OAuthProviderService {
  private static final Log logger = LogFactory.getLog( OAuthProviderService.class );

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private OAuthProviderService() {
    // Utility class - static methods only
  }

  @SuppressWarnings( "java:S1075" )
  private static final String OAUTH_PROVIDERS_PATH = "/plugin/login/api/v0/oauth-providers";
  private static final int CONNECT_TIMEOUT_MS = 10_000;
  private static final int READ_TIMEOUT_MS = 10_000;

  /**
   * Builds the full URL for the OAuth-providers REST endpoint.
   *
   * @param serverUrl The base Pentaho server URL (e.g. {@code http://localhost:8080/pentaho})
   * @return The full provider-lookup URL
   */
  static String buildProvidersUrl( String serverUrl ) {
    String baseUrl = serverUrl.endsWith( "/" )
      ? serverUrl.substring( 0, serverUrl.length() - 1 )
      : serverUrl;
    return baseUrl + OAUTH_PROVIDERS_PATH;
  }

  /**
   * Fetches enabled OAuth providers from the server's REST endpoint.
   *
   * @param serverUrl base Pentaho server URL
   * @return enabled providers; empty if server is reachable but none configured
   * @throws IOException on connection failure or non-2xx response, so callers
   *                     can distinguish "no providers" from "wrong URL"
   */
  public static List<OAuthProvider> fetchProviders( String serverUrl ) throws IOException {
    if ( serverUrl == null || serverUrl.trim().isEmpty() ) {
      return Collections.emptyList();
    }

    String providersUrl = buildProvidersUrl( serverUrl );
    logger.info( "Fetching OAuth providers from: " + providersUrl );

    HttpURLConnection connection = null;
    try {
      @SuppressWarnings( "java:S1874" )
      HttpURLConnection tmpConnection = (HttpURLConnection) new URL( providersUrl ).openConnection();
      connection = tmpConnection;
      connection.setRequestMethod( "GET" );
      connection.setConnectTimeout( CONNECT_TIMEOUT_MS );
      connection.setReadTimeout( READ_TIMEOUT_MS );
      connection.setRequestProperty( "Accept", "application/json" );

      int responseCode = connection.getResponseCode();

      if ( responseCode < 200 || responseCode >= 300 ) {
        throw new IOException( "Server returned HTTP " + responseCode );
      }

      String jsonBody = readResponseBody( connection );
      return parseProviders( jsonBody );
    } finally {
      if ( connection != null ) {
        connection.disconnect();
      }
    }
  }

  /**
   * Reads the response body from a {@link HttpURLConnection} as a string.
   */
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

  /**
   * Simple JSON array parser for OAuth provider objects.
   * Parses a JSON array of the form:
   * <pre>
   * [
   *   {
   *     "authorizationUri": "oauth2/authorization/azure",
   *     "imageUri": "https://...",
   *     "clientName": "Microsoft",
   *     "registrationId": "azure",
   *     "enabled": true
   *   },
   *   ...
   * ]
   * </pre>
   * Uses simple string parsing to avoid requiring a JSON library dependency.
   */
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

    // Split objects - handle nested braces are not expected in this simple schema
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

  /**
   * Splits a string containing JSON objects separated by commas.
   */
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
    // Find the colon after the key
    int colonIdx = json.indexOf( ':', keyIdx + searchKey.length() );
    if ( colonIdx < 0 ) {
      return null;
    }
    // Find the opening quote of the value
    int quoteStart = json.indexOf( '"', colonIdx + 1 );
    if ( quoteStart < 0 ) {
      return null;
    }
    // Find the closing quote (handle escaped quotes)
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
    // Look for true/false after the colon
    String remainder = json.substring( colonIdx + 1 ).trim();
    return remainder.startsWith( "true" );
  }
}

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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Handles browser-based authentication flow for Pentaho Server.
 * Opens the system browser, starts a local callback server, and retrieves the session ID.
 */
public class BrowserLoginHandler {
  private static final Log logger = LogFactory.getLog( BrowserLoginHandler.class );
  private static final int CALLBACK_PORT = 8183;
  private static final String CALLBACK_PATH = "/pentaho-auth-callback";
  private static final int TIMEOUT_SECONDS = 300; // 5 minutes
  private static final int TIMEOUT_MILLIS = TIMEOUT_SECONDS * 1000;
  private static final int DEFAULT_TIMEOUT = 30000; // 30 seconds in milliseconds

  private HttpServer callbackServer;
  private CompletableFuture<SessionData> sessionFuture;

  /**
   * Represents the session data received from the browser authentication.
   */
  public static class SessionData {
    private final String sessionId;
    private final String username;

    public SessionData( String sessionId, String username ) {
      this.sessionId = sessionId;
      this.username = username;
    }

    public String getSessionId() {
      return sessionId;
    }

    public String getUsername() {
      return username;
    }
  }

  /**
   * Initiates the browser-based login flow.
   *
   * @param pentahoServerUrl The base URL of the Pentaho server (e.g., http://localhost:8080/pentaho)
   * @return SessionData containing session ID and username, or null if login failed/cancelled
   * @throws IOException If there's an error starting the callback server or opening the browser
   */
  public SessionData performBrowserLogin( String pentahoServerUrl ) throws IOException {
    // Ensure clean state
    cleanup();

    // Initialize the future for session data
    sessionFuture = new CompletableFuture<>();

    // Start local callback server (binds to all interfaces)
    startCallbackServer();

    // Use the same host that the user typed into PRD for the Pentaho server URL.
    // This way, if the user connects via http://127.0.0.1:8080/pentaho the callback
    // is http://127.0.0.1:8183/... (which security.properties already allows),
    // rather than the machine's hostname which the server may reject.
    String hostname = resolveCallbackHost( pentahoServerUrl );
    String callbackUrl = "http://" + hostname + ":" + CALLBACK_PORT + CALLBACK_PATH;
    String authUrl = buildAuthUrl( pentahoServerUrl, callbackUrl );

    // Open browser
    openBrowser( authUrl );

    try {
      // Wait for callback with timeout
      SessionData sessionData = sessionFuture.get( TIMEOUT_MILLIS, TimeUnit.MILLISECONDS );
      return sessionData;
    } catch ( Exception e ) {
      logger.error( "Browser login failed or timed out", e );
      return null;
    } finally {
      cleanup();
    }
  }

  /**
   * Wrapper method that performs browser login and returns AuthenticationData.
   * This is the method to call from LoginTask.
   *
   * @param pentahoServerUrl The base URL of the Pentaho server
   * @return AuthenticationData with session information, or null if login failed/cancelled
   */
  public AuthenticationData startBrowserLogin( String pentahoServerUrl ) {
    try {
      SessionData sessionData = performBrowserLogin( pentahoServerUrl );
      if ( sessionData == null ) {
        return null;
      }

      // Create AuthenticationData with session information
      // Use username from session, but no password (empty string)
      // Timeout in constructor is in milliseconds
      AuthenticationData authData = new AuthenticationData( 
          pentahoServerUrl, 
          sessionData.getUsername(), 
          "", // No password for browser auth
          DEFAULT_TIMEOUT
      );
      
      // Store session ID and mark as browser authentication
      authData.setOption( "sessionId", sessionData.getSessionId() );
      authData.setOption( "browserAuth", "true" );
      
      // Set timeout option (in seconds for PublishUtil)
      authData.setOption( "timeout", "30" );
      
      // Set default server version (Pentaho 5.0+)
      authData.setOption( "server-version", "5" );
      
      return authData;
    } catch ( IOException e ) {
      logger.error( "Failed to perform browser login", e );
      return null;
    }
  }

  /**
   * Builds the authentication URL for the Pentaho server.
   * This URL should redirect to the auth callback page after successful login.
   */
  private String buildAuthUrl( String pentahoServerUrl, String callbackUrl ) {
    // Remove trailing slash if present
    if ( pentahoServerUrl.endsWith( "/" ) ) {
      pentahoServerUrl = pentahoServerUrl.substring( 0, pentahoServerUrl.length() - 1 );
    }

    // Build URL to servlet-based auth endpoint
    return pentahoServerUrl + "/plugin/browser-auth/api/login?callback=" + 
           java.net.URLEncoder.encode( callbackUrl, StandardCharsets.UTF_8 );
  }

  /**
   * Returns the host to use in the callback URL by extracting it from the
   * Pentaho server URL the user configured in PRD.
   * <p>
   * For example, if the user logs in via {@code http://127.0.0.1:8080/pentaho}
   * the callback will use {@code 127.0.0.1}, which already appears in the
   * server's {@code security.properties} allowed-hosts list without any extra
   * configuration.  Falls back to the local machine's best address when the
   * URL cannot be parsed.
   * <p>
   * Package-visible to allow verification in tests.
   */
  String resolveCallbackHost( String baseUrl ) {
    try {
      URI uri = URI.create( baseUrl );
      if ( uri.getHost() != null && !uri.getHost().isEmpty() ) {
        return uri.getHost();
      }
    } catch ( Exception e ) {
      logger.debug( "Could not parse server URL host, falling back to local callback host", e );
    }
    return getLocalCallbackHost();
  }

  /**
   * Returns the best reachable address of this machine as a fallback when the
   * server URL host cannot be determined.  Prefers a non-loopback address;
   * falls back to {@code localhost}.
   * <p>
   * Package-visible to allow overriding in tests.
   */
  String getLocalCallbackHost() {
    try {
      InetAddress address = InetAddress.getLocalHost();
      if ( !address.isLoopbackAddress() ) {
        return address.getHostAddress();
      }
    } catch ( Exception e ) {
      logger.debug( "Could not resolve local host address, falling back to localhost", e );
    }
    return "localhost";
  }

  /**
   * Starts the local HTTP server to receive the callback.
   * Binds to all interfaces (0.0.0.0) so callbacks arriving via any
   * hostname / IP address of this machine are accepted.
   */
  private void startCallbackServer() throws IOException {
    callbackServer = HttpServer.create( new InetSocketAddress( "0.0.0.0", CALLBACK_PORT ), 0 );
    callbackServer.createContext( CALLBACK_PATH, new CallbackHandler() );
    callbackServer.setExecutor( null ); // Use default executor
    callbackServer.start();
  }

  /**
   * Opens the system's default browser with the given URL.
   */
  private void openBrowser( String url ) throws IOException {
    if ( Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported( Desktop.Action.BROWSE ) ) {
      Desktop.getDesktop().browse( URI.create( url ) );
    } else {
      // Fallback for systems without Desktop support
      String os = System.getProperty( "os.name" ).toLowerCase();
      if ( os.contains( "win" ) ) {
        Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler " + url );
      } else if ( os.contains( "mac" ) ) {
        Runtime.getRuntime().exec( "open " + url );
      } else if ( os.contains( "nix" ) || os.contains( "nux" ) ) {
        Runtime.getRuntime().exec( "xdg-open " + url );
      }
    }
  }

  /**
   * Cleans up resources.
   */
  private void cleanup() {
    if ( callbackServer != null ) {
      callbackServer.stop( 1 ); // Wait 1 second for pending exchanges to finish
      callbackServer = null;
    }
    if ( sessionFuture != null && !sessionFuture.isDone() ) {
      sessionFuture.cancel( true );
    }
  }

  /**
   * Handles the HTTP callback from the browser.
   */
  private class CallbackHandler implements HttpHandler {
    @Override
    public void handle( HttpExchange exchange ) throws IOException {
      String method = exchange.getRequestMethod();
      
      if ( "GET".equalsIgnoreCase( method ) ) {
        handleGetCallback( exchange );
      } else if ( "POST".equalsIgnoreCase( method ) ) {
        handlePostCallback( exchange );
      } else {
        sendResponse( exchange, 405, "Method Not Allowed" );
      }
    }

    private void handleGetCallback( HttpExchange exchange ) throws IOException {
      // Parse query parameters
      String query = exchange.getRequestURI().getQuery();
      Map<String, String> params = parseQueryParams( query );

      // Server sends 'jsessionid' not 'sessionId'
      String sessionId = params.get( "jsessionid" );
      if ( sessionId == null ) {
        sessionId = params.get( "sessionId" ); // fallback
      }
      
      String username = params.get( "username" );
      String error = params.get( "error" );

      if ( error != null && !error.isEmpty() ) {
        // Login failed or was cancelled
        logger.error( "Authentication error received: " + error );
        sendResponse( exchange, 400, buildErrorPage( error ) );
        sessionFuture.completeExceptionally( new Exception( "Authentication failed: " + error ) );
        return;
      }

      if ( sessionId == null || sessionId.isEmpty() ) {
        // Missing session ID
        logger.error( "No JSESSIONID received in callback" );
        sendResponse( exchange, 400, buildErrorPage( "No session ID received" ) );
        sessionFuture.completeExceptionally( new Exception( "No JSESSIONID received" ) );
        return;
      }

      // Login successful — send response BEFORE completing the future.
      // This prevents a race condition where the blocking thread calls cleanup()
      // (which stops the server) before the response is fully sent.
      sendResponse( exchange, 200, buildSuccessPage() );
      SessionData sessionData = new SessionData( sessionId, username );
      logger.info( "Successfully received session info for user: " + username );
      sessionFuture.complete( sessionData );
    }

    private void handlePostCallback( HttpExchange exchange ) throws IOException {
      // For POST, read the body (could be JSON or form data)
      // For simplicity, treating it similar to GET for now
      handleGetCallback( exchange );
    }

    private Map<String, String> parseQueryParams( String query ) {
      Map<String, String> params = new HashMap<>();
      if ( query != null && !query.isEmpty() ) {
        String[] pairs = query.split( "&" );
        for ( String pair : pairs ) {
          String[] keyValue = pair.split( "=", 2 );
          if ( keyValue.length == 2 ) {
            String key = URLDecoder.decode( keyValue[0], StandardCharsets.UTF_8 );
            String value = URLDecoder.decode( keyValue[1], StandardCharsets.UTF_8 );
            params.put( key, value );
          }
        }
      }
      return params;
    }

    private void sendResponse( HttpExchange exchange, int statusCode, String htmlContent ) throws IOException {
      byte[] response = htmlContent.getBytes( StandardCharsets.UTF_8 );
      exchange.getResponseHeaders().set( "Content-Type", "text/html; charset=UTF-8" );
      exchange.sendResponseHeaders( statusCode, response.length );
      try ( OutputStream os = exchange.getResponseBody() ) {
        os.write( response );
      }
    }

    private String buildSuccessPage() {
      return "<!DOCTYPE html>\n" +
             "<html>\n" +
             "<head>\n" +
             "  <title>Authentication Successful</title>\n" +
             "  <style>\n" +
             "    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }\n" +
             "    .success { color: #4CAF50; font-size: 24px; }\n" +
             "  </style>\n" +
             "</head>\n" +
             "<body>\n" +
             "  <h1 class=\"success\">✓ Authentication Successful</h1>\n" +
             "  <p>You have successfully logged in to Pentaho Server.</p>\n" +
             "  <p>You can close this window and return to Report Designer.</p>\n" +
             "</body>\n" +
             "</html>";
    }

    private String buildErrorPage( String errorMessage ) {
      return "<!DOCTYPE html>\n" +
             "<html>\n" +
             "<head>\n" +
             "  <title>Authentication Failed</title>\n" +
             "  <style>\n" +
             "    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }\n" +
             "    .error { color: #f44336; font-size: 24px; }\n" +
             "  </style>\n" +
             "</head>\n" +
             "<body>\n" +
             "  <h1 class=\"error\">✗ Authentication Failed</h1>\n" +
             "  <p>Error: " + errorMessage + "</p>\n" +
             "  <p>You can close this window and try again in Report Designer.</p>\n" +
             "</body>\n" +
             "</html>";
    }
  }
}

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
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

import java.awt.Component;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

/**
 * Handles browser-based authentication flow for Pentaho Server.
 * Opens the system browser, starts a local callback server, and retrieves the session ID.
 */
public class BrowserLoginHandler {
  private static final Log logger = LogFactory.getLog( BrowserLoginHandler.class );
  private static final int CALLBACK_PORT = 8183;
  private static final String CALLBACK_PATH = "/pentaho-auth-callback";
  private static final int TIMEOUT_SECONDS = 300; // 5 minutes
  private static final int DEFAULT_TIMEOUT = 30000; // 30 seconds in milliseconds
  private static final String PARAM_AUTHORIZATION_URI = "authorizationUri";

  private static final int MAX_SESSION_VERIFY_ATTEMPTS = 3;

  private static final long SESSION_VERIFY_RETRY_DELAY_MS = 800L;

  private HttpServer callbackServer;
  private CompletableFuture<SessionData> sessionFuture;
  private OAuthProvider oauthProvider;
  private String callbackState;

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
   * Sets the OAuth provider to use for browser-based login.
   * When set, the browser will be directed to the provider's authorization URI
   * instead of the default browser-auth endpoint.
   *
   * @param provider The OAuth provider, or null for default SSO flow
   */
  public void setOAuthProvider( OAuthProvider provider ) {
    this.oauthProvider = provider;
  }

  /**
   * Initiates the browser-based login flow.
   *
   * @param pentahoServerUrl The base URL of the Pentaho server (e.g., http://localhost:8080/pentaho)
   * @return SessionData containing session ID and username, or null if login failed/cancelled
   * @throws IOException If there's an error starting the callback server or opening the browser
   */
  public SessionData performBrowserLogin( String pentahoServerUrl ) throws IOException {
    cleanup();

    sessionFuture = new CompletableFuture<>();

    startCallbackServer();

    callbackState = UUID.randomUUID().toString();
    String callbackHost = resolveCallbackHost( pentahoServerUrl );
    String callbackUrl = "http://" + callbackHost + ":" + CALLBACK_PORT + CALLBACK_PATH
      + "?state=" + java.net.URLEncoder.encode( callbackState, StandardCharsets.UTF_8 );
    String authUrl = buildAuthUrl( pentahoServerUrl, callbackUrl );

    openBrowser( authUrl );

    try {
      return sessionFuture.get( TIMEOUT_SECONDS, TimeUnit.SECONDS );
    } catch ( InterruptedException e ) {
      Thread.currentThread().interrupt();
      logger.error( "Browser login interrupted", e );
      return null;
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

      AuthenticationData authData = new AuthenticationData( 
          pentahoServerUrl, 
          sessionData.getUsername(), 
          "",
          DEFAULT_TIMEOUT
      );
      
      authData.setOption( "sessionId", sessionData.getSessionId() );
      authData.setOption( "browserAuth", "true" );

      if ( oauthProvider != null ) {
        if ( oauthProvider.getAuthorizationUri() != null ) {
          authData.setOption( "oauthAuthorizationUri", oauthProvider.getAuthorizationUri() );
        }
        if ( oauthProvider.getClientName() != null ) {
          authData.setOption( "oauthClientName", oauthProvider.getClientName() );
        }
        if ( oauthProvider.getRegistrationId() != null ) {
          authData.setOption( "oauthRegistrationId", oauthProvider.getRegistrationId() );
        }
      }

      authData.setOption( "timeout", "30" );
      authData.setOption( "server-version", "5" );

      if ( !verifySessionWithRetry( authData ) ) {
        logger.warn( "SSO session from callback was rejected by the server after "
          + MAX_SESSION_VERIFY_ATTEMPTS + " verification attempt(s). "
          + "The JSESSIONID from the browser-auth callback may be invalid or stale." );
        return null;
      }

      return authData;
    } catch ( IOException e ) {
      logger.error( "Failed to perform browser login", e );
      return null;
    }
  }

  /**
   * Verifies the SSO session via the server's REST API, retrying up to
   * {@link #MAX_SESSION_VERIFY_ATTEMPTS} times with a
   * {@link #SESSION_VERIFY_RETRY_DELAY_MS} ms delay to absorb session-propagation lag.
   * Returns {@code false} only on explicit 401/403 on every attempt; network
   * failures (null result) are treated as unknown and yield {@code true}.
   *
   * @param authData authentication data with the SSO session ID
   * @return {@code true} if valid or unverifiable due to network errors;
   *         {@code false} on repeated 401/403 rejections
   */
  boolean verifySessionWithRetry( final AuthenticationData authData ) {
    boolean hadExplicitAuthFailure = false;
    for ( int attempt = 1; attempt <= MAX_SESSION_VERIFY_ATTEMPTS; attempt++ ) {
      final Boolean result = SessionAuthenticationUtil.checkSessionValidity( authData );
      // Three-state check: TRUE -> valid, FALSE -> explicit 401/403, null -> unknown (e.g. network).
      if ( result == null ) {
        logger.debug( "SSO session verification attempt " + attempt + "/" + MAX_SESSION_VERIFY_ATTEMPTS
          + " could not reach server (connection error) \u2014 treating as unknown" );
      } else if ( result.booleanValue() ) {
        return true; // session confirmed valid
      } else {
        hadExplicitAuthFailure = true;
        logger.debug( "SSO session verification attempt " + attempt + "/" + MAX_SESSION_VERIFY_ATTEMPTS
          + " returned 401/403 \u2014 will retry after " + SESSION_VERIFY_RETRY_DELAY_MS + " ms" );
      }
      if ( attempt < MAX_SESSION_VERIFY_ATTEMPTS ) {
        try {
          Thread.sleep( SESSION_VERIFY_RETRY_DELAY_MS );
        } catch ( InterruptedException ie ) {
          Thread.currentThread().interrupt();
          return false;
        }
      }
    }
    return !hadExplicitAuthFailure;
  }

  /**
   * Reconstructs an {@link OAuthProvider} from the {@code oauthAuthorizationUri},
   * {@code oauthClientName} and {@code oauthRegistrationId} options stored on a
   * prior browser-auth {@link AuthenticationData}, so re-login can skip the
   * provider selection dialog.
   *
   * @param data previous (expired) authentication data, or {@code null}
   * @return reconstructed provider, or {@code null} if not a browser-auth session
   */
  public static OAuthProvider recoverOAuthProvider( AuthenticationData data ) {
    if ( data == null ) {
      return null;
    }
    String browserAuth = data.getOption( "browserAuth" );
    String authUri = data.getOption( "oauthAuthorizationUri" );
    if ( !"true".equals( browserAuth ) || authUri == null || authUri.trim().isEmpty() ) {
      return null;
    }
    OAuthProvider provider = new OAuthProvider();
    provider.setAuthorizationUri( authUri );
    provider.setClientName( data.getOption( "oauthClientName" ) );
    provider.setRegistrationId( data.getOption( "oauthRegistrationId" ) );
    provider.setEnabled( true );
    return provider;
  }

  /**
   * Runs browser-based SSO login on a background thread with a cancel dialog,
   * and prompts Retry/Cancel on failure. Shared by {@code ConnectToRepositoryAction}
   * and {@code LoginTask} to avoid duplicating the retry/UI loop.
   *
   * @param uiContext     parent component for dialogs
   * @param serverUrl     Pentaho server URL (non-null)
   * @param oauthProvider OAuth provider, or {@code null} for default SSO
   * @return session data on success, or {@code null} if cancelled/failed
   */
  public static AuthenticationData performBrowserLoginWithRetry(
      final Component uiContext, final String serverUrl, final OAuthProvider oauthProvider ) {
    while ( true ) {
      final AuthenticationData[] result = new AuthenticationData[1];
      final Thread loginThread = new Thread( () -> {
        BrowserLoginHandler handler = new BrowserLoginHandler();
        if ( oauthProvider != null ) {
          handler.setOAuthProvider( oauthProvider );
        }
        result[0] = handler.startBrowserLogin( serverUrl );
      } );
      loginThread.setDaemon( true );
      loginThread.setName( "BrowserLoginThread" );

      final GenericCancelHandler cancelHandler = new GenericCancelHandler( loginThread );
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          loginThread, cancelHandler, uiContext,
          Messages.getInstance().getString( "LoginTask.BrowserLogin.WaitingMessage" ) );

      if ( cancelHandler.isCancelled() ) {
        return null;
      }
      if ( result[0] != null ) {
        return result[0];
      }

      final int choice = JOptionPane.showOptionDialog( uiContext,
          Messages.getInstance().getString( "LoginTask.BrowserLogin.Failed.Message" ),
          Messages.getInstance().getString( "LoginTask.BrowserLogin.Error.Title" ),
          JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
          new String[] {
              Messages.getInstance().getString( "LoginTask.BrowserLogin.Retry" ),
              Messages.getInstance().getString( "LoginTask.BrowserLogin.Cancel" )
          },
          Messages.getInstance().getString( "LoginTask.BrowserLogin.Retry" ) );
      if ( choice != JOptionPane.YES_OPTION ) {
        return null;
      }
    }
  }

  /**
   * Builds the {@code /plugin/login/api/v0/browser-auth} URL with the encoded
   * {@code callback} (local PRD HTTP listener) and, if an OAuth provider is set,
   * an {@code authorizationUri} param so the server redirects straight to the IDP.
   * Mirrors PDI's {@code BrowserAuthenticationService.buildAuthenticationUrl()}.
   */
  private String buildAuthUrl( String pentahoServerUrl, String callbackUrl ) {
    String baseUrl = pentahoServerUrl;
    // Remove trailing slash if present
    if ( baseUrl.endsWith( "/" ) ) {
      baseUrl = baseUrl.substring( 0, baseUrl.length() - 1 );
    }

    StringBuilder authUrl = new StringBuilder( baseUrl );
    authUrl.append( "/plugin/login/api/v0/browser-auth?callback="  );
    authUrl.append( java.net.URLEncoder.encode( callbackUrl, StandardCharsets.UTF_8 ) );

    if ( oauthProvider != null && oauthProvider.getAuthorizationUri() != null
        && !oauthProvider.getAuthorizationUri().trim().isEmpty() ) {
      authUrl.append( '&' );
      authUrl.append( PARAM_AUTHORIZATION_URI );
      authUrl.append( '=' );
      authUrl.append( java.net.URLEncoder.encode( oauthProvider.getAuthorizationUri().trim(), StandardCharsets.UTF_8 ) );
      logger.debug( "Building auth URL with provider - authorizationUri=" + oauthProvider.getAuthorizationUri() );
    } else {
      logger.debug( "Building auth URL WITHOUT provider - default SSO flow" );
    }

    return authUrl.toString();
  }

  /**
   * Returns the callback host extracted from the configured Pentaho server URL
   * (e.g. {@code 127.0.0.1} for {@code http://127.0.0.1:8080/pentaho}) so it
   * matches the server's {@code security.properties} allowed-hosts list.
   * Falls back to {@link #getLocalCallbackHost()} if the URL cannot be parsed.
   * Package-visible for testing.
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
   */
  private void startCallbackServer() throws IOException {
    callbackServer = HttpServer.create( new InetSocketAddress( CALLBACK_PORT ), 0 );
    callbackServer.createContext( CALLBACK_PATH, new CallbackHandler() );
    callbackServer.setExecutor( null ); // Use default executor
    callbackServer.start();
  }

  /**
   * Opens the system's default browser with the given URL.
   */
  void openBrowser( String url ) throws IOException {
    if ( Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported( Desktop.Action.BROWSE ) ) {
      Desktop.getDesktop().browse( URI.create( url ) );
      return;
    }
    // Fallback for systems without Desktop support
    ProcessBuilder pb = buildBrowserProcess( System.getProperty( "os.name" ).toLowerCase(), url );
    if ( pb == null ) {
      return;
    }
    pb.redirectErrorStream( true );
    pb.start();
  }

  /**
   * Builds the {@link ProcessBuilder} used to launch the system browser for the given OS.
   * Returns {@code null} when the OS is not recognized.
   */
  private ProcessBuilder buildBrowserProcess( String os, String url ) {
    if ( os.contains( "win" ) ) {
      return new ProcessBuilder( windowsRundll32Path(), "url.dll,FileProtocolHandler", url );
    }
    if ( os.contains( "mac" ) ) {
      return new ProcessBuilder( "/usr/bin/open", url );
    }
    if ( os.contains( "nix" ) || os.contains( "nux" ) ) {
      return new ProcessBuilder( xdgOpenPath(), url );
    }
    return null;
  }

  /**
   * Resolves the absolute path to {@code rundll32.exe} from the Windows system directory,
   * avoiding reliance on the (potentially writable) PATH environment variable.
   */
  private String windowsRundll32Path() {
    String systemRoot = System.getenv( "SystemRoot" );
    if ( systemRoot == null || systemRoot.isEmpty() ) {
      systemRoot = "C:\\Windows";
    }
    return systemRoot + "\\System32\\rundll32.exe";
  }

  /**
   * Returns the absolute path to {@code xdg-open}, preferring {@code /usr/bin/xdg-open}
   * with a fallback to {@code /usr/local/bin/xdg-open} on systems where it lives there.
   */
  private String xdgOpenPath() {
    String xdgOpen = "/usr/bin/xdg-open";
    if ( !new java.io.File( xdgOpen ).canExecute() ) {
      String alt = "/usr/local/bin/xdg-open";
      if ( new java.io.File( alt ).canExecute() ) {
        return alt;
      }
    }
    return xdgOpen;
  }

  /**
   * Cleans up resources.
   */
  private void cleanup() {
    if ( callbackServer != null ) {
      callbackServer.stop( 1 );
      callbackServer = null;
    }
    if ( sessionFuture != null && !sessionFuture.isDone() ) {
      sessionFuture.cancel( true );
    }
    callbackState = null;
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
      String state = params.get( "state" );

      if ( callbackState == null || state == null || !callbackState.equals( state ) ) {
        logger.error( "Invalid callback state received" );
        sendResponse( exchange, 400, buildErrorPage( "Invalid callback state" ) );
        sessionFuture.completeExceptionally( new Exception( "Invalid callback state" ) );
        return;
      }

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
      String safeMessage = escapeHtml( errorMessage );
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
             "  <p>Error: " + safeMessage + "</p>\n" +
             "  <p>You can close this window and try again in Report Designer.</p>\n" +
             "</body>\n" +
             "</html>";
    }

    private String escapeHtml( String input ) {
      if ( input == null ) {
        return "";
      }
      return input.replace( "&", "&amp;" )
                  .replace( "<", "&lt;" )
                  .replace( ">", "&gt;" )
                  .replace( "\"", "&quot;" )
                  .replace( "'", "&#39;" );
    }
  }
}

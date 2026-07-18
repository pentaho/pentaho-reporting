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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.awt.Component;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Browser-based authentication flow for Pentaho Server.
 * Opens the system browser, starts a local callback server, and retrieves the session ID.
 */
public class BrowserLoginHandler {
  private static final Log logger = LogFactory.getLog( BrowserLoginHandler.class );
  private static final int CALLBACK_PORT = 8183;
  private static final String CALLBACK_PATH = "/pentaho-auth-callback";
  private static final int TIMEOUT_SECONDS = 300;
  private static final int DEFAULT_TIMEOUT = 30000;
  private static final String PARAM_AUTHORIZATION_URI = "authorizationUri";

  private static final int MAX_SESSION_VERIFY_ATTEMPTS = 3;
  private static final long SESSION_VERIFY_RETRY_DELAY_MS = 800L;
  private static final String SUCCESS_PAGE = """
    <!DOCTYPE html>
    <html>
    <head>
      <title>Authentication Successful</title>
      <style>
        body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
        .success { color: #4CAF50; font-size: 24px; }
      </style>
    </head>
    <body>
      <h1 class="success">\u2713 Authentication Successful</h1>
      <p>You have successfully logged in to Pentaho Server.</p>
      <p>You can close this window and return to Report Designer.</p>
    </body>
    </html>
    """;

  private HttpServer callbackServer;
  private CompletableFuture<SessionData> sessionFuture;
  private OAuthProvider oauthProvider;
  private String callbackState;

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

  public void setOAuthProvider( OAuthProvider provider ) {
    this.oauthProvider = provider;
  }

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

  boolean verifySessionWithRetry( final AuthenticationData authData ) {
    boolean hadExplicitAuthFailure = false;
    for ( int attempt = 1; attempt <= MAX_SESSION_VERIFY_ATTEMPTS; attempt++ ) {
      final Boolean result = SessionAuthenticationUtil.checkSessionValidity( authData );
      if ( result == null ) {
        logger.debug( "SSO session verification attempt " + attempt + "/" + MAX_SESSION_VERIFY_ATTEMPTS
          + " could not reach server (connection error) \u2014 treating as unknown" );
      } else if ( result.booleanValue() ) {
        return true;
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

  public static AuthenticationData performBrowserLoginWithRetry(
    final Component uiContext, final String serverUrl, final OAuthProvider oauthProvider ) {
    while ( true ) {
      final AuthenticationData[] result = new AuthenticationData[ 1 ];
      final Thread loginThread = new Thread( () -> {
        BrowserLoginHandler handler = new BrowserLoginHandler();
        if ( oauthProvider != null ) {
          handler.setOAuthProvider( oauthProvider );
        }
        result[ 0 ] = handler.startBrowserLogin( serverUrl );
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
      if ( result[ 0 ] != null ) {
        return result[ 0 ];
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

  private String buildAuthUrl( String pentahoServerUrl, String callbackUrl ) {
    String baseUrl = pentahoServerUrl;
    if ( baseUrl.endsWith( "/" ) ) {
      baseUrl = baseUrl.substring( 0, baseUrl.length() - 1 );
    }

    StringBuilder authUrl = new StringBuilder( baseUrl );
    authUrl.append( "/plugin/login/api/v0/browser-auth?callback=" );
    authUrl.append( java.net.URLEncoder.encode( callbackUrl, StandardCharsets.UTF_8 ) );

    if ( oauthProvider != null && oauthProvider.getAuthorizationUri() != null
      && !oauthProvider.getAuthorizationUri().trim().isEmpty() ) {
      authUrl.append( '&' );
      authUrl.append( PARAM_AUTHORIZATION_URI );
      authUrl.append( '=' );
      authUrl.append(
        java.net.URLEncoder.encode( oauthProvider.getAuthorizationUri().trim(), StandardCharsets.UTF_8 ) );
      logger.debug( "Building auth URL with provider - authorizationUri=" + oauthProvider.getAuthorizationUri() );
    } else {
      logger.debug( "Building auth URL WITHOUT provider - default SSO flow" );
    }

    return authUrl.toString();
  }

  @SuppressWarnings( "java:S1172" )
  String resolveCallbackHost( String baseUrl ) {
    // The callback server always runs locally on the PRD machine.
    // Using the remote server's host would direct the OAuth redirect to the
    // server VM instead of back to the local machine when the two are on
    // different machines.
    return getLocalCallbackHost();
  }

  String getLocalCallbackHost() {
    return "localhost";
  }

  private void startCallbackServer() throws IOException {
    callbackServer = HttpServer.create( new InetSocketAddress( CALLBACK_PORT ), 0 );
    callbackServer.createContext( CALLBACK_PATH, new CallbackHandler() );
    callbackServer.setExecutor( null );
    callbackServer.start();
  }

  void openBrowser( String url ) throws IOException {
    if ( Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported( Desktop.Action.BROWSE ) ) {
      Desktop.getDesktop().browse( URI.create( url ) );
      return;
    }
    ProcessBuilder pb = buildBrowserProcess( System.getProperty( "os.name" ).toLowerCase(), url );
    if ( pb == null ) {
      return;
    }
    pb.redirectErrorStream( true );
    pb.start();
  }

  ProcessBuilder buildBrowserProcess( String os, String url ) {
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

  private String windowsRundll32Path() {
    String systemRoot = getSystemRootEnv();
    if ( systemRoot == null || systemRoot.isEmpty() ) {
      systemRoot = "C:\\Windows";
    }
    return systemRoot + "\\System32\\rundll32.exe";
  }

  String getSystemRootEnv() {
    return System.getenv( "SystemRoot" );
  }

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
      String query = exchange.getRequestURI().getQuery();
      Map<String, String> params = parseQueryParams( query );

      String sessionId = params.get( "jsessionid" );
      if ( sessionId == null ) {
        sessionId = params.get( "sessionId" );
      }

      String username = params.get( "username" );
      String error = params.get( "error" );
      String state = params.get( "state" );

      if ( callbackState == null || !callbackState.equals( state ) ) {
        logger.error( "Invalid callback state received" );
        sendResponse( exchange, 400, buildErrorPage( "Invalid callback state" ) );
        sessionFuture.completeExceptionally( new Exception( "Invalid callback state" ) );
        return;
      }

      if ( error != null && !error.isEmpty() ) {
        logger.error( "Authentication error received: " + error );
        sendResponse( exchange, 400, buildErrorPage( error ) );
        sessionFuture.completeExceptionally( new Exception( "Authentication failed: " + error ) );
        return;
      }

      if ( sessionId == null || sessionId.isEmpty() ) {
        logger.error( "No JSESSIONID received in callback" );
        sendResponse( exchange, 400, buildErrorPage( "No session ID received" ) );
        sessionFuture.completeExceptionally( new Exception( "No JSESSIONID received" ) );
        return;
      }

      sendResponse( exchange, 200, SUCCESS_PAGE );
      SessionData sessionData = new SessionData( sessionId, username );
      logger.info( "Successfully received session info for user: " + username );
      sessionFuture.complete( sessionData );
    }

    private void handlePostCallback( HttpExchange exchange ) throws IOException {
      handleGetCallback( exchange );
    }

    private Map<String, String> parseQueryParams( String query ) {
      Map<String, String> params = new HashMap<>();
      if ( query != null && !query.isEmpty() ) {
        String[] pairs = query.split( "&" );
        for ( String pair : pairs ) {
          String[] keyValue = pair.split( "=", 2 );
          if ( keyValue.length == 2 ) {
            String key = URLDecoder.decode( keyValue[ 0 ], StandardCharsets.UTF_8 );
            String value = URLDecoder.decode( keyValue[ 1 ], StandardCharsets.UTF_8 );
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

    private String buildErrorPage( String errorMessage ) {
      String safeMessage = escapeHtml( errorMessage );
      return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "  <title>Authentication Failed</title>\n"
        + "  <style>\n"
        + "    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }\n"
        + "    .error { color: #f44336; font-size: 24px; }\n"
        + "  </style>\n"
        + "</head>\n"
        + "<body>\n"
        + "  <h1 class=\"error\">\u2717 Authentication Failed</h1>\n"
        + "  <p>Error: " + safeMessage + "</p>\n"
        + "  <p>You can close this window and try again in Report Designer.</p>\n"
        + "</body>\n"
        + "</html>";
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

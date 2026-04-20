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


package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;

public final class DisconnectFromRepositoryAction extends AbstractDesignerContextAction
  implements PropertyChangeListener {

  private static final Log logger = LogFactory.getLog( DisconnectFromRepositoryAction.class );
  private static final String ACTION_TEXT_KEY = "DisconnectFromRepositoryAction.Text";

  public DisconnectFromRepositoryAction() {
    putValue( Action.NAME, Messages.getInstance().getString( ACTION_TEXT_KEY ) );
    putValue( Action.SHORT_DESCRIPTION,
      Messages.getInstance().getString( "DisconnectFromRepositoryAction.Description" ) );
    updateSessionState();
    RepositorySessionManager.getInstance().addPropertyChangeListener( this );
  }

  @Override
  public void propertyChange( final PropertyChangeEvent evt ) {
    updateSessionState();
  }

  private void updateSessionState() {
    final boolean active = RepositorySessionManager.getInstance().hasActiveSession();
    setEnabled( active );
    if ( active ) {
      final String user = RepositorySessionManager.getInstance().getLoggedInUser();
      final String displayUser = ( user != null && !user.isEmpty() ) ? user : "unknown";
      final String loggedInMsg =
        Messages.getInstance().formatMessage( "DisconnectFromRepositoryAction.LoggedInAs", displayUser );
      putValue( Action.NAME,
        Messages.getInstance().getString( ACTION_TEXT_KEY )
          + "  (" + displayUser + ")" );
      putValue( Action.SHORT_DESCRIPTION, loggedInMsg );
    } else {
      putValue( Action.NAME,
        Messages.getInstance().getString( ACTION_TEXT_KEY ) );
      putValue( Action.SHORT_DESCRIPTION,
        Messages.getInstance().getString( "DisconnectFromRepositoryAction.Description" ) );
    }
  }

  @Override
  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    updateSessionState();
  }

  @Override
  public void actionPerformed( final ActionEvent e ) {
    invalidateServerSession();

    RepositorySessionManager.getInstance().clearSession();

    // Clear any cookies (notably stale JSESSIONID values) that the JVM-wide
    // CookieHandler may have captured during the previous session. Without
    // this, the next login attempt -- particularly when switching auth
    // method (U/P -> SSO or SSO -> U/P) -- can have stale JSESSIONID cookies
    // re-sent automatically by HttpURLConnection, which causes the server
    // to reject the new session and the "login again" dialog to reappear.
    clearJvmCookieStore();

    final ReportDesignerContext ctx = getReportDesignerContext();
    final java.awt.Component parent = ctx != null ? ctx.getView().getParent() : null;
    JOptionPane.showMessageDialog(
      parent,
      Messages.getInstance().getString( "DisconnectFromRepositoryAction.Success.Message" ),
      Messages.getInstance().getString( "DisconnectFromRepositoryAction.Success.Title" ),
      JOptionPane.INFORMATION_MESSAGE );
  }

  private void invalidateServerSession() {
    final AuthenticationData session = RepositorySessionManager.getInstance().getSession();
    if ( session == null || session.getUrl() == null ) {
      return;
    }

    String serverUrl = session.getUrl();
    if ( serverUrl.endsWith( "/" ) ) {
      serverUrl = serverUrl.substring( 0, serverUrl.length() - 1 );
    }
    final String logoutUrl = serverUrl + "/Logout";

    final String sessionId = session.getOption( "sessionId" );
    if ( sessionId != null && !sessionId.isEmpty() ) {
      // SSO / browser-auth session: invalidate via JSESSIONID cookie.
      invalidateViaHttp( logoutUrl, sessionId );
      return;
    }

    // Username/password session: HTTP-Basic does not maintain an explicit
    // client-side session token, but Pentaho still creates a server-side
    // HttpSession on first authenticated request. Hit /Logout with the same
    // credentials so the server destroys that session; otherwise a later
    // SSO login that lands on the same JSESSIONID cookie (kept alive in the
    // JVM cookie store) inherits the previous, now-stale, authentication
    // and the next API call comes back 401/403.
    final String username = session.getUsername();
    if ( username != null && !username.isEmpty() ) {
      invalidateViaHttpBasic( logoutUrl, username, session.getPassword() );
    }
  }

  /**
   * Direct HTTP call that sends the JSESSIONID cookie to the server's
   * {@code /Logout} endpoint to destroy the server-side session.
   */
  static void invalidateViaHttp( final String logoutUrl, final String sessionId ) {
    if ( !isSafeCookieValue( sessionId ) ) {
      logger.debug( "Refusing to send logout request: sessionId contains unsafe characters" );
      return;
    }
    HttpURLConnection conn = null;
    try {
      @SuppressWarnings( "java:S1874" )
      HttpURLConnection tmpConn = (HttpURLConnection) new URL( logoutUrl ).openConnection();
      conn = tmpConn;
      conn.setRequestMethod( "GET" );
      conn.setInstanceFollowRedirects( false );
      conn.setConnectTimeout( 5000 );
      conn.setReadTimeout( 5000 );
      conn.setRequestProperty( "Cookie", "JSESSIONID=" + sessionId );
      final int status = conn.getResponseCode();
      logger.debug( "Server logout returned status " + status );
    } catch ( Exception ex ) {
      logger.debug( "Failed to call server /Logout endpoint", ex );
    } finally {
      if ( conn != null ) {
        conn.disconnect();
      }
    }
  }

  /**
   * Direct HTTP call that sends HTTP-Basic credentials to the server's
   * {@code /Logout} endpoint so that the server destroys any HttpSession it
   * has bound to those credentials. This is needed for username/password
   * sessions where there is no explicit client-side session token.
   */
  static void invalidateViaHttpBasic( final String logoutUrl, final String username, final String password ) {
    HttpURLConnection conn = null;
    try {
      @SuppressWarnings( "java:S1874" )
      HttpURLConnection tmpConn = (HttpURLConnection) new URL( logoutUrl ).openConnection();
      conn = tmpConn;
      conn.setRequestMethod( "GET" );
      conn.setInstanceFollowRedirects( false );
      conn.setConnectTimeout( 5000 );
      conn.setReadTimeout( 5000 );
      final String pwd = ( password == null ) ? "" : password;
      final String token = Base64.getEncoder()
        .encodeToString( ( username + ":" + pwd ).getBytes( StandardCharsets.UTF_8 ) );
      conn.setRequestProperty( "Authorization", "Basic " + token );
      final int status = conn.getResponseCode();
      logger.debug( "Server logout (basic auth) returned status " + status );
    } catch ( Exception ex ) {
      logger.debug( "Failed to call server /Logout endpoint with basic auth", ex );
    } finally {
      if ( conn != null ) {
        conn.disconnect();
      }
    }
  }

  /**
   * Removes every cookie held by the JVM-wide {@link CookieHandler} default,
   * if it is a {@link CookieManager}. Pentaho's
   * {@code JCRSolutionFileModel} U/P constructor installs a global
   * CookieManager with {@code ACCEPT_ALL} policy, which silently captures
   * the server's {@code JSESSIONID} cookie. After a disconnect that cookie
   * survives and gets auto-attached by {@code HttpURLConnection} to the
   * very next login attempt -- causing the server to associate the new
   * login with the old (now stale) session and reject subsequent calls.
   * Clearing the store breaks that contamination chain.
   */
  static void clearJvmCookieStore() {
    try {
      final CookieHandler handler = CookieHandler.getDefault();
      if ( handler instanceof CookieManager ) {
        final CookieManager cookieManager = (CookieManager) handler;
        cookieManager.getCookieStore().removeAll();
      }
    } catch ( Exception ex ) {
      logger.debug( "Failed to clear JVM cookie store on disconnect", ex );
    }
  }

  static boolean isSafeCookieValue( final String value ) {
    if ( value == null || value.isEmpty() ) {
      return false;
    }
    for ( int i = 0; i < value.length(); i++ ) {
      final char c = value.charAt( i );
      if ( c == '\r' || c == '\n' || c == ';' || c < 0x20 || c == 0x7f ) {
        return false;
      }
    }
    return true;
  }
}

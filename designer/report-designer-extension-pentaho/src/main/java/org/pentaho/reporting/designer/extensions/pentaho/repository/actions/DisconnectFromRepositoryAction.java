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
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;

/**
 * Clears the active SSO session stored in {@link RepositorySessionManager}.
 * After disconnecting the user must log in again when performing
 * Open / Publish operations.
 */
public final class DisconnectFromRepositoryAction extends AbstractDesignerContextAction
    implements PropertyChangeListener {

  private static final Log logger = LogFactory.getLog( DisconnectFromRepositoryAction.class );

  public DisconnectFromRepositoryAction() {
    putValue( Action.NAME, Messages.getInstance().getString( "DisconnectFromRepositoryAction.Text" ) );
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
      final String loggedInMsg =
          Messages.getInstance().formatMessage( "DisconnectFromRepositoryAction.LoggedInAs", user );
      // Embed username into the menu item text — Swing popup menus do not
      // reliably display SHORT_DESCRIPTION tooltips, so this ensures it is
      // always visible. Also set SHORT_DESCRIPTION for environments that do
      // show menu tooltips.
      putValue( Action.NAME,
          Messages.getInstance().getString( "DisconnectFromRepositoryAction.Text" )
          + "  (" + user + ")" );
      putValue( Action.SHORT_DESCRIPTION, loggedInMsg );
    } else {
      putValue( Action.NAME,
          Messages.getInstance().getString( "DisconnectFromRepositoryAction.Text" ) );
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
    // Invalidate the server-side session by opening /Logout in the browser.
    // The browser carries the session cookies for that domain, so the server
    // will destroy the HttpSession.  Without this, the next Connect to a
    // different provider receives the stale session from the old provider.
    invalidateServerSession();

    RepositorySessionManager.getInstance().clearSession();

    final ReportDesignerContext ctx = getReportDesignerContext();
    final java.awt.Component parent = ctx != null ? ctx.getView().getParent() : null;
    JOptionPane.showMessageDialog(
        parent,
        Messages.getInstance().getString( "DisconnectFromRepositoryAction.Success.Message" ),
        Messages.getInstance().getString( "DisconnectFromRepositoryAction.Success.Title" ),
        JOptionPane.INFORMATION_MESSAGE );
  }

  /**
   * Calls the Pentaho server's {@code /Logout} endpoint via a direct HTTP
   * request, sending the JSESSIONID cookie so the server destroys the
   * correct session.  This is done internally (no browser redirect).
   */
  private void invalidateServerSession() {
    final AuthenticationData session = RepositorySessionManager.getInstance().getSession();
    if ( session == null || session.getUrl() == null ) {
      return;
    }
    final String sessionId = session.getOption( "sessionId" );
    if ( sessionId == null || sessionId.isEmpty() ) {
      return;
    }
    HttpURLConnection conn = null;
    try {
      String serverUrl = session.getUrl();
      if ( serverUrl.endsWith( "/" ) ) {
        serverUrl = serverUrl.substring( 0, serverUrl.length() - 1 );
      }
      conn = (HttpURLConnection) new URL( serverUrl + "/Logout" ).openConnection();
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
}

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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.BrowserLoginHandler;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProvider;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

/**
 * Opens the SSO login dialog and stores the authenticated session
 * in {@link RepositorySessionManager} so that subsequent Open / Publish
 * operations can skip the login step.
 */
public final class ConnectToRepositoryAction extends AbstractDesignerContextAction
    implements PropertyChangeListener {

  private static final Log logger = LogFactory.getLog( ConnectToRepositoryAction.class );

  /** Cache validity period — avoid hitting the server on every isEnabled() call. */
  private static final long SESSION_CHECK_INTERVAL_MS = 3000;
  private long lastSessionCheckTime;
  private boolean lastSessionCheckResult;

  public ConnectToRepositoryAction() {
    putValue( Action.NAME, Messages.getInstance().getString( "ConnectToRepositoryAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION,
        Messages.getInstance().getString( "ConnectToRepositoryAction.Description" ) );
    setEnabled( !RepositorySessionManager.getInstance().hasActiveSession() );
    RepositorySessionManager.getInstance().addPropertyChangeListener( this );
  }

  /**
   * Called by Swing whenever the menu containing this action is about to
   * be painted.  If a local session exists we check (with caching) whether
   * the server still recognises it.  If the server says 401/403 we
   * auto-clear the local session so Connect re-enables and Disconnect
   * disables.
   */
  @Override
  public boolean isEnabled() {
    if ( !RepositorySessionManager.getInstance().hasActiveSession() ) {
      return super.isEnabled();
    }
    // Have a local session — check server, but throttle to avoid
    // making an HTTP call on every single repaint.
    final long now = System.currentTimeMillis();
    if ( now - lastSessionCheckTime > SESSION_CHECK_INTERVAL_MS ) {
      lastSessionCheckTime = now;
      lastSessionCheckResult = isServerSessionValid();
      if ( !lastSessionCheckResult ) {
        logger.info( "Server session expired — clearing local session" );
        RepositorySessionManager.getInstance().clearSession();
      }
    }
    return !lastSessionCheckResult;
  }

  @Override
  public void propertyChange( final PropertyChangeEvent evt ) {
    // Reset the cache so the next isEnabled() call re-checks the server
    lastSessionCheckTime = 0;
    setEnabled( !RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Override
  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    lastSessionCheckTime = 0;
    if ( newContext == null ) {
      setEnabled( false );
    } else {
      setEnabled( !RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  /**
   * Performs a lightweight HTTP check against the server to see if the
   * stored session is still valid.  Returns {@code true} unless the server
   * responds with 401 or 403.
   */
  private boolean isServerSessionValid() {
    final AuthenticationData session = RepositorySessionManager.getInstance().getSession();
    if ( session == null || session.getUrl() == null ) {
      return false;
    }
    try {
      final HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
          session, session.getUrl(), 5000 );
      String checkUrl = session.getUrl();
      if ( checkUrl.endsWith( "/" ) ) {
        checkUrl = checkUrl.substring( 0, checkUrl.length() - 1 );
      }
      checkUrl += "/api/repo/files/tree?depth=0";
      final HttpGet request = new HttpGet( checkUrl );
      final HttpResponse response = client.execute( request );
      final int status = response.getStatusLine().getStatusCode();
      return status != 401 && status != 403;
    } catch ( Exception e ) {
      logger.debug( "Session validation failed — assuming still valid", e );
      return true;
    }
  }

  @Override
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext context = getReportDesignerContext();
    if ( context == null ) {
      return;
    }

    final Window window = LibSwingUtil.getWindowAncestor( context.getView().getParent() );
    final RepositoryLoginDialog loginDialog = createLoginDialog( window );
    // Repository → Connect should only show SSO options (server URL + provider).
    // Username/password fields have no place here.
    loginDialog.setDialogMode( RepositoryLoginDialog.DialogMode.SSO_ONLY );

    final AuthenticationData dialogData = loginDialog.performLogin( context, null );
    if ( dialogData == null ) {
      return;
    }

    if ( loginDialog.getLoginMethod() == RepositoryLoginDialog.LoginMethod.SSO ) {
      final OAuthProvider provider = loginDialog.getSelectedOAuthProvider();
      final AuthenticationData ssoResult = performBrowserLogin( window, dialogData.getUrl(), provider );
      if ( ssoResult != null ) {
        storeSession( ssoResult );
      }
    } else {
      // Username / Password — store it as a live session as well
      storeSession( dialogData );
    }
  }

  private void storeSession( final AuthenticationData loginData ) {
    final String username = loginData.getUsername();
    RepositorySessionManager.getInstance().setSession( loginData, username );

    JOptionPane.showMessageDialog(
        getReportDesignerContext().getView().getParent(),
        Messages.getInstance().formatMessage( "ConnectToRepositoryAction.Success.Message", username ),
        Messages.getInstance().getString( "ConnectToRepositoryAction.Success.Title" ),
        JOptionPane.INFORMATION_MESSAGE );
  }

  /**
   * Performs browser-based SSO login with retry support.
   */
  private AuthenticationData performBrowserLogin( final Window parentWindow,
                                                   final String serverUrl,
                                                   final OAuthProvider oauthProvider ) {
    while ( true ) {
      final AuthenticationData[] result = new AuthenticationData[1];
      final Thread browserLoginThread = new Thread( () -> {
        final BrowserLoginHandler handler = new BrowserLoginHandler();
        if ( oauthProvider != null ) {
          handler.setOAuthProvider( oauthProvider );
        }
        result[0] = handler.startBrowserLogin( serverUrl );
      } );
      browserLoginThread.setDaemon( true );
      browserLoginThread.setName( "ConnectBrowserLoginThread" );

      final GenericCancelHandler cancelHandler = new GenericCancelHandler( browserLoginThread );
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          browserLoginThread, cancelHandler, parentWindow,
          Messages.getInstance().getString( "LoginTask.BrowserLogin.WaitingMessage" ) );

      if ( cancelHandler.isCancelled() ) {
        return null;
      }
      if ( result[0] != null ) {
        return result[0];
      }

      final int choice = JOptionPane.showOptionDialog( parentWindow,
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

  private static RepositoryLoginDialog createLoginDialog( final Window window ) {
    if ( window instanceof Frame ) {
      return new RepositoryLoginDialog( (Frame) window, false );
    } else if ( window instanceof Dialog ) {
      return new RepositoryLoginDialog( (Dialog) window, false );
    }
    return new RepositoryLoginDialog( false );
  }
}

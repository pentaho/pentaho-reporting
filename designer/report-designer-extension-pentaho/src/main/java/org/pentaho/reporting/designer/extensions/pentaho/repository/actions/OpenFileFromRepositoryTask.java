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

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;

public class OpenFileFromRepositoryTask implements AuthenticatedServerTask {
  static final String OPTION_LAST_FILENAME = "lastFilename";

  private AuthenticationData loginData;
  private boolean storeUpdates;
  private ReportDesignerContext designerContext;
  private Component uiContext;
  private SelectFileFromRepositoryTask selectFileFromRepositoryTask;
  private String pendingReport;


  public OpenFileFromRepositoryTask( final ReportDesignerContext designerContext,
                                     final Component uiContext ) {

    this.designerContext = designerContext;
    this.uiContext = uiContext;

    selectFileFromRepositoryTask = new SelectFileFromRepositoryTask( uiContext );
    selectFileFromRepositoryTask.setReLoginListener( newData -> {
      this.loginData = newData;
      this.storeUpdates = true;
    } );
  }

  public void setLoginData( final AuthenticationData loginData,
                            final boolean storeUpdates ) {
    this.loginData = loginData;
    this.storeUpdates = storeUpdates;
  }

  public void run() {
    String selectedReport = null;
    try {
      if ( pendingReport != null ) {
        selectedReport = pendingReport;
        pendingReport = null;
      } else {
        final String oldName = loginData.getOption( OPTION_LAST_FILENAME );
        selectedReport = selectFileFromRepositoryTask.selectFile( loginData, oldName );
        if ( selectedReport == null ) {
          return;
        }
      }
      loginData.setOption( OPTION_LAST_FILENAME, selectedReport );
      if ( storeUpdates && !AuthenticationHelper.isBrowserAuth( loginData ) ) {
        designerContext.getGlobalAuthenticationStore().add( sanitizedForStore( loginData ), true );
      }

      final ReportRenderContext context = PublishUtil.openReport( designerContext, loginData, selectedReport );
      if ( context != null ) {
        context.setProperty( "pentaho-login-url", loginData.getUrl() );
        if ( storeUpdates && !AuthenticationHelper.isBrowserAuth( loginData ) ) {
          context.getAuthenticationStore().add( sanitizedForStore( loginData ), true );
        }
      }

      designerContext.getView().setWelcomeVisible( false );
    } catch ( Exception exception ) {
      handleOpenException( selectedReport, exception );
    }
  }

  private void handleOpenException( final String selectedReport, final Exception exception ) {
    if ( AuthenticationHelper.isAuthenticationError( exception ) && AuthenticationHelper.isBrowserAuth( loginData ) ) {
      handleSessionExpiredOnOpen( selectedReport );
    } else if ( AuthenticationHelper.isConnectionError( exception )
        || AuthenticationHelper.isAuthenticationError( exception ) ) {
      RepositorySessionManager.getInstance().clearSession();
      JOptionPane.showMessageDialog( uiContext,
        Messages.getInstance().getString( "LoadReportFromRepositoryAction.ServerDown.Message" ),
        Messages.getInstance().getString( "LoadReportFromRepositoryAction.ServerDown.Title" ),
        JOptionPane.ERROR_MESSAGE );
    } else {
      showOpenError( selectedReport, exception );
    }
  }

  private void handleSessionExpiredOnOpen( final String selectedReport ) {
    final String[] options = {
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.LoginAgain" ),
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.Cancel" )
    };
    final int choice = JOptionPane.showOptionDialog( uiContext,
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.Message" ),
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.Title" ),
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0] );

    if ( choice != JOptionPane.YES_OPTION ) {
      this.pendingReport = null;
      return;
    }

    this.pendingReport = selectedReport;
    final boolean retryViaBrowser = AuthenticationHelper.isBrowserAuth( loginData );
    final LoginTask loginTask =
        new LoginTask( designerContext, uiContext, null, this.loginData, false, retryViaBrowser );
    loginTask.run();

    if ( loginTask.getLoginData() != null ) {
      this.loginData = loginTask.getLoginData();
      this.storeUpdates = true;
      RepositorySessionManager.getInstance().setSession( this.loginData, this.loginData.getUsername() );
      SwingUtilities.invokeLater( this );
    } else {
      this.pendingReport = null;
    }
  }

  private void showOpenError( final String selectedReport, final Exception exception ) {
    final String detail = exception != null && exception.getMessage() != null
        ? exception.getMessage()
        : selectedReport;
    ExceptionDialog.showExceptionDialog( uiContext,
      Messages.getInstance().getString( "LoadReportFromRepositoryAction.Error.Title" ),
      Messages.getInstance().formatMessage( "LoadReportFromRepositoryAction.Error.Message",
        detail ), exception );
    UncaughtExceptionsModel.getInstance().addException( exception );
  }

  /**
   * Returns a copy of {@code data} suitable for persistence in an
   * {@link org.pentaho.reporting.designer.core.auth.AuthenticationStore}: the
   * transient SSO {@code sessionId} and {@code browserAuth} marker are stripped
   * so they cannot leak into a later launch and confuse subsequent U/P logins.
   */
  static AuthenticationData sanitizedForStore( final AuthenticationData data ) {
    if ( data == null || data.getUrl() == null ) {
      return data;
    }
    final AuthenticationData copy =
      new AuthenticationData( data.getUrl(), data.getUsername(), data.getPassword(),
        org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil.getTimeout( data ) );
    final String[] preserved = {
      org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil.SERVER_VERSION,
      OPTION_LAST_FILENAME
    };
    for ( String key : preserved ) {
      final String v = data.getOption( key );
      if ( v != null ) {
        copy.setOption( key, v );
      }
    }
    // Explicitly do NOT copy sessionId / browserAuth -- they are transient
    // session secrets that must never be persisted across launches.
    copy.setOption( AuthenticationHelper.OPTION_SESSION_ID, null );
    copy.setOption( AuthenticationHelper.OPTION_BROWSER_AUTH, null );
    return copy;
  }
}

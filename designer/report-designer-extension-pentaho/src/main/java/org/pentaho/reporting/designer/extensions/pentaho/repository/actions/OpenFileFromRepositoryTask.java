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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;

public class OpenFileFromRepositoryTask implements AuthenticatedServerTask {
  private AuthenticationData loginData;
  private boolean storeUpdates;
  private ReportDesignerContext designerContext;
  private Component uiContext;
  private SelectFileFromRepositoryTask selectFileFromRepositoryTask;

  public OpenFileFromRepositoryTask( final ReportDesignerContext designerContext,
                                     final Component uiContext ) {

    this.designerContext = designerContext;
    this.uiContext = uiContext;

    selectFileFromRepositoryTask = new SelectFileFromRepositoryTask( uiContext );
  }

  public void setLoginData( final AuthenticationData loginData,
                            final boolean storeUpdates ) {
    this.loginData = loginData;
    this.storeUpdates = storeUpdates;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes
   * the object's <code>run</code> method to be called in that separately executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run() {
    String selectedReport = null;
    try {
      final String oldName = loginData.getOption( "lastFilename" );
      selectedReport = selectFileFromRepositoryTask.selectFile( loginData, oldName );
      if ( selectedReport == null ) {
        return;
      }
      loginData.setOption( "lastFilename", selectedReport );
      if ( storeUpdates ) {
        designerContext.getGlobalAuthenticationStore().add( loginData, true );
      }

      final ReportRenderContext context = PublishUtil.openReport( designerContext, loginData, selectedReport );
      if ( context != null ) {
        context.setProperty( "pentaho-login-url", loginData.getUrl() );
        context.getAuthenticationStore().add( loginData, true );
      }

      designerContext.getView().setWelcomeVisible( false );
    } catch ( Exception exception ) {
      if ( AuthenticationHelper.isAuthenticationError( exception ) && AuthenticationHelper.isBrowserAuth( loginData ) ) {
        handleBrowserAuthError( selectedReport, exception );
      } else {
        showOpenError( selectedReport, exception );
      }
    }
  }

  private void handleBrowserAuthError( final String selectedReport, final Exception exception ) {
    String errorMessage = Messages.getInstance().formatMessage( "LoadReportFromRepositoryAction.Error.Message",
      selectedReport != null ? selectedReport : "Unknown" );
    errorMessage += "\n\n" + Messages.getInstance().formatMessage(
      "LoadReportFromRepositoryAction.SessionExpired.Message", exception.getMessage() );

    final String[] options = { "Login Again", "Cancel" };
    final int result = JOptionPane.showOptionDialog( uiContext, errorMessage,
      Messages.getInstance().getString( "LoadReportFromRepositoryAction.SessionExpired.Title" ),
      JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0] );

    if ( result == JOptionPane.YES_OPTION ) {
      final LoginTask loginTask = new LoginTask( designerContext, uiContext, null, this.loginData, false, true );
      loginTask.run();
      if ( loginTask.getLoginData() != null ) {
        this.loginData = loginTask.getLoginData();
        this.run();
      }
    }
  }

  private void showOpenError( final String selectedReport, final Exception exception ) {
    ExceptionDialog.showExceptionDialog( uiContext,
      Messages.getInstance().getString( "LoadReportFromRepositoryAction.Error.Title" ),
      Messages.getInstance().formatMessage( "LoadReportFromRepositoryAction.Error.Message",
        selectedReport != null ? selectedReport : exception.getMessage() ), exception );
    UncaughtExceptionsModel.getInstance().addException( exception );
  }
}

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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishSettings;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

public class LoginTask implements Runnable {

  /**
   * Optional login dialog factory. EE can provide an SSO-enabled dialog; otherwise
   * the default {@link RepositoryLoginDialog} is used (CE/master behaviour).
   */
  @FunctionalInterface
  public interface LoginDialogFactory {
    RepositoryLoginDialog createDialog( Window owner, boolean loginForPublish );
  }

  /**
   * Optional session provider. EE may supply an authenticated session to reuse
   * for open/publish operations; otherwise the login dialog is always shown.
   */
  @FunctionalInterface
  public interface LoginSessionProvider {
    AuthenticationData getActiveSession();
  }

  private static final AtomicReference<LoginDialogFactory> dialogFactory = new AtomicReference<>();
  private static final AtomicReference<LoginSessionProvider> sessionProvider = new AtomicReference<>();

  public static void setLoginDialogFactory( final LoginDialogFactory factory ) {
    dialogFactory.set( factory );
  }

  public static void setLoginSessionProvider( final LoginSessionProvider provider ) {
    sessionProvider.set( provider );
  }

  private final ReportDesignerContext designerContext;
  private final Component uiContext;
  private final AuthenticatedServerTask followUpTask;
  private final boolean loginForPublish;
  private boolean skipFirstShowDialog;
  private RepositoryLoginDialog loginDialog;
  private AuthenticationData loginData;

  public LoginTask( final ReportDesignerContext designerContext, final Component uiContext,
      final AuthenticatedServerTask followUpTask ) {
    this( designerContext, uiContext, followUpTask, null, false );
  }

  public LoginTask( final ReportDesignerContext designerContext, final Component uiContext,
      final AuthenticatedServerTask followUpTask, final AuthenticationData loginData ) {
    this( designerContext, uiContext, followUpTask, loginData, false );
  }

  public LoginTask( final ReportDesignerContext designerContext, final Component uiContext, final AuthenticatedServerTask followUpTask, final AuthenticationData loginData,
                    final boolean loginForPublish ) {
    if ( designerContext == null ) {
      throw new NullPointerException();
    }
    if ( uiContext == null ) {
      throw new NullPointerException();
    }
    this.loginForPublish = loginForPublish;
    this.designerContext = designerContext;
    this.uiContext = uiContext;
    this.followUpTask = followUpTask;
    if ( loginData != null ) {
      this.loginData = loginData;
      this.skipFirstShowDialog = true;
    } else {
      initializeLoginData();
    }
  }
  private void initializeLoginData() {

    final LoginSessionProvider provider = sessionProvider.get();
    final AuthenticationData activeSession =
      ( provider != null ) ? provider.getActiveSession() : null;

    if ( activeSession != null ) {
      this.loginData = activeSession;
      this.skipFirstShowDialog = true;
      return;
    }

    final ReportDocumentContext reportRenderContext =
      designerContext.getActiveContext();

    if ( reportRenderContext != null ) {
      final Object o =
        reportRenderContext.getProperties().get( "pentaho-login-url" );

      if ( o != null ) {
        // prepopulate the dialog with the correct login data,
        // but do not skip login completely.
        this.loginData =
          RepositoryLoginDialog.getStoredLoginData(
            String.valueOf( o ),
            designerContext );
      }
    }

    if ( this.loginData == null ) {
      this.loginData =
        RepositoryLoginDialog.getDefaultData( designerContext );
    }

    this.skipFirstShowDialog = false;
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
    boolean loginComplete;
    do {
      if ( loginDialog == null ) {
        loginDialog = createLoginDialog();
      }

      if ( skipFirstShowDialog ) {
        skipFirstShowDialog = false;
      } else {
        this.loginData = loginDialog.performLogin( designerContext, loginData );
        if ( loginData == null ) {
          return;
        }
      }

      final ValidateLoginTask validateLoginTask = new ValidateLoginTask( this );
      final Thread loginThread = new Thread( validateLoginTask );
      loginThread.setDaemon( true );
      loginThread.setPriority( Thread.MIN_PRIORITY );

      final GenericCancelHandler cancelHandler = new GenericCancelHandler( loginThread );
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( loginThread, cancelHandler, uiContext,
          Messages.getInstance().getString( "LoginTask.ValidateLoginMessage" ) );
      if ( cancelHandler.isCancelled() ) {
        return;
      }

      if ( validateLoginTask.getException() != null ) {
        final Exception exception = validateLoginTask.getException();
        ExceptionDialog.showExceptionDialog( uiContext, Messages.getInstance().getString(
            "LoadReportFromRepositoryAction.LoginError.Title" ), "<html><div style='width:450px'>" +  Messages.getInstance().formatMessage(
            "LoadReportFromRepositoryAction.LoginError.Message", friendlyLoginErrorMessage( exception ) ) + "</div></html>", exception );
        loginComplete = false;
      } else {
        loginComplete = validateLoginTask.isLoginComplete();
      }
    } while ( loginComplete == false );

    if ( loginDialog != null && loginDialog.isRememberSettings() ) {
      final ReportDocumentContext reportRenderContext = designerContext.getActiveContext();
      if ( reportRenderContext != null ) {
        final AuthenticationStore store = reportRenderContext.getAuthenticationStore();
        store.add( loginData, true );
      } else {
        designerContext.getGlobalAuthenticationStore().add( loginData, true );
      }
    }

    final boolean storeUpdates = isRememberSettingsEnabled();

    if ( followUpTask != null ) {
      followUpTask.setLoginData( loginData, storeUpdates );
      SwingUtilities.invokeLater( followUpTask );
    }

    UpdateReservedCharsTask updateReservedCharsTask = new UpdateReservedCharsTask( loginData );
    SwingUtilities.invokeLater( updateReservedCharsTask );
  }

  private RepositoryLoginDialog createLoginDialog() {
    final Window window = LibSwingUtil.getWindowAncestor( uiContext );
    final LoginDialogFactory factory = dialogFactory.get();
    if ( factory != null ) {
      return factory.createDialog( window, loginForPublish );
    }
    if ( window instanceof Frame frame ) {
      return new RepositoryLoginDialog( frame, loginForPublish );
    }
    if ( window instanceof Dialog dialog ) {
      return new RepositoryLoginDialog( dialog, loginForPublish );
    }
    return new RepositoryLoginDialog( loginForPublish );
  }

  private boolean isRememberSettingsEnabled() {
    if ( loginDialog == null ) {
      return PublishSettings.getInstance().isRememberSettings();
    }
    return loginDialog.isRememberSettings();
  }

  private static String friendlyLoginErrorMessage( final Exception exception ) {
    final String message = ( exception == null ) ? null : exception.getMessage();
    if ( exception != null && PublishUtil.isAuthenticationError( exception ) ) {
      return "Invalid username and/or password. Please verify your credentials and try again.";
    }
    if ( message == null
      || message.contains( "vfs.provider/connect.error" )
      || message.contains( "Connection refused" )
      || message.contains( "Unknown message with code" ) ) {
      return "Unable to connect to server. Please verify the server URL, confirm the server is running, and check your credentials.";
    }

    return message;
  }

  public AuthenticationData getLoginData() {
    return loginData;
  }
}

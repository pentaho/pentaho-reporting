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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.BrowserLoginHandler;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

public class LoginTask implements Runnable {
  private final ReportDesignerContext designerContext;
  private final Component uiContext;
  private final AuthenticatedServerTask followUpTask;
  private final boolean loginForPublish;
  private boolean skipFirstShowDialog;
  private boolean isRetryAfterAuthFailure;
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

  public LoginTask( final ReportDesignerContext designerContext, final Component uiContext,
      final AuthenticatedServerTask followUpTask, final AuthenticationData loginData, final boolean loginForPublish ) {
    this( designerContext, uiContext, followUpTask, loginData, loginForPublish, false );
  }

  public LoginTask( final ReportDesignerContext designerContext, final Component uiContext,
      final AuthenticatedServerTask followUpTask, final AuthenticationData loginData, final boolean loginForPublish,
      final boolean isRetryAfterAuthFailure ) {
    if ( designerContext == null ) {
      throw new NullPointerException();
    }
    if ( uiContext == null ) {
      throw new NullPointerException();
    }
    this.loginForPublish = loginForPublish;
    this.isRetryAfterAuthFailure = isRetryAfterAuthFailure;
    this.designerContext = designerContext;
    this.uiContext = uiContext;
    this.followUpTask = followUpTask;
    if ( loginData != null ) {
      this.loginData = loginData;
      this.skipFirstShowDialog = true;
    } else {
      final ReportDocumentContext reportRenderContext = designerContext.getActiveContext();
      if ( reportRenderContext != null ) {
        final Object o = reportRenderContext.getProperties().get( "pentaho-login-url" );
        if ( o != null ) {
          // prepopulate the dialog with the correct login data, but do not skip login completely.
          this.loginData = RepositoryLoginDialog.getStoredLoginData( String.valueOf( o ), designerContext );
        }
      }
      if ( this.loginData == null ) {
        this.loginData = RepositoryLoginDialog.getDefaultData( designerContext );
      }
      this.skipFirstShowDialog = false;
    }
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
    boolean loginComplete = false;
    do {
      // If this is a retry after authentication failure with browser auth, skip dialog and go directly to browser login
      if ( isRetryAfterAuthFailure ) {
        final Window window = LibSwingUtil.getWindowAncestor( uiContext );
        final AuthenticationData ssoResult =
            performBrowserLogin( window, loginData != null ? loginData.getUrl() : null );
        if ( ssoResult == null ) {
          // Browser login cancelled or failed — fall through to show the dialog
          // on the next loop iteration so the user gets a chance to change
          // settings or switch login method.
          isRetryAfterAuthFailure = false;
          loginComplete = false;
        } else {
          this.loginData = ssoResult;
          loginComplete = true;
          break;
        }
      }
      
      if ( loginDialog == null ) {
        final Window window = LibSwingUtil.getWindowAncestor( uiContext );
        if ( window instanceof Frame ) {
          loginDialog = new RepositoryLoginDialog( (Frame) window, loginForPublish );
        } else if ( window instanceof Dialog ) {
          loginDialog = new RepositoryLoginDialog( (Dialog) window, loginForPublish );
        } else {
          loginDialog = new RepositoryLoginDialog( loginForPublish );
        }
      }

      // Show the login dialog - it has radio buttons for SSO or Username/Password
      this.loginData = loginDialog.performLogin( designerContext, loginData );
      if ( loginData == null ) {
        // User cancelled
        return;
      }
      
      // Check which login method the user selected
      RepositoryLoginDialog.LoginMethod selectedMethod = loginDialog.getLoginMethod();
      
      if ( selectedMethod == RepositoryLoginDialog.LoginMethod.SSO ) {
        // Browser login - user selected SSO — use the URL from the dialog
        final Window window = LibSwingUtil.getWindowAncestor( uiContext );
        final AuthenticationData ssoResult = performBrowserLogin( window, loginData.getUrl() );
        if ( ssoResult == null ) {
          // Browser login cancelled or failed — keep the dialog data (preserves
          // the URL the user typed) and let the do-while loop show the dialog
          // again so the user can retry SSO, switch to username/password, or
          // change the URL.
          loginComplete = false;
        } else {
          // Browser login successful — use the SSO result which contains the
          // session ID and browserAuth flag
          this.loginData = ssoResult;
          loginComplete = true;
        }
      } else {
        // Traditional username/password login
        // loginData already has username/password from the dialog
        
        if ( skipFirstShowDialog ) {
          skipFirstShowDialog = false;
        }
        
        // Validate username/password login
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
              "LoadReportFromRepositoryAction.LoginError.Title" ), Messages.getInstance().formatMessage(
              "LoadReportFromRepositoryAction.LoginError.Message", exception.getMessage() ), exception );
          loginComplete = false;
        } else {
          loginComplete = validateLoginTask.isLoginComplete();
        }
      }
    } while ( loginComplete == false );

    // Determine if we should remember/store the credentials
    final boolean rememberSettings;
    if ( loginDialog != null ) {
      rememberSettings = loginDialog.isRememberSettings();
    } else {
      // For browser login (no dialog shown), always remember credentials
      rememberSettings = true;
    }

    if ( rememberSettings ) {
      final ReportDocumentContext reportRenderContext = designerContext.getActiveContext();
      if ( reportRenderContext != null ) {
        final AuthenticationStore store = reportRenderContext.getAuthenticationStore();
        store.add( loginData, true );
      } else {
        designerContext.getGlobalAuthenticationStore().add( loginData, true );
      }
    }

    final boolean storeUpdates = rememberSettings;

    if ( followUpTask != null ) {
      followUpTask.setLoginData( loginData, storeUpdates );
      SwingUtilities.invokeLater( followUpTask );
    }

    UpdateReservedCharsTask updateReservedCharsTask = new UpdateReservedCharsTask( loginData );
    SwingUtilities.invokeLater( updateReservedCharsTask );
  }

  public AuthenticationData getLoginData() {
    return loginData;
  }

  /**
   * Performs browser-based login independent of username/password dialog.
   * Runs in a background thread to avoid freezing the UI.
   * 
   * @param parentWindow Parent window for dialogs
   * @param serverUrl    The Pentaho server URL exactly as the user entered it in the login
   *                     dialog (e.g. {@code http://127.0.0.1:8080/pentaho}).  The callback
   *                     host is derived from this URL so it matches the entry in
   *                     {@code security.properties}.
   * @return AuthenticationData with session information, or null if cancelled/failed
   */
  private AuthenticationData performBrowserLogin( final Window parentWindow, String serverUrl ) {
    // Use the URL the user typed in the dialog.  Fall back to the stored default only
    // when no URL was provided (should not happen in practice).
    if ( serverUrl == null || serverUrl.trim().isEmpty() ) {
      AuthenticationData defaultData = getDefaultData();
      if ( defaultData != null && defaultData.getUrl() != null ) {
        serverUrl = defaultData.getUrl();
      } else {
        serverUrl = "http://localhost:8080/pentaho";
      }
    }
    
    final String finalServerUrl = serverUrl;

    // Retry loop — keeps trying browser login until success or user cancels
    while ( true ) {
      final AuthenticationData[] result = new AuthenticationData[1];

      // Run browser login in a background thread to avoid freezing UI
      final Thread browserLoginThread = new Thread( new Runnable() {
        public void run() {
          BrowserLoginHandler browserLoginHandler = new BrowserLoginHandler();
          result[0] = browserLoginHandler.startBrowserLogin( finalServerUrl );
        }
      } );
      browserLoginThread.setDaemon( true );
      browserLoginThread.setName( "BrowserLoginThread" );

      // Show progress dialog while waiting for browser login
      final GenericCancelHandler cancelHandler = new GenericCancelHandler( browserLoginThread );
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          browserLoginThread,
          cancelHandler,
          uiContext,
          Messages.getInstance().getString( "LoginTask.BrowserLogin.WaitingMessage" ) );

      if ( cancelHandler.isCancelled() ) {
        return null;
      }

      if ( result[0] != null ) {
        // Login succeeded
        return result[0];
      }

      // Login failed — offer Retry or Cancel
      final int choice = JOptionPane.showOptionDialog( uiContext,
          Messages.getInstance().getString( "LoginTask.BrowserLogin.Failed.Message" ),
          Messages.getInstance().getString( "LoginTask.BrowserLogin.Error.Title" ),
          JOptionPane.YES_NO_OPTION,
          JOptionPane.ERROR_MESSAGE,
          null,
          new String[] {
            Messages.getInstance().getString( "LoginTask.BrowserLogin.Retry" ),
            Messages.getInstance().getString( "LoginTask.BrowserLogin.Cancel" )
          },
          Messages.getInstance().getString( "LoginTask.BrowserLogin.Retry" ) );

      if ( choice != JOptionPane.YES_OPTION ) {
        // User clicked Cancel or closed the dialog
        return null;
      }
      // User clicked Re-Login — loop continues
    }
  }

  /**
   * Gets default authentication data from the authentication store.
   * 
   * @return Default AuthenticationData or null if none exists
   */
  private AuthenticationData getDefaultData() {
    final ReportDocumentContext reportRenderContext = designerContext.getActiveContext();
    if ( reportRenderContext == null ) {
      final String[] knownUrls = designerContext.getGlobalAuthenticationStore().getKnownURLs();
      if ( knownUrls.length > 0 ) {
        return designerContext.getGlobalAuthenticationStore().getCredentials( knownUrls[0] );
      }
    } else {
      final String[] knownUrls = reportRenderContext.getAuthenticationStore().getKnownURLs();
      if ( knownUrls.length > 0 ) {
        return reportRenderContext.getAuthenticationStore().getCredentials( knownUrls[0] );
      }
    }
    
    return null;
  }
}

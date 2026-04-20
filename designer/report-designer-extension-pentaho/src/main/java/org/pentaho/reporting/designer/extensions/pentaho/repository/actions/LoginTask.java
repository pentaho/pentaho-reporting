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

import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.BrowserLoginHandler;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProvider;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;

public class LoginTask implements Runnable {
  private enum LoginResult { SUCCESS, FAILED, CANCELLED }

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
          this.loginData = RepositoryLoginDialog.getStoredLoginData( String.valueOf( o ), designerContext );
        }
      }
      if ( this.loginData == null ) {
        this.loginData = RepositoryLoginDialog.getDefaultData( designerContext );
      }
      this.skipFirstShowDialog = false;
    }
  }

  @SuppressWarnings( "java:S3776" ) 
  public void run() {
    boolean loginComplete = false;
    do {
      if ( isRetryAfterAuthFailure && performRetryAfterAuthFailure() ) {
        break;
      }
      
      if ( loginDialog == null ) {
        loginDialog = createLoginDialog();
      }

      this.loginData = loginDialog.performLogin( designerContext, loginData );
      if ( loginData == null ) {
        return;
      }

      RepositoryLoginDialog.LoginMethod selectedMethod = loginDialog.getLoginMethod();

      if ( selectedMethod != RepositoryLoginDialog.LoginMethod.SSO ) {
        loginData.setOption( AuthenticationHelper.OPTION_BROWSER_AUTH, null );
        loginData.setOption( AuthenticationHelper.OPTION_SESSION_ID, null );
      }

      if ( selectedMethod == RepositoryLoginDialog.LoginMethod.SSO ) {
        loginComplete = performSSOLogin();
      } else {
        final LoginResult result = performUsernamePasswordLogin();
        if ( result == LoginResult.CANCELLED ) {
          return;
        }
        loginComplete = result == LoginResult.SUCCESS;
      }
    } while ( !loginComplete );

    if ( !verifyServerReachable() ) {
      RepositorySessionManager.getInstance().clearSession();
      return;
    }

    storeLoginSession();
    storeRememberedSettings();
    executeFollowUpTasks();
  }

  private boolean performRetryAfterAuthFailure() {
    final Window window = LibSwingUtil.getWindowAncestor( uiContext );
    final OAuthProvider retryProvider = BrowserLoginHandler.recoverOAuthProvider( loginData );
    String retryUrl = getRetryUrl();
    
    final AuthenticationData ssoResult =
        BrowserLoginHandler.performBrowserLoginWithRetry( window, retryUrl, retryProvider );
    if ( ssoResult == null ) {
      isRetryAfterAuthFailure = false;
      return false;
    }
    this.loginData = ssoResult;
    return true;
  }

  private String getRetryUrl() {
    String retryUrl = loginData != null ? loginData.getUrl() : null;
    if ( retryUrl == null || retryUrl.trim().isEmpty() ) {
      final AuthenticationData defaultData = getDefaultData();
      retryUrl = ( defaultData != null && defaultData.getUrl() != null )
          ? defaultData.getUrl() : "http://localhost:8080/pentaho";
    }
    return retryUrl;
  }

  private RepositoryLoginDialog createLoginDialog() {
    final Window window = LibSwingUtil.getWindowAncestor( uiContext );
    RepositoryLoginDialog dialog;
    if ( window instanceof Frame ) {
      dialog = new RepositoryLoginDialog( (Frame) window, loginForPublish );
    } else if ( window instanceof Dialog ) {
      dialog = new RepositoryLoginDialog( (Dialog) window, loginForPublish );
    } else {
      dialog = new RepositoryLoginDialog( loginForPublish );
    }
    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.FULL );
    return dialog;
  }

  private boolean performSSOLogin() {
    final Window window = LibSwingUtil.getWindowAncestor( uiContext );
    final OAuthProvider selectedProvider = loginDialog.getSelectedOAuthProvider();
    final AuthenticationData ssoResult =
        BrowserLoginHandler.performBrowserLoginWithRetry( window, loginData.getUrl(), selectedProvider );
    if ( ssoResult != null ) {
      this.loginData = ssoResult;
      return true;
    }
    return false;
  }

  private LoginResult performUsernamePasswordLogin() {
    if ( skipFirstShowDialog ) {
      skipFirstShowDialog = false;
    }
    
    final ValidateLoginTask validateLoginTask = new ValidateLoginTask( this );
    final Thread loginThread = new Thread( validateLoginTask );
    loginThread.setDaemon( true );
    loginThread.setPriority( Thread.MIN_PRIORITY );

    final GenericCancelHandler cancelHandler = new GenericCancelHandler( loginThread );
    BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( loginThread, cancelHandler, uiContext,
        Messages.getInstance().getString( "LoginTask.ValidateLoginMessage" ) );
    
    if ( cancelHandler.isCancelled() ) {
      return LoginResult.CANCELLED;
    }

    if ( validateLoginTask.getException() != null ) {
      final Exception exception = validateLoginTask.getException();
      ExceptionDialog.showExceptionDialog( uiContext, getServerDownTitle(), getServerDownMessage(), exception );
      return LoginResult.FAILED;
    }
    return validateLoginTask.isLoginComplete() ? LoginResult.SUCCESS : LoginResult.FAILED;
  }

  private boolean verifyServerReachable() {
    if ( !AuthenticationHelper.isBrowserAuth( loginData )
      && !SessionAuthenticationUtil.isServerReachable( loginData ) ) {
      ExceptionDialog.showExceptionDialog( uiContext, getServerDownTitle(), getServerDownMessage(), null );
      return false;
    }
    return true;
  }

  private String getServerDownTitle() {
    return Messages.getInstance().getString( loginForPublish
        ? "PublishToServerAction.ServerDown.Title"
        : "LoadReportFromRepositoryAction.ServerDown.Title" );
  }

  private String getServerDownMessage() {
    return Messages.getInstance().getString( loginForPublish
        ? "PublishToServerAction.ServerDown.Message"
        : "LoadReportFromRepositoryAction.ServerDown.Message" );
  }

  private void storeLoginSession() {
    RepositorySessionManager.getInstance().setSession( loginData, loginData.getUsername() );
  }

  private void storeRememberedSettings() {
    final boolean rememberSettings;
    if ( loginDialog != null ) {
      rememberSettings = loginDialog.isRememberSettings();
    } else {
      rememberSettings = true;
    }

    final ReportDocumentContext reportRenderContext = designerContext.getActiveContext();
    final AuthenticationStore store = ( reportRenderContext != null )
        ? reportRenderContext.getAuthenticationStore()
        : designerContext.getGlobalAuthenticationStore();

    if ( rememberSettings && !AuthenticationHelper.isBrowserAuth( loginData ) ) {
      final AuthenticationData dataToStore = sanitizedForStore( loginData );
      store.add( dataToStore, true );
    } else if ( !rememberSettings && loginData != null && loginData.getUrl() != null
        && !AuthenticationHelper.isBrowserAuth( loginData ) ) {
      store.removeCredentials( loginData.getUrl() );
    }
  }

  /**
   * Builds a copy of {@code data} safe to persist: transient SSO markers
   * ({@code sessionId}, {@code browserAuth}) are stripped so that a stored
   * entry is never mistakenly detected as an active SSO session later.
   * <p>
   * This method is only called for non-SSO (username/password) logins; SSO
   * sessions never touch the auth store at all.
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
      "lastFilename"
    };
    for ( String key : preserved ) {
      final String v = data.getOption( key );
      if ( v != null ) {
        copy.setOption( key, v );
      }
    }
    copy.setOption( AuthenticationHelper.OPTION_SESSION_ID, null );
    copy.setOption( AuthenticationHelper.OPTION_BROWSER_AUTH, null );
    return copy;
  }

  private void executeFollowUpTasks() {
    final boolean rememberSettings = loginDialog == null
        || loginDialog.isRememberSettings();

    if ( followUpTask != null ) {
      followUpTask.setLoginData( loginData, rememberSettings );
      SwingUtilities.invokeLater( followUpTask );
    }

    UpdateReservedCharsTask updateReservedCharsTask = new UpdateReservedCharsTask( loginData );
    SwingUtilities.invokeLater( updateReservedCharsTask );
  }

  public AuthenticationData getLoginData() {
    return loginData;
  }

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

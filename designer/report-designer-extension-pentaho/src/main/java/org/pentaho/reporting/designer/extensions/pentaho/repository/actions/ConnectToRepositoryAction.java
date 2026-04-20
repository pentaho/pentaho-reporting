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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.BrowserLoginHandler;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProvider;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public final class ConnectToRepositoryAction extends AbstractDesignerContextAction
  implements PropertyChangeListener {

  public ConnectToRepositoryAction() {
    putValue( Action.NAME, Messages.getInstance().getString( "ConnectToRepositoryAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION,
      Messages.getInstance().getString( "ConnectToRepositoryAction.Description" ) );
    setEnabled( !RepositorySessionManager.getInstance().hasActiveSession() );
    RepositorySessionManager.getInstance().addPropertyChangeListener( this );
  }

  @Override
  public void propertyChange( final PropertyChangeEvent evt ) {
    setEnabled( !RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Override
  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    if ( newContext == null ) {
      setEnabled( false );
    } else {
      setEnabled( !RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  private boolean validateCredentials( final AuthenticationData loginData ) {
    return SessionAuthenticationUtil.isServerReachable( loginData );
  }

  @Override
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext context = getReportDesignerContext();
    if ( context == null ) {
      return;
    }

    final Window window = LibSwingUtil.getWindowAncestor( context.getView().getParent() );
    final RepositoryLoginDialog loginDialog = createLoginDialog( window );
    loginDialog.setDialogMode( RepositoryLoginDialog.DialogMode.FULL );

    final AuthenticationData dialogData = loginDialog.performLogin( context, null );
    if ( dialogData == null ) {
      return;
    }

    if ( loginDialog.getLoginMethod() == RepositoryLoginDialog.LoginMethod.SSO ) {
      final OAuthProvider provider = loginDialog.getSelectedOAuthProvider();
      final AuthenticationData ssoResult =
        BrowserLoginHandler.performBrowserLoginWithRetry( window, dialogData.getUrl(), provider );
      if ( ssoResult != null ) {
        storeSession( context, loginDialog, ssoResult );
      }
    } else {
      // The dialog may have prefilled dialogData from a previously stored SSO
      // entry. Strip stale SSO markers so downstream isBrowserAuth() checks
      // correctly classify this as a basic-auth login.
      dialogData.setOption( AuthenticationHelper.OPTION_BROWSER_AUTH, null );
      dialogData.setOption( AuthenticationHelper.OPTION_SESSION_ID, null );
      if ( validateCredentials( dialogData ) ) {
        storeSession( context, loginDialog, dialogData );
      } else {
        JOptionPane.showMessageDialog(
          context.getView().getParent(),
          Messages.getInstance().getString( "ConnectToRepositoryAction.LoginError.Message" ),
          Messages.getInstance().getString( "ConnectToRepositoryAction.LoginError.Title" ),
          JOptionPane.ERROR_MESSAGE );
      }
    }
  }

  private void storeSession( final ReportDesignerContext context,
                             final RepositoryLoginDialog loginDialog,
                             final AuthenticationData loginData ) {
    final String username = loginData.getUsername() != null ? loginData.getUsername() : "";
    RepositorySessionManager.getInstance().setSession( loginData, username );

    // Persist to the global auth store only for username/password logins
    // where the user ticked "Remember my settings".
    if ( loginDialog.isRememberSettings() && !AuthenticationHelper.isBrowserAuth( loginData ) ) {
      context.getGlobalAuthenticationStore().add( loginData, true );
      final org.pentaho.reporting.designer.core.editor.ReportDocumentContext active = context.getActiveContext();
      if ( active != null ) {
        active.getAuthenticationStore().add( loginData, true );
      }
    }

    JOptionPane.showMessageDialog(
      context.getView().getParent(),
      Messages.getInstance().formatMessage( "ConnectToRepositoryAction.Success.Message", username ),
      Messages.getInstance().getString( "ConnectToRepositoryAction.Success.Title" ),
      JOptionPane.INFORMATION_MESSAGE );
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

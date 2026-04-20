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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingUtilities;

public final class LoadReportFromRepositoryAction extends AbstractDesignerContextAction {
  public LoadReportFromRepositoryAction() {
    putValue( Action.NAME, Messages.getInstance().getString( "LoadReportFromRepositoryAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "LoadReportFromRepositoryAction.Description" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getOpenIcon() );
    putValue( Action.ACCELERATOR_KEY, Messages.getInstance().getOptionalKeyStroke(
        "LoadReportFromRepositoryAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    if ( reportDesignerContext == null ) {
      return;
    }
    final OpenFileFromRepositoryTask openFileFromRepositoryTask =
        new OpenFileFromRepositoryTask( reportDesignerContext, reportDesignerContext.getView().getParent() );

    final AuthenticationData cachedSession = RepositorySessionManager.getInstance().getSession();
    if ( cachedSession != null ) {
      // Only SSO/browser-auth sessions can "expire" server-side; username/password
      // re-sends credentials on every request, so probing it is meaningless and any
      // transient probe failure would wrongly trigger the "Login Again" dialog.
      if ( AuthenticationHelper.isBrowserAuth( cachedSession )
          && SessionAuthenticationUtil.isSessionExplicitlyExpired( cachedSession ) ) {
        // SSO session was explicitly rejected (401/403 on two attempts) -- ask
        // the user before re-logging in.
        final String[] options = {
            Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.LoginAgain" ),
            Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.Cancel" )
        };
        final int choice = javax.swing.JOptionPane.showOptionDialog(
            reportDesignerContext.getView().getParent(),
            Messages.getInstance().getString( "LoadReportFromRepositoryAction.SessionExpired.Message" ),
            Messages.getInstance().getString( "LoadReportFromRepositoryAction.SessionExpired.Title" ),
            javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE, null,
            options, options[ 0 ] );
        if ( choice != javax.swing.JOptionPane.YES_OPTION ) {
          return;
        }
        RepositorySessionManager.getInstance().clearSession();
        final LoginTask reLoginTask =
            new LoginTask( reportDesignerContext, reportDesignerContext.getView().getParent(),
                openFileFromRepositoryTask, cachedSession, false, true );
        SwingUtilities.invokeLater( reLoginTask );
        return;
      }
      openFileFromRepositoryTask.setLoginData( cachedSession, false );
      SwingUtilities.invokeLater( openFileFromRepositoryTask );
      return;
    }
    
    final LoginTask loginTask =
        new LoginTask( reportDesignerContext, reportDesignerContext.getView().getParent(),
            openFileFromRepositoryTask, null, false );
    SwingUtilities.invokeLater( loginTask );
  }
}

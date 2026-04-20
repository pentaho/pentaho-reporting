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
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.report.SaveReportAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * User: Martin Date: 25.01.2006 Time: 11:26:24
 */
public class PublishToServerAction extends AbstractReportContextAction {
  public PublishToServerAction() {
    putValue( Action.NAME, Messages.getInstance().getString( "PublishToServerAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "PublishToServerAction.Description" ) );
    final URL url =
        PublishToServerAction.class
            .getResource( "/org/pentaho/reporting/designer/extensions/pentaho/repository/resources/PublishToServerIcon.png" );
    if ( url != null ) {
      putValue( Action.SMALL_ICON, new ImageIcon( url ) );
    }
    putValue( Action.ACCELERATOR_KEY, Messages.getInstance().getKeyStroke( "PublishToServerAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {

    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    if ( activeContext.isChanged() ) {
      // ask the user and maybe save the report..
      final int option =
          JOptionPane.showConfirmDialog( reportDesignerContext.getView().getParent(), Messages.getInstance().getString(
              "PublishToServerAction.ReportModifiedWarning.Message" ), Messages.getInstance().getString(
              "PublishToServerAction.ReportModifiedWarning.Title" ), JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.WARNING_MESSAGE );
      if ( option == JOptionPane.YES_OPTION
          && !new SaveReportAction().saveReport( reportDesignerContext, activeContext,
              reportDesignerContext.getView().getParent() ) ) {
        return;
      }
      if ( option == JOptionPane.CANCEL_OPTION ) {
        return;
      }
    }

    final PublishToServerTask publishToServerTask =
        new PublishToServerTask( reportDesignerContext, reportDesignerContext.getView().getParent() );

    final AuthenticationData cachedSession = RepositorySessionManager.getInstance().getSession();
    if ( cachedSession != null ) {
      // Only SSO/browser-auth sessions can "expire" server-side; username/password
      // re-sends credentials on every request, so probing it is meaningless and any
      // transient probe failure would wrongly trigger the "Login Again" dialog.
      if ( AuthenticationHelper.isBrowserAuth( cachedSession )
          && SessionAuthenticationUtil.isSessionExplicitlyExpired( cachedSession ) ) {
        handleExpiredSsoSession( reportDesignerContext, publishToServerTask, cachedSession );
        return;
      }
      publishToServerTask.setLoginData( cachedSession, false );
      SwingUtilities.invokeLater( publishToServerTask );
    } else {
      final LoginTask loginTask =
          new LoginTask( reportDesignerContext, reportDesignerContext.getView().getParent(), publishToServerTask,
              null, true );
      SwingUtilities.invokeLater( loginTask );
    }

  }

  /**
   * Prompts the user that the SSO session was explicitly rejected (401/403 on
   * two consecutive probes) and, on confirmation, clears the cached session and
   * dispatches a fresh {@link LoginTask} that will resume the publish flow.
   * Extracted from {@link #actionPerformed(ActionEvent)} purely to keep that
   * method's cognitive complexity within Sonar's limit; behaviour is unchanged.
   */
  private void handleExpiredSsoSession( final ReportDesignerContext reportDesignerContext,
                                        final PublishToServerTask publishToServerTask,
                                        final AuthenticationData cachedSession ) {
    final String[] options = {
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.LoginAgain" ),
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.Cancel" )
    };
    final int choice = JOptionPane.showOptionDialog(
        reportDesignerContext.getView().getParent(),
        Messages.getInstance().getString( "PublishToServerAction.SessionExpired.Message" ),
        Messages.getInstance().getString( "PublishToServerAction.SessionExpired.Title" ),
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
        options, options[ 0 ] );
    if ( choice != JOptionPane.YES_OPTION ) {
      return;
    }
    RepositorySessionManager.getInstance().clearSession();
    final LoginTask reLoginTask =
        new LoginTask( reportDesignerContext, reportDesignerContext.getView().getParent(),
            publishToServerTask, cachedSession, true, true );
    SwingUtilities.invokeLater( reLoginTask );
  }

}

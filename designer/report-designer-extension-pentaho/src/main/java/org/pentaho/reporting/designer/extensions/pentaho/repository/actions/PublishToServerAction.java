/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.report.SaveReportAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

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

  /**
   * Invoked when an action occurs.
   */
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
      if ( option == JOptionPane.YES_OPTION ) {
        if ( ( new SaveReportAction() ).saveReport( reportDesignerContext, activeContext, reportDesignerContext
            .getView().getParent() ) == false ) {
          return;
        }
      }
      if ( option == JOptionPane.CANCEL_OPTION ) {
        return;
      }
    }

    final PublishToServerTask publishToServerTask =
        new PublishToServerTask( reportDesignerContext, reportDesignerContext.getView().getParent() );
    final LoginTask loginTask =
        new LoginTask( reportDesignerContext, reportDesignerContext.getView().getParent(), publishToServerTask, null,
            true );

    SwingUtilities.invokeLater( loginTask );

  }

}

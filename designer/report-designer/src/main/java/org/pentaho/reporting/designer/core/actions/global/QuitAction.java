/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.core.actions.global;

import org.apache.logging.log4j.LogManager;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.report.CloseReportAction;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.libraries.designtime.swing.ConsumableActionEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class QuitAction extends AbstractDesignerContextAction {
  public QuitAction() {
    putValue( Action.NAME, ActionMessages.getString( "QuitAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "QuitAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "QuitAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "QuitAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext context = getReportDesignerContext();
    final WorkspaceSettings theWorkspaceSettings = WorkspaceSettings.getInstance();
    theWorkspaceSettings.setBounds( context.getView().getParent().getBounds() );
    final int contextCount = context.getReportRenderContextCount();
    final ReportRenderContext[] contextArray = new ReportRenderContext[ contextCount ];
    for ( int i = 0; i < contextCount; i++ ) {
      contextArray[ i ] = context.getReportRenderContext( i );
    }

    final ReportRenderContext[] filteredArray = CloseReportAction.filterSubreports( getReportDesignerContext(),
      contextArray );

    for ( int i = 0; i < filteredArray.length; i++ ) {
      final ReportRenderContext reportRenderContext = filteredArray[ i ];
      if ( CloseReportAction.performCloseReport( getReportDesignerContext(), reportRenderContext ) == false ) {
        return;
      }
    }

    if ( e instanceof ConsumableActionEvent ) {
      // indicate that this event should not be handled ..
      final ConsumableActionEvent ce = (ConsumableActionEvent) e;
      ce.consume();
    }

    LogManager.shutdown();
    System.exit( 0 );
  }
}

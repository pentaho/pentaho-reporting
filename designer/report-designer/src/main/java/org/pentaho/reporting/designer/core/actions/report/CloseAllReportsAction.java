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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CloseAllReportsAction extends AbstractReportContextAction {
  public CloseAllReportsAction() {
    putValue( Action.NAME, ActionMessages.getString( "CloseAllReportsAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "CloseAllReportsAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "CloseAllReportsAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "CloseAllReportsAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext context = getReportDesignerContext();
    final int contextCount = context.getReportRenderContextCount();
    final ReportRenderContext[] contextArray = new ReportRenderContext[ contextCount ];
    for ( int i = 0; i < contextCount; i++ ) {
      contextArray[ i ] = context.getReportRenderContext( i );
    }

    final ReportRenderContext[] filteredArray =
      CloseReportAction.filterSubreports( getReportDesignerContext(), contextArray );

    for ( int i = 0; i < filteredArray.length; i++ ) {
      final ReportRenderContext reportRenderContext = filteredArray[ i ];
      if ( CloseReportAction.performCloseReport( getReportDesignerContext(), reportRenderContext ) == false ) {
        return;
      }
    }
  }
}

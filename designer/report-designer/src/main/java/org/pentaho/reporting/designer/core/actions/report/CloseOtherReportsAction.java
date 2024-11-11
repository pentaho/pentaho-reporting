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


package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CloseOtherReportsAction extends AbstractReportContextAction {
  private int tabIndex;

  public CloseOtherReportsAction() {
    this( -1 );
  }

  public CloseOtherReportsAction( final int indexToClose ) {
    this.tabIndex = indexToClose;
    putValue( Action.NAME, ActionMessages.getString( "CloseOtherReportsAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "CloseOtherReportsAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "CloseOtherReportsAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "CloseOtherReportsAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if ( tabIndex == -1 ) {
      final ReportDocumentContext activeContext = getActiveContext();
      if ( activeContext == null ) {
        return;
      }

      performCloseReport( reportDesignerContext1, activeContext );
    } else {
      if ( tabIndex >= 0 && tabIndex < reportDesignerContext1.getReportRenderContextCount() ) {
        final ReportRenderContext context = reportDesignerContext1.getReportRenderContext( tabIndex );
        performCloseReport( reportDesignerContext1, context );
      }
    }
  }

  private void performCloseReport( final ReportDesignerContext context, final ReportDocumentContext activeContext ) {
    final int contextCount = context.getReportRenderContextCount();
    final ArrayList<ReportRenderContext> contexts = new ArrayList<ReportRenderContext>( contextCount );
    for ( int i = 0; i < contextCount; i++ ) {
      final ReportRenderContext renderContext = context.getReportRenderContext( i );
      if ( renderContext == activeContext ) {
        continue;
      }
      if ( ModelUtility.isDescendant( renderContext.getReportDefinition(), activeContext.getReportDefinition() )
        == false ) {
        contexts.add( renderContext );
      }
    }


    final ReportRenderContext[] contextArray = contexts.toArray( new ReportRenderContext[ contexts.size() ] );
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

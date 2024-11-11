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
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Closes all childs of the currently selected context.
 *
 * @author Thomas Morgner
 */
public class CloseChildReportsAction extends AbstractReportContextAction {
  private int tabIndex;

  public CloseChildReportsAction() {
    this( -1 );
  }

  public CloseChildReportsAction( final int tabIndex ) {
    this.tabIndex = tabIndex;
    putValue( Action.NAME, ActionMessages.getString( "CloseChildReportsAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "CloseChildReportsAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "CloseChildReportsAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "CloseChildReportsAction.Accelerator" ) );
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

  private void performCloseReport( final ReportDesignerContext context,
                                   final ReportDocumentContext activeContext ) {
    final int contextCount = context.getReportRenderContextCount();
    final ArrayList<ReportRenderContext> contexts = new ArrayList<ReportRenderContext>( contextCount );
    final AbstractReportDefinition parentReportDefinition = activeContext.getReportDefinition();
    for ( int i = 0; i < contextCount; i++ ) {
      final ReportRenderContext renderContext = context.getReportRenderContext( i );
      final AbstractReportDefinition childReportDefinition = renderContext.getReportDefinition();
      if ( parentReportDefinition != childReportDefinition &&
        ModelUtility.isDescendant( parentReportDefinition, childReportDefinition ) ) {
        contexts.add( renderContext );
      }
    }

    for ( int i = 0; i < contexts.size(); i++ ) {
      // there are only subreports here ..
      final ReportRenderContext renderContext = contexts.get( i );
      CloseReportAction.performUnconditionalClose( getReportDesignerContext(), renderContext );
    }
  }
}

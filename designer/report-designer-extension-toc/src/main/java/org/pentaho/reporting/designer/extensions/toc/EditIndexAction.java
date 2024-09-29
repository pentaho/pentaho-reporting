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


package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.extensions.toc.IndexElement;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditIndexAction extends AbstractElementSelectionAction {
  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  public EditIndexAction() {
    putValue( Action.NAME, Messages.getInstance().getString( "EditIndexAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "EditIndexAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, Messages.getInstance().getOptionalMnemonic( "EditIndexAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, Messages.getInstance().getOptionalKeyStroke( "EditIndexAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  protected void updateSelection() {
    if ( isSingleElementSelection() == false ) {
      setEnabled( false );
      return;
    }

    final Object selectedElement = getSelectionModel().getSelectedElement( 0 );
    if ( selectedElement instanceof IndexElement ) {
      setEnabled( true );
      return;
    }
    setEnabled( false );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext designerContext = getReportDesignerContext();
    if ( designerContext == null ) {
      return;
    }

    final ReportDocumentContext activeReportContext = getActiveContext();
    if ( activeReportContext == null ) {
      return;
    }

    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if ( selectionModel1 == null ) {
      return;
    }
    final Object leadSelection = selectionModel1.getLeadSelection();
    if ( leadSelection instanceof Element == false ) {
      return;
    }

    final Element element = (Element) leadSelection;
    if ( element instanceof IndexElement ) {
      final int contextCount = designerContext.getReportRenderContextCount();
      for ( int i = 0; i < contextCount; i++ ) {
        final ReportRenderContext rrc = designerContext.getReportRenderContext( i );
        if ( rrc.getReportDefinition() == element ) {
          designerContext.setActiveDocument( rrc );
          return;
        }
      }

      final IndexElement report = (IndexElement) element;
      try {
        designerContext.addSubReport( activeReportContext, report );
      } catch ( ReportDataFactoryException e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }
    }

  }
}

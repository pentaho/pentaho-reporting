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

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportQueryNode;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActivateQueryAction extends AbstractElementSelectionAction implements ToggleStateAction {
  public ActivateQueryAction() {
    putValue( Action.NAME, ActionMessages.getString( "ActivateQueryAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ActivateQueryAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ActivateQueryAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ActivateQueryAction.Accelerator" ) );
  }

  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  protected void updateSelection() {
    final DocumentContextSelectionModel reportSelectionModel = getSelectionModel();
    if ( reportSelectionModel == null ) {
      setEnabled( false );
      return;
    }

    final Object[] selectedElements = reportSelectionModel.getSelectedElements();
    for ( int i = 0; i < selectedElements.length; i++ ) {
      final Object selectedElement = selectedElements[ i ];
      if ( selectedElement instanceof ReportQueryNode ) {
        final ReportQueryNode node = (ReportQueryNode) selectedElement;
        setSelected(
          ObjectUtilities.equal( node.getQueryName(), getActiveContext().getReportDefinition().getQuery() ) );
        setEnabled( true );
        return;
      }
    }

    setEnabled( false );
  }

  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel reportSelectionModel = getSelectionModel();
    if ( reportSelectionModel == null ) {
      return;
    }

    final Object[] selectedElements = reportSelectionModel.getSelectedElements();
    for ( int i = 0; i < selectedElements.length; i++ ) {
      final Object selectedElement = selectedElements[ i ];
      if ( selectedElement instanceof ReportQueryNode ) {
        final ReportQueryNode node = (ReportQueryNode) selectedElement;
        getActiveContext().getReportDefinition().setQuery( node.getQueryName() );
        return;
      }
    }
  }
}

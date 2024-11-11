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


package org.pentaho.reporting.designer.core.actions.elements;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.groups.EditGroupDetailsDialog;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.EditGroupUndoEntry;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public final class EditGroupAction extends AbstractElementSelectionAction {
  public EditGroupAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditGroupAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "EditGroupAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditGroupAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditGroupAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getGroupIcon() );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel selectionModel = getSelectionModel();
    if ( selectionModel == null ) {
      return;
    }

    if ( selectionModel.getSelectionCount() != 1 ) {
      return;
    }
    final Object selectedElement = selectionModel.getSelectedElement( 0 );
    if ( selectedElement instanceof RelationalGroup == false ) {
      setEnabled( false );
      return;
    }

    final EditGroupUndoEntry groupUndoEntry = performEditGroup( getReportDesignerContext(), selectedElement, false );
    if ( groupUndoEntry != null ) {
      final ReportDocumentContext activeContext = getActiveContext();
      groupUndoEntry.redo( activeContext );
    }
  }

  public static EditGroupUndoEntry performEditGroup( final ReportDesignerContext context,
                                                     final Object selectedElement,
                                                     final boolean addGroup ) {
    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final EditGroupDetailsDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new EditGroupDetailsDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new EditGroupDetailsDialog( (JFrame) window );
    } else {
      dialog = new EditGroupDetailsDialog();
    }

    final RelationalGroup group = (RelationalGroup) selectedElement;
    return dialog.editGroup( group, context.getActiveContext(), addGroup );
  }

  protected void updateSelection() {
    if ( isSingleElementSelection() == false ) {
      setEnabled( false );
      return;
    }

    final Object selectedElement = getSelectionModel().getSelectedElement( 0 );
    if ( selectedElement instanceof RelationalGroup == false ) {
      setEnabled( false );
      return;
    }
    setEnabled( true );
  }
}

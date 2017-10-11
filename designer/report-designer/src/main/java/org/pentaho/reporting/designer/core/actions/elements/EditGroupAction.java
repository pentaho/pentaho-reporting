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

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


package org.pentaho.reporting.designer.core.actions.elements.move;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.report.drag.MoveDragOperation;
import org.pentaho.reporting.designer.core.editor.report.snapping.EmptySnapModel;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntryBuilder;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public final class MoveRightAction extends AbstractElementSelectionAction {
  public MoveRightAction() {
    putValue( Action.NAME, ActionMessages.getString( "MoveRightAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "MoveRightAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "MoveRightAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "MoveRightAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType( Element.class );
    if ( visualElements.isEmpty() ) {
      return;
    }

    final MassElementStyleUndoEntryBuilder builder = new MassElementStyleUndoEntryBuilder( visualElements );
    final MoveDragOperation mop = new MoveDragOperation
      ( visualElements, new Point(), EmptySnapModel.INSTANCE, EmptySnapModel.INSTANCE );
    mop.update( new Point( +5, 0 ), 1 );
    mop.finish();

    final MassElementStyleUndoEntry massElementStyleUndoEntry = builder.finish();
    getActiveContext().getUndo()
      .addChange( ActionMessages.getString( "MoveRightAction.UndoName" ), massElementStyleUndoEntry );
  }
}

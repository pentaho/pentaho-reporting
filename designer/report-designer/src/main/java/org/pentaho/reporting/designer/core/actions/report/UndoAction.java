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

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class UndoAction extends AbstractReportContextAction implements ChangeListener {
  public UndoAction() {
    putValue( Action.NAME, ActionMessages.getString( "UndoAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "UndoAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "UndoAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getUndoIconSmall() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "UndoAction.Accelerator" ) );
  }

  /**
   * Invoked when the target of the listener has changed its state.
   *
   * @param e a ChangeEvent object
   */
  public void stateChanged( final ChangeEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }
    setEnabled( activeContext.getUndo().isUndoPossible() );
    if ( isEnabled() ) {
      putValue( Action.SHORT_DESCRIPTION,
        ActionMessages.getString( "UndoAction.DescriptionPattern", activeContext.getUndo().getUndoName() ) );
    } else {
      putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "UndoAction.Description" ) );
    }
  }

  protected void updateActiveContext( final ReportDocumentContext oldContext, final ReportDocumentContext newContext ) {
    super.updateActiveContext( oldContext, newContext );
    if ( oldContext != null ) {
      oldContext.getUndo().removeUndoListener( this );
    }
    if ( newContext != null ) {
      newContext.getUndo().addUndoListener( this );
    }
    stateChanged( null );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {

    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }
    activeContext.getUndo().undo( activeContext );
    activeContext.getReportDefinition().notifyNodeStructureChanged();
  }
}

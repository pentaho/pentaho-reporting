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

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;

public final class RedoAction extends AbstractReportContextAction implements ChangeListener {
  public RedoAction() {
    putValue( Action.NAME, ActionMessages.getString( "RedoAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "RedoAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "RedoAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getRedoIconSmall() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "RedoAction.Accelerator" ) );
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
    setEnabled( activeContext.getUndo().isRedoPossible() );
    if ( isEnabled() ) {
      putValue( Action.SHORT_DESCRIPTION,
        ActionMessages.getString( "RedoAction.DescriptionPattern", activeContext.getUndo().getRedoName() ) );
    } else {
      putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "RedoAction.Description" ) );
    }
  }

  protected void updateActiveContext( final ReportRenderContext oldContext, final ReportRenderContext newContext ) {
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
    activeContext.getUndo().redo( activeContext );
    activeContext.getReportDefinition().notifyNodeStructureChanged();
  }
}

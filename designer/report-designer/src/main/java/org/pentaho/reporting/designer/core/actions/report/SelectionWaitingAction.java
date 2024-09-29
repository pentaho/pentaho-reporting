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
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document me
 *
 * @author Thomas Morgner
 */
public class SelectionWaitingAction extends AbstractReportContextAction
  implements ToggleStateAction {
  private class SelectionWaitingHandler implements PropertyChangeListener {
    private SelectionWaitingHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      putValue( Action.SELECTED_KEY, evt.getNewValue() );
    }
  }

  private PropertyChangeListener selectionWaitingHandler;

  public SelectionWaitingAction() {
    selectionWaitingHandler = new SelectionWaitingHandler();
    putValue( Action.NAME, ActionMessages.getString( "SelectionWaitingAction.Text" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getRubberbandSelectionIcon() );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "SelectionWaitingAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "SelectionWaitingAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "SelectionWaitingAction.Accelerator" ) );
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    super.updateDesignerContext( oldContext, newContext );
    if ( oldContext != null ) {
      oldContext
        .removePropertyChangeListener( ReportDesignerContext.SELECTION_WAITING_PROPERTY, selectionWaitingHandler );
    }
    if ( newContext != null ) {
      newContext.addPropertyChangeListener( ReportDesignerContext.SELECTION_WAITING_PROPERTY, selectionWaitingHandler );
    }
  }


  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext activeContext = getReportDesignerContext();
    if ( activeContext == null ) {
      return;
    }

    activeContext.setSelectionWaiting( activeContext.isSelectionWaiting() == false );
  }
}

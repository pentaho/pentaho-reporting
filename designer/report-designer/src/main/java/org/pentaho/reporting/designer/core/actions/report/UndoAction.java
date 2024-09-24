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

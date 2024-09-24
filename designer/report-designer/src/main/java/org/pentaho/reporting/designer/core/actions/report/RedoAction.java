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

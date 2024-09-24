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

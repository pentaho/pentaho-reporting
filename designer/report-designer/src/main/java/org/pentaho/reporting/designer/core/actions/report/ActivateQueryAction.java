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

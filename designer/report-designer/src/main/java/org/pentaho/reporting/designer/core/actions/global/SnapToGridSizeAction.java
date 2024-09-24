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

package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.GridSizeDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class SnapToGridSizeAction extends AbstractDesignerContextAction {
  public SnapToGridSizeAction() {
    putValue( Action.NAME, ActionMessages.getString( "SnapToGridSizeAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "SnapToGridSizeAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "SnapToGridSizeAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "SnapToGridSizeAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final GridSizeDialog spinnerDialog;
    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    if ( window instanceof JDialog ) {
      spinnerDialog = new GridSizeDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      spinnerDialog = new GridSizeDialog( (JFrame) window );
    } else {
      spinnerDialog = new GridSizeDialog();
    }

    spinnerDialog.setUnit( WorkspaceSettings.getInstance().getUnit() );
    spinnerDialog.setGridDivisionSize( WorkspaceSettings.getInstance().getGridDivisions() );
    spinnerDialog.setGridSize( WorkspaceSettings.getInstance().getGridSize() );

    if ( spinnerDialog.showDialog() ) {
      WorkspaceSettings.getInstance().setUnit( spinnerDialog.getUnit() );
      WorkspaceSettings.getInstance().setGridSize( spinnerDialog.getGridSize() );
      WorkspaceSettings.getInstance().setGridDivisions( spinnerDialog.getGridDivisionSize() );
    }
  }
}

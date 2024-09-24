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

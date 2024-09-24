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
import org.pentaho.reporting.designer.core.util.SystemInformationDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public final class SystemInformationAction extends AbstractDesignerContextAction {
  public SystemInformationAction() {
    putValue( Action.NAME, ActionMessages.getString( "SystemInformationAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "SystemInformationAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "SystemInformationAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "SystemInformationAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final SystemInformationDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new SystemInformationDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new SystemInformationDialog( (JFrame) window );
    } else {
      dialog = new SystemInformationDialog();
    }
    dialog.performShow();
  }
}

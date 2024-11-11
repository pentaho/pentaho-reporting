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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.groups.EditGroupsDialog;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditGroupsAction extends AbstractReportContextAction {
  public EditGroupsAction() {
    super();
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getGenericSquare() );
    putValue( Action.NAME, ActionMessages.getString( "EditGroupsAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "EditGroupsAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditGroupsAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditGroupsAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final ReportDesignerContext context = getReportDesignerContext();
    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final EditGroupsDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new EditGroupsDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new EditGroupsDialog( (JFrame) window );
    } else {
      dialog = new EditGroupsDialog();
    }


    dialog.editGroups( activeContext );
  }
}

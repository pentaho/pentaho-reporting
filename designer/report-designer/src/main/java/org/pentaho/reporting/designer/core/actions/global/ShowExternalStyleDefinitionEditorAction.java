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


package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.styles.styleeditor.StyleDefinitionEditorDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowExternalStyleDefinitionEditorAction extends AbstractDesignerContextAction {
  public ShowExternalStyleDefinitionEditorAction() {
    putValue( Action.NAME, ActionMessages.getString( "ShowExternalStyleDefinitionEditorAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION,
      ActionMessages.getString( "ShowExternalStyleDefinitionEditorAction.Description" ) );
    putValue( Action.MNEMONIC_KEY,
      ActionMessages.getOptionalMnemonic( "ShowExternalStyleDefinitionEditorAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "ShowExternalStyleDefinitionEditorAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext context = getReportDesignerContext();
    final StyleDefinitionEditorDialog dialog =
      StyleDefinitionEditorDialog.createDialog( context.getView().getParent(), context );
    dialog.setStandalone( true );
    dialog.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
    dialog.setVisible( true );
  }
}

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

package org.pentaho.reporting.designer.core.editor.styles.styleeditor;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.editor.styles.Messages;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class OpenAction extends AbstractDesignerContextAction {
  private StyleDefinitionEditorContext editorContext;

  public OpenAction( final StyleDefinitionEditorContext editorContext ) {
    this.editorContext = editorContext;
    putValue( Action.NAME, Messages.getString( "StyleDefinitionEditorDialog.OpenAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, Messages.getString( "StyleDefinitionEditorDialog.OpenAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, Messages.getOptionalMnemonic( "StyleDefinitionEditorDialog.OpenAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      Messages.getOptionalKeyStroke( "StyleDefinitionEditorDialog.OpenAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getOpenIcon() );

    setReportDesignerContext( editorContext.getDesignerContext() );
    setEnabled( true );
  }

  public void actionPerformed( final ActionEvent e ) {
    StyleDefinitionUtilities.openStyleDefinition( editorContext );
  }
}

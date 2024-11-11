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


package org.pentaho.reporting.designer.core.actions.elements.format;

import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.format.ElementFormatDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class EditBorderStyleAction extends ElementFormatAction {
  public EditBorderStyleAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditBorderStyleAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "EditBorderStyleAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditBorderStyleAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditBorderStyleAction.Accelerator" ) );
  }

  protected ElementFormatDialog createDialog( final Window window ) {
    final ElementFormatDialog elementFormatDialog = super.createDialog( window );
    elementFormatDialog.setActivePane( ElementFormatDialog.BORDERS_PANE );
    return elementFormatDialog;
  }
}

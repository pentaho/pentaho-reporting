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
public class EditColorsStyleAction extends ElementFormatAction {
  public EditColorsStyleAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditColorsStyleAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "EditColorsStyleAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditColorsStyleAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditColorsStyleAction.Accelerator" ) );
  }

  protected ElementFormatDialog createDialog( final Window window ) {
    final ElementFormatDialog elementFormatDialog = super.createDialog( window );
    elementFormatDialog.setActivePane( ElementFormatDialog.COLOR_PANE );
    return elementFormatDialog;
  }
}

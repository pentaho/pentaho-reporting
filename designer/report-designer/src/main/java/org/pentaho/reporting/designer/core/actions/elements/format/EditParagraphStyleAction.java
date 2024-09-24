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
public class EditParagraphStyleAction extends ElementFormatAction {
  public EditParagraphStyleAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditParagraphStyleAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "EditParagraphStyleAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditParagraphStyleAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditParagraphStyleAction.Accelerator" ) );
  }

  protected ElementFormatDialog createDialog( final Window window ) {
    final ElementFormatDialog elementFormatDialog = super.createDialog( window );
    elementFormatDialog.setActivePane( ElementFormatDialog.PARAGRAPH_PANE );
    return elementFormatDialog;
  }
}

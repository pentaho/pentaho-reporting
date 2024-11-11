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


package org.pentaho.reporting.designer.core.util;

import org.pentaho.openformula.ui.FormulaEditorDialog;
import org.pentaho.reporting.libraries.designtime.swing.ToolbarButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DesignerFormulaEditorDialog extends FormulaEditorDialog {
  private class InsertTextAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private InsertTextAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getHyperlinkIcon() );
      putValue( Action.SHORT_DESCRIPTION,
        UtilMessages.getInstance().getString( "DesignerFormulaEditorDialog.InsertDrillDown" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      insertText( "=DRILLDOWN(\"Text\"; \"Text\"; Any)" );  // NON-NLS
    }
  }

  public DesignerFormulaEditorDialog() {
  }

  public DesignerFormulaEditorDialog( final Frame owner ) {
    super( owner );
  }

  public DesignerFormulaEditorDialog( final Dialog owner ) {
    super( owner );
  }

  protected void init() {
    super.init();

    final JToolBar toolBar = getOperatorPanel();
    toolBar.add( new ToolbarButton( new InsertTextAction() ) );
  }
}

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

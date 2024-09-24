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

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

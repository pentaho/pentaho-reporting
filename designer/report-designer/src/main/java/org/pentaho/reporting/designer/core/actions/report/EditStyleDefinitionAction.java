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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.styles.styleeditor.StyleDefinitionEditorDialog;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.AttributeEditUndoEntry;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditStyleDefinitionAction extends AbstractReportContextAction {
  public EditStyleDefinitionAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditStyleDefinitionAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "EditStyleDefinitionAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditStyleDefinitionAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getEmptyIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditStyleDefinitionAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final MasterReport masterReportElement = getActiveContext().getContextRoot();
    ElementStyleDefinition styleDefinition = masterReportElement.getStyleDefinition();
    if ( styleDefinition != null ) {
      styleDefinition = styleDefinition.clone();
    }

    final ReportDesignerContext context = getReportDesignerContext();
    final StyleDefinitionEditorDialog dialog =
      StyleDefinitionEditorDialog.createDialog( context.getView().getParent(), context );
    final ElementStyleDefinition elementStyleDefinition = dialog.performEdit( styleDefinition );
    if ( elementStyleDefinition == styleDefinition ) {
      return;
    }

    masterReportElement.setStyleDefinition( elementStyleDefinition );
    final AttributeEditUndoEntry undoEntry = new AttributeEditUndoEntry( masterReportElement.getObjectID(),
      AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET, styleDefinition, elementStyleDefinition );
    getActiveContext().getUndo()
      .addChange( ActionMessages.getString( "EditStyleDefinitionAction.UndoName" ), undoEntry );
  }
}

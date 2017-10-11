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

package org.pentaho.reporting.designer.core.actions.elements.format;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.format.EditableStyleSheet;
import org.pentaho.reporting.designer.core.editor.format.ElementFormatDialog;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.ElementFormatUndoEntry;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class ElementFormatAction extends AbstractElementSelectionAction {
  public ElementFormatAction() {
    putValue( Action.NAME, ActionMessages.getString( "ElementFormatAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ElementFormatAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ElementFormatAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ElementFormatAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType( Element.class );
    final EditableStyleSheet styleSheet = EditableStyleSheet.create( visualElements );

    final Map<StyleKey, Expression> styleExpressions;
    if ( visualElements.isEmpty() ) {
      styleExpressions = null;
    } else {
      styleExpressions = visualElements.get( 0 ).getStyleExpressions();
    }

    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final ElementFormatDialog dialog = createDialog( window );
    final ElementFormatUndoEntry.EditResult result =
      dialog.performEdit( getReportDesignerContext(), styleSheet, styleExpressions );
    if ( result == null ) {
      return;
    }

    final ElementFormatUndoEntry undoEntry = result.process( visualElements );
    getActiveContext().getUndo().addChange( ActionMessages.getString( "ElementFormatAction.UndoName" ), undoEntry );
  }

  protected ElementFormatDialog createDialog( final Window window ) {
    final ElementFormatDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new ElementFormatDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new ElementFormatDialog( (JFrame) window );
    } else {
      dialog = new ElementFormatDialog();
    }
    return dialog;
  }


}

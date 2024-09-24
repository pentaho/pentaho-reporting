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

package org.pentaho.reporting.designer.core.util.table.expressions;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.expressions.ExpressionEditorDialog;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class ExpressionCellHandler extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
  private class EditorAction extends AbstractAction {
    private EditorAction() {
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( table == null ) {
        return;
      }

      final ExpressionEditorDialog editorDialog;
      final Object o = LibSwingUtil.getWindowAncestor( table );
      if ( o instanceof Dialog ) {
        editorDialog = new ExpressionEditorDialog( (Dialog) o );
      } else if ( o instanceof Frame ) {
        editorDialog = new ExpressionEditorDialog( (Frame) o );
      } else {
        editorDialog = new ExpressionEditorDialog();
      }
      final Expression expression = editorDialog.performEditExpression
        ( reportDesignerContext, value );
      if ( editorDialog.isConfirmed() == false ) {
        cancelCellEditing();
        return;
      }

      value = expression;
      stopCellEditing();
    }
  }

  private JButton editButton;
  private ImageIcon addIcon;
  private ImageIcon editIcon;
  private JTable table;
  private ReportDesignerContext reportDesignerContext;
  private Expression value;

  public ExpressionCellHandler() {
    editButton = new JButton( new EditorAction() );
    editButton.setHorizontalAlignment( SwingConstants.CENTER );
    editButton.setBorderPainted( false );

    addIcon = new ImageIcon
      ( ExpressionCellHandler.class.getResource( "/org/pentaho/reporting/designer/core/icons/Add.png" ) ); // NON-NLS
    editIcon = new ImageIcon
      ( ExpressionCellHandler.class.getResource( "/org/pentaho/reporting/designer/core/icons/Edit.png" ) );// NON-NLS
  }

  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    configureEditButton( table, value, isSelected );
    return editButton;
  }

  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    configureEditButton( table, value, isSelected );
    return editButton;
  }

  private void configureEditButton( final JTable table, final Object value, final boolean isSelected ) {
    this.table = table;
    if ( value instanceof Expression ) {
      this.value = (Expression) value;
    } else {
      this.value = null;
    }

    if ( this.value == null ) {
      editButton.setIcon( addIcon );
    } else {
      editButton.setIcon( editIcon );
    }
    if ( isSelected ) {
      editButton.setBackground( table.getSelectionBackground() );
    } else {
      editButton.setBackground( table.getBackground() );
    }
  }

  public Object getCellEditorValue() {
    return value;
  }

  public boolean isCellEditable( final EventObject anEvent ) {
    if ( anEvent instanceof MouseEvent ) {
      final MouseEvent mouseEvent = (MouseEvent) anEvent;
      return mouseEvent.getClickCount() >= 1 && mouseEvent.getButton() == MouseEvent.BUTTON1;
    }
    return true;
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    this.reportDesignerContext = reportDesignerContext;
  }
}

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

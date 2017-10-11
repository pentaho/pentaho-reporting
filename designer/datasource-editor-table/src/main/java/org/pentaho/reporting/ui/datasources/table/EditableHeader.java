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

package org.pentaho.reporting.ui.datasources.table;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.EventObject;

public class EditableHeader extends JTableHeader implements CellEditorListener {
  private int editingColumn;
  private TableCellEditor cellEditor;
  private Component editorComp;
  private TableEditorModel tableModel;

  public EditableHeader( final TableColumnModel columnModel, final TableEditorModel aTableModel ) {
    super( columnModel );
    setReorderingAllowed( false );

    cellEditor = null;
    tableModel = aTableModel;
  }

  public void updateUI() {
    setUI( new EditableHeaderUI() );
    resizeAndRepaint();
    invalidate();
  }

  public boolean editCellAt( final int index, final EventObject e ) {
    if ( cellEditor != null && !cellEditor.stopCellEditing() ) {
      return false;
    }
    if ( !isCellEditable( index ) ) {
      return false;
    }
    final TableCellEditor editor = getCellEditor( index );

    if ( editor != null && editor.isCellEditable( e ) ) {
      editorComp = prepareEditor( editor, index );
      editorComp.setBounds( getHeaderRect( index ) );
      add( editorComp );
      editorComp.validate();
      setCellEditor( editor );
      setEditingColumn( index );
      editor.addCellEditorListener( this );

      return true;
    }
    return false;
  }

  public boolean isCellEditable( final int index ) {
    if ( getReorderingAllowed() ) {
      return false;
    }
    final int columnIndex = columnModel.getColumn( index ).getModelIndex();
    if ( columnIndex == 0 ) {
      return false;
    }

    final EditableHeaderTableColumn col = (EditableHeaderTableColumn) columnModel.getColumn( columnIndex );
    return col.isHeaderEditable();
  }

  public TableCellEditor getCellEditor( final int index ) {
    final int columnIndex = columnModel.getColumn( index ).getModelIndex();
    if ( columnIndex == 0 ) {
      return null;
    }
    final EditableHeaderTableColumn col = (EditableHeaderTableColumn) columnModel.getColumn( columnIndex );
    return col.getHeaderEditor();
  }

  public void setCellEditor( final TableCellEditor newEditor ) {
    final TableCellEditor oldEditor = cellEditor;
    cellEditor = newEditor;
    if ( oldEditor != null ) {
      oldEditor.removeCellEditorListener( this );
    }
    if ( newEditor != null ) {
      newEditor.addCellEditorListener( this );
    }
  }

  public Component prepareEditor( final TableCellEditor editor, final int index ) {
    final Object value = columnModel.getColumn( index ).getHeaderValue();
    final boolean isSelected = true;
    final JTable table = getTable();
    return editor.getTableCellEditorComponent( table, value, isSelected, -1, index );
  }

  public TableCellEditor getCellEditor() {
    return cellEditor;
  }

  public Component getEditorComponent() {
    return editorComp;
  }

  public void setEditingColumn( final int aColumn ) {
    editingColumn = aColumn;
  }

  public int getEditingColumn() {
    return editingColumn;
  }

  public void removeEditor() {
    final TableCellEditor editor = getCellEditor();
    if ( editor != null ) {
      editor.removeCellEditorListener( this );

      requestFocus();
      remove( editorComp );

      final int index = getEditingColumn();
      final Rectangle cellRect = getHeaderRect( index );

      setCellEditor( null );
      setEditingColumn( -1 );
      editorComp = null;

      repaint( cellRect );
    }
  }

  public boolean isEditing() {
    return ( cellEditor != null );
  }

  public void editingStopped( final ChangeEvent e ) {
    final TableCellEditor editor = getCellEditor();
    if ( editor != null ) {
      final Object value = editor.getCellEditorValue();
      final int index = getEditingColumn();
      columnModel.getColumn( index ).setHeaderValue( value );

      removeEditor();

      if ( tableModel != null && value instanceof TypedHeaderInformation ) {
        final TypedHeaderInformation hi = (TypedHeaderInformation) value;
        tableModel.setColumnName( index, hi.getName() );
        tableModel.setColumnClass( index, hi.getType() );
      }
    }
  }

  public void editingCanceled( final ChangeEvent e ) {
    removeEditor();
  }
}

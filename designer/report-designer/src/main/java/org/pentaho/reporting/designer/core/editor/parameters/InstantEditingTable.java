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

package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

/**
 * Todo: Document me!
 * <p/>
 * Date: 10.05.2010 Time: 16:20:14
 *
 * @author Thomas Morgner.
 */
public class InstantEditingTable extends ElementMetaDataTable {
  private static class InstantEditingTableCellEditor implements TableCellEditor {
    private TableCellEditor backend;

    private InstantEditingTableCellEditor( final TableCellEditor backend ) {
      if ( backend == null ) {
        throw new NullPointerException();
      }
      this.backend = backend;
    }

    public Component getTableCellEditorComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final int row,
                                                  final int column ) {
      return backend.getTableCellEditorComponent( table, value, isSelected, row, column );
    }

    public Object getCellEditorValue() {
      return backend.getCellEditorValue();
    }

    public boolean isCellEditable( final EventObject anEvent ) {
      return true;
    }

    public boolean shouldSelectCell( final EventObject anEvent ) {
      return true;
    }

    public boolean stopCellEditing() {
      return backend.stopCellEditing();
    }

    public void cancelCellEditing() {
      backend.cancelCellEditing();
    }

    public void addCellEditorListener( final CellEditorListener l ) {
      backend.addCellEditorListener( l );
    }

    public void removeCellEditorListener( final CellEditorListener l ) {
      backend.removeCellEditorListener( l );
    }
  }

  public InstantEditingTable() {
  }

  public TableCellEditor getCellEditor( final int row, final int viewColumn ) {
    final TableCellEditor tableCellEditor = super.getCellEditor( row, viewColumn );
    if ( tableCellEditor == null ) {
      return null;
    }
    return new InstantEditingTableCellEditor( tableCellEditor );
  }
}

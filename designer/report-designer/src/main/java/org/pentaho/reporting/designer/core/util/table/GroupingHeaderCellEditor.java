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

package org.pentaho.reporting.designer.core.util.table;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class GroupingHeaderCellEditor extends AbstractCellEditor implements TableCellEditor {
  private GroupingHeaderCellRenderer renderer;
  private GroupingHeader header;

  public GroupingHeaderCellEditor() {
    renderer = new GroupingHeaderCellRenderer();
    renderer.setRequestFocusEnabled( false );
  }

  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    this.header = (GroupingHeader) value;
    final Component rendererComponent =
      renderer.getTableCellRendererComponent( table, value, isSelected, true, row, column );
    SwingUtilities.invokeLater( new AutoInvokeRunnable( header ) );
    return rendererComponent;
  }

  public Object getCellEditorValue() {
    return header;
  }

  public boolean shouldSelectCell( final EventObject anEvent ) {
    return true;
  }


  private class AutoInvokeRunnable implements Runnable {
    private GroupingHeader header;

    private AutoInvokeRunnable( final GroupingHeader header ) {
      this.header = header;
    }

    public void run() {
      if ( header != null ) {
        this.header.setCollapsed( this.header.isCollapsed() == false );
        fireEditingStopped();
      }
    }
  }

}

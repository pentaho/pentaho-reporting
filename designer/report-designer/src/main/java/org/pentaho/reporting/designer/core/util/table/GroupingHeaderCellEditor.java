/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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

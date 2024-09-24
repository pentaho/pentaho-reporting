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

package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import java.awt.Component;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.pentaho.reporting.designer.extensions.pentaho.repository.model.RepositoryTableModel;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class RepositoryEntryCellRenderer extends DefaultTableCellRenderer {
  private Icon leafIcon;
  private Icon closedIcon;


  public RepositoryEntryCellRenderer() {
    this.leafIcon = ( UIManager.getIcon( "Tree.leafIcon" ) );
    this.closedIcon = ( UIManager.getIcon( "Tree.closedIcon" ) );
    putClientProperty( "html.disable", Boolean.TRUE ); // NON-NLS
  }

  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    final JLabel component =
      (JLabel) super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    try {
      if ( column == 0 ) {
        final RepositoryTableModel tableModel = (RepositoryTableModel) table.getModel();
        final RowSorter rowSorter = table.getRowSorter();
        final FileObject e;
        if ( rowSorter != null ) {
          e = tableModel.getElementForRow( rowSorter.convertRowIndexToModel( row ) );
        } else {
          e = tableModel.getElementForRow( row );
        }

        if ( e.getType() == FileType.FOLDER ) {
          component.setIcon( closedIcon );
        } else {
          component.setIcon( leafIcon );
        }
      } else {
        component.setIcon( null );
      }
    } catch ( FileSystemException fse ) {
      // ok, ugly, but not fatal.
    }
    return component;
  }
}

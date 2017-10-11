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

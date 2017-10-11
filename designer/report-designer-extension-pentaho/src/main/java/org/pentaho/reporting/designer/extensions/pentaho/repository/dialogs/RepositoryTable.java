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

package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import org.apache.commons.vfs2.FileObject;
import org.pentaho.reporting.designer.extensions.pentaho.repository.model.RepositoryTableModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.RepositoryEntryCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellRenderer;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Component;
import java.awt.Dimension;
import java.text.DateFormat;
import java.util.Date;

public class RepositoryTable extends JTable {
  private class DateCellRenderer extends DefaultTableCellRenderer {
    /**
     * Creates a default table cell renderer.
     */
    public DateCellRenderer() {
    }

    /**
     * Returns the default table cell renderer.
     * <p/>
     * During a printing operation, this method will be called with <code>isSelected</code> and <code>hasFocus</code>
     * values of <code>false</code> to prevent selection and focus from appearing in the printed output. To do other
     * customization based on whether or not the table is being printed, check the return value from {@link
     * javax.swing.JComponent#isPaintingForPrint()}.
     *
     * @param table      the <code>JTable</code>
     * @param value      the value to assign to the cell at <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus   true if cell has focus
     * @param row        the row of the cell to render
     * @param column     the column of the cell to render
     * @return the default table cell renderer
     * @see javax.swing.JComponent#isPaintingForPrint()
     */
    public Component getTableCellRendererComponent( final JTable table,
                                                    final Object value,
                                                    final boolean isSelected,
                                                    final boolean hasFocus,
                                                    final int row,
                                                    final int column ) {
      if ( value instanceof Date == false ) {
        return super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
      }


      final Date date = (Date) value;
      return super.getTableCellRendererComponent( table, DateFormat.getDateTimeInstance().format( date ),
        isSelected, hasFocus, row, column );
    }
  }

  private RepositoryTableModel repositoryTableModel;
  private FileObject selectedPath;

  public RepositoryTable() {
    this.repositoryTableModel = new RepositoryTableModel();
    setAutoCreateRowSorter( true );
    setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN );
    setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    setShowHorizontalLines( false );
    setShowVerticalLines( false );
    setModel( repositoryTableModel );
    setIntercellSpacing( new Dimension( 0, 0 ) );
    setDefaultRenderer( String.class, new RepositoryEntryCellRenderer() );
    setDefaultRenderer( Date.class, new DateCellRenderer() );
    setDefaultRenderer( Object.class, new GenericCellRenderer() );
  }

  public boolean isShowHiddenFiles() {
    return repositoryTableModel.isShowHiddenFiles();
  }

  public void setShowHiddenFiles( final boolean showHiddenFiles ) {
    repositoryTableModel.setShowHiddenFiles( showHiddenFiles );
  }

  public String[] getFilters() {
    return repositoryTableModel.getFilters();
  }

  public void setFilters( final String[] filters ) {
    repositoryTableModel.setFilters( filters );
  }

  public FileObject getSelectedPath() {
    return selectedPath;
  }

  public void setSelectedPath( final FileObject selectedPath ) {
    final FileObject oldSelectedPath = this.selectedPath;
    this.selectedPath = selectedPath;
    this.repositoryTableModel.setSelectedPath( selectedPath );
    firePropertyChange( "selectedPath", oldSelectedPath, selectedPath );
  }

  public FileObject getSelectedFileObject( final int rowIndex ) {
    return this.repositoryTableModel.getElementForRow( rowIndex );
  }

  public void refresh() {
    repositoryTableModel.fireTableDataChanged();
  }
}

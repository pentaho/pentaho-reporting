package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.vfs.FileObject;
import org.pentaho.reporting.designer.extensions.pentaho.repository.model.RepositoryTableModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.RepositoryEntryCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellRenderer;

public class RepositoryTable extends JTable
{
  private class DateCellRenderer extends DefaultTableCellRenderer
  {
    /**
     * Creates a default table cell renderer.
     */
    public DateCellRenderer()
    {
    }

    /**
     * Returns the default table cell renderer.
     * <p/>
     * During a printing operation, this method will be called with
     * <code>isSelected</code> and <code>hasFocus</code> values of
     * <code>false</code> to prevent selection and focus from appearing
     * in the printed output. To do other customization based on whether
     * or not the table is being printed, check the return value from
     * {@link javax.swing.JComponent#isPaintingForPrint()}.
     *
     * @param table      the <code>JTable</code>
     * @param value      the value to assign to the cell at
     *                   <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus   true if cell has focus
     * @param row        the row of the cell to render
     * @param column     the column of the cell to render
     * @return the default table cell renderer
     * @see javax.swing.JComponent#isPaintingForPrint()
     */
    public Component getTableCellRendererComponent(final JTable table,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final boolean hasFocus,
                                                   final int row,
                                                   final int column)
    {
      if (value instanceof Date == false)
      {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      }


      final Date date = (Date) value;
      return super.getTableCellRendererComponent(table, DateFormat.getDateTimeInstance().format(date),
          isSelected, hasFocus, row, column);
    }
  }

  private RepositoryTableModel repositoryTableModel;
  private FileObject selectedPath;

  public RepositoryTable()
  {
    this.repositoryTableModel = new RepositoryTableModel();
    setAutoCreateRowSorter(true);
    setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setShowHorizontalLines(false);
    setShowVerticalLines(false);
    setModel(repositoryTableModel);
    setIntercellSpacing(new Dimension(0, 0));
    setDefaultRenderer(String.class, new RepositoryEntryCellRenderer());
    setDefaultRenderer(Date.class, new DateCellRenderer());
    setDefaultRenderer(Object.class, new GenericCellRenderer());
  }

  public String[] getFilters()
  {
    return repositoryTableModel.getFilters();
  }

  public void setFilters(final String[] filters)
  {
    repositoryTableModel.setFilters(filters);
  }

  public FileObject getSelectedPath()
  {
    return selectedPath;
  }

  public void setSelectedPath(final FileObject selectedPath)
  {
    final FileObject oldSelectedPath = this.selectedPath;
    this.selectedPath = selectedPath;
    this.repositoryTableModel.setSelectedPath(selectedPath);
    firePropertyChange("selectedPath", oldSelectedPath, selectedPath);
  }

  public FileObject getSelectedFileObject(final int rowIndex)
  {
    return this.repositoryTableModel.getElementForRow(rowIndex);
  }

  public void refresh()
  {
    repositoryTableModel.fireTableDataChanged();
  }
}

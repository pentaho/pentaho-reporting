/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.cards;

import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A wrapping table model.
 *
 * @author Thomas Morgner
 */
public class WrappingTableModel implements TableModel
{
  /**
   * A helper class, that translates tableevents received from the wrapped table model and forwards them with changed
   * indices to the regitered listeners.
   */
  private class TableEventTranslator implements TableModelListener
  {
    /**
     * the registered listeners.
     */
    private final ArrayList listeners;

    /**
     * Default Constructor.
     */
    public TableEventTranslator()
    {
      listeners = new ArrayList();
    }

    /**
     * This fine grain notification tells listeners the exact range of cells, rows, or columns that changed. The
     * received rows are translated to fit the external tablemodel size.
     *
     * @param e the event, that should be translated.
     */
    public void tableChanged(final TableModelEvent e)
    {
      // inefficient, but necessary ...
      final int columnIndex = TableModelEvent.ALL_COLUMNS;

      final int firstRow = e.getFirstRow();
      final int lastRow = e.getLastRow();

      final int firstRowIndex = (firstRow / 2);
      final int lastRowIndex = (lastRow / 2);

      final TableModelEvent event =
          new TableModelEvent(WrappingTableModel.this, firstRowIndex, lastRowIndex,
              columnIndex, e.getType());

      for (int i = 0; i < listeners.size(); i++)
      {
        final TableModelListener l = (TableModelListener) listeners.get(i);
        l.tableChanged(event);
      }

    }

    /**
     * Adds the TableModelListener to this Translator.
     *
     * @param l the tablemodel listener
     */
    public void addTableModelListener(final TableModelListener l)
    {
      listeners.add(l);
    }

    /**
     * Removes the TableModelListener from this Translator.
     *
     * @param l the tablemodel listener
     */
    public void removeTableModelListener(final TableModelListener l)
    {
      listeners.remove(l);
    }
  }

  /**
   * A table event translator.
   */
  private TableEventTranslator translator;

  /**
   * The column prefix 1.
   */
  private String columnPrefix1;

  /**
   * The column prefix 2.
   */
  private String columnPrefix2;

  /**
   * The table model.
   */
  private TableModel model;

  /**
   * Creates a new wrapping table model.
   *
   * @param model the underlying table model.
   */
  public WrappingTableModel(final TableModel model)
  {
    this(model, "Column1_", "Column2_");
  }

  /**
   * Creates a new wrapping table model.
   *
   * @param model   the underlying table model.
   * @param prefix1 the first column prefix.
   * @param prefix2 the second column prefix.
   */
  public WrappingTableModel(final TableModel model, final String prefix1,
                            final String prefix2)
  {
    if (prefix1 == null)
    {
      throw new NullPointerException();
    }
    if (prefix2 == null)
    {
      throw new NullPointerException();
    }
    if (prefix1.equals(prefix2))
    {
      throw new IllegalArgumentException("Prefix 1 and 2 are identical");
    }
    this.model = model;
    this.columnPrefix1 = prefix1;
    this.columnPrefix2 = prefix2;
    this.translator = new TableEventTranslator();
  }

  /**
   * Returns column prefix 1.
   *
   * @return Column prefix 1.
   */
  public String getColumnPrefix1()
  {
    return columnPrefix1;
  }

  /**
   * Returns column prefix 2.
   *
   * @return Column prefix 2.
   */
  public String getColumnPrefix2()
  {
    return columnPrefix2;
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount()
  {
    return (int) Math.ceil(model.getRowCount() / 2.0);
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount()
  {
    return 2 * model.getColumnCount();
  }

  /**
   * Returns the name of the column at <code>columnIndex</code>.  This is used to initialize the table's column header
   * name.  Note: this name does not need to be unique; two columns in a table can have the same name.
   *
   * @param columnIndex the index of the column
   * @return the name of the column
   */
  public String getColumnName(final int columnIndex)
  {
    final int tmpColumnIndex = (columnIndex % model.getColumnCount());
    if (columnIndex < model.getColumnCount())
    {
      return getColumnPrefix1() + model.getColumnName(tmpColumnIndex);
    }
    else
    {
      return getColumnPrefix2() + model.getColumnName(tmpColumnIndex);
    }
  }

  /**
   * Returns the most specific superclass for all the cell values in the column.  This is used by the
   * <code>JTable</code> to set up a default renderer and editor for the column.
   *
   * @param columnIndex the index of the column
   * @return the common ancestor class of the object values in the model.
   */
  public Class getColumnClass(final int columnIndex)
  {
    final int tmpColumnIndex = (columnIndex % model.getColumnCount());
    return model.getColumnClass(tmpColumnIndex);
  }

  /**
   * Returns true if the cell at <code>rowIndex</code> and <code>columnIndex</code> is editable.  Otherwise,
   * <code>setValueAt</code> on the cell will not change the value of that cell.
   *
   * @param rowIndex    the row whose value to be queried
   * @param columnIndex the column whose value to be queried
   * @return true if the cell is editable
   * @see #setValueAt
   */
  public boolean isCellEditable(final int rowIndex, final int columnIndex)
  {
    final int tmpColumnIndex = (columnIndex % model.getColumnCount());
    final int tmpRowIndex = calculateRow(rowIndex, columnIndex);
    if (tmpRowIndex >= model.getRowCount())
    {
      return false;
    }
    return model.isCellEditable(tmpRowIndex, tmpColumnIndex);
  }

  /**
   * Calculates the physical row.
   *
   * @param row    the (logical) row index.
   * @param column the column index.
   * @return The physical row.
   */
  private int calculateRow(final int row, final int column)
  {
    if (column < model.getColumnCount())
    {
      // high row ...
      return row * 2;
    }
    else
    {
      // low row ...
      return (row * 2) + 1;
    }
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex    the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt(final int rowIndex, final int columnIndex)
  {
    final int tmpColumnIndex = (columnIndex % model.getColumnCount());
    final int tmpRowIndex = calculateRow(rowIndex, columnIndex);
    if (tmpRowIndex >= model.getRowCount())
    {
      return null;
    }
    return model.getValueAt(tmpRowIndex, tmpColumnIndex);
  }

  /**
   * Sets the value in the cell at <code>columnIndex</code> and <code>rowIndex</code> to <code>aValue</code>.
   *
   * @param aValue      the new value
   * @param rowIndex    the row whose value is to be changed
   * @param columnIndex the column whose value is to be changed
   * @see #getValueAt
   * @see #isCellEditable
   */
  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
  {
    final int tmpColumnIndex = (columnIndex % model.getColumnCount());
    final int tmpRowIndex = calculateRow(rowIndex, columnIndex);
    if (tmpRowIndex >= model.getRowCount())
    {
      return;
    }
    model.setValueAt(aValue, tmpRowIndex, tmpColumnIndex);
  }

  /**
   * Adds a listener to the list that is notified each time a change to the data model occurs.
   *
   * @param l the TableModelListener
   */
  public void addTableModelListener(final TableModelListener l)
  {
    translator.addTableModelListener(l);
  }

  /**
   * Removes a listener from the list that is notified each time a change to the data model occurs.
   *
   * @param l the TableModelListener
   */
  public void removeTableModelListener(final TableModelListener l)
  {
    translator.removeTableModelListener(l);
  }
}

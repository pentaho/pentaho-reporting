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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.functions;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class PaintComponentTableModel
    extends AbstractTableModel
{
  private ArrayList rows;

  public PaintComponentTableModel()
  {
    rows = new ArrayList();
  }

  public void addComponent(final Component component)
  {
    if (component == null)
    {
      throw new NullPointerException("Component must not be null.");
    }
    rows.add(component);
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
    return 2;
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
    return rows.size();
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param  rowIndex  the row whose value is to be queried
   * @param  columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt(final int rowIndex,
                           final int columnIndex)
  {
    final Object c = rows.get(rowIndex);
    if (columnIndex == 0)
    {
      return c;
    }
    return c.getClass().getName();
  }

  /**
   * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex the column being queried
   * @return the Object.class
   */
  public Class getColumnClass(final int columnIndex)
  {
    if (columnIndex == 0)
    {
      return Component.class;
    }
    else
    {
      return String.class;
    }
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
   * <code>column</code> cannot be found, returns an empty string.
   *
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName(final int column)
  {
    if (column == 0)
    {
      return "Component";
    }
    else
    {
      return "Class";
    }
  }
}

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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.fonts;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.table.AbstractTableModel;

/**
 * A sample data source for the JFreeReport Demo Application.
 *
 * @author Thomas Morgner
 */
public class FontTableModel extends AbstractTableModel
{
  /**
   * Comparator for sorting fonts.
   */
  private static class FontComparator implements Comparator
  {
    public FontComparator()
    {
    }

    /**
     * Compares two fonts.
     *
     * @param o  font 1.
     * @param o1 font 2.
     * @return an integer representing the relative order of the two fonts.
     */
    public int compare(final Object o, final Object o1)
    {
      final Font f1 = (Font) o;
      final Font f2 = (Font) o1;
      int comp = f1.getFamily().compareTo(f2.getFamily());
      if (comp == 0)
      {
        comp = f1.getName().compareTo(f2.getName());
      }
      return comp;
    }
  }

  /**
   * Storage for the fonts.
   */
  private Font[] fonts = null;

  /**
   * Sample dataset.
   */
  public FontTableModel()
  {
    fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    Arrays.sort(fonts, new FontComparator());
  }

  /**
   * Returns the number of rows in the table model.
   *
   * @return the row count.
   */
  public int getRowCount()
  {
    return fonts.length;
  }

  /**
   * Returns the number of columns in the table model.
   *
   * @return the column count.
   */
  public int getColumnCount()
  {
    return 2;
  }

  /**
   * Returns the class of the data in the specified column.
   *
   * @param column the column (zero-based index).
   * @return the column class.
   */
  public Class getColumnClass(final int column)
  {
    return String.class;
  }

  /**
   * Returns the name of the specified column.
   *
   * @param column the column (zero-based index).
   * @return the column name.
   */
  public String getColumnName(final int column)
  {
    if (column == 0)
    {
      return "family";
    }
    else if (column == 1)
    {
      return "fontname";
    }
    throw new IndexOutOfBoundsException();
  }

  /**
   * Returns the data value at the specified row and column.
   *
   * @param row    the row index (zero based).
   * @param column the column index (zero based).
   * @return the value.
   */
  public Object getValueAt(final int row, final int column)
  {
    if (column == 0)
    {
      return fonts[row].getFamily();
    }
    else if (column == 1)
    {
      return fonts[row].getName();
    }
    throw new IndexOutOfBoundsException();
  }

}

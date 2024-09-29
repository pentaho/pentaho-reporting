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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.bookstore;

import javax.swing.table.AbstractTableModel;

/**
 * A sample data source for the JFreeReport Demo Application.
 *
 * @author Thomas Morgner
 */
public class BookstoreTableModel extends AbstractTableModel
{
  /**
   * Storage for the data.
   */
  private final Object[][] data;

  /**
   * Default constructor - builds a sample data source.
   */
  public BookstoreTableModel()
  {
    data = new Object[][]
        {
            {"Mr. Black", "1666 Pennsylvania Ave.", "012345 Washington", "01212",
                "Robert A. Heinlein - Starship Trooper", new Integer(1), new Double(12.49)},
            {"Mr. Black", "1666 Pennsylvania Ave.", "012345 Washington", "01231",
                "Robert A. Heinlein - Glory Road", new Integer(1), new Double(12.99)},
            {"Mr. Black", "1666 Pennsylvania Ave.", "012345 Washington", "12121",
                "Frank Herbert - Dune", new Integer(1), new Double(10.99)},
            {"Mr. Black", "1666 Pennsylvania Ave.", "012345 Washington", "A1232",
                "Bierce Ambrose - The Devils Dictionary", new Integer(2), new Double(19.99)},
            {"John F. Google", "12a Nowaday Road", "99999 Boston", "12333",
                "Samuel Adams - How to sell tea ", new Integer(100), new Double(10.99)},
            {"John F. Google", "12a Nowaday Road", "99999 Boston", "88812",
                "Adam Smith - The wealth of nations", new Integer(1), new Double(49.95)},
            {"John F. Google", "12a Nowaday Road", "99999 Boston", "33123",
                "D. Khan - How to conquer friends", new Integer(1), new Double(15.99)},
            {"John F. Google", "12a Nowaday Road", "99999 Boston", "33123",
                "D. Khan - How to conquer friends", new Integer(1), new Double(19.49)},
//      {"Cleeve Johnson", "87 Oakham Drive", "99999 Boston", "33123",
//       "D. Khan - How to conquer friends", new Integer(1), new Double(15.99)},
//      {"Cleeve Johnson", "87 Oakham Drive", "99999 Boston", "33123",
//       "J. Ceaser - Choosing the right friends", new Integer(1), new Double(25.99)},
//      {"Cleeve Johnson", "87 Oakham Drive", "99999 Boston", "33123",
//       "Galileo - When to tell the truth", new Integer(1), new Double(29.59)}
        };
  }

  /**
   * Returns the number of rows in the table model.
   *
   * @return the row count.
   */
  public int getRowCount()
  {
    return data.length;
  }

  /**
   * Returns the number of columns in the table model.
   *
   * @return the column count.
   */
  public int getColumnCount()
  {
    return 8;
  }

  /**
   * Returns the class of the data in the specified column.
   *
   * @param column the column (zero-based index).
   * @return the column class.
   */
  public Class getColumnClass(final int column)
  {
    if (column == 5)
    {
      return Integer.class;
    }
    else if (column == 6)
    {
      return Double.class;
    }
    else
    {
      return String.class;
    }
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
      return "name";
    }
    else if (column == 1)
    {
      return "street";
    }
    else if (column == 2)
    {
      return "town";
    }
    else if (column == 3)
    {
      return "productcode";
    }
    else if (column == 4)
    {
      return "productname";
    }
    else if (column == 5)
    {
      return "count";
    }
    else if (column == 6)
    {
      return "price";
    }
    else if (column == 7)
    {
      return "total";
    }
    else
    {
      return null;
    }
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
    if (column == 7)
    {
      final Integer i = (Integer) data[row][5];
      final Double d = (Double) data[row][6];
      return new Double(i.intValue() * d.doubleValue());
    }
    else
    {
      return data[row][column];
    }
  }

}

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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.groups;

import javax.swing.table.AbstractTableModel;

/**
 * A sample data source for the JFreeReport Demo Application.
 *
 * @author David Gilbert
 */
public class ColorAndLetterTableModel extends AbstractTableModel
{

  /**
   * Storage for the data.
   */
  private final Object[][] data;

  private int size;

  public ColorAndLetterTableModel()
  {
    this(120);
  }

  /**
   * Default constructor - builds a sample data source.
   */
  public ColorAndLetterTableModel(final int size)
  {
    if (size > 120 || size < 0)
    {
      throw new IndexOutOfBoundsException("Size is invalid.");
    }
    this.size = size;

    data = new Object[120][5];
    data[0] = new Object[]{"One", "Red", "A", new Integer(1), new Double(1.1)};
    data[1] = new Object[]{"Two", "Red", "A", new Integer(2), new Double(2.2)};
    data[2] = new Object[]{"Three", "Red", "A", new Integer(3), new Double(3.3)};
    data[3] = new Object[]{"Four", "Green", "A", new Integer(4), new Double(4.4)};
    data[4] = new Object[]{"Five", "Green", "A", new Integer(5), new Double(5.5)};
    data[5] = new Object[]{"Six", "Green", "A", new Integer(6), new Double(6.6)};
    data[6] = new Object[]{"Seven", "Green", "A", new Integer(7), new Double(7.7)};
    data[7] = new Object[]{"Eight", "Green", "A", new Integer(8), new Double(8.8)};
    data[8] = new Object[]{"Nine", "Blue", "A", new Integer(9), new Double(9.9)};
    data[9] = new Object[]{"Ten", "Blue", "A", new Integer(10), new Double(10.10)};
    data[10] = new Object[]{"Eleven", "Blue", "A", new Integer(11), new Double(11.11)};
    data[11] = new Object[]{"Twelve", "Blue", "A", new Integer(12), new Double(12.12)};
    data[12] = new Object[]{"Thirteen", "Blue", "A", new Integer(13), new Double(13.13)};
    data[13] = new Object[]{"Fourteen", "Blue", "A", new Integer(14), new Double(14.14)};
    data[14] = new Object[]{"Fifteen", "Blue", "A", new Integer(15), new Double(15.15)};
    data[15] = new Object[]{"Sixteen", "Blue", "A", new Integer(16), new Double(16.16)};
    data[16] = new Object[]{"Seventeen", "Blue", "A", new Integer(17), new Double(17.17)};
    data[17] = new Object[]{"Eighteen", "Blue", "A", new Integer(18), new Double(18.18)};
    data[18] = new Object[]{"Nineteen", "Green", "B", new Integer(19), new Double(19.19)};
    data[19] = new Object[]{"Twenty", "Green", "B", new Integer(20), new Double(20.20)};
    data[20] = new Object[]{"Twenty One", "Green", "B", new Integer(21), new Double(21.21)};
    data[21] = new Object[]{"Twenty Two", "Green", "B", new Integer(22), new Double(22.22)};
    data[22] = new Object[]{"Twenty Three", "Green", "B", new Integer(23), new Double(23.23)};
    data[23] = new Object[]{"Twenty Four", "Green", "B", new Integer(24), new Double(24.24)};
    data[24] = new Object[]{"Twenty Five", "Green", "B", new Integer(25), new Double(25.25)};
    data[25] = new Object[]{"Twenty Six", "Green", "B", new Integer(26), new Double(26.26)};
    data[26] = new Object[]{"Twenty Seven", "Green", "B", new Integer(27), new Double(27.27)};
    data[27] = new Object[]{"Twenty Eight", "Green", "B", new Integer(28), new Double(28.28)};
    data[28] = new Object[]{"Twenty Nine", "Red", "C", new Integer(29), new Double(29.29)};
    data[29] = new Object[]{"Thirty", "Red", "C", new Integer(30), new Double(30.30)};
    data[30] = new Object[]{"Thirty One", "Red", "C", new Integer(31), new Double(31.31)};
    data[31] = new Object[]{"Thirty Two", "Red", "C", new Integer(32), new Double(32.32)};
    data[32] = new Object[]{"Thirty Three", "Red", "C", new Integer(33), new Double(33.33)};
    data[33] = new Object[]{"Thirty Four", "Red", "C", new Integer(34), new Double(34.34)};
    data[34] = new Object[]{"Thirty Five", "Red", "C", new Integer(35), new Double(35.35)};
    data[35] = new Object[]{"Thirty Six", "Red", "C", new Integer(36), new Double(36.36)};
    data[36] = new Object[]{"Thirty Seven", "Blue", "C", new Integer(37), new Double(37.37)};
    data[37] = new Object[]{"Thirty Eight", "Blue", "C", new Integer(38), new Double(38.38)};
    data[38] = new Object[]{"Thirty Nine", "Blue", "C", new Integer(39), new Double(39.39)};
    data[39] = new Object[]{"Forty", "Blue", "C", new Integer(40), new Double(40.40)};
    data[40] = new Object[]{"Forty One", "Blue", "C", new Integer(41), new Double(41.41)};
    data[41] = new Object[]{"Forty Two", "Blue", "C", new Integer(42), new Double(42.42)};
    data[42] = new Object[]{"Forty Three", "Blue", "D", new Integer(43), new Double(43.43)};
    data[43] = new Object[]{"Forty Four", "Blue", "D", new Integer(44), new Double(44.44)};
    data[44] = new Object[]{"Forty Five", "Blue", "D", new Integer(45), new Double(45.45)};
    data[45] = new Object[]{"Forty Six", "Blue", "D", new Integer(46), new Double(46.46)};
    data[46] = new Object[]{"Forty Seven", "Blue", "D", new Integer(47), new Double(47.47)};
    data[47] = new Object[]{"Forty Eight", "Blue", "D", new Integer(48), new Double(48.48)};
    data[48] = new Object[]{"Forty Nine", "Blue", "D", new Integer(49), new Double(49.49)};
    data[49] = new Object[]{"Fifty", "Blue", "D", new Integer(50), new Double(50.50)};
    data[50] = new Object[]{"Fifty One", "Blue", "D", new Integer(51), new Double(51.51)};
    data[51] = new Object[]{"Fifty Two", "Blue", "D", new Integer(52), new Double(52.52)};
    data[52] = new Object[]{"Fifty Three", "Blue", "D", new Integer(53), new Double(53.53)};
    data[53] = new Object[]{"Fifty Four", "Blue", "D", new Integer(54), new Double(54.54)};
    data[54] = new Object[]{"Fifty Five", "Blue", "D", new Integer(55), new Double(55.55)};
    data[55] = new Object[]{"Fifty Six", "Blue", "D", new Integer(56), new Double(56.56)};
    data[56] = new Object[]{"Fifty Seven", "Blue", "D", new Integer(57), new Double(57.57)};
    data[57] = new Object[]{"Fifty Eight", "Blue", "D", new Integer(58), new Double(58.58)};
    data[58] = new Object[]{"Fifty Nine", "Blue", "D", new Integer(59), new Double(59.59)};
    data[59] = new Object[]{"Sixty", "Blue", "D", new Integer(60), new Double(60.60)};
    data[60] = new Object[]{"Sixty One", "Blue", "D", new Integer(61), new Double(61.60)};
    data[61] = new Object[]{"Sixty Two", "Blue", "D", new Integer(62), new Double(62.60)};
    data[62] = new Object[]{"Sixty Three", "Blue", "D", new Integer(63), new Double(63.60)};
    data[63] = new Object[]{"Sixty Four", "Blue", "D", new Integer(64), new Double(64.60)};
    data[64] = new Object[]{"Sixty Five", "Blue", "D", new Integer(65), new Double(65.60)};
    data[65] = new Object[]{"Sixty Six", "Blue", "D", new Integer(66), new Double(66.60)};
    data[66] = new Object[]{"Sixty Seven", "Blue", "D", new Integer(67), new Double(67.60)};
    data[67] = new Object[]{"Sixty Eight", "Blue", "D", new Integer(68), new Double(68.60)};
    data[68] = new Object[]{"Sixty Nine", "Blue", "D", new Integer(69), new Double(69.60)};
    data[69] = new Object[]{"Seventy", "Blue", "D", new Integer(70), new Double(70.60)};
    data[70] = new Object[]{"Seventy One", "Blue", "D", new Integer(71), new Double(71.60)};
    data[71] = new Object[]{"Seventy Two", "Blue", "D", new Integer(72), new Double(72.60)};
    data[72] = new Object[]{"Seventy Three", "Blue", "D", new Integer(73), new Double(73.60)};
    data[73] = new Object[]{"Seventy Four", "Blue", "D", new Integer(74), new Double(74.60)};
    data[74] = new Object[]{"Seventy Five", "Blue", "D", new Integer(75), new Double(75.60)};
    data[75] = new Object[]{"Seventy Six", "Blue", "D", new Integer(76), new Double(76.60)};
    data[76] = new Object[]{"Seventy Seven", "Blue", "D", new Integer(77), new Double(77.60)};
    data[77] = new Object[]{"Seventy Eight", "Blue", "D", new Integer(78), new Double(78.60)};
    data[78] = new Object[]{"Seventy Nine", "Blue", "D", new Integer(79), new Double(79.60)};
    data[79] = new Object[]{"Eighty", "Blue", "D", new Integer(80), new Double(80.60)};
    data[80] = new Object[]{"Eighty One", "Blue", "D", new Integer(81), new Double(81.60)};
    data[81] = new Object[]{"Eighty Two", "Blue", "D", new Integer(82), new Double(82.60)};
    data[82] = new Object[]{"Eighty Three", "Blue", "D", new Integer(83), new Double(83.60)};
    data[83] = new Object[]{"Eighty Four", "Blue", "D", new Integer(84), new Double(84.60)};
    data[84] = new Object[]{"Eighty Five", "Blue", "D", new Integer(85), new Double(85.60)};
    data[85] = new Object[]{"Eighty Six", "Blue", "D", new Integer(86), new Double(86.60)};
    data[86] = new Object[]{"Eighty Seven", "Blue", "D", new Integer(87), new Double(87.60)};
    data[87] = new Object[]{"Eighty Eight", "Blue", "D", new Integer(88), new Double(88.60)};
    data[88] = new Object[]{"Eighty Nine", "Blue", "D", new Integer(89), new Double(89.60)};
    data[89] = new Object[]{"Ninety", "Blue", "D", new Integer(90), new Double(90.60)};
    data[90] = new Object[]{"Ninety One", "Blue", "D", new Integer(91), new Double(91.60)};
    data[91] = new Object[]{"Ninety Two", "Blue", "D", new Integer(92), new Double(92.60)};
    data[92] = new Object[]{"Ninety Three", "Blue", "D", new Integer(93), new Double(93.60)};
    data[93] = new Object[]{"Ninety Four", "Blue", "D", new Integer(94), new Double(94.60)};
    data[94] = new Object[]{"Ninety Five", "Blue", "D", new Integer(95), new Double(95.60)};
    data[95] = new Object[]{"Ninety Six", "Blue", "D", new Integer(96), new Double(96.60)};
    data[96] = new Object[]{"Ninety Seven", "Blue", "D", new Integer(97), new Double(97.60)};
    data[97] = new Object[]{"Ninety Eight", "Blue", "D", new Integer(98), new Double(98.60)};
    data[98] = new Object[]{"Ninety Nine", "Blue", "D", new Integer(99), new Double(99.60)};
    data[99] = new Object[]{"One Hundred", "Blue", "D", new Integer(100), new Double(100.60)};
    data[100] = new Object[]{"One Hundred and One", "Blue", "D",
        new Integer(101), new Double(101.60)};
    data[101] = new Object[]{"One Hundred and Two", "Blue", "D",
        new Integer(102), new Double(102.60)};
    data[102] = new Object[]{"One Hundred and Three", "Blue", "D",
        new Integer(103), new Double(103.60)};
    data[103] = new Object[]{"One Hundred and Four", "Blue", "D",
        new Integer(104), new Double(104.60)};
    data[104] = new Object[]{"One Hundred and Five", "Blue", "D",
        new Integer(105), new Double(105.60)};
    data[105] = new Object[]{"One Hundred and Six", "Blue", "D",
        new Integer(106), new Double(106.60)};
    data[106] = new Object[]{"One Hundred and Seven", "Blue", "D",
        new Integer(107), new Double(107.60)};
    data[107] = new Object[]{"One Hundred and Eight", "Blue", "D",
        new Integer(108), new Double(108.60)};
    data[108] = new Object[]{"One Hundred and Nine", "Blue", "D",
        new Integer(109), new Double(109.60)};
    data[109] = new Object[]{"One Hundred and Ten", "Blue", "D",
        new Integer(110), new Double(110.60)};
    data[110] = new Object[]{"One Hundred and Eleven", "Blue", "D",
        new Integer(111), new Double(111.60)};
    data[111] = new Object[]{"One Hundred and Twelve", "Blue", "D",
        new Integer(112), new Double(112.60)};
    data[112] = new Object[]{"One Hundred and Thirteen", "Blue", "D",
        new Integer(113), new Double(113.60)};
    data[113] = new Object[]{"One Hundred and Fourteen", "Blue", "D",
        new Integer(114), new Double(114.60)};
    data[114] = new Object[]{"One Hundred and Fifteen", "Blue", "D",
        new Integer(115), new Double(115.60)};
    data[115] = new Object[]{"One Hundred and Sixteen", "Blue", "D",
        new Integer(116), new Double(116.60)};
    data[116] = new Object[]{"One Hundred and Seventeen", "Blue", "D",
        new Integer(117), new Double(117.60)};
    data[117] = new Object[]{"One Hundred and Eighteen", "Blue", "D",
        new Integer(118), new Double(118.60)};
    data[118] = new Object[]{"One Hundred and Nineteen", "Blue", "D",
        new Integer(119), new Double(119.60)};
    data[119] = new Object[]{"One Hundred and Twenty", "Blue", "D",
        new Integer(120), new Double(120.60)};
  }

  /**
   * Returns the number of rows in the table model.
   *
   * @return the row count.
   */
  public int getRowCount()
  {
    return size;
  }

  /**
   * Returns the number of columns in the table model.
   *
   * @return the column count.
   */
  public int getColumnCount()
  {
    return 5;
  }

  /**
   * Returns the class of the data in the specified column.
   *
   * @param column the column (zero-based index).
   * @return the column class.
   */
  public Class getColumnClass(final int column)
  {
    if (column == 3)
    {
      return Integer.class;
    }
    else if (column == 4)
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
      return "Name";
    }
    else if (column == 1)
    {
      return "Color";
    }
    else if (column == 2)
    {
      return "Letter";
    }
    else if (column == 3)
    {
      return "Integer";
    }
    else if (column == 4)
    {
      return "Double";
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
    //if (column == 2 && row == 100) return null;
    return data[row][column];
  }

}

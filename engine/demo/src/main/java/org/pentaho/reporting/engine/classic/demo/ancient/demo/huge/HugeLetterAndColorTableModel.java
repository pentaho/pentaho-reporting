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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.huge;

import java.awt.Color;
import java.util.TreeMap;
import javax.swing.table.AbstractTableModel;

/**
 * A sample data source for the JFreeReport Demo Application.
 *
 * @author Thomas Morgner
 */
public class HugeLetterAndColorTableModel extends AbstractTableModel
{
  private TreeMap colors;
  private String[] colorNames;
  private int rowCount;

  /**
   * Default constructor - builds a sample data source.
   */
  public HugeLetterAndColorTableModel()
  {
    colors = new TreeMap();
    colors.put("AliceBlue", new Color(0xF0F8FF));
    colors.put("AntiqueWhite", new Color(0xFAEBD7));
    colors.put("Aqua", new Color(0x00FFFF));
    colors.put("Aquamarine", new Color(0x7FFFD4));
    colors.put("Azure", new Color(0xF0FFFF));
    colors.put("Beige", new Color(0xF5F5DC));
    colors.put("Bisque", new Color(0xFFE4C4));
    colors.put("Black", new Color(0x000000));
    colors.put("BlanchedAlmond", new Color(0xFFEBCD));
    colors.put("Blue", new Color(0x0000FF));
    colors.put("BlueViolet", new Color(0x8A2BE2));
    colors.put("Brown", new Color(0xA52A2A));
    colors.put("BurlyWood", new Color(0xDEB887));
    colors.put("CadetBlue", new Color(0x5F9EA0));
    colors.put("Chartreuse", new Color(0x7FFF00));
    colors.put("Chocolate", new Color(0xD2691E));
    colors.put("Coral", new Color(0xFF7F50));
    colors.put("CornflowerBlue", new Color(0x6495ED));
    colors.put("Cornsilk", new Color(0xFFF8DC));
    colors.put("Crimson", new Color(0xDC143C));
    colors.put("Cyan", new Color(0x00FFFF));
    colors.put("DarkBlue", new Color(0x00008B));
    colors.put("DarkCyan", new Color(0x008B8B));
    colors.put("DarkGoldenRod", new Color(0xB8860B));
    colors.put("DarkGray", new Color(0xA9A9A9));
    colors.put("DarkGreen", new Color(0x006400));
    colors.put("DarkKhaki", new Color(0xBDB76B));
    colors.put("DarkMagenta", new Color(0x8B008B));
    colors.put("DarkOliveGreen", new Color(0x556B2F));
    colors.put("Darkorange", new Color(0xFF8C00));
    colors.put("DarkOrchid", new Color(0x9932CC));
    colors.put("DarkRed", new Color(0x8B0000));
    colors.put("DarkSalmon", new Color(0xE9967A));
    colors.put("DarkSeaGreen", new Color(0x8FBC8F));
    colors.put("DarkSlateBlue", new Color(0x483D8B));
    colors.put("DarkSlateGray", new Color(0x2F4F4F));
    colors.put("DarkTurquoise", new Color(0x00CED1));
    colors.put("DarkViolet", new Color(0x9400D3));
    colors.put("DeepPink", new Color(0xFF1493));
    colors.put("DeepSkyBlue", new Color(0x00BFFF));
    colors.put("DimGray", new Color(0x696969));
    colors.put("DodgerBlue", new Color(0x1E90FF));
    colors.put("Feldspar", new Color(0xD19275));
    colors.put("FireBrick", new Color(0xB22222));
    colors.put("FloralWhite", new Color(0xFFFAF0));
    colors.put("ForestGreen", new Color(0x228B22));
    colors.put("Fuchsia", new Color(0xFF00FF));
    colors.put("Gainsboro", new Color(0xDCDCDC));
    colors.put("GhostWhite", new Color(0xF8F8FF));
    colors.put("Gold", new Color(0xFFD700));
    colors.put("GoldenRod", new Color(0xDAA520));
    colors.put("Gray", new Color(0x808080));
    colors.put("Green", new Color(0x008000));
    colors.put("GreenYellow", new Color(0xADFF2F));
    colors.put("HoneyDew", new Color(0xF0FFF0));
    colors.put("HotPink", new Color(0xFF69B4));
    colors.put("IndianRed", new Color(0xCD5C5C));
    colors.put("Indigo", new Color(0x4B0082));
    colors.put("Ivory", new Color(0xFFFFF0));
    colors.put("Khaki", new Color(0xF0E68C));
    colors.put("Lavender", new Color(0xE6E6FA));
    colors.put("LavenderBlush", new Color(0xFFF0F5));
    colors.put("LawnGreen", new Color(0x7CFC00));
    colors.put("LemonChiffon", new Color(0xFFFACD));
    colors.put("LightBlue", new Color(0xADD8E6));
    colors.put("LightCoral", new Color(0xF08080));
    colors.put("LightCyan", new Color(0xE0FFFF));
    colors.put("LightGoldenRodYellow", new Color(0xFAFAD2));
    colors.put("LightGrey", new Color(0xD3D3D3));
    colors.put("LightGreen", new Color(0x90EE90));
    colors.put("LightPink", new Color(0xFFB6C1));
    colors.put("LightSalmon", new Color(0xFFA07A));
    colors.put("LightSeaGreen", new Color(0x20B2AA));
    colors.put("LightSkyBlue", new Color(0x87CEFA));
    colors.put("LightSlateBlue", new Color(0x8470FF));
    colors.put("LightSlateGray", new Color(0x778899));
    colors.put("LightSteelBlue", new Color(0xB0C4DE));
    colors.put("LightYellow", new Color(0xFFFFE0));
    colors.put("Lime", new Color(0x00FF00));
    colors.put("LimeGreen", new Color(0x32CD32));
    colors.put("Linen", new Color(0xFAF0E6));
    colors.put("Magenta", new Color(0xFF00FF));
    colors.put("Maroon", new Color(0x800000));
    colors.put("MediumAquaMarine", new Color(0x66CDAA));
    colors.put("MediumBlue", new Color(0x0000CD));
    colors.put("MediumOrchid", new Color(0xBA55D3));
    colors.put("MediumPurple", new Color(0x9370D8));
    colors.put("MediumSeaGreen", new Color(0x3CB371));
    colors.put("MediumSlateBlue", new Color(0x7B68EE));
    colors.put("MediumSpringGreen", new Color(0x00FA9A));
    colors.put("MediumTurquoise", new Color(0x48D1CC));
    colors.put("MediumVioletRed", new Color(0xC71585));
    colors.put("MidnightBlue", new Color(0x191970));
    colors.put("MintCream", new Color(0xF5FFFA));
    colors.put("MistyRose", new Color(0xFFE4E1));
    colors.put("Moccasin", new Color(0xFFE4B5));
    colors.put("NavajoWhite", new Color(0xFFDEAD));
    colors.put("Navy", new Color(0x000080));
    colors.put("OldLace", new Color(0xFDF5E6));
    colors.put("Olive", new Color(0x808000));
    colors.put("OliveDrab", new Color(0x6B8E23));
    colors.put("Orange", new Color(0xFFA500));
    colors.put("OrangeRed", new Color(0xFF4500));
    colors.put("Orchid", new Color(0xDA70D6));
    colors.put("PaleGoldenRod", new Color(0xEEE8AA));
    colors.put("PaleGreen", new Color(0x98FB98));
    colors.put("PaleTurquoise", new Color(0xAFEEEE));
    colors.put("PaleVioletRed", new Color(0xD87093));
    colors.put("PapayaWhip", new Color(0xFFEFD5));
    colors.put("PeachPuff", new Color(0xFFDAB9));
    colors.put("Peru", new Color(0xCD853F));
    colors.put("Pink", new Color(0xFFC0CB));
    colors.put("Plum", new Color(0xDDA0DD));
    colors.put("PowderBlue", new Color(0xB0E0E6));
    colors.put("Purple", new Color(0x800080));
    colors.put("Red", new Color(0xFF0000));
    colors.put("RosyBrown", new Color(0xBC8F8F));
    colors.put("RoyalBlue", new Color(0x4169E1));
    colors.put("SaddleBrown", new Color(0x8B4513));
    colors.put("Salmon", new Color(0xFA8072));
    colors.put("SandyBrown", new Color(0xF4A460));
    colors.put("SeaGreen", new Color(0x2E8B57));
    colors.put("SeaShell", new Color(0xFFF5EE));
    colors.put("Sienna", new Color(0xA0522D));
    colors.put("Silver", new Color(0xC0C0C0));
    colors.put("SkyBlue", new Color(0x87CEEB));
    colors.put("SlateBlue", new Color(0x6A5ACD));
    colors.put("SlateGray", new Color(0x708090));
    colors.put("Snow", new Color(0xFFFAFA));
    colors.put("SpringGreen", new Color(0x00FF7F));
    colors.put("SteelBlue", new Color(0x4682B4));
    colors.put("Tan", new Color(0xD2B48C));
    colors.put("Teal", new Color(0x008080));
    colors.put("Thistle", new Color(0xD8BFD8));
    colors.put("Tomato", new Color(0xFF6347));
    colors.put("Turquoise", new Color(0x40E0D0));
    colors.put("Violet", new Color(0xEE82EE));
    colors.put("VioletRed", new Color(0xD02090));
    colors.put("Wheat", new Color(0xF5DEB3));
    colors.put("White", new Color(0xFFFFFF));
    colors.put("WhiteSmoke", new Color(0xF5F5F5));
    colors.put("Yellow", new Color(0xFFFF00));
    colors.put("YellowGreen", new Color(0x9ACD32));

    colorNames = (String[]) colors.keySet().toArray(new String[colors.size()]);

    rowCount = 200000;
  }


  /**
   * Returns the number of rows in the table model.
   *
   * @return the row count.
   */
  public int getRowCount()
  {
    return rowCount;
  }

  public void setRowCount(final int rowCount)
  {
    if (rowCount < 0)
    {
      throw new IllegalArgumentException();
    }
    this.rowCount = rowCount;
  }

  /**
   * Returns the number of columns in the table model.
   *
   * @return the column count.
   */
  public int getColumnCount()
  {
    return 6;
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
    else if (column == 5)
    {
      return Color.class;
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
    else if (column == 5)
    {
      return "ColorObject";
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
    switch (column)
    {
      case 0:
        return convertToEnglishNumber(row);
      case 1:
        return computeColorName(row);
      case 2:
        return computeLetter(row);
      case 3:
        return new Integer(1);
      case 4:
        return new Double(1.1);
      case 5:
        return colors.get(computeColorName(row));
      default:
        throw new IllegalArgumentException("Unexcpected column.");
    }
  }

  private String computeLetter(final int row)
  {
    // a new char every 100 rows, and we have 24 chars.
    final int idx = (row / 100) % 24;
    return "" + (char) ('A' + idx);
  }

  private String computeColorName(final int row)
  {
    // a new color every 1000 rows, and we have ?? colors in totoal.
    final int idx = (row / 1000) % colorNames.length;
    return colorNames[idx];
  }

  private static final String[] ONES_NAMES = {
      " one", " two", " three", " four", " five",
      " six", " seven", " eight", " nine", " ten",
      " eleven", " twelve", " thirteen", " fourteen",
      " fifteen", " sixteen", " seventeen",
      " eighteen", " nineteen"
  };

  private static final String[] TENS_NAMES = {
      " twenty", " thirty", " forty", " fifty",
      " sixty", " seventy", " eighty", " ninety"
  };
  //
  // so quintillions is as big as it gets. The
  // program would automatically handle larger
  // numbers if this array were extended.
  //
  private static final String[] GROUPS = {
      "",
      " thousand",
      " million",
      " billion",
      " trillion",
      " quadrillion",
      " quintillion"
  };

  private String convertToEnglishNumber(long n)
  {

    final StringBuffer result = new StringBuffer();

    // Go through the number one group at a time.
    for (int i = GROUPS.length - 1; i >= 0; i--)
    {

      // Is the number as big as this group?

      final long cutoff = (long) Math.pow(10.0, (i * 3.0));

      if (n >= cutoff)
      {

        int thisPart = (int) (n / cutoff);

        // Use the ONES_NAMES[] array for both the
        // hundreds and the ONES_NAMES digit. Note
        // that TENS_NAMES[] starts at "twenty".
        if (thisPart >= 100)
        {
          result.append(ONES_NAMES[(thisPart / 100) - 1]);
          result.append(" hundred");
          thisPart = thisPart % 100;
        }
        if (thisPart >= 20)
        {
          result.append(TENS_NAMES[(thisPart / 10) - 2]);
          thisPart = thisPart % 10;
        }
        if (thisPart >= 1)
        {
          result.append(ONES_NAMES[thisPart - 1]);
        }

        result.append(GROUPS[i]);

        n = n % cutoff;

      }
    }

    if (result.length() == 0)
    {
      return "zero";
    }
    else
    {
      // remove initial space
      return result.substring(1);
    }
  }

}

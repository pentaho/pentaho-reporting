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

package org.pentaho.reporting.engine.classic.demo.util;

import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A table cell renderer that formats numbers with right alignment in each cell.
 *
 * @author David Gilbert
 */
public class NumberCellRenderer extends DefaultTableCellRenderer
{

  /**
   * Default constructor - builds a renderer that right justifies the contents of a table cell.
   */
  public NumberCellRenderer()
  {
    super();
    setHorizontalAlignment(SwingConstants.RIGHT);
  }

  /**
   * Returns itself as the renderer. Supports the TableCellRenderer interface.
   *
   * @param table      the table.
   * @param value      the data to be rendered.
   * @param isSelected a boolean that indicates whether or not the cell is selected.
   * @param hasFocus   a boolean that indicates whether or not the cell has the focus.
   * @param row        the (zero-based) row index.
   * @param column     the (zero-based) column index.
   * @return the component that can render the contents of the cell.
   */
  public Component getTableCellRendererComponent(final JTable table,
                                                 final Object value, final boolean isSelected,
                                                 final boolean hasFocus, final int row, final int column)
  {

    setFont(null);
    final NumberFormat nf = NumberFormat.getNumberInstance();
    if (value != null)
    {
      setText(nf.format(value));
    }
    else
    {
      setText("");
    }
    if (isSelected)
    {
      setBackground(table.getSelectionBackground());
    }
    else
    {
      setBackground(null);
    }
    return this;
  }

}

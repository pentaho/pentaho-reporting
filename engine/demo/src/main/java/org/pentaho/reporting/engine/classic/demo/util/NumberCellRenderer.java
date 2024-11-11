/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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

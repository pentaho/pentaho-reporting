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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.swingicons;

import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * A table cell renderer that draws an image in a table cell. <P> This class will be moved to the JCommon class
 * library.
 *
 * @author David Gilbert
 */
public class ImageCellRenderer extends DefaultTableCellRenderer
    implements TableCellRenderer
{

  /**
   * The icon.
   */
  private final ImageIcon icon = new ImageIcon();

  /**
   * Constructs a new renderer.
   */
  public ImageCellRenderer()
  {
    super();
    setHorizontalAlignment(JLabel.CENTER);
    setVerticalAlignment(JLabel.CENTER);
    setIcon(icon);
  }

  /**
   * Returns itself as the renderer. Supports the TableCellRenderer interface.
   *
   * @param table      The table.
   * @param value      The data to be rendered.
   * @param isSelected A boolean that indicates whether or not the cell is selected.
   * @param hasFocus   A boolean that indicates whether or not the cell has the focus.
   * @param row        The (zero-based) row index.
   * @param column     The (zero-based) column index.
   * @return The component that can render the contents of the cell.
   */
  public Component getTableCellRendererComponent
      (final JTable table, final Object value, final boolean isSelected,
       final boolean hasFocus, final int row, final int column)
  {

    setFont(null);
    icon.setImage((Image) value);
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

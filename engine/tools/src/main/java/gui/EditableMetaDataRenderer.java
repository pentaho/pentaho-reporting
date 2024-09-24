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

package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class EditableMetaDataRenderer extends DefaultTableCellRenderer {
  public EditableMetaDataRenderer() {
  }

  /**
   * Returns the default table cell renderer.
   * <p/>
   * During a printing operation, this method will be called with <code>isSelected</code> and <code>hasFocus</code>
   * values of <code>false</code> to prevent selection and focus from appearing in the printed output. To do other
   * customization based on whether or not the table is being printed, check the return value from {@link
   * javax.swing.JComponent#isPaintingForPrint()}.
   *
   * @param table      the <code>JTable</code>
   * @param value      the value to assign to the cell at <code>[row, column]</code>
   * @param isSelected true if cell is selected
   * @param hasFocus   true if cell has focus
   * @param row        the row of the cell to render
   * @param column     the column of the cell to render
   * @return the default table cell renderer
   * @see javax.swing.JComponent#isPaintingForPrint()
   */
  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {

    final EditableMetaDataTableModel tableModel = (EditableMetaDataTableModel) table.getModel();
    if ( tableModel.isValidValue( table.convertRowIndexToModel( row ), table.convertColumnIndexToModel( column ) ) ) {
      setBackground( Color.WHITE );
    } else {
      setBackground( Color.ORANGE );
    }
    super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    return this;
  }
}

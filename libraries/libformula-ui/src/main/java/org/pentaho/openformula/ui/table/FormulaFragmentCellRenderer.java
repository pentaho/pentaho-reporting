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

package org.pentaho.openformula.ui.table;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class FormulaFragmentCellRenderer implements TableCellRenderer, ListCellRenderer {
  private DefaultTableCellRenderer tableCellRenderer;
  private DefaultListCellRenderer listCellRenderer;

  public FormulaFragmentCellRenderer() {
    tableCellRenderer = new DefaultTableCellRenderer();
    tableCellRenderer.putClientProperty( "html.disable", Boolean.TRUE ); // NON-NLS

    listCellRenderer = new DefaultListCellRenderer();
    listCellRenderer.putClientProperty( "html.disable", Boolean.TRUE ); // NON-NLS
  }

  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    if ( value == null ) {
      return tableCellRenderer.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    }

    String sValue = String.valueOf( value );
    if ( StringUtils.isEmpty( sValue ) ) {
      return tableCellRenderer.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    } else if ( sValue.startsWith( "=" ) ) {
      // User does not need to prepend equal sign as it gets prepended automatically
      // Strip off the equal sign and update expression editor and table cell.
      sValue = sValue.substring( 1 );
    }

    final Component retval = tableCellRenderer.getTableCellRendererComponent
      ( table, FormulaUtil.createCellUITextFromFormula( sValue ), isSelected, hasFocus, row, column );
    if ( FormulaUtil.isValidFormulaFragment( sValue ) == false ) {
      retval.setBackground( Color.RED );
    } else if ( retval.getBackground() == Color.RED ) {
      retval.setBackground( table.getBackground() );
    }
    return retval;
  }

  /**
   * Return a component that has been configured to display the specified value. That component's <code>paint</code>
   * method is then called to "render" the cell.  If it is necessary to compute the dimensions of a list because the
   * list cells do not have a fixed size, this method is called to generate a component on which
   * <code>getPreferredSize</code> can be invoked.
   *
   * @param list         The JList we're painting.
   * @param value        The value returned by list.getModel().getElementAt(index).
   * @param index        The cells index.
   * @param isSelected   True if the specified cell was selected.
   * @param cellHasFocus True if the specified cell has the focus.
   * @return A component whose paint() method will render the specified value.
   * @see javax.swing.JList
   * @see javax.swing.ListSelectionModel
   * @see javax.swing.ListModel
   */
  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {

    if ( value == null ) {
      return listCellRenderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    }
    final String sValue = String.valueOf( value );
    if ( StringUtils.isEmpty( sValue ) ) {
      return listCellRenderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    }

    final Component retval = listCellRenderer.getListCellRendererComponent
      ( list, FormulaUtil.createCellUITextFromFormula( sValue ), index, isSelected, cellHasFocus );
    if ( FormulaUtil.isValidFormulaFragment( sValue ) == false ) {
      retval.setBackground( Color.RED );
    } else {
      retval.setBackground( list.getBackground() );
    }
    return retval;
  }
}

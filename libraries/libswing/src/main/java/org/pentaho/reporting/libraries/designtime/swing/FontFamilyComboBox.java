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

package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import java.awt.*;

/**
 * A preconfigured combobox for font-family names.
 */
public class FontFamilyComboBox extends SmartComboBox {
  /**
   * A cell-renderer that renders the font.
   */
  private static class FontListCellRenderer extends JLabel implements ListCellRenderer {
    /**
     * Creates a new renderer.
     */
    private FontListCellRenderer() {
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
     * @see JList
     * @see javax.swing.ListSelectionModel
     * @see javax.swing.ListModel
     */
    public Component getListCellRendererComponent( final JList list,
                                                   final Object value,
                                                   final int index,
                                                   final boolean isSelected,
                                                   final boolean cellHasFocus ) {
      final Font listFont = list.getFont();
      if ( value == null ) {
        setText( "" );
        setFont( listFont );
        return this;
      }

      final String fontName = String.valueOf( value );
      setFont( listFont );
      setText( fontName );
      return this;
    }
  }

  private DefaultComboBoxModel model;

  /**
   * Creates a new combobox and populates it with the font families found in the AWT-Toolkit.
   */
  public FontFamilyComboBox() {
    final String[] availableFontFamilyNames =
      GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    model = new DefaultComboBoxModel( availableFontFamilyNames );

    setModel( model );
    setRenderer( new FontListCellRenderer() );
  }

  /**
   * Updates the selected value without firing an ActionEvent.
   *
   * @param o the newly selected value.
   */
  public void setValueFromModel( final Object o ) {
    final Action oldAction = getAction();
    setAction( null );
    model.setSelectedItem( o );
    setAction( oldAction );
  }

}

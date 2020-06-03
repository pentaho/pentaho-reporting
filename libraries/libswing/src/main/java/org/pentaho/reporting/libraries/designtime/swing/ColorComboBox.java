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
 * A predefined combobox that contains the predefined excel color pallette.
 */
public class ColorComboBox extends SmartComboBox {
  /**
   * Creates a new color combobox and populates it with the excel colors.
   */
  public ColorComboBox() {
    final DefaultComboBoxModel model = new DefaultComboBoxModel( ColorUtility.getPredefinedExcelColors() );
    model.insertElementAt( null, 0 );
    model.setSelectedItem( null );

    setModel( model );
    setRenderer( new ColorCellRenderer() );
    final int height1 = getPreferredSize().height;
    setMaximumSize( new Dimension( height1 * 4, height1 ) );
    setFocusable( false );
    setEditable( false );
  }

  /**
   * Defines the selected value without fireing a action event.
   *
   * @param o the new selected value.
   */
  public void setValueFromModel( final Color o ) {
    final Action old = getAction();
    setAction( null );
    setSelectedItem( o );
    setAction( old );
  }

  /**
   * Returns the currently selected value from the model, or null if no value is selected.
   *
   * @return the selected color.
   */
  public Color getValueFromModel() {
    return (Color) getSelectedItem();
  }
}

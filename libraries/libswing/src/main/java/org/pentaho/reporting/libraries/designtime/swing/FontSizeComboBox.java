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
 * A preconfigured combobox for font-sizes.
 *
 * @author Thomas Morgner
 */
public final class FontSizeComboBox extends JComboBox {
  public FontSizeComboBox() {
    final Integer[] fontSizes = new Integer[] {
      new Integer( 6 ),
      new Integer( 7 ),
      new Integer( 8 ),
      new Integer( 9 ),
      new Integer( 10 ),
      new Integer( 11 ),
      new Integer( 12 ),
      new Integer( 14 ),
      new Integer( 16 ),
      new Integer( 18 ),
      new Integer( 20 ),
      new Integer( 24 ),
      new Integer( 28 ),
      new Integer( 32 ),
      new Integer( 36 ),
      new Integer( 48 ),
      new Integer( 72 ) };
    setModel( new DefaultComboBoxModel( fontSizes ) );
    setFocusable( false );
    final int height1 = getPreferredSize().height;
    setMaximumSize( new Dimension( height1 * 2, height1 ) );
  }

  /**
   * Updates the selected value without fireing an ActionEvent.
   *
   * @param o the new selected value.
   */
  protected void setValueFromModel( final Object o ) {
    final Action action = getAction();
    setAction( null );
    setSelectedItem( o );
    setAction( action );
  }

}

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

package org.pentaho.reporting.designer.core.util.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ArrayCellRenderer extends DefaultTableCellRenderer {
  public ArrayCellRenderer() {
    putClientProperty( "html.disable", Boolean.TRUE ); // NON-NLS
  }

  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {

    final JLabel rendererComponent =
      (JLabel) super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    if ( ArrayAccessUtility.isArray( value ) == false ) {
      rendererComponent.setText( "" );
    } else {
      rendererComponent.setText( ArrayAccessUtility.getArrayAsString( value ) );
    }
    return rendererComponent;
  }
}

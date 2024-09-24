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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class DefaultTableHeaderRenderer extends DefaultTableCellRenderer {
  public DefaultTableHeaderRenderer() {
    setHorizontalAlignment( JLabel.CENTER );
  }

  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    if ( table != null ) {
      final JTableHeader header = table.getTableHeader();
      if ( header != null ) {
        setForeground( header.getForeground() );
        setBackground( header.getBackground() );
        setFont( header.getFont() );
      }
    }

    setText( ( value == null ) ? "" : value.toString() );
    setBorder( UIManager.getBorder( "TableHeader.cellBorder" ) );
    return this;
  }
}

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
 * A list cell renderer that corrects the obvious Swing bug where empty strings or null-values as first element in a
 * JList or a JCombobox cause the list to have a size near to zero. This bug prevents any meaningful interaction with
 * the List/Combobox.
 *
 * @author Thomas Morgner.
 */
public class EmptyValueListCellRenderer extends DefaultListCellRenderer {
  public EmptyValueListCellRenderer() {
  }

  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    if ( value == null || "".equals( value ) ) {
      return super.getListCellRendererComponent( list, " ", index, isSelected, cellHasFocus );
    }
    return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
  }
}

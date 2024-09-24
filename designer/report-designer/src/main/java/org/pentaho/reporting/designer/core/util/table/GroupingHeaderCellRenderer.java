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
import javax.swing.text.StyleContext;
import java.awt.*;
import java.net.URL;

/**
 * User: Martin Date: 30.01.2006 Time: 10:57:31
 */
public class GroupingHeaderCellRenderer extends DefaultTableCellRenderer {
  private static final String EXPAND_ICON_LOCATION = "/org/pentaho/reporting/designer/core/icons/btn_plus.png";
    //$NON-NLS-1$
  private static final String COLLAPSE_ICON_LOCATION = "/org/pentaho/reporting/designer/core/icons/btn_minus.png";
    //$NON-NLS-1$

  private Icon expandImage;
  private Icon collapseImage;

  public GroupingHeaderCellRenderer() {
    final URL expandIconUrl = GroupingHeaderCellRenderer.class.getResource( EXPAND_ICON_LOCATION );
    if ( expandIconUrl == null ) {
      throw new IllegalStateException( "Icon-file was not found: " + EXPAND_ICON_LOCATION );
    }

    final URL collapseIconUrl = GroupingHeaderCellRenderer.class.getResource( COLLAPSE_ICON_LOCATION );
    if ( collapseIconUrl == null ) {
      throw new IllegalStateException( "Icon-file was not found: " + COLLAPSE_ICON_LOCATION );
    }

    expandImage = new ImageIcon( Toolkit.getDefaultToolkit().createImage( expandIconUrl ) );
    collapseImage = new ImageIcon( Toolkit.getDefaultToolkit().createImage( collapseIconUrl ) );
  }

  public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected,
                                                  final boolean hasFocus, final int row, final int column ) {

    final JLabel label =
      (JLabel) super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    if ( value == null ) {
      return label;
    }
    label.setBackground( Color.GRAY );
    label.setForeground( Color.WHITE );
    if ( column >= 1 ) {
      label.setText( "" ); //$NON-NLS-1$
      label.setIcon( null );
    } else {
      final Font font = StyleContext.getDefaultStyleContext().getFont( label.getFont().getName(), Font.BOLD,
        label.getFont().getSize() );
      label.setFont( font );
      final GroupingHeader groupingHeader = (GroupingHeader) value;
      final boolean isCollapsed = groupingHeader.isCollapsed();
      if ( table.getModel() instanceof GroupedTableModel ) {
        label.setIcon( isCollapsed ? expandImage : collapseImage );
      }
    }
    return label;
  }
}

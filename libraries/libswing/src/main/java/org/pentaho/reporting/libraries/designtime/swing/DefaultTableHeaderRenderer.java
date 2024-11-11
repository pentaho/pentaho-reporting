/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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

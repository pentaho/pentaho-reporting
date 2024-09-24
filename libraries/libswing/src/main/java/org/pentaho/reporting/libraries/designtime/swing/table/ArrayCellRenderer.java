/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.libraries.designtime.swing.table;

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

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

package org.pentaho.reporting.ui.datasources.kettle.parameter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ArgumentCountCellRenderer extends DefaultTableCellRenderer {
  public ArgumentCountCellRenderer() {
  }

  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    String valueText = Messages.getInstance().formatMessage( "ArgumentCountCellRenderer.DisplayMessage", row + 1 );
    return super.getTableCellRendererComponent( table, valueText, isSelected, hasFocus, row, column );
  }
}

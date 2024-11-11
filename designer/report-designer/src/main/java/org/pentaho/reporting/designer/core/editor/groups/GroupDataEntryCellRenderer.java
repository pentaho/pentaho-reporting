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


package org.pentaho.reporting.designer.core.editor.groups;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class GroupDataEntryCellRenderer extends DefaultTableCellRenderer {
  public GroupDataEntryCellRenderer() {
    putClientProperty( "html.disable", Boolean.TRUE );//NON-NLS
  }

  // implements javax.swing.table.TableCellRenderer
  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    final JLabel rendererComponent =
      (JLabel) super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    if ( value instanceof GroupDataEntry == false ) {
      rendererComponent.setText( " " );
    } else {
      final GroupDataEntry entry = (GroupDataEntry) value;
      rendererComponent.setText( entry.getFieldsAsText() );
    }
    return rendererComponent;
  }
}

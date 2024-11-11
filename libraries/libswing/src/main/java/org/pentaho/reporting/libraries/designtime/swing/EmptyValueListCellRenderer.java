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

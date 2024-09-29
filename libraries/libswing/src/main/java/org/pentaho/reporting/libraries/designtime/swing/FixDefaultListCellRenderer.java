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


package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This class fixes a bug in the DefaultListCellRenderer's behaviour that caused cells to be infinitely small when the
 * text to be displayed is a empty string.
 */
public class FixDefaultListCellRenderer extends DefaultListCellRenderer {
  public FixDefaultListCellRenderer() {
  }

  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    if ( value == null ) {
      return super.getListCellRendererComponent( list, "<null>", index, isSelected, cellHasFocus );//$NON-NLS-1$
    }
    if ( "".equals( value ) ) {
      return super.getListCellRendererComponent( list, " ", index, isSelected, cellHasFocus );
    }
    return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
  }
}


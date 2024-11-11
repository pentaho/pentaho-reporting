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


package org.pentaho.reporting.ui.datasources.pmd.util;

import javax.swing.*;
import java.awt.*;

public class QueryNameListCellRenderer extends DefaultListCellRenderer {
  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    final JLabel listCellRendererComponent =
      (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    if ( value != null ) {
      final String queryName = ( (DataSetQuery) value ).getQueryName();
      if ( !"".equals( queryName ) ) {
        listCellRendererComponent.setText( queryName );
      } else {
        listCellRendererComponent.setText( " " );
      }
    }
    return listCellRendererComponent;
  }
}

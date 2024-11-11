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


package org.pentaho.reporting.engine.classic.core.designtime.datafactory;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.pentaho.reporting.libraries.base.util.StringUtils;

public class QueryNameListCellRenderer extends DefaultListCellRenderer {
  public Component getListCellRendererComponent( final JList list, final Object value, final int index,
      final boolean isSelected, final boolean cellHasFocus ) {
    final JLabel listCellRendererComponent =
        (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    if ( value != null ) {
      final DataSetQuery dataSetQuery = (DataSetQuery) value;
      final String queryName = dataSetQuery.getQueryName();
      if ( !StringUtils.isEmpty( queryName, false ) ) {
        listCellRendererComponent.setText( queryName );
      } else {
        listCellRendererComponent.setText( " " );
      }
    }
    return listCellRendererComponent;
  }
}

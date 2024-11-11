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


package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.ui;

import java.awt.Component;

import javax.swing.JList;

import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.Query;
import org.pentaho.reporting.libraries.designtime.swing.FixDefaultListCellRenderer;

public class QueryListCellRenderer extends FixDefaultListCellRenderer {
  public QueryListCellRenderer() {
  }

  public Component getListCellRendererComponent( final JList list, final Object value, final int index,
      final boolean isSelected, final boolean cellHasFocus ) {
    if ( value instanceof Query ) {
      Query q = (Query) value;
      return super.getListCellRendererComponent( list, q.getName(), index, isSelected, cellHasFocus );
    }

    return super.getListCellRendererComponent( list, null, index, isSelected, cellHasFocus );
  }
}

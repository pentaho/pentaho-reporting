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

package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * User: Martin Date: 01.02.2006 Time: 19:12:03
 */
public class InspectionResultTable extends JTable {
  private static class InspectionResultTableCellRenderer extends DefaultTableCellRenderer {
    private InspectionResultTableCellRenderer() {
      putClientProperty( "html.disable", Boolean.TRUE );//NON-NLS
    }

    public Component getTableCellRendererComponent( final JTable table,
                                                    final Object value,
                                                    final boolean isSelected,
                                                    final boolean hasFocus,
                                                    final int row,
                                                    final int column ) {
      final JLabel label =
        (JLabel) super.getTableCellRendererComponent( table, null, isSelected, hasFocus, row, column );
      if ( value instanceof InspectionResult ) {
        final InspectionResult result = (InspectionResult) value;
        final InspectionResult.Severity severity = result.getSeverity();
        if ( severity == InspectionResult.Severity.ERROR ) {
          label.setIcon( IconLoader.getInstance().getErrorIcon() );
        } else if ( severity == InspectionResult.Severity.WARNING ) {
          label.setIcon( IconLoader.getInstance().getWarningIcon() );
        } else if ( severity == InspectionResult.Severity.HINT ) {
          label.setIcon( IconLoader.getInstance().getInfoIcon() );
        }

        label.setText( result.getDescription() );
      }
      return label;
    }
  }


  private InspectionResultTableModel inspectionResultTableModel;

  public InspectionResultTable() {
    inspectionResultTableModel = new InspectionResultTableModel();
    setModel( inspectionResultTableModel );

    setDefaultRenderer( InspectionResult.class, new InspectionResultTableCellRenderer() );
  }

  public InspectionResult getInspectionResult( final int row ) {
    return inspectionResultTableModel.getInspectionResult( row );
  }
}

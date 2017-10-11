/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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

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

package org.pentaho.reporting.designer.core.util.table.expressions;

import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Locale;

public class ReportPreProcessorCellRenderer extends DefaultTableCellRenderer {
  public ReportPreProcessorCellRenderer() {
    putClientProperty( "html.disable", Boolean.TRUE ); // NON-NLS
  }

  /**
   * Returns the default table cell renderer.
   *
   * @param table      the <code>JTable</code>
   * @param value      the value to assign to the cell at <code>[row, column]</code>
   * @param isSelected true if cell is selected
   * @param hasFocus   true if cell has focus
   * @param row        the row of the cell to render
   * @param column     the column of the cell to render
   * @return the default table cell renderer
   */
  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    // just configure it
    super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    setText( " " );
    setToolTipText( null );
    if ( value != null ) {
      final ReportPreProcessorRegistry registry = ReportPreProcessorRegistry.getInstance();
      final String expressionName = value.getClass().getName();
      if ( registry.isReportPreProcessorRegistered( expressionName ) ) {
        final ReportPreProcessorMetaData data =
          registry.getReportPreProcessorMetaData( expressionName );
        setText( data.getDisplayName( Locale.getDefault() ) );
        if ( data.isDeprecated() ) {
          setToolTipText( data.getDeprecationMessage( Locale.getDefault() ) );
        }
      } else {
        setText( expressionName );
      }
    }
    return this;
  }
}

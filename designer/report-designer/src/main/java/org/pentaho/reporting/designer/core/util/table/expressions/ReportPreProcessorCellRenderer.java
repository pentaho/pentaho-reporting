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

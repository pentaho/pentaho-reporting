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

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class ReportPreProcessorListCellRenderer extends DefaultListCellRenderer {
  public ReportPreProcessorListCellRenderer() {
  }

  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    final JLabel rendererComponent = (JLabel)
      super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    if ( value instanceof ReportPreProcessorMetaData ) {
      final ReportPreProcessorMetaData metaData = (ReportPreProcessorMetaData) value;
      rendererComponent.setText( metaData.getDisplayName( Locale.getDefault() ) );
      rendererComponent.setToolTipText( metaData.getDeprecationMessage( Locale.getDefault() ) );
    } else if ( value instanceof ReportPreProcessor ) {
      String key = value.getClass().getName();
      if ( ReportPreProcessorRegistry.getInstance().isReportPreProcessorRegistered( key ) ) {
        ReportPreProcessorMetaData metaData =
          ReportPreProcessorRegistry.getInstance().getReportPreProcessorMetaData( key );
        String displayName = metaData.getDisplayName( Locale.getDefault() );
        rendererComponent
          .setText( Messages.getString( "ReportPreProcessorCellEditor.EditingInstanceMessage", displayName ) );
        rendererComponent.setToolTipText( metaData.getDeprecationMessage( Locale.getDefault() ) );
      } else {
        rendererComponent.setText( Messages.getString( "ReportPreProcessorCellEditor.EditingInstanceMessage", key ) );
        rendererComponent.setToolTipText( null );
      }
    } else {
      setText( " " );
    }
    return rendererComponent;
  }
}

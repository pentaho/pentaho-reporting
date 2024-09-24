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

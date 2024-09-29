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


package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class ExpressionListCellRenderer extends DefaultListCellRenderer {
  public ExpressionListCellRenderer() {
  }

  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    final JLabel rendererComponent = (JLabel)
      super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    if ( value instanceof ExpressionMetaData ) {
      final ExpressionMetaData metaData = (ExpressionMetaData) value;

      final boolean deprecated;
      final boolean expert;
      final boolean preferred;
      deprecated = metaData.isDeprecated();
      expert = metaData.isExpert();
      preferred = metaData.isPreferred();

      String prefix = "";
      if ( deprecated ) {
        prefix = "*";
      }
      int fontStyle = Font.PLAIN;
      if ( expert ) {
        fontStyle |= Font.ITALIC;
      }
      if ( preferred ) {
        fontStyle |= Font.BOLD;
      }
      rendererComponent.setFont( getFont().deriveFont( fontStyle ) );
      final String displayName = metaData.getDisplayName( Locale.getDefault() );
      final String groupName = metaData.getGrouping( Locale.getDefault() );
      rendererComponent.setToolTipText( metaData.getDeprecationMessage( Locale.getDefault() ) );
      rendererComponent.setText( prefix + displayName + " (" + groupName + ")" );
    } else {
      rendererComponent.setText( " " );
    }
    return rendererComponent;
  }
}

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

package org.pentaho.reporting.designer.core.editor.expressions;

import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Locale;

public class ExpressionTreeCellRenderer extends DefaultTreeCellRenderer {
  public ExpressionTreeCellRenderer() {
  }

  public Component getTreeCellRendererComponent( final JTree tree,
                                                 final Object value,
                                                 final boolean sel,
                                                 final boolean expanded,
                                                 final boolean leaf,
                                                 final int row,
                                                 final boolean hasFocus ) {
    final JLabel rendererComponent = (JLabel)
      super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
    if ( value instanceof ExpressionMetaData ) {
      final ExpressionMetaData metaData = (ExpressionMetaData) value;
      rendererComponent.setText( metaData.getDisplayName( Locale.getDefault() ) );
      rendererComponent.setToolTipText( metaData.getDeprecationMessage( Locale.getDefault() ) );
    }
    return rendererComponent;
  }
}

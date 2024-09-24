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

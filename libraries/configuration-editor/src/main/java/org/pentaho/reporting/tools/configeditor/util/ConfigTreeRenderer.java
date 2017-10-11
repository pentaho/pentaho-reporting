/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.tools.configeditor.util;

import org.pentaho.reporting.tools.configeditor.model.ConfigTreeModuleNode;
import org.pentaho.reporting.tools.configeditor.model.ConfigTreeRootNode;
import org.pentaho.reporting.tools.configeditor.model.ConfigTreeSectionNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;


/**
 * Implements a config tree renderer that fixes some AWT-Graphics problems in conjunction with the clipping. It seems
 * that the AWT-Graphics ignores the clipping bounds for some primitive operations. Clipping is done if the operations
 * are performed on the Graphics2D level.
 *
 * @author Thomas Morgner
 * @see BugFixProxyGraphics2D
 */
public class ConfigTreeRenderer extends DefaultTreeCellRenderer {
  /**
   * DefaultConstructor.
   */
  public ConfigTreeRenderer() {
    setDoubleBuffered( false );
  }

  /**
   * Configures the renderer based on the passed in components. The value is set from messaging the tree with
   * <code>convertValueToText</code>, which ultimately invokes <code>toString</code> on <code>value</code>. The
   * foreground color is set based on the selection and the icon is set based on on leaf and expanded.
   *
   * @param tree     the tree that renders the node.
   * @param value    the tree node
   * @param sel      whether the node is selected.
   * @param expanded whether the node is expanded
   * @param leaf     whether the node is a leaf
   * @param row      the row number of the node in the tree.
   * @param hasFocus whether the node has the input focus
   * @return the renderer component.
   */
  public Component getTreeCellRendererComponent( final JTree tree, final Object value,
                                                 final boolean sel,
                                                 final boolean expanded,
                                                 final boolean leaf, final int row,
                                                 final boolean hasFocus ) {
    if ( value instanceof ConfigTreeRootNode ) {
      return super.getTreeCellRendererComponent( tree, "<Root>", //$NON-NLS-1$
        sel, expanded, leaf, row, hasFocus );
    } else if ( value instanceof ConfigTreeSectionNode ) {
      final ConfigTreeSectionNode node = (ConfigTreeSectionNode) value;
      return super.getTreeCellRendererComponent( tree, node.getName(),
        sel, expanded, leaf, row, hasFocus );
    } else if ( value instanceof ConfigTreeModuleNode ) {
      final ConfigTreeModuleNode node = (ConfigTreeModuleNode) value;
      final StringBuilder text = new StringBuilder( 100 );
      text.append( node.getModule().getName() );
      //      text.append(" - "); //$NON-NLS-1$
      //      text.append(node.getModule().getMajorVersion());
      //      text.append('.');
      //      text.append(node.getModule().getMinorVersion());
      //      text.append('-');
      //      text.append(node.getModule().getPatchLevel());
      return super.getTreeCellRendererComponent( tree, text.toString(),
        sel, expanded, leaf, row, hasFocus );
    }
    return super.getTreeCellRendererComponent( tree, value,
      sel, expanded, leaf, row, hasFocus );
  }

  /**
   * Paints the value.  The background is filled based on selected. The TreeCellRenderer or Swing or something else has
   * a bug inside so that the clipping of the graphics is not done correctly. If a rectangle is painted with
   * Graphics.fillRect(int, int, int, int) the graphics is totally messed up.
   *
   * @param g the graphics.
   */
  public void paint( final Graphics g ) {
    super.paint( new BugFixProxyGraphics2D( (Graphics2D) g ) );
  }
}

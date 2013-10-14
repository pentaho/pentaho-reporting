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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */
package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.pentaho.reporting.ui.datasources.jdbc.Messages;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;

public class DataSourceDefinitionCellRenderer extends DefaultTreeCellRenderer
{
  public DataSourceDefinitionCellRenderer()
  {
  }

  public Component getTreeCellRendererComponent(final JTree tree,
                                                final Object value,
                                                final boolean sel,
                                                final boolean expanded,
                                                final boolean leaf,
                                                final int row,
                                                final boolean hasFocus)
  {
    final JLabel listCellRendererComponent = (JLabel)
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    if (value instanceof JdbcConnectionDefinition)
    {
      final JdbcConnectionDefinition def = (JdbcConnectionDefinition) value;
      final String jndiName = def.getName();
      if (!"".equals(jndiName))
      {
        listCellRendererComponent.setText(jndiName);
      }
      else
      {
        listCellRendererComponent.setText(" ");
      }
    }
    else if (ConnectionsTreeModel.MetaNode.ROOT.equals(value))
    {
      listCellRendererComponent.setText("ROOT"); // NON-NLS
    }
    else if (ConnectionsTreeModel.MetaNode.PRIVATE.equals(value))
    {
      listCellRendererComponent.setText(Messages.getString("ConnectionPanel.StoredConnections"));
    }
    else if (ConnectionsTreeModel.MetaNode.SHARED.equals(value))
    {
      listCellRendererComponent.setText(Messages.getString("ConnectionPanel.SharedConnections"));
    }
    return listCellRendererComponent;
  }
}

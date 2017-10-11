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

package org.pentaho.reporting.tools.configeditor.model;

import javax.swing.tree.TreeNode;

/**
 * The root node contains the local and the global node and is the main entry point into the tree.
 *
 * @author Thomas Morgner
 */
public class ConfigTreeRootNode extends AbstractConfigTreeNode {
  /**
   * Creates a new root node with the given name.
   *
   * @param name the name of the node.
   */
  public ConfigTreeRootNode( final String name ) {
    super( name );
  }

  /**
   * Returns the parent <code>TreeNode</code> of the receiver.
   *
   * @return always null, as the root node never has a parent.
   */
  public TreeNode getParent() {
    return null;
  }

}

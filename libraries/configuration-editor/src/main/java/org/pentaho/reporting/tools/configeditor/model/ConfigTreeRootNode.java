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

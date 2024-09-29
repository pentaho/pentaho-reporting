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
 * The config tree is the base class for all nodes in the ConfigTreeModel.
 *
 * @author Thomas Morgner
 */
public interface ConfigTreeNode extends TreeNode {
  /**
   * Returns the name of the node.
   *
   * @return the name of the node.
   */
  public String getName();

  /**
   * Defines the parent of this node; this replaces all previously defined parents.
   *
   * @param parent the new parent node.
   */
  public void setParent( TreeNode parent );
}

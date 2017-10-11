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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * An abstract base implementation of the config tree node interface. The implementation provides all base services
 * needed to have an valid TreeNode.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractConfigTreeNode implements ConfigTreeNode {
  /**
   * The list of tree nodes that act as childs.
   */
  private final ArrayList<ConfigTreeNode> childs;
  /**
   * The name of this node.
   */
  private final String name;
  /**
   * The parent of this node or null, if this is the root node.
   */
  private TreeNode parent;

  /**
   * Creates a new config tree node with the given name. The node will be able to have child nodes.
   *
   * @param name the name of the node.
   */
  protected AbstractConfigTreeNode( final String name ) {
    childs = new ArrayList<ConfigTreeNode>();
    this.name = name;
  }

  /**
   * Adds the given node to the tree model.
   *
   * @param node the new node that should be added.
   */
  public void add( final ConfigTreeNode node ) {
    if ( node == null ) {
      throw new NullPointerException();
    }
    if ( childs.contains( node ) == false ) {
      childs.add( node );
      node.setParent( this );
    }
  }

  /**
   * Removes all child nodes.
   */
  protected void reset() {
    for ( int i = 0; i < childs.size(); i++ ) {
      final ConfigTreeNode node = childs.get( i );
      node.setParent( null );
    }
    childs.clear();
  }

  /**
   * Returns the child <code>TreeNode</code> at index <code>childIndex</code>.
   *
   * @param childIndex the index of the child node within this parent node.
   * @return the child node.
   */
  public TreeNode getChildAt( final int childIndex ) {
    return childs.get( childIndex );
  }

  /**
   * Returns the number of children <code>TreeNode</code>s the receiver contains.
   *
   * @return the number of child nodes.
   */
  public int getChildCount() {
    return childs.size();
  }

  /**
   * Returns true if the receiver allows children.
   *
   * @return true, if this node allows child nodes.
   */
  public boolean getAllowsChildren() {
    return true;
  }

  /**
   * Returns the index of <code>node</code> in the receivers children. If the receiver does not contain
   * <code>node</code>, -1 will be returned.
   *
   * @param node the suspected child node.
   * @return the index of the given node or -1 if the node is not contained in this node.
   */
  public int getIndex( final TreeNode node ) {
    return childs.indexOf( node );
  }

  /**
   * Returns true if the receiver is a leaf.
   *
   * @return true, if this node is a leaf node, false otherwise.
   */
  public boolean isLeaf() {
    return false;
  }

  /**
   * Returns the children of the receiver as an <code>Enumeration</code>.
   *
   * @return all childs as enumeration.
   */
  public Enumeration children() {
    return Collections.enumeration( childs );
  }

  /**
   * Return the name of the node.
   *
   * @return the name of the node.
   */
  public String getName() {
    return name;
  }

  /**
   * Return the parent of this node or null if there is no parent.
   *
   * @return the parent
   * @see TreeNode#getParent()
   */
  public TreeNode getParent() {
    return parent;
  }

  /**
   * Defines the parent of this node, or null if the node should not have a parent.
   *
   * @param parent the new parent or null.
   */
  public void setParent( final TreeNode parent ) {
    this.parent = parent;
  }
}

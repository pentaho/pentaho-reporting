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

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.tools.configeditor.Messages;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Provides a tree model view for an report configuration. The configuration will be separated into a local and a global
 * part. The local nodes are read from the report's configuration instance, while the global nodes are always read from
 * the global report configuration instance.
 *
 * @author Thomas Morgner
 */
public class ConfigTreeModel implements TreeModel {
  /**
   * Externalized string access
   */
  private final Messages messages;

  /**
   * The root node for the tree model.
   */
  private final ConfigTreeRootNode root;
  /**
   * The section containing the global tree nodes.
   */
  private final ConfigTreeSectionNode globalSection;
  /**
   * The section containing the report-local tree nodes.
   */
  private final ConfigTreeSectionNode localSection;
  /**
   * The factory used to update the tree model.
   */
  private ModuleNodeFactory nodeFactory;
  /**
   * A list of model listeners.
   */
  private final ArrayList<TreeModelListener> listeners;

  /**
   * Creates a new tree model from the given specifications. These specifications contain the config description entry
   * definitions used to describe the report configuration keys.
   *
   * @param packageManager the specifications.
   * @param includeGlobals true to show global and local nodes, false to only show local nodes.
   */
  public ConfigTreeModel( final AbstractBoot packageManager, final boolean includeGlobals ) {
    this.messages = Messages.getInstance();
    this.root = new ConfigTreeRootNode( "<root>" ); //$NON-NLS-1$ // This one will not be visible to the user..
    this.globalSection = new ConfigTreeSectionNode( messages.getString(
      "ConfigTreeModel.GLOBAL_CONFIGURATION" ) ); //$NON-NLS-1$
    this.localSection = new ConfigTreeSectionNode( messages.getString(
      "ConfigTreeModel.LOCAL_CONFIGURATION" ) ); //$NON-NLS-1$
    this.listeners = new ArrayList<TreeModelListener>();
    if ( includeGlobals ) {
      root.add( globalSection );
    }
    root.add( localSection );
    nodeFactory = new ModuleNodeFactory( packageManager );
  }


  public void load( final InputStream in, final boolean append )
    throws IOException {
    nodeFactory.load( in, append );
  }

  public void load( final boolean append )
    throws IOException {
    nodeFactory.load( append );
  }

  /**
   * Initializes the tree from the given report configuration.
   */
  public void updateConfiguration() {
    updateConfiguration( true );
  }

  public void updateConfiguration( final boolean includeGlobals ) {
    globalSection.reset();
    localSection.reset();
    nodeFactory.init();
    final ConfigTreeModuleNode[] globalList = nodeFactory.getGlobalNodes();
    for ( int i = 0; i < globalList.length; i++ ) {
      globalSection.add( globalList[ 0 ] );
    }
    final ConfigTreeModuleNode[] localList = nodeFactory.getLocalNodes();
    for ( int i = 0; i < localList.length; i++ ) {
      localSection.add( localList[ i ] );
    }

    fireTreeModelChanged();
  }

  /**
   * Informs all listeners, that the tree's contents have changed.
   */
  private void fireTreeModelChanged() {
    for ( int i = 0; i < listeners.size(); i++ ) {
      final TreeModelListener l = listeners.get( i );
      l.treeStructureChanged( new TreeModelEvent( this, new TreePath( root ) ) );
    }
  }

  /**
   * Returns the root of the tree.  Returns <code>null</code> only if the tree has no nodes.
   *
   * @return the root of the tree
   */
  public Object getRoot() {
    return root;
  }

  /**
   * Returns the child of <code>parent</code> at index <code>index</code> in the parent's child array.
   * <code>parent</code> must be a node previously obtained from this data source. This should not return
   * <code>null</code> if <code>index</code> is a valid index for <code>parent</code> (that is <code>index >= 0 && index
   * < getChildCount(parent</code>)).
   *
   * @param parent a node in the tree, obtained from this data source
   * @param index  the index from where to read the child.
   * @return the child of <code>parent</code> at index <code>index</code>
   */
  public Object getChild( final Object parent, final int index ) {
    final TreeNode node = (TreeNode) parent;
    return node.getChildAt( index );
  }

  /**
   * Returns the number of children of <code>parent</code>. Returns 0 if the node is a leaf or if it has no children.
   * <code>parent</code> must be a node previously obtained from this data source.
   *
   * @param parent a node in the tree, obtained from this data source
   * @return the number of children of the node <code>parent</code>
   */
  public int getChildCount( final Object parent ) {
    final TreeNode node = (TreeNode) parent;
    return node.getChildCount();
  }

  /**
   * Returns <code>true</code> if <code>node</code> is a leaf. It is possible for this method to return
   * <code>false</code> even if <code>node</code> has no children. A directory in a filesystem, for example, may contain
   * no files; the node representing the directory is not a leaf, but it also has no children.
   *
   * @param node a node in the tree, obtained from this data source
   * @return true if <code>node</code> is a leaf
   */
  public boolean isLeaf( final Object node ) {
    final TreeNode tnode = (TreeNode) node;
    return tnode.isLeaf();
  }

  /**
   * Messaged when the user has altered the value for the item identified by <code>path</code> to <code>newValue</code>.
   * If <code>newValue</code> signifies a truly new value the model should post a <code>treeNodesChanged</code> event.
   *
   * @param path     path to the node that the user has altered
   * @param newValue the new value from the TreeCellEditor
   */
  public void valueForPathChanged( final TreePath path, final Object newValue ) {
  }

  /**
   * Returns the index of child in parent.  If <code>parent</code> is <code>null</code> or <code>child</code> is
   * <code>null</code>, returns -1.
   *
   * @param parent a note in the tree, obtained from this data source
   * @param child  the node we are interested in
   * @return the index of the child in the parent, or -1 if either <code>child</code> or <code>parent</code> are
   * <code>null</code>
   */
  public int getIndexOfChild( final Object parent, final Object child ) {
    final TreeNode node = (TreeNode) parent;
    final TreeNode childNode = (TreeNode) child;
    return node.getIndex( childNode );
  }

  /**
   * Adds a listener for the <code>TreeModelEvent</code> posted after the tree changes.
   *
   * @param l the listener to add
   * @see #removeTreeModelListener
   */
  public void addTreeModelListener( final TreeModelListener l ) {
    if ( l == null ) {
      throw new NullPointerException();
    }
    listeners.add( l );
  }

  /**
   * Removes a listener previously added with <code>addTreeModelListener</code>.
   *
   * @param l the listener to remove
   * @see #addTreeModelListener
   */
  public void removeTreeModelListener( final TreeModelListener l ) {
    listeners.remove( l );
  }

  /**
   * Returns the entry for the given key or null, if the key has no metadata.
   *
   * @param key the name of the key
   * @return the entry or null if not found.
   */
  public ConfigDescriptionEntry getEntryForKey( final String key ) {
    return nodeFactory.getEntryForKey( key );
  }
}

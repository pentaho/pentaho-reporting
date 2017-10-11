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

package org.pentaho.reporting.designer.extensions.pentaho.repository.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.LinkedList;

public class RepositoryTreeModel implements TreeModel {
  private class RepositoryTreeRoot {
    private FileObject root;

    private RepositoryTreeRoot() {
    }

    public FileObject getRoot() {
      return root;
    }

    public void setRoot( final FileObject root ) {
      this.root = root;
    }
  }

  private static final Log logger = LogFactory.getLog( RepositoryTreeModel.class );
  private String[] filters;
  private EventListenerList listenerList;
  private boolean showFoldersOnly;
  private boolean showHiddenFiles;
  private RepositoryTreeRoot root;
  private static final String[] EMPTY_FILTER = new String[0];

  public RepositoryTreeModel() {
    this.listenerList = new EventListenerList();
    this.filters = EMPTY_FILTER;
    this.showFoldersOnly = true;
    this.root = new RepositoryTreeRoot();
  }

  public RepositoryTreeModel( final FileObject repositoryRoot, final String[] filters, final boolean showFoldersOnly ) {
    this();
    this.filters = filters.clone();
    this.showFoldersOnly = showFoldersOnly;
    this.root.setRoot( repositoryRoot );
  }

  public void setShowFoldersOnly( final boolean showFoldersOnly ) {
    this.showFoldersOnly = showFoldersOnly;
    fireTreeDataChanged();
  }

  public boolean isShowFoldersOnly() {
    return showFoldersOnly;
  }

  public boolean isShowHiddenFiles() {
    return showHiddenFiles;
  }

  public void setShowHiddenFiles( final boolean showHiddenFiles ) {
    this.showHiddenFiles = showHiddenFiles;
    fireTreeDataChanged();
  }

  public String[] getFilters() {
    return filters.clone();
  }

  public void setFilters( final String[] filters ) {
    this.filters = filters.clone();
    fireTreeDataChanged();
  }

  public FileObject getFileSystemRoot() {
    return root.getRoot();
  }

  public void setFileSystemRoot( final FileObject root ) {
    this.root.setRoot( root );
    fireTreeDataChanged();
  }

  /**
   * Returns the root of the tree. Returns <code>null</code> only if the tree has no nodes.
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
   * @param parent
   *          a node in the tree, obtained from this data source
   * @return the child of <code>parent</code> at index <code>index</code>
   */
  public Object getChild( Object parent, final int index ) {
    if ( parent instanceof RepositoryTreeRoot ) {
      final RepositoryTreeRoot root1 = (RepositoryTreeRoot) parent;
      parent = root1.getRoot();
      if ( parent == null ) {
        return null;
      }
    }

    try {
      final FileObject parElement = (FileObject) parent;
      final FileObject[] children = parElement.getChildren();
      int count = 0;
      for ( int i = 0; i < children.length; i++ ) {
        final FileObject child = children[i];
        if ( isShowFoldersOnly() && child.getType() != FileType.FOLDER ) {
          continue;
        }
        if ( isShowHiddenFiles() == false && child.isHidden() ) {
          continue;
        }
        if ( child.getType() != FileType.FOLDER
            && PublishUtil.acceptFilter( filters, child.getName().getBaseName() ) == false ) {
          continue;
        }

        if ( count == index ) {
          return child;
        }

        count += 1;
      }
      return children[index];
    } catch ( FileSystemException fse ) {
      logger.debug( "Failed", fse );
      return null;
    }
  }

  /**
   * Returns the number of children of <code>parent</code>. Returns 0 if the node is a leaf or if it has no children.
   * <code>parent</code> must be a node previously obtained from this data source.
   *
   * @param parent
   *          a node in the tree, obtained from this data source
   * @return the number of children of the node <code>parent</code>
   */
  public int getChildCount( Object parent ) {
    if ( parent instanceof RepositoryTreeRoot ) {
      final RepositoryTreeRoot root1 = (RepositoryTreeRoot) parent;
      parent = root1.getRoot();
      if ( parent == null ) {
        return 0;
      }
    }
    try {
      final FileObject parElement = (FileObject) parent;
      if ( parElement.getType() != FileType.FOLDER ) {
        return 0;
      }

      final FileObject[] children = parElement.getChildren();
      int count = 0;
      for ( int i = 0; i < children.length; i++ ) {
        final FileObject child = children[i];
        if ( isShowFoldersOnly() && child.getType() != FileType.FOLDER ) {
          continue;
        }
        if ( isShowHiddenFiles() == false && child.isHidden() ) {
          continue;
        }
        if ( child.getType() != FileType.FOLDER
            && PublishUtil.acceptFilter( filters, child.getName().getBaseName() ) == false ) {
          continue;
        }

        count += 1;
      }
      return count;
    } catch ( FileSystemException fse ) {
      logger.debug( "Failed", fse );
      return 0;
    }
  }

  /**
   * Returns <code>true</code> if <code>node</code> is a leaf. It is possible for this method to return
   * <code>false</code> even if <code>node</code> has no children. A directory in a filesystem, for example, may contain
   * no files; the node representing the directory is not a leaf, but it also has no children.
   *
   * @param node
   *          a node in the tree, obtained from this data source
   * @return true if <code>node</code> is a leaf
   */
  public boolean isLeaf( final Object node ) {
    if ( node instanceof RepositoryTreeRoot ) {
      return false;
    }

    try {
      final FileObject parElement = (FileObject) node;
      return ( parElement.getType() != FileType.FOLDER );
    } catch ( FileSystemException fse ) {
      logger.debug( "Failed", fse );
      return false;
    }
  }

  /**
   * Messaged when the user has altered the value for the item identified by <code>path</code> to <code>newValue</code>.
   * If <code>newValue</code> signifies a truly new value the model should post a <code>treeNodesChanged</code> event.
   *
   * @param path
   *          path to the node that the user has altered
   * @param newValue
   *          the new value from the TreeCellEditor
   */
  public void valueForPathChanged( final TreePath path, final Object newValue ) {

  }

  /**
   * Returns the index of child in parent. If either <code>parent</code> or <code>child</code> is <code>null</code>,
   * returns -1. If either <code>parent</code> or <code>child</code> don't belong to this tree model, returns -1.
   *
   * @param parent
   *          a note in the tree, obtained from this data source
   * @param childNode
   *          the node we are interested in
   * @return the index of the child in the parent, or -1 if either <code>child</code> or <code>parent</code> are
   *         <code>null</code> or don't belong to this tree model
   */
  public int getIndexOfChild( Object parent, final Object childNode ) {
    if ( parent instanceof RepositoryTreeRoot ) {
      final RepositoryTreeRoot root1 = (RepositoryTreeRoot) parent;
      parent = root1.getRoot();
      if ( parent == null ) {
        return -1;
      }
    }

    try {
      final FileObject parChild = (FileObject) childNode;
      final FileObject parElement = (FileObject) parent;
      final FileObject[] childs = parElement.getChildren();
      int count = 0;
      for ( int i = 0; i < childs.length; i++ ) {
        final FileObject child = childs[i];
        if ( isShowFoldersOnly() && child.getType() != FileType.FOLDER ) {
          continue;
        }
        if ( isShowHiddenFiles() == false && child.isHidden() ) {
          continue;
        }
        if ( child.getType() != FileType.FOLDER
            && PublishUtil.acceptFilter( filters, child.getName().getBaseName() ) == false ) {
          continue;
        }

        if ( child.getName().equals( parChild.getName() ) ) {
          return count;
        }

        count += 1;
      }

      return -1;
    } catch ( FileSystemException fse ) {
      logger.debug( "Failed", fse );
      return -1;
    }
  }

  /**
   * Adds a listener for the <code>TreeModelEvent</code> posted after the tree changes.
   *
   * @param l
   *          the listener to add
   * @see #removeTreeModelListener
   */
  public void addTreeModelListener( final TreeModelListener l ) {
    listenerList.add( TreeModelListener.class, l );
  }

  /**
   * Removes a listener previously added with <code>addTreeModelListener</code>.
   *
   * @param l
   *          the listener to remove
   * @see #addTreeModelListener
   */
  public void removeTreeModelListener( final TreeModelListener l ) {
    listenerList.remove( TreeModelListener.class, l );
  }

  public void fireTreeDataChanged() {
    final TreeModelEvent event = new TreeModelEvent( this, new TreePath( root ) );
    final TreeModelListener[] modelListeners = listenerList.getListeners( TreeModelListener.class );
    for ( int i = 0; i < modelListeners.length; i++ ) {
      final TreeModelListener modelListener = modelListeners[i];
      modelListener.treeStructureChanged( event );
    }
  }

  public TreePath getTreePathForSelection( FileObject selectedFolder, final String selection )
    throws FileSystemException {
    if ( root.getRoot() == null ) {
      return null;
    }
    if ( root.getRoot().equals( selectedFolder ) ) {
      return new TreePath( root );
    }

    final LinkedList<Object> list = new LinkedList<Object>();
    while ( selectedFolder != null ) {
      list.add( 0, selectedFolder );
      final FileObject parent = selectedFolder.getParent();
      if ( selectedFolder.equals( parent ) ) {
        break;
      }
      if ( root.getRoot().equals( parent ) ) {
        break;
      }
      selectedFolder = parent;
    }
    list.add( 0, root );
    return new TreePath( list.toArray() );
  }

  public static FileObject findNodeByName( final FileObject node, final String name ) throws FileSystemException {
    if ( node.getType() != FileType.FOLDER ) {
      return null;
    }
    final FileObject child = node.getChild( name );
    if ( child == null ) {
      return null;
    }
    return child;
  }

}

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

package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class DataFactoryTreeModel implements TreeModel {
  private ArrayList<DataFactoryWrapper> root;
  private EventListenerList listenerList;

  public DataFactoryTreeModel() {
    listenerList = new EventListenerList();
    root = new ArrayList<DataFactoryWrapper>();
  }

  public void add( final DataFactoryWrapper wrapper ) {
    if ( wrapper == null ) {
      throw new NullPointerException();
    }

    root.add( wrapper );
    fireTreeDataChanged();
  }

  public void edit( final int index, final DataFactory newDataFactory ) {
    if ( newDataFactory == null ) {
      throw new NullPointerException();
    }

    final DataFactoryWrapper wrapper = root.get( index );
    wrapper.setEditedDataFactory( newDataFactory );
    fireTreeDataChanged( new TreePath( new Object[] { root, wrapper } ) );
  }

  public void importFromReport( final CompoundDataFactory cdf ) {
    final int size = cdf.size();
    for ( int i = 0; i < size; i++ ) {
      root.add( new DataFactoryWrapper( cdf.getReference( i ) ) );
    }
    fireTreeDataChanged();
  }

  public int size() {
    return root.size();
  }

  public DataFactoryWrapper get( final int i ) {
    return root.get( i );
  }

  public Object getRoot() {
    return root;
  }

  public Object getChild( final Object parent, final int index ) {
    if ( parent == root ) {
      int size = 0;
      for ( int i = 0; i < root.size(); i++ ) {
        final DataFactoryWrapper wrapper = root.get( i );
        if ( wrapper.isRemoved() == false ) {
          if ( index == size ) {
            return wrapper;
          }
          size += 1;
        }
      }
      throw new IndexOutOfBoundsException();
    }

    if ( parent instanceof DataFactoryWrapper ) {
      final DataFactoryWrapper df = (DataFactoryWrapper) parent;
      if ( df.isRemoved() ) {
        throw new IllegalStateException();
      }
      final String[] queries = df.getEditedDataFactory().getQueryNames();
      return queries[ index ];
    }
    return null;
  }

  public int getChildCount( final Object parent ) {
    if ( parent == root ) {
      int size = 0;
      for ( int i = 0; i < root.size(); i++ ) {
        final DataFactoryWrapper wrapper = root.get( i );
        if ( wrapper.isRemoved() == false ) {
          size += 1;
        }
      }
      return size;
    }
    if ( parent instanceof DataFactoryWrapper ) {
      final DataFactoryWrapper df = (DataFactoryWrapper) parent;
      if ( df.isRemoved() ) {
        throw new IllegalStateException();
      }
      final String[] queries = df.getEditedDataFactory().getQueryNames();
      return queries.length;
    }
    return 0;
  }

  public boolean isLeaf( final Object node ) {
    if ( node == root ) {
      return false;
    }
    if ( node instanceof DataFactoryWrapper ) {
      return false;
    }
    return true;
  }

  public void valueForPathChanged( final TreePath path, final Object newValue ) {
    fireTreeDataChanged( path );
  }

  public int getIndexOfChild( final Object parent, final Object child ) {
    if ( parent == root ) {
      int size = 0;
      for ( int i = 0; i < root.size(); i++ ) {
        final DataFactoryWrapper wrapper = root.get( i );
        if ( wrapper.isRemoved() == false ) {
          if ( wrapper == child ) {
            return size;
          }
          size += 1;
        }
      }
      return -1;
    }
    if ( parent instanceof DataFactoryWrapper ) {
      final DataFactoryWrapper df = (DataFactoryWrapper) parent;
      if ( df.isRemoved() ) {
        throw new IllegalStateException( "Trues to access a node that has been removed." );
      }
      final String[] queries = df.getEditedDataFactory().getQueryNames();
      for ( int i = 0; i < queries.length; i++ ) {
        final String query = queries[ i ];
        if ( ObjectUtilities.equal( query, child ) ) {
          return i;
        }
      }
      return -1;
    }
    return -1;
  }

  public void fireTreeDataChanged() {
    fireTreeDataChanged( new TreePath( getRoot() ) );
  }

  public void fireTreeDataChanged( final TreePath treePath ) {
    final TreeModelListener[] treeModelListeners = getListeners();
    final TreeModelEvent treeEvent = new TreeModelEvent( this, treePath );
    for ( int i = 0; i < treeModelListeners.length; i++ ) {
      final TreeModelListener listener = treeModelListeners[ i ];
      listener.treeStructureChanged( treeEvent );
    }
  }

  protected TreeModelListener[] getListeners() {
    return listenerList.getListeners( TreeModelListener.class );
  }

  public void addTreeModelListener( final TreeModelListener l ) {
    listenerList.add( TreeModelListener.class, l );
  }

  public void removeTreeModelListener( final TreeModelListener l ) {
    listenerList.remove( TreeModelListener.class, l );
  }

  public void remove( final DataFactory dataFactory ) {
    for ( int i = 0; i < root.size(); i++ ) {
      final DataFactoryWrapper wrapper = root.get( i );
      if ( wrapper.getEditedDataFactory() == dataFactory ) {
        wrapper.setEditedDataFactory( null );
        fireTreeDataChanged();
        return;
      }
    }
  }

  public int indexOf( final DataFactory dataFactory ) {
    for ( int i = 0; i < root.size(); i++ ) {
      final DataFactoryWrapper factoryWrapper = root.get( i );
      if ( factoryWrapper.getEditedDataFactory() == dataFactory ) {
        return i;
      }
    }
    return -1;
  }

  public DataFactoryWrapper[] toArray() {
    return root.toArray( new DataFactoryWrapper[ root.size() ] );
  }
}

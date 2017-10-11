/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class DefaultQueryDialogModel<T> implements QueryDialogModel<T> {
  private ArrayList<Query<T>> backend;
  private ArrayList<QueryDialogModelListener> listeners;
  private QueryDialogComboBoxModel<T> model;
  private int selectedIndex;

  private String globalScriptLanguage;
  private String globalScript;

  public DefaultQueryDialogModel() {
    backend = new ArrayList<Query<T>>();
    listeners = new ArrayList<QueryDialogModelListener>();
    model = new QueryDialogComboBoxModel<T>( this );
    selectedIndex = -1;
  }

  public void addQuery( final Query<T> query ) {
    backend.add( query );

    final QueryDialogModelEvent<T> event = new QueryDialogModelEvent<T>( this, backend.size() - 1, query, -1, null );
    fireEvent( new Func<QueryDialogModelListener<T>>() {
      public void run( final QueryDialogModelListener<T> value ) {
        value.queryAdded( event );
      }
    } );
  }

  public QueryDialogComboBoxModel<T> getQueries() {
    return model;
  }

  public boolean isQuerySelected() {
    return selectedIndex != -1;
  }

  public void setSelectedQuery( final Query<T> query ) {
    int newIndex;
    if ( query != null ) {
      newIndex = backend.indexOf( query );
      if ( newIndex == -1 ) {
        throw new IllegalStateException();
      }
    } else {
      newIndex = -1;
    }

    if ( newIndex == selectedIndex ) {
      return;
    }

    Query<T> oldValue = getSelectedQuery();
    int oldIndex = selectedIndex;

    selectedIndex = newIndex;

    final QueryDialogModelEvent<T> event =
        new QueryDialogModelEvent<T>( this, selectedIndex, query, oldIndex, oldValue );
    fireEvent( new Func<QueryDialogModelListener<T>>() {
      public void run( final QueryDialogModelListener<T> value ) {
        value.selectionChanged( event );
      }
    } );
  }

  private void fireEvent( Func<QueryDialogModelListener<T>> delegate ) {
    // noinspection unchecked
    for ( QueryDialogModelListener<T> listener : listeners.toArray( new QueryDialogModelListener[listeners.size()] ) ) {
      delegate.run( listener );
    }
  }

  public Query<T> getSelectedQuery() {
    if ( selectedIndex == -1 ) {
      return null;
    }
    return backend.get( selectedIndex );
  }

  public void removeQuery( final Query<T> query ) {
    int index = backend.indexOf( query );
    if ( index == -1 ) {
      return;
    }

    Query<T> oldValue = backend.remove( index );

    if ( index == selectedIndex ) {
      selectedIndex = -1;
      final QueryDialogModelEvent<T> event = new QueryDialogModelEvent<T>( this, selectedIndex, null, index, oldValue );
      fireEvent( new Func<QueryDialogModelListener<T>>() {
        public void run( final QueryDialogModelListener<T> value ) {
          value.selectionChanged( event );
        }
      } );
    }

    final QueryDialogModelEvent<T> event = new QueryDialogModelEvent<T>( this, -1, null, index, oldValue );
    fireEvent( new Func<QueryDialogModelListener<T>>() {
      public void run( final QueryDialogModelListener<T> value ) {
        value.queryRemoved( event );
      }
    } );
  }

  public void updateQuery( final int index, final Query<T> query ) {
    Query<T> oldValue = backend.set( index, query );
    if ( ObjectUtilities.equal( oldValue, query ) ) {
      return;
    }

    final QueryDialogModelEvent<T> event = new QueryDialogModelEvent<T>( this, index, query, index, oldValue );
    fireEvent( new Func<QueryDialogModelListener<T>>() {
      public void run( final QueryDialogModelListener<T> value ) {
        value.queryUpdated( event );
      }
    } );
  }

  public void clear() {
    setSelectedQuery( null );
    backend.clear();

    final QueryDialogModelEvent<T> event = new QueryDialogModelEvent<T>( this );
    fireEvent( new Func<QueryDialogModelListener<T>>() {
      public void run( final QueryDialogModelListener<T> value ) {
        value.queryDataChanged( event );
      }
    } );
  }

  public void updateSelectedQuery( final Query<T> newQuery ) {
    updateQuery( selectedIndex, newQuery );
  }

  public int getQueryCount() {
    return backend.size();
  }

  public Query<T> getQuery( final int index ) {
    if ( index >= backend.size() ) {
      throw new IndexOutOfBoundsException();
    }
    return backend.get( index );
  }

  public void setGlobalScripting( final String lang, final String script ) {
    if ( ObjectUtilities.equal( lang, globalScriptLanguage ) && ObjectUtilities.equal( script, globalScript ) ) {
      return;
    }

    this.globalScript = script;
    this.globalScriptLanguage = lang;

    final QueryDialogModelEvent<T> event = new QueryDialogModelEvent<T>( this );
    fireEvent( new Func<QueryDialogModelListener<T>>() {
      public void run( final QueryDialogModelListener<T> value ) {
        value.globalScriptChanged( event );
      }
    } );
  }

  public String getGlobalScriptLanguage() {
    return globalScriptLanguage;
  }

  public String getGlobalScript() {
    return globalScript;
  }

  public void addQueryDialogModelListener( final QueryDialogModelListener<T> listener ) {
    listeners.add( listener );
  }

  public void removeQueryDialogModelListener( final QueryDialogModelListener<T> listener ) {
    listeners.remove( listener );
  }

  public Iterator<Query<T>> iterator() {
    return Collections.unmodifiableList( backend ).iterator();
  }
}

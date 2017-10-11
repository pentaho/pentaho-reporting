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

import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class QueryDialogComboBoxModel<T> implements ComboBoxModel {
  private class UpdateHandler implements QueryDialogModelListener<T> {
    private UpdateHandler() {
    }

    public void globalScriptChanged( final QueryDialogModelEvent<T> event ) {
      // ignored
    }

    public void queryAdded( final QueryDialogModelEvent<T> event ) {
      fireIntervalAddedEvent( new ListDataEvent( this, ListDataEvent.INTERVAL_ADDED, event.getNewIndex(), event
          .getNewIndex() ) );
    }

    public void queryRemoved( final QueryDialogModelEvent<T> event ) {
      fireIntervalRemovedEvent( new ListDataEvent( this, ListDataEvent.INTERVAL_REMOVED, event.getOldIndex(), event
          .getOldIndex() ) );
    }

    public void queryUpdated( final QueryDialogModelEvent<T> event ) {
      fireContentsChangedEvent( new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, event.getNewIndex(), event
          .getNewIndex() ) );
    }

    public void queryDataChanged( final QueryDialogModelEvent<T> event ) {
      fireContentsChangedEvent( new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() ) );
    }

    public void selectionChanged( final QueryDialogModelEvent<T> event ) {
      fireContentsChangedEvent( new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, -1, -1 ) );
    }
  }

  private EventListenerList listeners;
  private QueryDialogModel<T> dialogModel;

  public QueryDialogComboBoxModel( QueryDialogModel<T> dialogModel ) {
    this.listeners = new EventListenerList();
    this.dialogModel = dialogModel;
    this.dialogModel.addQueryDialogModelListener( new UpdateHandler() );
  }

  public Object getSelectedItem() {
    return dialogModel.getSelectedQuery();
  }

  @SuppressWarnings( "unchecked" )
  public void setSelectedItem( final Object anItem ) {
    dialogModel.setSelectedQuery( (Query<T>) anItem );
  }

  public int getSize() {
    return dialogModel.getQueryCount();
  }

  public Query<T> getElementAt( final int index ) {
    return dialogModel.getQuery( index );
  }

  public void addListDataListener( final ListDataListener l ) {
    listeners.add( ListDataListener.class, l );
  }

  public void removeListDataListener( final ListDataListener l ) {
    listeners.remove( ListDataListener.class, l );
  }

  protected void fireContentsChangedEvent( ListDataEvent event ) {
    ListDataListener[] listeners1 = listeners.getListeners( ListDataListener.class );
    for ( int i = listeners1.length - 1; i >= 0; i -= 1 ) {
      ListDataListener l = listeners1[i];
      l.contentsChanged( event );
    }
  }

  protected void fireIntervalRemovedEvent( ListDataEvent event ) {
    ListDataListener[] listeners1 = listeners.getListeners( ListDataListener.class );
    for ( int i = listeners1.length - 1; i >= 0; i -= 1 ) {
      ListDataListener l = listeners1[i];
      l.intervalRemoved( event );
    }
  }

  protected void fireIntervalAddedEvent( ListDataEvent event ) {
    ListDataListener[] listeners1 = listeners.getListeners( ListDataListener.class );
    for ( int i = listeners1.length - 1; i >= 0; i -= 1 ) {
      ListDataListener l = listeners1[i];
      l.intervalAdded( event );
    }
  }

}

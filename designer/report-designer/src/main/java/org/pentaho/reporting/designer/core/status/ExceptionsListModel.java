/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.status;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ExceptionsListModel implements ListModel {
  private EventListenerList listenerList;
  private Throwable[] throwables;

  public ExceptionsListModel() {
    this.listenerList = new EventListenerList();
    this.throwables = UncaughtExceptionsModel.getInstance().getThrowables();
  }

  /**
   * Returns the length of the list.
   *
   * @return the length of the list
   */
  public int getSize() {
    return throwables.length;
  }

  /**
   * Returns the value at the specified index.
   *
   * @param index the requested index
   * @return the value at <code>index</code>
   */
  public Object getElementAt( final int index ) {
    return throwables[ index ];
  }

  /**
   * Adds a listener to the list that's notified each time a change to the data model occurs.
   *
   * @param l the <code>ListDataListener</code> to be added
   */
  public void addListDataListener( final ListDataListener l ) {
    listenerList.add( ListDataListener.class, l );
  }

  /**
   * Removes a listener from the list that's notified each time a change to the data model occurs.
   *
   * @param l the <code>ListDataListener</code> to be removed
   */
  public void removeListDataListener( final ListDataListener l ) {
    listenerList.remove( ListDataListener.class, l );
  }

  protected void fireListUpdate() {
    final ListDataListener[] listDataListeners = listenerList.getListeners( ListDataListener.class );
    final ListDataEvent event = new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, -1, -1 );
    for ( int i = 0; i < listDataListeners.length; i++ ) {
      final ListDataListener listener = listDataListeners[ i ];
      listener.contentsChanged( event );
    }
  }

  public void refresh() {
    this.throwables = UncaughtExceptionsModel.getInstance().getThrowables();
    fireListUpdate();
  }
}

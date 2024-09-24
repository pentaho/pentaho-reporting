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

package org.pentaho.reporting.designer.core.editor.format;

import javax.swing.event.EventListenerList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BorderSelectionModel {
  private EventListenerList eventListeners;
  private HashSet<BorderSelection> selectedItems;

  public BorderSelectionModel() {
    eventListeners = new EventListenerList();
    selectedItems = new HashSet();
  }

  public void addBorderSelectionListener( final BorderSelectionListener listener ) {
    eventListeners.add( BorderSelectionListener.class, listener );
  }

  public void removeBorderSelectionListener( final BorderSelectionListener listener ) {
    eventListeners.remove( BorderSelectionListener.class, listener );
  }

  public BorderSelection[] getSelections() {
    return selectedItems.toArray( new BorderSelection[ selectedItems.size() ] );
  }

  public boolean isSelected( BorderSelection selection ) {
    return selectedItems.contains( selection );
  }

  public void addSelection( BorderSelection selection ) {
    if ( selectedItems.add( selection ) ) {
      fireSelectionAdded( selection );
    }
  }

  public void removeSelection( BorderSelection selection ) {
    if ( selectedItems.remove( selection ) ) {
      fireSelectionRemoved( selection );
    }
  }

  public void clearSelection() {
    final Iterator<BorderSelection> iterator = selectedItems.iterator();
    while ( iterator.hasNext() ) {
      final BorderSelection selection = iterator.next();
      iterator.remove();
      fireSelectionRemoved( selection );
    }
  }

  private void fireSelectionRemoved( final BorderSelection selection ) {
    final BorderSelectionEvent event = new BorderSelectionEvent( this, selection );
    final BorderSelectionListener[] listeners = eventListeners.getListeners( BorderSelectionListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final BorderSelectionListener listener = listeners[ i ];
      listener.selectionRemoved( event );
    }
  }

  private void fireSelectionAdded( final BorderSelection selection ) {
    final BorderSelectionEvent event = new BorderSelectionEvent( this, selection );
    final BorderSelectionListener[] listeners = eventListeners.getListeners( BorderSelectionListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final BorderSelectionListener listener = listeners[ i ];
      listener.selectionAdded( event );
    }
  }
}

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

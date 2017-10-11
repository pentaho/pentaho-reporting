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

package org.pentaho.reporting.designer.core.model.selection;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.libraries.designtime.swing.WeakEventListenerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class DefaultReportSelectionModel implements DocumentContextSelectionModel {
  private WeakEventListenerList listenerList;
  private LinkedHashMap<Object, Object> backend;
  private Object[] valuesCached;

  public DefaultReportSelectionModel() {
    listenerList = new WeakEventListenerList();
    backend = new LinkedHashMap<Object, Object>();
  }

  public void addReportSelectionListener( final ReportSelectionListener listener ) {
    listenerList.add( ReportSelectionListener.class, listener );
  }

  public void removeReportSelectionListener( final ReportSelectionListener listener ) {
    listenerList.remove( ReportSelectionListener.class, listener );
  }

  public int getSelectionCount() {
    return backend.size();
  }

  public Object getSelectedElement( final int index ) {
    fillCaches();
    return valuesCached[ index ];
  }

  public void clearSelection() {
    final Object[] objects;
    if ( valuesCached == null ) {
      objects = backend.values().toArray();
    } else {
      objects = valuesCached;
    }
    for ( int i = 0; i < objects.length; i++ ) {
      remove( objects[ i ] );
    }
  }

  private boolean contains( final Object[] elements, final Object o ) {
    for ( int i = 0; i < elements.length; i++ ) {
      if ( elements[ i ] == o ) {
        return true;
      }
    }
    return false;
  }

  public void setSelectedElements( final Object[] elements ) {

    // Bulk update.
    // Step 1: Remove all elements that are not part of the new selection.
    // Keep all elements that are part of the new selection, so that we do not fire add-events for them later
    final Iterator iterator = backend.values().iterator();
    while ( iterator.hasNext() ) {
      final Object elementFromOldSelection = iterator.next();
      if ( contains( elements, elementFromOldSelection ) == false ) {
        iterator.remove();
        valuesCached = null;
        fireSelectionRemoved( elementFromOldSelection );
      }
    }

    // now add all new elements and fire events for the new ones ..
    for ( int i = 0; i < elements.length; i++ ) {
      final Object element = elements[ i ];
      final Object key = computeKey( element );
      if ( backend.containsKey( key ) == false ) {
        backend.put( key, element );
        valuesCached = null;
        fireSelectionAdded( element );
      }
    }
  }

  private Object computeKey( final Object o ) {
    if ( o instanceof Element ) {
      final Element e = (Element) o;
      return e.getObjectID();
    } else {
      return System.identityHashCode( o );
    }
  }

  protected void fireSelectionAdded( final Object selection ) {
    final ReportSelectionEvent event = new ReportSelectionEvent( this, selection );
    final ReportSelectionListener[] reportSelectionListeners =
      listenerList.getListeners( ReportSelectionListener.class );
    for ( int i = 0; i < reportSelectionListeners.length; i++ ) {
      final ReportSelectionListener listener = reportSelectionListeners[ i ];
      listener.selectionAdded( event );
    }

    fireLeadSelectionChanged( getLeadSelection() );
  }

  protected void fireSelectionRemoved( final Object selection ) {
    final ReportSelectionEvent event = new ReportSelectionEvent( this, selection );
    final ReportSelectionListener[] reportSelectionListeners =
      listenerList.getListeners( ReportSelectionListener.class );
    for ( int i = 0; i < reportSelectionListeners.length; i++ ) {
      final ReportSelectionListener listener = reportSelectionListeners[ i ];
      listener.selectionRemoved( event );
    }

    fireLeadSelectionChanged( getLeadSelection() );
  }

  protected void fireLeadSelectionChanged( final Object selection ) {
    final ReportSelectionEvent event = new ReportSelectionEvent( this, selection );
    final ReportSelectionListener[] reportSelectionListeners =
      listenerList.getListeners( ReportSelectionListener.class );
    for ( int i = 0; i < reportSelectionListeners.length; i++ ) {
      final ReportSelectionListener listener = reportSelectionListeners[ i ];
      listener.leadSelectionChanged( event );
    }
  }

  public boolean add( final Object element ) {
    final Object key = computeKey( element );
    if ( backend.containsKey( key ) == false ) {
      backend.put( key, element );
      valuesCached = null;
      fireSelectionAdded( element );
      return true;
    }
    return false;
  }

  public void remove( final Object element ) {
    final Object key = computeKey( element );
    if ( backend.containsKey( key ) ) {
      backend.remove( key );
      valuesCached = null;
      fireSelectionRemoved( element );
    }
  }

  public boolean isSelected( final Object o ) {
    return backend.containsKey( computeKey( o ) );
  }

  public Object[] getSelectedElements() {
    fillCaches();
    return valuesCached.clone();
  }

  public Object getLeadSelection() {
    if ( backend.isEmpty() ) {
      return null;
    }

    fillCaches();
    return valuesCached[ valuesCached.length - 1 ];
  }

  private void fillCaches() {
    if ( valuesCached == null ) {
      valuesCached = backend.values().toArray();
    }
  }

  public <T> List<T> getSelectedElementsOfType( final Class<T> t ) {
    ArrayList<T> list = new ArrayList<T>();
    for ( Object o : backend.values() ) {
      if ( t.isInstance( o ) ) {
        //noinspection unchecked
        list.add( (T) o );
      }
    }
    return Collections.unmodifiableList( list );
  }
}

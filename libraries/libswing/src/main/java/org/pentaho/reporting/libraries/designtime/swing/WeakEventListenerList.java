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

package org.pentaho.reporting.libraries.designtime.swing;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An event listener list that stores the listeners as weak references. This list behaves like the {@link
 * javax.swing.event.EventListenerList}, but does not create the risk of memory leaks when maintaining listener lists
 * for global objects.
 * <p/>
 * The list is fully synchronized and safe to be used in multi-threading environments.
 *
 * @author Thomas Morgner
 */
public class WeakEventListenerList {
  private static final Object[] EMPTY_ARRAY = new Object[ 0 ];
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private Object[] data;
  private int size;
  private final int increment;
  private int count;

  /**
   * Creates a new listener list with a increment of 10.
   */
  public WeakEventListenerList() {
    data = EMPTY_ARRAY;
    increment = 10;
  }

  /**
   * Adds a new listener of the given type to this list.
   *
   * @param t   the type of the listener.
   * @param l   the listener.
   * @param <T> the type of the listener, must be a subclass of {@code java.util.EventListener}.
   */
  public <T extends EventListener> void add( final Class<T> t, final T l ) {
    lock.writeLock().lock();
    try {
      ensureCapacity( size + 2 );
      data[ size ] = t;
      data[ size + 1 ] = new WeakReference( l );
      size += 2;
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Removes an existing listener of the given type from this eventlistener list.
   *
   * @param t   the type of the listener.
   * @param l   the listener.
   * @param <T> the type of the listener, must be a subclass of {@code java.util.EventListener}.
   */
  public <T extends EventListener> void remove( final Class<T> t, final T l ) {
    lock.writeLock().lock();
    try {
      final int position = findInternal( t, l );
      if ( position < 0 ) {
        return;
      }

      final int shiftElements = size - position - 2;
      if ( shiftElements == 0 ) {
        data[ position ] = null;
        data[ position + 1 ] = null;
        size -= 2;
        return;
      }

      size -= 2;
      System.arraycopy( data, position + 2, data, position, shiftElements );

      data[ size ] = null;
      data[ size + 1 ] = null;
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Tries to find the given listener <code>l</code>, which has to be stored under the given type.
   *
   * @param t   the type of the listener.
   * @param l   the listener.
   * @param <T> the type of the listener, must be a subclass of {@code java.util.EventListener}.
   * @return the index of the listener, or -1 if the listener is not an element of this collection.
   */
  private <T extends EventListener> int findInternal( final Class<T> t, final T l ) {
    for ( int i = 0; i < size; i += 2 ) {
      final WeakReference l2 = (WeakReference) data[ i + 1 ];
      if ( l2 == null ) {
        continue;
      }
      final Object o = l2.get();
      if ( o == null ) {
        data[ i ] = null;
        data[ i + 1 ] = null;
        count += 1;
      } else if ( o == l ) {
        if ( data[ i ] == t ) {
          return i;
        }
      }
    }
    return -1;
  }

  /**
   * Returns the number of listeners of any type stored in this eventlistener list.
   *
   * @return the overall size of this list.
   */
  public int getSize() {
    lock.readLock().lock();
    try {
      return size / 2;
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Counts the number of listeners of a given type that are contained in this list.
   *
   * @param t the type of the listener.
   * @return the number of listeners.
   */
  public int getListenerCount( final Class<?> t ) {
    lock.readLock().lock();
    try {
      int retval = 0;
      for ( int i = 0; i < size; i += 2 ) {
        if ( data[ i ] != t ) {
          continue;
        }

        final WeakReference l2 = (WeakReference) data[ i + 1 ];
        final Object o = l2.get();
        if ( o == null ) {
          data[ i ] = null;
          data[ i + 1 ] = null;
          count += 1;
        } else {
          retval += 1;
        }
      }
      return retval;
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Returns all listeners of the given type that are stored in this list.
   *
   * @param t   the type of the listener to be retrieved.
   * @param <T> a implementation or derived interface of EventListener.
   * @return the array containing all listeners.
   */
  public <T extends EventListener> T[] getListeners( final Class<T> t ) {
    final ArrayList list = new ArrayList();
    lock.readLock().lock();
    try {
      for ( int i = 0; i < size; i += 2 ) {
        if ( data[ i ] != t ) {
          continue;
        }

        final WeakReference l2 = (WeakReference) data[ i + 1 ];
        final Object o = l2.get();
        if ( o == null ) {
          data[ i ] = null;
          data[ i + 1 ] = null;
          count += 1;
        } else {
          list.add( o );
        }
      }
    } finally {
      lock.readLock().unlock();
    }

    pack();

    final T[] result = (T[]) Array.newInstance( t, list.size() );
    return (T[]) list.toArray( result );
  }

  /**
   * Removes any null-entries from this list.
   */
  private void pack() {
    if ( count < 5 ) {
      lock.writeLock().lock();
      try {
        int correction = 0;
        for ( int i = 0; i < size; i += 2 ) {
          final Object o = data[ i ];
          if ( o == null ) {
            correction += 2;
            continue;
          }
          data[ i - correction ] = o;
          data[ i - correction + 1 ] = data[ i + 1 ];
        }
      } finally {
        lock.writeLock().unlock();
      }
    }
  }


  /**
   * Ensures, that the list backend can store at least <code>c</code> elements. This method does nothing, if the new
   * capacity is less than the current capacity.
   *
   * @param c the new capacity of the list.
   */
  private void ensureCapacity( final int c ) {
    if ( data.length < c ) {
      final Object[] newData = new Object[ Math.max( data.length + increment, c ) ];
      System.arraycopy( data, 0, newData, 0, size );
      data = newData;
    }
  }

}

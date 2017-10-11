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

package org.pentaho.reporting.engine.classic.core.util;

import java.util.Arrays;

/**
 * An Array-List with a linear instead of exponential growth.
 * <p/>
 * This list is not synchronized and does not implement the full List interface. In fact, this list can only be used to
 * add new values or to clear the complete list.
 *
 * @author Thomas Morgner
 * @noinspection unchecked, SuspiciousSystemArraycopy
 */
public class BulkArrayList<T> implements Cloneable {
  public static interface Func<T> {
    void process( T value, int index );
  }

  /**
   * An empty array used to avoid object creation.
   */
  private static final Object[] EMPTY_ARRAY = new Object[0];
  /**
   * The array holding the list data.
   */
  private Object[] data;
  /**
   * The size of the list.
   */
  private int size;
  /**
   * The number of free slots added on every resize.
   */
  private int increment;

  /**
   * Creates a new IntList with the given initial capacity. The capacity will also be used as increment value when
   * extending the capacity of the list.
   *
   * @param capacity
   *          the initial capacity.
   */
  public BulkArrayList( final int capacity ) {
    data = new Object[capacity];
    increment = capacity;
  }

  public BulkArrayList( final T[] data, final int increment ) {
    this( Math.max( increment, data.length + increment ) );
    this.increment = increment;

    System.arraycopy( data, 0, this.data, 0, data.length );
  }

  /**
   * Ensures, that the list backend can store at least <code>c</code> elements. This method does nothing, if the new
   * capacity is less than the current capacity.
   *
   * @param c
   *          the new capacity of the list.
   */
  private void ensureCapacity( final int c ) {
    if ( data.length <= c ) {
      final Object[] newData = new Object[Math.max( data.length + increment, c + 1 )];
      System.arraycopy( data, 0, newData, 0, size );
      data = newData;
    }
  }

  /**
   * Adds the given int value to the list.
   *
   * @param value
   *          the new value to be added.
   */
  public void add( final T value ) {
    ensureCapacity( size );
    data[size] = value;
    size += 1;
  }

  public void remove( final int index ) {
    if ( index < 0 || index >= size ) {
      throw new IllegalArgumentException();
    }

    final int tailSize = size - index - 1;
    if ( tailSize > 0 ) {
      System.arraycopy( data, index + 1, data, index, tailSize );
    }
    size -= 1;
    data[size] = 0;
  }

  /**
   * Adds the given int value to the list.
   *
   * @param value
   *          the new value to be defined.
   * @param index
   *          the position of the valur that should be redefined.
   */
  public void set( final int index, final T value ) {
    ensureCapacity( index );
    data[index] = value;
    if ( index >= size ) {
      size = index + 1;
    }
  }

  public void removeRange( final int index, final int count ) {
    if ( index < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( count < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    if ( index + count > size ) {
      throw new IndexOutOfBoundsException();
    }

    System.arraycopy( data, index + count, data, index, count );
    size -= count;
    Arrays.fill( data, size, size + count, null );
  }

  /**
   * Returns the value at the given index.
   *
   * @param index
   *          the index
   * @return the value at the given index
   * @throws IndexOutOfBoundsException
   *           if the index is greater or equal to the list size or if the index is negative.
   */
  public T get( final int index ) {
    if ( index >= size || index < 0 ) {
      throw new IndexOutOfBoundsException( "Illegal Index: " + index + " Max:" + size );
    }
    return (T) data[index];
  }

  /**
   * Clears the list.
   */
  public void clear() {
    size = 0;
    Arrays.fill( data, null );
  }

  /**
   * Returns the number of elements in this list.
   *
   * @return the number of elements in the list
   */
  public int size() {
    return size;
  }

  /**
   * Copys the list contents into a new array.
   *
   * @param retval
   *          the array that should receive the contents.
   * @return the list contents as array.
   */
  public <T> T[] toArray( final T[] retval ) {
    if ( retval.length < size ) {
      return (T[]) Arrays.copyOf( data, size, retval.getClass() );
    }

    System.arraycopy( data, 0, retval, 0, size );
    Arrays.fill( retval, size, retval.length, null );
    return retval;
  }

  /**
   * Creates a copy of this list.
   *
   * @return a copy of this list.
   * @throws CloneNotSupportedException
   *           if something went wrong during the cloning.
   */
  public BulkArrayList<T> clone() throws CloneNotSupportedException {
    final BulkArrayList<T> intList = (BulkArrayList<T>) super.clone();
    intList.data = data.clone();
    return intList;
  }

  public void foreach( Func<T> func ) {
    foreach( func, 0, size );
  }

  public void foreach( Func<T> func, int start, int end ) {
    for ( int i = start; i < end; i += 1 ) {
      func.process( (T) data[i], i );
    }
  }
}

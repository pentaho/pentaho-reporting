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

import java.util.EmptyStackException;

/**
 * A Array-List for integer objects. Ints can be added to the list and will be stored in an int-array.
 * <p/>
 * Using this list for storing ints is much faster than creating java.lang.Integer objects and storing them in an
 * ArrayList.
 * <p/>
 * This list is not synchronized and does not implement the full List interface. In fact, this list can only be used to
 * add new values or to clear the complete list.
 *
 * @author Thomas Morgner
 */
public class IntList implements Cloneable {
  /**
   * An empty array used to avoid object creation.
   */
  private static final int[] EMPTY_ARRAY = new int[0];
  /**
   * The array holding the list data.
   */
  private int[] data;
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
  public IntList( final int capacity ) {
    data = new int[capacity];
    increment = capacity;
  }

  public Object clone() {
    try {
      final IntList i = (IntList) super.clone();
      i.data = data.clone();
      return i;
    } catch ( CloneNotSupportedException ce ) {
      throw new IllegalStateException( ce );
    }
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
      final int[] newData = new int[Math.max( data.length + increment, c + 1 )];
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
  public void add( final int value ) {
    ensureCapacity( size );
    data[size] = value;
    size += 1;
  }

  public int pop() {
    if ( size == 0 ) {
      throw new EmptyStackException();
    }

    size -= 1;
    final int retval = data[size];
    data[size] = 0;
    return retval;
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
  public int get( final int index ) {
    if ( index >= size || index < 0 ) {
      throw new IndexOutOfBoundsException( String.valueOf( index ) );
    }
    return data[index];
  }

  /**
   * Clears the list.
   */
  public void clear() {
    size = 0;
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
   * @return the list contents as array.
   */
  public int[] toArray() {
    if ( size == 0 ) {
      return IntList.EMPTY_ARRAY;
    }

    final int[] retval = new int[size];
    System.arraycopy( data, 0, retval, 0, size );
    return retval;
  }

  public int[] toArray( final int fromIndex, final int toIndex ) {
    if ( fromIndex < 0 ) {
      throw new IndexOutOfBoundsException( "fromIndex = " + fromIndex );
    }
    if ( toIndex > size() ) {
      throw new IndexOutOfBoundsException( "toIndex = " + toIndex );
    }
    if ( fromIndex > toIndex ) {
      throw new IllegalArgumentException( "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")" );
    }

    if ( size == 0 ) {
      return IntList.EMPTY_ARRAY;
    }
    final int listSize = toIndex - fromIndex;

    final int[] retval = new int[listSize];
    System.arraycopy( data, fromIndex, retval, 0, listSize );
    return retval;
  }

  public void set( final int index, final int value ) {
    if ( index >= size || index < 0 ) {
      throw new IndexOutOfBoundsException( String.valueOf( index ) );
    }

    data[index] = value;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "IntList" );
    sb.append( "{size=" ).append( size );
    sb.append( ", increment=" ).append( increment );
    sb.append( ", data=" );
    if ( data == null ) {
      sb.append( "null" );
    } else {
      sb.append( "{" );
      for ( int i = 0; data != null && i < size; ++i ) {
        sb.append( i == 0 ? "" : ", " ).append( data[i] );
      }
      sb.append( data == null ? "null" : "}" );
    }
    sb.append( '}' );
    return sb.toString();
  }
}

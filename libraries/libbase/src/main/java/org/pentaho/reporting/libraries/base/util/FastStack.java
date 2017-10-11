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

package org.pentaho.reporting.libraries.base.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * A very simple unsynchronized stack. This one is faster than the java.util-Version, which is based on the synchronized
 * java.util.Vector class.
 *
 * @author Thomas Morgner
 */
public final class FastStack<T> implements Serializable, Cloneable {
  /**
   * The contents of the stack. The array is likely to be larger than the actual size of the stack, so use
   * <code>size</code> to get the last accessible item.
   */
  private Object[] contents;
  /**
   * The current fill-level of the stack.
   */
  private int size;
  /**
   * The initial size of the stack, but also the growth-factor.
   */
  private int initialSize;
  /**
   * A constant for serialization support.
   */
  private static final long serialVersionUID = 3111917250800511580L;

  /**
   * Creates a new stack with an initial size and growth of 10 items.
   */
  public FastStack() {
    initialSize = 10;
  }

  /**
   * Creates a new stack with an initial size and growth as specified.
   *
   * @param size the initial size and growth.
   */
  public FastStack( final int size ) {
    initialSize = Math.max( 1, size );
  }

  /**
   * Checks whether the stack is empty.
   *
   * @return true, if the stack is empty, false otherwise.
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Returns the number of elements in the stack.
   *
   * @return the stack size.
   */
  public int size() {
    return size;
  }

  /**
   * Pushes a new object on the stack. Null-references are allowed.
   *
   * @param o the object, maybe null.
   */
  public void push( final T o ) {
    if ( contents == null ) {
      contents = new Object[ initialSize ];
      contents[ 0 ] = o;
      size = 1;
      return;
    }

    final int oldSize = size;
    size += 1;
    if ( contents.length == size ) {
      // grow ..
      final Object[] newContents = new Object[ size + initialSize ];
      System.arraycopy( contents, 0, newContents, 0, size );
      this.contents = newContents;
    }
    this.contents[ oldSize ] = o;
  }

  /**
   * Loads the top-most element from the stack, without removing it from the stack.
   *
   * @return the top-most object.
   * @throws EmptyStackException if the stack is empty.
   */
  public T peek() {
    if ( size == 0 ) {
      throw new EmptyStackException();
    }
    return (T) contents[ size - 1 ];
  }

  /**
   * Loads the top-most element from the stack and removes it from the stack at the same time.
   *
   * @return the top-most object.
   * @throws EmptyStackException if the stack is empty.
   */
  public T pop() {
    if ( size == 0 ) {
      throw new EmptyStackException();
    }
    size -= 1;
    final T retval = (T) contents[ size ];
    contents[ size ] = null;
    return retval;
  }

  /**
   * Creates a shallow copy of the stack.
   *
   * @return the cloned stack.
   */
  public FastStack<T> clone() {
    try {
      final FastStack<T> stack = (FastStack<T>) super.clone();
      if ( contents != null ) {
        stack.contents = contents.clone();
      }
      return stack;
    } catch ( final CloneNotSupportedException cne ) {
      throw new IllegalStateException( "Clone not supported? Why?" );
    }
  }

  /**
   * Removes all contents from the stack.
   */
  public void clear() {
    if ( contents != null ) {
      Arrays.fill( contents, 0, size, null );
    }
    this.size = 0;
  }

  /**
   * Returns the element from the stack at the given index-position.
   *
   * @param index the element's index.
   * @return the object.
   * @throws IndexOutOfBoundsException if the index given is greater than the number of objects in the stack.
   */
  public T get( final int index ) {
    if ( index >= size ) {
      throw new IndexOutOfBoundsException();
    }
    return (T) contents[ index ];
  }

  public String toString() {
    return "FastStack{" +
      "contents=" + ( contents == null ? null : Arrays.asList( contents ) ) +
      ", size=" + size +
      ", initialSize=" + initialSize +
      '}';
  }
}

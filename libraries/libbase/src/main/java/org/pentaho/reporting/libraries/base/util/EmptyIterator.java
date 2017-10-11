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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An helper class to implement an empty iterator. This iterator will always return false when <code>hasNext</code> is
 * called.
 */
public final class EmptyIterator<T> implements Iterator<T> {
  public static final Iterator INSTANCE = new EmptyIterator();

  public static <T> Iterator<T> emptyIterator() {
    return (Iterator<T>) INSTANCE;
  }

  /**
   * DefaultConstructor.
   */
  private EmptyIterator() {
  }

  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other words, returns <tt>true</tt> if <tt>next</tt>
   * would return an element rather than throwing an exception.)
   *
   * @return <tt>true</tt> if the iterator has more elements.
   */
  public boolean hasNext() {
    return false;
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration.
   * @throws java.util.NoSuchElementException iteration has no more elements.
   */
  public T next() {
    throw new NoSuchElementException( "This iterator is empty." );
  }

  /**
   * Removes from the underlying collection the last element returned by the iterator (optional operation).  This method
   * can be called only once per call to <tt>next</tt>.  The behavior of an iterator is unspecified if the underlying
   * collection is modified while the iteration is in progress in any way other than by calling this method.
   *
   * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this Iterator.
   * @throws IllegalStateException         if the <tt>next</tt> method has not yet been called, or the <tt>remove</tt>
   *                                       method has already been called after the last call to the <tt>next</tt>
   *                                       method.
   */
  public void remove() {
    throw new UnsupportedOperationException( "This iterator is empty, no remove supported." );
  }
}

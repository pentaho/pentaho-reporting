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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * An enumeration that iterates over an array.
 *
 * @author Thomas Morgner
 */
public class ArrayEnumeration implements Enumeration
{
  /**
   * The base datasource.
   */
  private Object[] objectarray = null;
  /**
   * The counter holds the current position inside the array.
   */
  private int counter = 0;

  /**
   * Creates a new enumeration for the given array.
   *
   * @param objectarray the array over which to iterate
   * @throws NullPointerException if the array is null.
   */
  public ArrayEnumeration(Object[] objectarray)
  {
    if (objectarray == null)
    {
      throw new NullPointerException("The array must not be null.");
    }

    this.objectarray = objectarray;
  }

  /**
   * Returns true if this enumeration has at least one more Element.
   *
   * @return true, if there are more elements, false otherwise.
   */
  public boolean hasMoreElements()
  {
    return (counter < objectarray.length);
  }

  /**
   * Returns the next element in the Array.
   *
   * @return the next element in the array.
   * @throws NoSuchElementException if no more elements exist.
   */
  public Object nextElement()
  {
    if (counter >= objectarray.length)
    {
      throw new NoSuchElementException();
    }

    Object retval = objectarray[counter];
    counter += 1;
    return retval;
  }
}

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

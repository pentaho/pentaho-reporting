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

/**
 * A class that caches commonly used Integer-objects. This reduces the number of objects created when processing the
 * report. JDK 1.5 provides a similiar facility, but this cheap functionality is not worth upgrading to that JDK.
 *
 * @author Thomas Morgner
 */
public class IntegerCache {
  /**
   * A cache holding the first 1000 integers.
   */
  private static Integer[] cachedNumbers;

  static {
    cachedNumbers = new Integer[1000];
    for ( int i = 0; i < cachedNumbers.length; i++ ) {
      cachedNumbers[i] = new Integer( i );
    }
  }

  /**
   * Default constructor.
   */
  private IntegerCache() {
  }

  /**
   * Returns the integer-object for the given primitive integer.
   *
   * @param i
   *          the primitive integer value.
   * @return the constructed integer object.
   */
  public static Integer getInteger( final int i ) {
    if ( i < 0 ) {
      return new Integer( i );
    }
    if ( i > 999 ) {
      return new Integer( i );
    }
    return cachedNumbers[i];
  }
}

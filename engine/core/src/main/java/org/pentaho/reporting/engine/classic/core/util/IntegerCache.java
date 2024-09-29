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

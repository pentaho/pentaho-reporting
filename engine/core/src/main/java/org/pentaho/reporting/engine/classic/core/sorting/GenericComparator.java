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


package org.pentaho.reporting.engine.classic.core.sorting;

import java.util.Comparator;

public class GenericComparator implements Comparator<Object> {
  public static final GenericComparator INSTANCE = new GenericComparator();

  public GenericComparator() {
  }

  public int compare( final Object value1, final Object value2 ) {

    if ( value1 == null && value2 == null ) {
      return 0;
    }
    if ( value1 == null ) {
      return -1;
    }
    if ( value2 == null ) {
      return +1;
    }
    if ( value1 instanceof Number && value2 instanceof Number ) {
      final Number n1 = (Number) value1;
      final Number n2 = (Number) value2;
      final double d1 = n1.doubleValue();
      final double d2 = n2.doubleValue();
      return Double.compare( d1, d2 );
    }
    if ( value1 instanceof Comparable && value2 instanceof Comparable ) {
      try {
        final Comparable<Object> c1 = (Comparable<Object>) value1;
        final Comparable<Object> c2 = (Comparable<Object>) value2;
        return c1.compareTo( c2 );
      } catch ( final Exception cce ) {
        // some comparables behave really weird ..
      }
    }
    final String s1 = String.valueOf( value1 );
    final String s2 = String.valueOf( value2 );
    return s1.compareTo( s2 );
  }
}

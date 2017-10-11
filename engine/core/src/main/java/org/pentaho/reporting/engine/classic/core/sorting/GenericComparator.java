/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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

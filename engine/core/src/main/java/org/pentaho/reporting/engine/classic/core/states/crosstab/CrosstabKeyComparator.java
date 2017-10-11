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

package org.pentaho.reporting.engine.classic.core.states.crosstab;

import java.util.Comparator;

public class CrosstabKeyComparator implements Comparator<Object[]> {
  public static final CrosstabKeyComparator INSTANCE = new CrosstabKeyComparator();

  public CrosstabKeyComparator() {
  }

  /**
   * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument
   * is less than, equal to, or greater than the second.
   * <p>
   * <p/>
   * The implementor must ensure that <tt>sgn(compare(x, y)) == -sgn(compare(y, x))</tt> for all <tt>x</tt> and
   * <tt>y</tt>. (This implies that <tt>compare(x, y)</tt> must throw an exception if and only if <tt>compare(y,
   * x)</tt> throws an exception.)
   * <p>
   * <p/>
   * The implementor must also ensure that the relation is transitive: <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y,
   * z)&gt;0))</tt> implies <tt>compare(x, z)&gt;0</tt>.
   * <p>
   * <p/>
   * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt> implies that <tt>sgn(compare(x,
   * z))==sgn(compare(y, z))</tt> for all <tt>z</tt>.
   * <p>
   * <p/>
   * It is generally the case, but <i>not</i> strictly required that <tt>(compare(x, y)==0) == (x.equals(y))</tt>.
   * Generally speaking, any comparator that violates this condition should clearly indicate this fact. The recommended
   * language is "Note: this comparator imposes orderings that are inconsistent with equals."
   *
   * @param key1
   *          the first object to be compared.
   * @param key2
   *          the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   *         than the second.
   * @throws ClassCastException
   *           if the arguments' types prevent them from being compared by this Comparator.
   */
  @SuppressWarnings( "unchecked" )
  public int compare( final Object[] key1, final Object[] key2 ) {
    if ( key1 == null || key2 == null ) {
      throw new IllegalArgumentException( "All keys must be non-null" );
    }
    final int length = key1.length;
    if ( length != key2.length ) {
      throw new IllegalArgumentException( "All keys must have the same length" );
    }

    for ( int i = 0; i < length; i++ ) {
      final Object value1 = key1[i];
      final Object value2 = key2[i];

      if ( value1 == null && value2 == null ) {
        continue;
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
        if ( d1 < d2 ) {
          return -1;
        }
        if ( d1 > d2 ) {
          return +1;
        }
        continue;
      }
      if ( value1 instanceof Comparable && value2 instanceof Comparable ) {
        try {
          final Comparable<Object> c1 = (Comparable<Object>) value1;
          final Comparable<Object> c2 = (Comparable<Object>) value2;
          final int result = c1.compareTo( c2 );
          if ( result == 0 ) {
            continue;
          }
          return result;
        } catch ( final Exception cce ) {
          // some comparables behave really weird ..
        }
      }
      final String s1 = String.valueOf( value1 );
      final String s2 = String.valueOf( value2 );
      final int result = s1.compareTo( s2 );
      if ( result == 0 ) {
        continue;
      }
      return result;
    }

    return 0;
  }
}

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.serializer;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The class comparator can be used to compare and sort classes and their superclasses. The comparator is not able to
 * compare classes which have no relation...
 *
 * @author Thomas Morgner
 */
@SuppressWarnings( "unchecked" )
public class ClassComparator implements Comparator<Class>, Serializable {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = -5225335361837391120L;

  /**
   * Defaultconstructor.
   */
  public ClassComparator() {
    super();
  }

  /**
   * Compares its two arguments for order.  Returns a negative integer, zero, or a positive integer as the first
   * argument is less than, equal to, or greater than the second.<p>
   * <p/>
   * Note: throws ClassCastException if the arguments' types prevent them from being compared by this Comparator. And
   * IllegalArgumentException if the classes share no relation.
   * <p/>
   * The implementor must ensure that <tt>sgn(compare(x, y)) == -sgn(compare(y, x))</tt> for all <tt>x</tt> and
   * <tt>y</tt>.  (This implies that <tt>compare(x, y)</tt> must throw an exception if and only if <tt>compare(y,
   * x)</tt> throws an exception.)<p>
   * <p/>
   * The implementor must also ensure that the relation is transitive: <tt>((compare(x, y)&gt;0) &amp;&amp;
   * (compare(y,
   * z)&gt;0))</tt> implies <tt>compare(x, z)&gt;0</tt>.<p>
   * <p/>
   * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt> implies that <tt>sgn(compare(x,
   * z))==sgn(compare(y, z))</tt> for all <tt>z</tt>.<p>
   * <p/>
   * It is generally the case, but <i>not</i> strictly required that <tt>(compare(x, y)==0) == (x.equals(y))</tt>.
   * Generally speaking, any comparator that violates this condition should clearly indicate this fact.  The
   * recommended
   * language is "Note: this comparator imposes orderings that are inconsistent with equals."
   *
   * @param c1 the first object to be compared.
   * @param c2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   * than the second.
   */
  public int compare( final Class c1, final Class c2 ) {
    if ( c1.equals( c2 ) ) {
      return 0;
    }
    if ( c1.isAssignableFrom( c2 ) ) {
      return -1;
    } else {
      if ( !c2.isAssignableFrom( c2 ) ) {
        throw new IllegalArgumentException(
          "The classes share no relation"
        );
      }
      return 1;
    }
  }

  /**
   * Checks, whether the given classes are comparable. This method will return true, if one of the classes is
   * assignable
   * from the other class.
   *
   * @param c1 the first class to compare
   * @param c2 the second class to compare
   * @return true, if the classes share a direct relation, false otherwise.
   */
  public boolean isComparable( final Class c1, final Class c2 ) {
    return ( c1.isAssignableFrom( c2 ) || c2.isAssignableFrom( c1 ) );
  }
}

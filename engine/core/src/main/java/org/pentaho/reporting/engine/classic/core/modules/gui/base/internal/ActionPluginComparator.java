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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.internal;

import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ActionPlugin;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Creation-Date: 17.05.2007, 20:14:23
 *
 * @author Thomas Morgner
 */
public class ActionPluginComparator implements Comparator<ActionPlugin>, Serializable {
  public ActionPluginComparator() {
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
   * @param ap1
   *          the first object to be compared.
   * @param ap2
   *          the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   *         than the second.
   * @throws ClassCastException
   *           if the arguments' types prevent them from being compared by this Comparator.
   */
  public int compare( final ActionPlugin ap1, final ActionPlugin ap2 ) {
    final int to1 = ap1.getToolbarOrder();
    final int to2 = ap2.getToolbarOrder();

    if ( to1 < to2 ) {
      return -1;
    } else if ( to1 > to2 ) {
      return +1;
    }
    return 0;
  }
}

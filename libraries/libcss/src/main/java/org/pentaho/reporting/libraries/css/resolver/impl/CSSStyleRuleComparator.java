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

package org.pentaho.reporting.libraries.css.resolver.impl;

import org.pentaho.reporting.libraries.css.model.CSSStyleRule;
import org.pentaho.reporting.libraries.css.selectors.CSSSelector;
import org.pentaho.reporting.libraries.css.selectors.SelectorWeight;

import java.util.Comparator;

/**
 * Creation-Date: 08.12.2005, 21:31:51
 *
 * @author Thomas Morgner
 */
public class CSSStyleRuleComparator implements Comparator {
  public CSSStyleRuleComparator() {
  }

  /**
   * Compares its two arguments for order.  Returns a negative integer, zero, or a positive integer as the first
   * argument is less than, equal to, or greater than the second.<p>
   * <p/>
   * The implementor must ensure that <tt>sgn(compare(x, y)) == -sgn(compare(y, x))</tt> for all <tt>x</tt> and
   * <tt>y</tt>.  (This implies that <tt>compare(x, y)</tt> must throw an exception if and only if <tt>compare(y,
   * x)</tt> throws an exception.)<p>
   * <p/>
   * The implementor must also ensure that the relation is transitive: <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y,
   * z)&gt;0))</tt> implies <tt>compare(x, z)&gt;0</tt>.<p>
   * <p/>
   * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt> implies that <tt>sgn(compare(x,
   * z))==sgn(compare(y, z))</tt> for all <tt>z</tt>.<p>
   * <p/>
   * It is generally the case, but <i>not</i> strictly required that <tt>(compare(x, y)==0) == (x.equals(y))</tt>.
   * Generally speaking, any comparator that violates this condition should clearly indicate this fact. The recommended
   * language is "Note: this comparator imposes orderings that are inconsistent with equals."
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   * than the second.
   * @throws ClassCastException if the arguments' types prevent them from being compared by this Comparator.
   */
  public int compare( final Object o1, final Object o2 ) {
    final CSSStyleRule r1 = (CSSStyleRule) o1;
    final CSSStyleRule r2 = (CSSStyleRule) o2;

    final CSSSelector selector1 = r1.getSelector();
    final CSSSelector selector2 = r2.getSelector();

    final SelectorWeight weight1 = selector1.getWeight();
    final SelectorWeight weight2 = selector2.getWeight();

    return weight1.compareTo( weight2 );
  }
}

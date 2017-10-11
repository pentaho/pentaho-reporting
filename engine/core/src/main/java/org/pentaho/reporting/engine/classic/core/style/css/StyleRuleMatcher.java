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

package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.css.selector.SelectorWeight;

/**
 * A (possibly stateful) style matcher. This class is responsible for checking which style rule applies to the given
 * document.
 * <p/>
 * It is guaranteed, that the matcher receives the elements in the order in which they appear in the document.
 * <p/>
 * Although the style rule matcher does not receive explicit element-opened and element-closed events, these events can
 * be derived from the layout element and its relation to the parent (and possibly previously received element and its
 * parent).
 *
 * @author Thomas Morgner
 */
public interface StyleRuleMatcher {
  public static class MatcherResult implements Comparable<MatcherResult> {
    private SelectorWeight weight;
    private ElementStyleRule rule;

    public MatcherResult( final SelectorWeight weight, final ElementStyleRule rule ) {
      if ( weight == null ) {
        throw new IllegalStateException();
      }
      if ( rule == null ) {
        throw new IllegalStateException();
      }
      this.weight = weight;
      this.rule = rule;
    }

    public SelectorWeight getWeight() {
      return weight;
    }

    public ElementStyleRule getRule() {
      return rule;
    }

    public int compareTo( final MatcherResult o ) {
      if ( o == null ) {
        return -1;
      }

      return weight.compareTo( o.getWeight() );
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final MatcherResult that = (MatcherResult) o;

      if ( !rule.equals( that.rule ) ) {
        return false;
      }
      if ( !weight.equals( that.weight ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = weight.hashCode();
      result = 31 * result + rule.hashCode();
      return result;
    }
  }

  /**
   * Creates an independent copy of this style rule matcher.
   *
   * @return
   */
  public StyleRuleMatcher deriveInstance();

  public MatcherResult[] getMatchingRules( ReportElement element );

  public boolean isMatchingPseudoElement( ReportElement element, String pseudo );

  void initialize( DocumentContext layoutProcess );
}

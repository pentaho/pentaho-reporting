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

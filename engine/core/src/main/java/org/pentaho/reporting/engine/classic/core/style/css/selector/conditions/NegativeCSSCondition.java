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


package org.pentaho.reporting.engine.classic.core.style.css.selector.conditions;

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.NegativeCondition;

public class NegativeCSSCondition implements NegativeCondition, CSSCondition {
  private CSSCondition condition;

  public NegativeCSSCondition( final CSSCondition condition ) {
    if ( condition == null ) {
      throw new NullPointerException();
    }
    this.condition = condition;
  }

  public Condition getCondition() {
    return condition;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return SAC_NEGATIVE_CONDITION;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final NegativeCSSCondition that = (NegativeCSSCondition) o;

    if ( !condition.equals( that.condition ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return condition.hashCode();
  }

  public String print( final NamespaceCollection namespaces ) {
    return "not(" + condition.print( namespaces ) + ")";
  }
}

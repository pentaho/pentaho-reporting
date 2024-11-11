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
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;

/**
 * Creation-Date: 24.11.2005, 19:45:12
 *
 * @author Thomas Morgner
 */
public final class AndCSSCondition implements CombinatorCondition, CSSCondition {
  private CSSCondition firstCondition;
  private CSSCondition secondCondition;

  public AndCSSCondition( final CSSCondition firstCondition, final CSSCondition secondCondition ) {
    if ( firstCondition == null ) {
      throw new NullPointerException();
    }
    if ( secondCondition == null ) {
      throw new NullPointerException();
    }
    this.firstCondition = firstCondition;
    this.secondCondition = secondCondition;
  }

  /**
   * Returns the first condition.
   */
  public Condition getFirstCondition() {
    return firstCondition;
  }

  /**
   * Returns the second condition.
   */
  public Condition getSecondCondition() {
    return secondCondition;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return Condition.SAC_AND_CONDITION;
  }

  public String print( final NamespaceCollection namespaces ) {
    return firstCondition.print( namespaces ) + secondCondition.print( namespaces );
  }
}

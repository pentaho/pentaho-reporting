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


package org.pentaho.reporting.libraries.css.selectors;

import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * Creation-Date: 30.11.2005, 16:43:45
 *
 * @author Thomas Morgner
 */
public class CSSConditionalSelector extends AbstractSelector implements ConditionalSelector {
  private static final int ID_CONDITION = 0;
  private static final int ATTR_CONDITION = 1;
  private static final int OTHER_CONDITION = 2;

  private Condition condition;
  private SimpleSelector simpleSelector;

  public CSSConditionalSelector( final SimpleSelector simpleSelector,
                                 final Condition condition ) {
    this.simpleSelector = simpleSelector;
    this.condition = condition;
  }

  protected SelectorWeight createWeight() {
    final int[] conditions = new int[ 3 ];
    countConditions( conditions, condition );
    return new SelectorWeight
      ( 0, conditions[ 0 ], conditions[ 1 ], conditions[ 2 ] + 1 );
  }

  private void countConditions
    ( final int[] conditionCounter, final Condition condition ) {
    if ( condition.getConditionType() == Condition.SAC_ID_CONDITION ) {
      conditionCounter[ ID_CONDITION ] += 1;
    } else if ( condition instanceof AttributeCondition ) {
      conditionCounter[ ATTR_CONDITION ] += 1;
    } else if ( condition instanceof CombinatorCondition ) {
      CombinatorCondition c = (CombinatorCondition) condition;
      countConditions( conditionCounter, c.getFirstCondition() );
      countConditions( conditionCounter, c.getSecondCondition() );
    } else {
      conditionCounter[ OTHER_CONDITION ] += 1;
    }
  }

  /**
   * Returns the simple selector. <p>The simple selector can't be a <code>ConditionalSelector</code>.</p>
   */
  public SimpleSelector getSimpleSelector() {
    return simpleSelector;
  }

  /**
   * Returns the condition to be applied on the simple selector.
   */
  public Condition getCondition() {
    return condition;
  }

  /**
   * An integer indicating the type of <code>Selector</code>
   */
  public short getSelectorType() {
    return SAC_CONDITIONAL_SELECTOR;
  }
}

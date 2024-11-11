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


package org.pentaho.reporting.libraries.css.selectors.conditions;

import org.w3c.css.sac.Condition;
import org.w3c.css.sac.NegativeCondition;

/**
 * Creation-Date: 24.11.2005, 19:49:05
 *
 * @author Thomas Morgner
 */
public class NegativeCSSCondition implements NegativeCondition, CSSCondition {
  private CSSCondition condition;

  public NegativeCSSCondition( final CSSCondition condition ) {
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
}

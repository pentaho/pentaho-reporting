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


package org.pentaho.reporting.libraries.css.selectors.conditions;

import org.w3c.css.sac.PositionalCondition;

/**
 * Creation-Date: 24.11.2005, 19:51:10
 *
 * @author Thomas Morgner
 */
public class PositionalCSSCondition implements CSSCondition, PositionalCondition {
  private int position;
  private boolean matchByType;
  private boolean matchByName;

  public PositionalCSSCondition( final int position,
                                 final boolean matchByType,
                                 final boolean matchByName ) {
    this.position = position;
    this.matchByType = matchByType;
    this.matchByName = matchByName;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return SAC_POSITIONAL_CONDITION;
  }

  /**
   * Returns the position in the tree. <p>A negative value means from the end of the child node list. <p>The child node
   * list begins at 0.
   */
  public int getPosition() {
    return position;
  }

  /**
   * <code>true</code> if the child node list only shows nodes of the same type of the selector (only elements, only
   * PIS, ...)
   */
  public boolean getTypeNode() {
    return matchByType;
  }

  /**
   * <code>true</code> if the node should have the same node type (for element, same namespaceURI and same localName).
   */
  public boolean getType() {
    return matchByName;
  }
}

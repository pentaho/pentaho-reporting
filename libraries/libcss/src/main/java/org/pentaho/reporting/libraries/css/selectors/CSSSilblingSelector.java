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

package org.pentaho.reporting.libraries.css.selectors;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

import java.io.Serializable;

/**
 * We do not support DOM node types, we always assume elements here (or evaluate both selectors to see if they match).
 *
 * @author Thomas Morgner
 */
public class CSSSilblingSelector extends AbstractSelector
  implements SiblingSelector, Serializable {
  private short nodeType;
  private Selector selector;
  private SimpleSelector silblingSelector;

  public CSSSilblingSelector( final short nodeType,
                              final Selector selector,
                              final SimpleSelector silblingSelector ) {
    this.nodeType = nodeType;
    this.selector = selector;
    this.silblingSelector = silblingSelector;
  }

  /**
   * The node type to considered in the siblings list. All DOM node types are supported. In order to support the "any"
   * node type, the code ANY_NODE is added to the DOM node types.
   */
  public short getNodeType() {
    return nodeType;
  }

  /**
   * Returns the first selector.
   */
  public Selector getSelector() {
    return selector;
  }

  /*
  * Returns the second selector.
  */
  public SimpleSelector getSiblingSelector() {
    return silblingSelector;
  }

  /**
   * An integer indicating the type of <code>Selector</code>
   */
  public short getSelectorType() {
    return SAC_DIRECT_ADJACENT_SELECTOR;
  }

  protected SelectorWeight createWeight() {
    if ( silblingSelector instanceof CSSSelector == false ||
      selector instanceof CSSSelector == false ) {
      throw new ClassCastException( "Invalid selector implementation!" );
    }
    CSSSelector anchestor = (CSSSelector) silblingSelector;
    CSSSelector simple = (CSSSelector) selector;
    return new SelectorWeight( anchestor.getWeight(), simple.getWeight() );
  }
}

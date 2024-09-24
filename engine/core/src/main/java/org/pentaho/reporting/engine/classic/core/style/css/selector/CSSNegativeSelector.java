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

package org.pentaho.reporting.engine.classic.core.style.css.selector;

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * Creation-Date: 30.11.2005, 16:09:21
 *
 * @author Thomas Morgner
 */
public class CSSNegativeSelector extends AbstractSelector implements NegativeSelector {
  private SimpleSelector selector;
  private CSSSelector cssSelector;

  public CSSNegativeSelector( final SimpleSelector selector ) {
    this.selector = selector;
    this.cssSelector = (CSSSelector) selector;
  }

  protected SelectorWeight createWeight() {
    if ( selector instanceof CSSSelector == false ) {
      return new SelectorWeight( 0, 0, 0, 0 );
    }

    final CSSSelector sel = (CSSSelector) selector;
    return sel.getWeight();
  }

  /**
   * Returns the simple selector.
   */
  public SimpleSelector getSimpleSelector() {
    return selector;
  }

  /**
   * An integer indicating the type of <code>Selector</code>
   */
  public short getSelectorType() {
    return SAC_NEGATIVE_SELECTOR;
  }

  public String print( final NamespaceCollection namespaces ) {
    return "not(" + cssSelector.print( namespaces ) + ")";
  }
}

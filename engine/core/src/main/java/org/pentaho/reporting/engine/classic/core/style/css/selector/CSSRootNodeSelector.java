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
import org.w3c.css.sac.SimpleSelector;

import java.io.Serializable;

public class CSSRootNodeSelector extends AbstractSelector implements SimpleSelector, Serializable {
  public CSSRootNodeSelector() {
  }

  protected SelectorWeight createWeight() {
    return new SelectorWeight( 0, 0, 0, 1 );
  }

  /**
   * An integer indicating the type of <code>Selector</code>
   */
  public short getSelectorType() {
    return SAC_ROOT_NODE_SELECTOR;
  }

  public String print( final NamespaceCollection namespaces ) {
    // todo: Not yet supported by parser
    return ":root";
  }
}

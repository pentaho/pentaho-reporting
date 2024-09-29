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

/**
 * Creation-Date: 05.12.2005, 19:50:49
 *
 * @author Thomas Morgner
 */
public abstract class AbstractSelector implements CSSSelector {
  private SelectorWeight weight;

  public AbstractSelector() {
  }

  public SelectorWeight getWeight() {
    if ( weight == null ) {
      weight = createWeight();
    }
    return weight;
  }

  protected abstract SelectorWeight createWeight();
}

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.css.selectors;

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

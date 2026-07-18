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



package org.pentaho.reporting.libraries.css.resolver.tokens.computed;

/**
 * The elemnts function. This is a lookup to the current pending context. The elements function grabs the last value
 * from the pending context and drops all previous elements. If the pending context is empty, it preserves its content.
 * <p/>
 * The elements get removed from the normal flow and get added to the pending flow. Due to the highly volatile nature of
 * that step, no - I repeat - no validation is done to normalize inline and block elements.
 *
 * @author Thomas Morgner
 */
public class ElementsToken extends ComputedToken {
  private String key;

  public ElementsToken( final String key ) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}

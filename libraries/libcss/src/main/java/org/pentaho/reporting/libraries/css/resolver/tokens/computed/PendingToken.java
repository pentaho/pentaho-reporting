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


package org.pentaho.reporting.libraries.css.resolver.tokens.computed;

/**
 * The pending function. This is a lookup to the current pending context. If the pending context is empty, the element
 * is not displayed (as if it had been declared 'display: none'.
 * <p/>
 * The elements get removed from the normal flow and get added to the pending flow. Due to the highly volatile nature of
 * that step, no - I repeat - no validation is done to normalize inline and block elements.
 *
 * @author Thomas Morgner
 */
public class PendingToken extends ComputedToken {
  private String key;

  public PendingToken( final String key ) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}

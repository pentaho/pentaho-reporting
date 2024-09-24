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

package org.pentaho.reporting.libraries.css.resolver.tokens.resolved;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.CounterToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.types.TextType;

/**
 * Creation-Date: 12.06.2006, 14:38:29
 *
 * @author Thomas Morgner
 */
public class ResolvedCounterToken implements TextType {
  private CounterToken parent;
  private int counterValue;

  public ResolvedCounterToken( final CounterToken parent,
                               final int counterValue ) {
    this.parent = parent;
    this.counterValue = counterValue;
  }

  public CounterToken getParent() {
    return parent;
  }

  public String getText() {
    final CounterToken counterToken = getParent();
    final CounterStyle style = counterToken.getStyle();
    return style.getCounterValue( counterValue );
  }

  public int getCounterValue() {
    return counterValue;
  }
}

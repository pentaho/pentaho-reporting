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


package org.pentaho.reporting.libraries.css.resolver.tokens.computed;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;

/**
 * Creation-Date: 25.05.2006, 17:16:40
 *
 * @author Thomas Morgner
 */
public class CounterToken extends ComputedToken {
  private String name;
  private CounterStyle style;

  public CounterToken( final String name, final CounterStyle style ) {
    this.name = name;
    this.style = style;
  }

  public String getName() {
    return name;
  }

  public CounterStyle getStyle() {
    return style;
  }
}

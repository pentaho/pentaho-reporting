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
 * This is a meta-token. It must be completly resolved during the ContentNormalization, and must be replaced by a
 * sequence of 'Counter' tokens.
 *
 * @author Thomas Morgner
 */
public class CountersToken extends ComputedToken {
  private String name;
  private String separator;
  private CounterStyle style;

  public CountersToken( final String name,
                        final String separator,
                        final CounterStyle style ) {
    this.name = name;
    this.separator = separator;
    this.style = style;
  }

  public String getSeparator() {
    return separator;
  }

  public String getName() {
    return name;
  }

  public CounterStyle getStyle() {
    return style;
  }
}

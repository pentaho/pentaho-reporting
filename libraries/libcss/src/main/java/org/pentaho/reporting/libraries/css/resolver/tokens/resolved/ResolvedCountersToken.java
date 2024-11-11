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
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.CountersToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.types.TextType;


/**
 * Creation-Date: 12.06.2006, 14:38:29
 *
 * @author Thomas Morgner
 */
public class ResolvedCountersToken implements TextType {
  private CountersToken parent;
  private int[] counterValues;

  public ResolvedCountersToken( final CountersToken parent,
                                final int[] counterValues ) {
    this.parent = parent;
    this.counterValues = counterValues;
  }

  public CountersToken getParent() {
    return parent;
  }

  public String getText() {
    final CountersToken counterToken = getParent();
    final CounterStyle style = counterToken.getStyle();
    final String separator = counterToken.getSeparator();
    final StringBuffer buffer = new StringBuffer();

    for ( int i = 0; i < counterValues.length; i++ ) {
      if ( i != 0 ) {
        buffer.append( separator );
      }
      final int value = counterValues[ i ];
      buffer.append( style.getCounterValue( value ) );
    }

    return buffer.toString();
  }

  public int[] getCounterValue() {
    return (int[]) counterValues.clone();
  }
}

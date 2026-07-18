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



package org.pentaho.reporting.libraries.css.counter.other;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;

public class AsterisksCounterStyle implements CounterStyle {
  public AsterisksCounterStyle() {
  }

  public String getCounterValue( final int index ) {
    final StringBuffer b = new StringBuffer( index );
    for ( int i = 0; i < index; i++ ) {
      b.append( '*' );
    }
    return b.toString();
  }

  public String getSuffix() {
    return "";
  }
}

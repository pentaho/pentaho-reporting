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



package org.pentaho.reporting.libraries.css.counter.numeric;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;

public class BinaryCounterStyle implements CounterStyle {
  public BinaryCounterStyle() {
  }

  public String getCounterValue( final int index ) {
    return Integer.toBinaryString( index );
  }

  public String getSuffix() {
    return ".";
  }
}

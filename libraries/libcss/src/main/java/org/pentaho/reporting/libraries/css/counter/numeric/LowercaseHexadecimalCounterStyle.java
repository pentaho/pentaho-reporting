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


package org.pentaho.reporting.libraries.css.counter.numeric;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;

public class LowercaseHexadecimalCounterStyle implements CounterStyle {
  public LowercaseHexadecimalCounterStyle() {
  }

  public String getCounterValue( final int index ) {
    return Integer.toHexString( index );
  }

  public String getSuffix() {
    return ".";
  }
}

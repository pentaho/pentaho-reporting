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

package org.pentaho.reporting.libraries.css.counter.numeric;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;

public class UppercaseHexadecimalCounterStyle implements CounterStyle {
  public UppercaseHexadecimalCounterStyle() {
  }

  public String getCounterValue( final int index ) {
    return Integer.toHexString( index ).toUpperCase();
  }

  public String getSuffix() {
    return ".";
  }
}

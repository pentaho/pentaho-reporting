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


package org.pentaho.reporting.libraries.css.counter.glyph;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;

public class DiamondCounterStyle implements CounterStyle {
  public DiamondCounterStyle() {
  }

  public String getCounterValue( final int index ) {
    return "\u25c6";
  }

  public String getSuffix() {
    return "";
  }
}

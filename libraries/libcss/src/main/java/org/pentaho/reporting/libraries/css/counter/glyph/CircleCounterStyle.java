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



package org.pentaho.reporting.libraries.css.counter.glyph;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;

public class CircleCounterStyle implements CounterStyle {
  public CircleCounterStyle() {
  }

  public String getCounterValue( final int index ) {
    return "\u25e6";
  }

  public String getSuffix() {
    return "";
  }

}

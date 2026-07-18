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

public class DiscCounterStyle implements CounterStyle {
  public DiscCounterStyle() {
  }

  public String getCounterValue( final int index ) {
    return "\u2022";
  }

  public String getSuffix() {
    return "";
  }
}

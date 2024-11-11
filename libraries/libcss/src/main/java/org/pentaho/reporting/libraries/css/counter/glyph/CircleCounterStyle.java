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

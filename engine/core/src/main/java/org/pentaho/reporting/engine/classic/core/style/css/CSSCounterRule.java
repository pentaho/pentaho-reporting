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


package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

public class CSSCounterRule extends ElementStyleSheet {
  private String name;

  public CSSCounterRule( final String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
